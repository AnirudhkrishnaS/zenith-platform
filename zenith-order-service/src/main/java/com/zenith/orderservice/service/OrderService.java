package com.zenith.orderservice.service;

import com.zenith.orderservice.client.InventoryServiceClient;
import com.zenith.orderservice.client.StoreServiceClient;
import com.zenith.orderservice.client.StoreServiceClient.ProductInfo;
import com.zenith.orderservice.dto.OrderResponse;
import com.zenith.orderservice.dto.PlaceOrderRequest;
import com.zenith.orderservice.entity.Order;
import com.zenith.orderservice.entity.OrderItem;
import com.zenith.orderservice.enums.OrderStatus;
import com.zenith.orderservice.enums.PaymentStatus;
import com.zenith.orderservice.event.OrderEvent;
import com.zenith.orderservice.event.OrderEventPublisher;
import com.zenith.orderservice.exception.BadOrderRequestException;
import com.zenith.orderservice.exception.ForbiddenException;
import com.zenith.orderservice.exception.IllegalStatusTransitionException;
import com.zenith.orderservice.exception.OrderNotFoundException;
import com.zenith.orderservice.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final String ORDER_NUMBER_PREFIX = "ZEN-";

    private final OrderRepository orderRepository;
    private final StoreServiceClient storeClient;
    private final InventoryServiceClient inventoryClient;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository,
                        StoreServiceClient storeClient,
                        InventoryServiceClient inventoryClient,
                        OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.storeClient = storeClient;
        this.inventoryClient = inventoryClient;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long orderId, Long userId, String userType) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        boolean isCustomer = order.getCustomerId().equals(userId);
        boolean isStoreOwner = "BUSINESS_OWNER".equals(userType)
                && userId.equals(storeClient.getStoreOwnerId(order.getStoreId()));

        if (!isCustomer && !isStoreOwner) {
            throw new ForbiddenException("You do not have access to this order");
        }
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(Long customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getStoreOrders(Long storeId, Long userId) {
        Long ownerId = storeClient.getStoreOwnerId(storeId);
        if (ownerId == null || !ownerId.equals(userId)) {
            throw new ForbiddenException("You are not the owner of this store");
        }
        return orderRepository.findByStoreIdOrderByCreatedAtDesc(storeId).stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse acceptOrder(Long orderId, Long userId) {
        Order order = findOrderForStoreOwner(orderId, userId);
        OrderStatus prev = order.getStatus();
        transition(order, OrderStatus.ACCEPTED);
        Order saved = orderRepository.save(order);
        eventPublisher.publish(OrderEvent.from("order.accepted", saved, prev));
        return OrderResponse.from(saved);
    }

    @Transactional
    public OrderResponse rejectOrder(Long orderId, Long userId, String reason) {
        Order order = findOrderForStoreOwner(orderId, userId);
        OrderStatus prev = order.getStatus();
        inventoryClient.release(order.getOrderNumber());
        transition(order, OrderStatus.REJECTED);
        order.setRejectReason(reason);
        Order saved = orderRepository.save(order);
        eventPublisher.publish(OrderEvent.from("order.rejected", saved, prev));
        return OrderResponse.from(saved);
    }

    @Transactional
    public OrderResponse prepareOrder(Long orderId, Long userId) {
        Order order = findOrderForStoreOwner(orderId, userId);
        OrderStatus prev = order.getStatus();
        inventoryClient.commit(order.getOrderNumber());
        transition(order, OrderStatus.PREPARING);
        Order saved = orderRepository.save(order);
        eventPublisher.publish(OrderEvent.from("order.preparing", saved, prev));
        return OrderResponse.from(saved);
    }

    @Transactional
    public OrderResponse readyOrder(Long orderId, Long userId) {
        Order order = findOrderForStoreOwner(orderId, userId);
        OrderStatus prev = order.getStatus();
        transition(order, OrderStatus.READY_FOR_PICKUP);
        Order saved = orderRepository.save(order);
        eventPublisher.publish(OrderEvent.from("order.ready_for_pickup", saved, prev));
        return OrderResponse.from(saved);
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long customerId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (!order.getCustomerId().equals(customerId)) {
            throw new ForbiddenException("You can only cancel your own orders");
        }
        OrderStatus prev = order.getStatus();
        inventoryClient.release(order.getOrderNumber());
        transition(order, OrderStatus.CANCELLED);
        order.setCancelReason(reason);
        Order saved = orderRepository.save(order);
        eventPublisher.publish(OrderEvent.from("order.cancelled", saved, prev));
        return OrderResponse.from(saved);
    }

    private Order findOrderForStoreOwner(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        Long ownerId = storeClient.getStoreOwnerId(order.getStoreId());
        if (ownerId == null || !ownerId.equals(userId)) {
            throw new ForbiddenException("You are not the owner of this store");
        }
        return order;
    }

    private void transition(Order order, OrderStatus target) {
        if (!order.getStatus().canTransitionTo(target)) {
            throw new IllegalStatusTransitionException(order.getStatus(), target);
        }
        order.setStatus(target);
    }

    @Transactional
    public OrderResponse placeOrder(Long customerId, PlaceOrderRequest request) {
        if (!storeClient.storeExists(request.getStoreId())) {
            throw new BadOrderRequestException("Store not found: " + request.getStoreId());
        }

        List<OrderItem> items = new ArrayList<>();
        List<InventoryServiceClient.ReserveItem> reserveItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (PlaceOrderRequest.Item reqItem : request.getItems()) {
            ProductInfo product = resolveProduct(request.getStoreId(), reqItem);
            if (product == null) {
                String identifier = reqItem.getSku() != null ? "sku " + reqItem.getSku() : "upc " + reqItem.getUpc();
                throw new BadOrderRequestException("Product not found for " + identifier + " in store " + request.getStoreId());
            }

            reserveItems.add(new InventoryServiceClient.ReserveItem(product.skuId(), reqItem.getQuantity()));

            OrderItem item = new OrderItem();
            item.setProductId(product.productId());
            item.setSku(product.sku() != null ? product.sku() : reqItem.getSku());
            item.setUpc(product.upc() != null ? product.upc() : reqItem.getUpc());
            item.setProductName(product.name());
            item.setQuantity(reqItem.getQuantity());
            item.setPrice(product.price());
            items.add(item);

            total = total.add(product.price().multiply(BigDecimal.valueOf(reqItem.getQuantity())));
        }

        long seq = orderRepository.getNextOrderNumber();
        String orderNumber = ORDER_NUMBER_PREFIX + String.format("%06d", seq);

        //TODO reserve succeeded, order row never (or no longer) exists → stuck/orphan reservations.(Two logic : Periodic Job or Catch exception reserve)
        inventoryClient.reserve(orderNumber, request.getStoreId(), reserveItems);

        Order order = new Order();
        order.setId(seq);
        order.setOrderNumber(orderNumber);
        order.setCustomerId(customerId);
        order.setStoreId(request.getStoreId());
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setTotal(total);

        for (OrderItem item : items) {
            item.setOrder(order);
        }
        order.setItems(items);

        Order saved = orderRepository.save(order);
        eventPublisher.publish(OrderEvent.from("order.placed", saved, null));
        return OrderResponse.from(saved);
    }

    private ProductInfo resolveProduct(Long storeId, PlaceOrderRequest.Item reqItem) {
        if (reqItem.getSku() != null && !reqItem.getSku().isBlank()) {
            return storeClient.getProductBySku(storeId, reqItem.getSku());
        }
        if (reqItem.getUpc() != null && !reqItem.getUpc().isBlank()) {
            return storeClient.getProductByUpc(storeId, reqItem.getUpc());
        }
        return null;
    }
}
