package com.webshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OrderStatusUpdateDto {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(pending|paid|shipped|delivered|cancelled)$", message = "Invalid status value")
    private String status;

    public OrderStatusUpdateDto() {
    }

    public OrderStatusUpdateDto(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
