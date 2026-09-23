package com.webshop.service;

import com.webshop.dto.SupplierAddressInputDto;
import com.webshop.dto.SupplierInputDto;
import com.webshop.exception.ResourceNotFoundException;
import com.webshop.model.Supplier;
import com.webshop.model.SupplierAddress;
import com.webshop.repository.SupplierAddressRepository;
import com.webshop.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierAddressRepository supplierAddressRepository;

    public SupplierService(SupplierRepository supplierRepository, SupplierAddressRepository supplierAddressRepository) {
        this.supplierRepository = supplierRepository;
        this.supplierAddressRepository = supplierAddressRepository;
    }

    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));
    }

    public Supplier createSupplier(SupplierInputDto input) {
        Supplier supplier = new Supplier();
        supplier.setName(input.getName());
        supplier.setContactName(input.getContactName());
        supplier.setEmail(input.getEmail());
        supplier.setPhone(input.getPhone());
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(Long id, SupplierInputDto input) {
        Supplier supplier = getSupplierById(id);
        if (input.getName() != null) {
            supplier.setName(input.getName());
        }
        if (input.getContactName() != null) {
            supplier.setContactName(input.getContactName());
        }
        if (input.getEmail() != null) {
            supplier.setEmail(input.getEmail());
        }
        if (input.getPhone() != null) {
            supplier.setPhone(input.getPhone());
        }
        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(Long id) {
        Supplier supplier = getSupplierById(id);
        supplierRepository.delete(supplier);
    }

    @Transactional(readOnly = true)
    public List<SupplierAddress> getSupplierAddresses(Long supplierId) {
        // verify supplier exists
        getSupplierById(supplierId);
        return supplierAddressRepository.findBySupplierId(supplierId);
    }

    public SupplierAddress addSupplierAddress(Long supplierId, SupplierAddressInputDto input) {
        getSupplierById(supplierId);
        SupplierAddress address = new SupplierAddress();
        address.setSupplierId(supplierId);
        address.setStreetAddress(input.getStreetAddress());
        address.setCity(input.getCity());
        address.setState(input.getState());
        address.setPostalCode(input.getPostalCode());
        address.setCountry(input.getCountry());
        return supplierAddressRepository.save(address);
    }

    @Transactional(readOnly = true)
    public SupplierAddress getSupplierAddressById(Long supplierId, Long addressId) {
        getSupplierById(supplierId);
        return supplierAddressRepository.findById(addressId)
                .filter(addr -> addr.getSupplierId().equals(supplierId))
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId + " for supplier " + supplierId));
    }

    public void deleteSupplierAddress(Long supplierId, Long addressId) {
        SupplierAddress address = getSupplierAddressById(supplierId, addressId);
        supplierAddressRepository.delete(address);
    }
}
