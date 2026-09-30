package com.webshop.model;

import com.webshop.repository.CategoryRepository;
import com.webshop.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/** Demonstrates the Category 1:M Product mapping, cascade and LAZY loading. */
@DataJpaTest
class CategoryProductAssociationTest {

    @Autowired CategoryRepository categoryRepository;
    @Autowired ProductRepository productRepository;
    @Autowired EntityManager em;

    private Category saveCategoryWithTwoProducts() {
        Category category = new Category(null, "Books", "All books");
        category.addProduct(product("Novel"));
        category.addProduct(product("Cookbook"));
        Category saved = categoryRepository.saveAndFlush(category);   // cascade PERSIST
        em.clear();
        return saved;
    }

    private Product product(String name) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(new BigDecimal("9.90"));
        p.setStockQuantity(5);
        p.setSupplierId(1L);
        return p;
    }

    @Test
    void savingCategoryCascadesToProducts() {
        Category saved = saveCategoryWithTwoProducts();
        assertEquals(2, productRepository.findByCategoryId(saved.getId()).size());
    }

    @Test
    void deletingCategoryCascadesToProducts() {
        Category saved = saveCategoryWithTwoProducts();
        categoryRepository.delete(categoryRepository.findById(saved.getId()).orElseThrow());  // cascade REMOVE
        categoryRepository.flush();
        assertEquals(0, productRepository.findByCategoryId(saved.getId()).size());
    }

    @Test
    void productsAreLoadedLazily() {
        Category saved = saveCategoryWithTwoProducts();
        Category loaded = categoryRepository.findById(saved.getId()).orElseThrow();
        assertFalse(Hibernate.isInitialized(loaded.getProducts()), "no products query yet");
        assertEquals(2, loaded.getProducts().size());                 // triggers the second SELECT
        assertTrue(Hibernate.isInitialized(loaded.getProducts()));
    }
}
