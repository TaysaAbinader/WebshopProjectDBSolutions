package com.webshop.model;

import com.webshop.repository.ProductRepository;
import com.webshop.repository.SupplierRepository;
import com.webshop.service.SupplierService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/** N:M between Product and Supplier through the ProductSuppliers join table. */
@DataJpaTest
@Import(SupplierService.class)
class ProductSupplierAssociationTest {

    @Autowired ProductRepository productRepository;
    @Autowired SupplierRepository supplierRepository;
    @Autowired SupplierService supplierService;
    @Autowired EntityManager em;

    private Category category;
    private Supplier acme;
    private Supplier globex;
    private Product laptop;
    private Product phone;

    @BeforeEach
    void setUp() {
        category = new Category(null, "Electronics", null);
        em.persist(category);
        acme = supplierRepository.save(new Supplier(null, "Acme", null, null, null));
        globex = supplierRepository.save(new Supplier(null, "Globex", null, null, null));
        laptop = product("Laptop", acme);
        phone = product("Phone", acme);
        laptop.addSupplier(acme);
        laptop.addSupplier(globex);   // a product with two suppliers
        phone.addSupplier(acme);      // a supplier with two products (acme)
        productRepository.save(laptop);
        productRepository.save(phone);
        em.flush();
        em.clear();
    }

    private Product product(String name, Supplier main) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(new BigDecimal("100.00"));
        p.setStockQuantity(3);
        p.setSupplierId(main.getId());
        category.addProduct(p);
        return p;
    }

    @Test
    void productHasManySuppliers() {
        Product loaded = productRepository.findById(laptop.getId()).orElseThrow();
        assertEquals(2, loaded.getSuppliers().size());
    }

    @Test
    void retrieveAllProductsForASupplier() {
        assertEquals(2, productRepository.findBySuppliers_Id(acme.getId()).size());
        assertEquals(1, productRepository.findBySuppliers_Id(globex.getId()).size());
        assertEquals(2, supplierService.getProductsOfSupplier(acme.getId()).size());
    }

    @Test
    void removingASupplierFromAProductKeepsBothEntities() {
        Product loaded = productRepository.findById(laptop.getId()).orElseThrow();
        loaded.removeSupplier(supplierRepository.findById(globex.getId()).orElseThrow());
        productRepository.flush();
        em.clear();
        assertEquals(1, productRepository.findById(laptop.getId()).orElseThrow().getSuppliers().size());
        assertTrue(supplierRepository.existsById(globex.getId()));
    }

    @Test
    void deletingProductRemovesJoinRowsOnly() {
        productRepository.deleteById(laptop.getId());
        productRepository.flush();
        Long links = em.createQuery("select count(s) from Product p join p.suppliers s", Long.class).getSingleResult();
        assertEquals(1L, links);
        assertTrue(supplierRepository.existsById(acme.getId()));
    }

    @Test
    void deletingSupplierDetachesItFromProducts() {
        supplierService.deleteSupplier(globex.getId());
        em.flush();
        em.clear();
        assertFalse(supplierRepository.existsById(globex.getId()));
        assertEquals(1, productRepository.findById(laptop.getId()).orElseThrow().getSuppliers().size());
    }
}
