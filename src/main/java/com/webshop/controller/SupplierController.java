package com.webshop.controller;

import com.webshop.dto.SupplierAddressInputDto;
import com.webshop.dto.SupplierInputDto;
import com.webshop.model.Supplier;
import com.webshop.model.SupplierAddress;
import com.webshop.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/suppliers")
@CrossOrigin(origins = "*")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<List<Supplier>> listSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@Valid @RequestBody SupplierInputDto input) {
        Supplier created = supplierService.createSupplier(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierInputDto input) {
        Supplier updated = supplierService.updateSupplier(id, input);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{supplierId}/addresses")
    public ResponseEntity<List<SupplierAddress>> getAddresses(@PathVariable Long supplierId) {
        return ResponseEntity.ok(supplierService.getSupplierAddresses(supplierId));
    }

    @PostMapping("/{supplierId}/addresses")
    public ResponseEntity<SupplierAddress> addAddress(
            @PathVariable Long supplierId,
            @Valid @RequestBody SupplierAddressInputDto input
    ) {
        SupplierAddress created = supplierService.addSupplierAddress(supplierId, input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{supplierId}/addresses/{addressId}")
    public ResponseEntity<SupplierAddress> getAddressById(
            @PathVariable Long supplierId,
            @PathVariable Long addressId
    ) {
        return ResponseEntity.ok(supplierService.getSupplierAddressById(supplierId, addressId));
    }

    @DeleteMapping("/{supplierId}/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long supplierId,
            @PathVariable Long addressId
    ) {
        supplierService.deleteSupplierAddress(supplierId, addressId);
        return ResponseEntity.noContent().build();
    }
}
