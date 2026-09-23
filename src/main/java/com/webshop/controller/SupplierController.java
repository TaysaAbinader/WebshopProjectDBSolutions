package com.webshop.controller;

import com.webshop.dto.SupplierAddressInputDto;
import com.webshop.dto.SupplierInputDto;
import com.webshop.model.Supplier;
import com.webshop.model.SupplierAddress;
import com.webshop.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/suppliers")
@Tag(name = "Suppliers", description = "Endpoints for managing product suppliers and vendor profiles")
@CrossOrigin(origins = "*")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    @Operation(summary = "List suppliers", description = "Retrieve all registered supplier organizations")
    public ResponseEntity<List<Supplier>> listSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID", description = "Retrieve a supplier profile by its unique ID")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @PostMapping
    @Operation(summary = "Create supplier", description = "Add a new vendor or supplier to the system")
    public ResponseEntity<Supplier> createSupplier(@Valid @RequestBody SupplierInputDto input) {
        Supplier created = supplierService.createSupplier(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update supplier", description = "Update an existing supplier profile")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierInputDto input) {
        Supplier updated = supplierService.updateSupplier(id, input);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete supplier", description = "Remove a supplier profile")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{supplierId}/addresses")
    @Operation(summary = "List supplier addresses", description = "Retrieve operational addresses for a supplier")
    public ResponseEntity<List<SupplierAddress>> getAddresses(@PathVariable Long supplierId) {
        return ResponseEntity.ok(supplierService.getSupplierAddresses(supplierId));
    }

    @PostMapping("/{supplierId}/addresses")
    @Operation(summary = "Add supplier address", description = "Add a new address record for a supplier")
    public ResponseEntity<SupplierAddress> addAddress(
            @PathVariable Long supplierId,
            @Valid @RequestBody SupplierAddressInputDto input
    ) {
        SupplierAddress created = supplierService.addSupplierAddress(supplierId, input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{supplierId}/addresses/{addressId}")
    @Operation(summary = "Get supplier address by ID", description = "Retrieve a specific supplier address")
    public ResponseEntity<SupplierAddress> getAddressById(
            @PathVariable Long supplierId,
            @PathVariable Long addressId
    ) {
        return ResponseEntity.ok(supplierService.getSupplierAddressById(supplierId, addressId));
    }

    @DeleteMapping("/{supplierId}/addresses/{addressId}")
    @Operation(summary = "Delete supplier address", description = "Remove an address record from a supplier profile")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long supplierId,
            @PathVariable Long addressId
    ) {
        supplierService.deleteSupplierAddress(supplierId, addressId);
        return ResponseEntity.noContent().build();
    }
}
