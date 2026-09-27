package com.webshop.controller;

import com.webshop.dto.AddressInputDto;
import com.webshop.dto.CustomerInputDto;
import com.webshop.model.Customer;
import com.webshop.model.CustomerAddress;
import com.webshop.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> listCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<Customer> registerCustomer(@Valid @RequestBody CustomerInputDto input) {
        Customer created = customerService.registerCustomer(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{customerId}/addresses")
    public ResponseEntity<List<CustomerAddress>> getAddresses(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerAddresses(customerId));
    }

    @PostMapping("/{customerId}/addresses")
    public ResponseEntity<CustomerAddress> addAddress(
            @PathVariable Long customerId,
            @Valid @RequestBody AddressInputDto input
    ) {
        CustomerAddress created = customerService.addCustomerAddress(customerId, input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{customerId}/addresses/{addressId}")
    public ResponseEntity<CustomerAddress> getAddressById(
            @PathVariable Long customerId,
            @PathVariable Long addressId
    ) {
        return ResponseEntity.ok(customerService.getCustomerAddressById(customerId, addressId));
    }

    @DeleteMapping("/{customerId}/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long customerId,
            @PathVariable Long addressId
    ) {
        customerService.deleteCustomerAddress(customerId, addressId);
        return ResponseEntity.noContent().build();
    }
}
