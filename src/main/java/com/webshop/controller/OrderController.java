package com.webshop.controller;

import com.webshop.dto.OrderInputDto;
import com.webshop.dto.OrderStatusUpdateDto;
import com.webshop.model.Order;
import com.webshop.model.OrderDetailView;
import com.webshop.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDetailView>> listOrders(
            @RequestParam(name = "customer_id", required = false) Long customerIdSnake,
            @RequestParam(name = "customerId", required = false) Long customerIdCamel,
            @RequestParam(name = "status", required = false) String status
    ) {
        Long customerId = customerIdSnake != null ? customerIdSnake : customerIdCamel;
        return ResponseEntity.ok(orderService.getOrders(customerId, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailView> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderDetailById(id));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderInputDto input) {
        Order created = orderService.createOrder(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @RequestMapping(value = "/{id}/status", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateDto input
    ) {
        Order updated = orderService.updateOrderStatus(id, input);
        return ResponseEntity.ok(updated);
    }
}
