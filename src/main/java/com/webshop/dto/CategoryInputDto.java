package com.webshop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

public class CategoryInputDto {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    @Valid
    private List<CategoryProductInputDto> products = new ArrayList<>();

    public CategoryInputDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CategoryProductInputDto> getProducts() {
        return products;
    }

    public void setProducts(List<CategoryProductInputDto> products) {
        this.products = products;
    }
}
