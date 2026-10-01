package com.webshop.controller;

import com.webshop.dto.AddressInputDto;
import com.webshop.dto.CompanyCustomerInputDto;
import com.webshop.dto.CustomerInputDto;
import com.webshop.dto.CustomerProfileInputDto;
import com.webshop.model.CompanyCustomer;
import com.webshop.model.Customer;
import com.webshop.model.CustomerAddress;
import com.webshop.model.CustomerProfile;
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

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerInputDto input) {
        return ResponseEntity.ok(customerService.updateCustomer(id, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
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

    @GetMapping("/{id}/profile")
    public ResponseEntity<CustomerProfile> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerProfile(id));
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<CustomerProfile> saveProfile(@PathVariable Long id, @RequestBody CustomerProfileInputDto input) {
        return ResponseEntity.ok(customerService.saveCustomerProfile(id, input));
    }

    @DeleteMapping("/{id}/profile")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        customerService.deleteCustomerProfile(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyCustomer>> listCompanyCustomers() {
        return ResponseEntity.ok(customerService.getAllCompanyCustomers());
    }

    @PostMapping("/companies")
    public ResponseEntity<CompanyCustomer> registerCompanyCustomer(@Valid @RequestBody CompanyCustomerInputDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.registerCompanyCustomer(input));
    }
}
