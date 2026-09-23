package com.webshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class OrderInputDto {

    @NotNull(message = "Customer ID is required")
    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("shipping_address_id")
    private Long shippingAddressId;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemInputDto> items;

    public OrderInputDto() {
    }

    public OrderInputDto(Long customerId, Long shippingAddressId, List<OrderItemInputDto> items) {
        this.customerId = customerId;
        this.shippingAddressId = shippingAddressId;
        this.items = items;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(Long shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public List<OrderItemInputDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemInputDto> items) {
        this.items = items;
    }
}
