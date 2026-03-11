package com.zenith.orderservice.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class StoreServiceClient {

    private final RestClient restClient;

    public StoreServiceClient(RestClient storeRestClient) {
        this.restClient = storeRestClient;
    }

    public boolean storeExists(Long storeId) {
        try {
            restClient.get()
                    .uri("/api/stores/{id}", storeId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new RuntimeException("not found");
                    })
                    .body(Map.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ProductInfo getProduct(Long storeId, Long productId) {
        try {
            Map<?, ?> body = restClient.get()
                    .uri("/api/stores/{storeId}/products/{productId}", storeId, productId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new RuntimeException("not found");
                    })
                    .body(Map.class);
            if (body == null) return null;
            return mapToProductInfo(storeId, body);
        } catch (Exception e) {
            return null;
        }
    }

    public ProductInfo getProductBySku(Long storeId, String sku) {
        try {
            Map<?, ?> body = restClient.get()
                    .uri("/api/stores/{storeId}/products/sku/{sku}",
                            storeId, URLEncoder.encode(sku, StandardCharsets.UTF_8))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new RuntimeException("not found");
                    })
                    .body(Map.class);
            if (body == null) return null;
            return mapToProductInfo(storeId, body);
        } catch (Exception e) {
                return null;
        }
    }

    public ProductInfo getProductByUpc(Long storeId, String upc) {
        try {
            Map<?, ?> body = restClient.get()
                    .uri("/api/stores/{storeId}/products/upc/{upc}",
                            storeId, URLEncoder.encode(upc, StandardCharsets.UTF_8))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new RuntimeException("not found");
                    })
                    .body(Map.class);
            if (body == null) return null;
            return mapToProductInfo(storeId, body);
        } catch (Exception e) {
            return null;
        }
    }

    private ProductInfo mapToProductInfo(Long storeId, Map<?, ?> body) {
        System.out.println(body);
        Long productId = body.get("productId") != null ? Long.valueOf(body.get("id").toString()) : null;
        String sku = (String) body.get("sku");
        String upc = (String) body.get("upc");
        String name = body.get("productName") != null ? (String) body.get("productName") : (String) body.get("name");
        BigDecimal price = new BigDecimal(body.get("price").toString());
        Long returnedStoreId = body.get("storeId") != null ? Long.valueOf(body.get("storeId").toString()) : null;
        if (returnedStoreId == null || !returnedStoreId.equals(storeId)) return null;
        return new ProductInfo(productId, sku, upc, name, price);
    }

    public record ProductInfo(Long productId, String sku, String upc, String name, BigDecimal price) {}
}
