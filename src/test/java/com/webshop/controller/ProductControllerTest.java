package com.webshop.controller;

import com.webshop.dto.ProductInputDto;
import com.webshop.model.Product;
import com.webshop.model.ProductCatalogView;
import com.webshop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void shouldReturnProductsCatalogList() throws Exception {
        ProductCatalogView view = new ProductCatalogView();
        view.setProductId(1L);
        view.setProductName("Noise-Canceling Headphones");
        view.setPrice(BigDecimal.valueOf(199.99));
        view.setStockQuantity(20);
        view.setCategoryName("Audio");

        when(productService.getProducts(any(), any(), any())).thenReturn(List.of(view));

        mockMvc.perform(get("/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_name").value("Noise-Canceling Headphones"))
                .andExpect(jsonPath("$[0].price").value(199.99));
    }

    @Test
    void shouldValidateProductCreationPayload() throws Exception {
        // Missing name and price should return 400
        mockMvc.perform(post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stock_quantity\": 10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PARAMETER"));
    }
}
