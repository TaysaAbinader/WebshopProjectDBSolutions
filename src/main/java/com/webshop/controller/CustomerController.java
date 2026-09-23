package com.webshop.controller;

import com.webshop.dto.AddressInputDto;
import com.webshop.dto.CustomerInputDto;
import com.webshop.model.Customer;
import com.webshop.model.CustomerAddress;
import com.webshop.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/customers")
@Tag(name = "Customers", description = "Endpoints for managing customer accounts and addresses")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @Operation(summary = "List customers", description = "Retrieve all registered customer accounts")
    public ResponseEntity<List<Customer>> listCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieve a single customer profile")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping
    @Operation(summary = "Register customer", description = "Register a new customer account")
    public ResponseEntity<Customer> registerCustomer(@Valid @RequestBody CustomerInputDto input) {
        Customer created = customerService.registerCustomer(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{customerId}/addresses")
    @Operation(summary = "List customer addresses", description = "Retrieve all shipping addresses for a specific customer")
    public ResponseEntity<List<CustomerAddress>> getAddresses(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerAddresses(customerId));
    }

    @PostMapping("/{customerId}/addresses")
    @Operation(summary = "Add customer address", description = "Add a new shipping address for a customer")
    public ResponseEntity<CustomerAddress> addAddress(
            @PathVariable Long customerId,
            @Valid @RequestBody AddressInputDto input
    ) {
        CustomerAddress created = customerService.addCustomerAddress(customerId, input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{customerId}/addresses/{addressId}")
    @Operation(summary = "Get customer address by ID", description = "Retrieve a specific customer address")
    public ResponseEntity<CustomerAddress> getAddressById(
            @PathVariable Long customerId,
            @PathVariable Long addressId
    ) {
        return ResponseEntity.ok(customerService.getCustomerAddressById(customerId, addressId));
    }

    @DeleteMapping("/{customerId}/addresses/{addressId}")
    @Operation(summary = "Delete customer address", description = "Remove an address from a customer account")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long customerId,
            @PathVariable Long addressId
    ) {
        customerService.deleteCustomerAddress(customerId, addressId);
        return ResponseEntity.noContent().build();
    }
}
