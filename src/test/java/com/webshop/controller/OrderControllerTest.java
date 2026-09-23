package com.webshop.controller;

import com.webshop.dto.OrderInputDto;
import com.webshop.dto.OrderItemInputDto;
import com.webshop.exception.InsufficientStockException;
import com.webshop.model.Order;
import com.webshop.model.OrderDetailView;
import com.webshop.service.OrderService;
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
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void shouldReturnOrdersFromView() throws Exception {
        OrderDetailView view = new OrderDetailView();
        view.setOrderId(100L);
        view.setCustomerId(1L);
        view.setCustomerName("Alice Smith");
        view.setOrderStatus("pending");
        view.setTotalAmount(BigDecimal.valueOf(150.00));

        when(orderService.getOrders(any(), any())).thenReturn(List.of(view));

        mockMvc.perform(get("/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].order_id").value(100))
                .andExpect(jsonPath("$[0].customer_name").value("Alice Smith"))
                .andExpect(jsonPath("$[0].total_amount").value(150.00));
    }

    @Test
    void shouldHandleInsufficientStockException() throws Exception {
        when(orderService.createOrder(any())).thenThrow(new InsufficientStockException("Insufficient stock for product"));

        String orderPayload = "{\"customer_id\": 1, \"items\": [{\"product_id\": 10, \"quantity\": 5}]}";

        mockMvc.perform(post("/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_STOCK"));
    }
}
