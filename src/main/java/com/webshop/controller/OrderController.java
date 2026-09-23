package com.webshop.controller;

import com.webshop.dto.OrderInputDto;
import com.webshop.dto.OrderStatusUpdateDto;
import com.webshop.model.Order;
import com.webshop.model.OrderDetailView;
import com.webshop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/orders")
@Tag(name = "Orders", description = "Endpoints for managing purchase orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "List orders", description = "Retrieve orders list via v_order_details view with optional filters")
    public ResponseEntity<List<OrderDetailView>> listOrders(
            @RequestParam(name = "customer_id", required = false) Long customerId,
            @RequestParam(name = "status", required = false) String status
    ) {
        return ResponseEntity.ok(orderService.getOrders(customerId, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details", description = "Retrieve order details from v_order_details view")
    public ResponseEntity<OrderDetailView> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderDetailById(id));
    }

    @PostMapping
    @Operation(summary = "Create order", description = "Submit a new purchase order with items")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderInputDto input) {
        Order created = orderService.createOrder(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Change the lifecycle status of an existing order")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateDto input
    ) {
        Order updated = orderService.updateOrderStatus(id, input);
        return ResponseEntity.ok(updated);
    }
}
