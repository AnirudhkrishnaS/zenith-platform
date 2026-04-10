package com.zenith.orderservice.client;

import com.zenith.orderservice.exception.InventoryCallException;
import com.zenith.orderservice.exception.InventoryReserveException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class InventoryServiceClient {

    private final RestClient restClient;

    public InventoryServiceClient(@Qualifier("inventoryRestClient") RestClient inventoryRestClient) {
        this.restClient = inventoryRestClient;
    }

    public void reserve(String orderNumber, Long storeId, List<ReserveItem> items) {
        restClient.post()
                .uri("/api/inventory/reserve")
                .body(Map.of(
                        "orderId", orderNumber,
                        "storeId", storeId,
                        "items", items.stream()
                                .map(i -> Map.of("skuId", i.skuId(), "quantity", i.quantity()))
                                .toList()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new InventoryReserveException(res.getStatusCode(), "Inventory reserve failed");
                })
                .toBodilessEntity();
    }

    public void commit(String orderNumber) {
        restClient.post()
                .uri("/api/inventory/commit/{orderId}", orderNumber)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new InventoryCallException("commit", orderNumber, res.getStatusCode());
                })
                .toBodilessEntity();
    }

    public void release(String orderNumber) {
        restClient.post()
                .uri("/api/inventory/release/{orderId}", orderNumber)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new InventoryCallException("release", orderNumber, res.getStatusCode());
                })
                .toBodilessEntity();
    }

    public record ReserveItem(Long skuId, int quantity) {}
}
