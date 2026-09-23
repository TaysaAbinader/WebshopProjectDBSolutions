package com.webshop.service;

import com.webshop.dto.OrderInputDto;
import com.webshop.dto.OrderItemInputDto;
import com.webshop.dto.OrderStatusUpdateDto;
import com.webshop.exception.InsufficientStockException;
import com.webshop.exception.ResourceNotFoundException;
import com.webshop.model.CustomerAddress;
import com.webshop.model.Order;
import com.webshop.model.OrderDetailView;
import com.webshop.model.OrderItem;
import com.webshop.model.Product;
import com.webshop.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderDetailViewRepository orderDetailViewRepository;
    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository customerAddressRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            OrderDetailViewRepository orderDetailViewRepository,
            CustomerRepository customerRepository,
            CustomerAddressRepository customerAddressRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderDetailViewRepository = orderDetailViewRepository;
        this.customerRepository = customerRepository;
        this.customerAddressRepository = customerAddressRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderDetailView> getOrders(Long customerId, String status) {
        return orderDetailViewRepository.searchOrders(customerId, status);
    }

    @Transactional(readOnly = true)
    public OrderDetailView getOrderDetailById(Long id) {
        return orderDetailViewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Order getOrderEntityById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        order.setItems(items);

        BigDecimal total = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        return order;
    }

    public Order createOrder(OrderInputDto input) {
        if (!customerRepository.existsById(input.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found with ID: " + input.getCustomerId());
        }

        if (input.getShippingAddressId() != null) {
            CustomerAddress address = customerAddressRepository.findById(input.getShippingAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + input.getShippingAddressId()));
            if (!address.getCustomerId().equals(input.getCustomerId())) {
                throw new IllegalArgumentException("Shipping address does not belong to the customer");
            }
        }

        // Validate stock for all items
        for (OrderItemInputDto itemDto : input.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemDto.getProductId()));
            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName() + 
                        " (requested: " + itemDto.getQuantity() + ", available: " + product.getStockQuantity() + ")");
            }
        }

        // Save order header
        Order order = new Order();
        order.setCustomerId(input.getCustomerId());
        order.setShippingAddressId(input.getShippingAddressId());
        order.setStatus("pending");
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> savedItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemInputDto itemDto : input.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId()).get();

            OrderItem item = new OrderItem();
            item.setOrderId(savedOrder.getId());
            item.setProductId(product.getId());
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(product.getPrice());

            OrderItem savedItem = orderItemRepository.save(item);
            savedItems.add(savedItem);

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }

        savedOrder.setItems(savedItems);
        savedOrder.setTotalAmount(totalAmount);
        return savedOrder;
    }

    public Order updateOrderStatus(Long id, OrderStatusUpdateDto input) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        order.setStatus(input.getStatus());
        Order updated = orderRepository.save(order);
        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        updated.setItems(items);

        BigDecimal total = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        updated.setTotalAmount(total);

        return updated;
    }
}
