package com.webshop.repository;

import com.webshop.model.SupplierAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierAddressRepository extends JpaRepository<SupplierAddress, Long> {
    List<SupplierAddress> findBySupplierId(Long supplierId);
}
