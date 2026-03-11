package com.zenith.orderservice.service;

import com.zenith.orderservice.client.StoreServiceClient;
import com.zenith.orderservice.client.StoreServiceClient.ProductInfo;
import com.zenith.orderservice.dto.OrderResponse;
import com.zenith.orderservice.dto.PlaceOrderRequest;
import com.zenith.orderservice.entity.Order;
import com.zenith.orderservice.entity.OrderItem;
import com.zenith.orderservice.entity.OrderStatus;
import com.zenith.orderservice.entity.PaymentStatus;
import com.zenith.orderservice.exception.BadOrderRequestException;
import com.zenith.orderservice.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final StoreServiceClient storeClient;

    public OrderService(OrderRepository orderRepository, StoreServiceClient storeClient) {
        this.orderRepository = orderRepository;
        this.storeClient = storeClient;
    }

    @Transactional
    public OrderResponse placeOrder(Long customerId, PlaceOrderRequest request) {
        if (!storeClient.storeExists(request.getStoreId())) {
            throw new BadOrderRequestException("Store not found: " + request.getStoreId());
        }

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (PlaceOrderRequest.Item reqItem : request.getItems()) {
            ProductInfo product = resolveProduct(request.getStoreId(), reqItem);
            if (product == null) {
                String identifier = reqItem.getSku() != null ? "sku " + reqItem.getSku() : "upc " + reqItem.getUpc();
                throw new BadOrderRequestException("Product not found for " + identifier + " in store " + request.getStoreId());
            }

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

        Order order = new Order();
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
