package com.webshop.service;

import com.webshop.dto.CategoryInputDto;
import com.webshop.exception.ResourceNotFoundException;
import com.webshop.dto.CategoryProductInputDto;
import com.webshop.model.Category;
import com.webshop.model.Product;
import com.webshop.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    public Category createCategory(CategoryInputDto input) {
        Category category = new Category();
        category.setName(input.getName());
        category.setDescription(input.getDescription());
        if (input.getProducts() != null) {
            for (CategoryProductInputDto p : input.getProducts()) {
                Product product = new Product();
                product.setName(p.getName());
                product.setDescription(p.getDescription());
                product.setPrice(p.getPrice());
                product.setStockQuantity(p.getStockQuantity());
                product.setSupplierId(p.getSupplierId());
                category.addProduct(product);
            }
        }
        // CascadeType.ALL: the products above are persisted together with the category
        return categoryRepository.save(category);
    }

    /** Touching getProducts() inside the transaction triggers the LAZY load (extra SELECT). */
    @Transactional(readOnly = true)
    public List<Product> getProductsOfCategory(Long id) {
        Category category = getCategoryById(id);
        return new ArrayList<>(category.getProducts());
    }

    public Category updateCategory(Long id, CategoryInputDto input) {
        Category category = getCategoryById(id);
        if (input.getName() != null) {
            category.setName(input.getName());
        }
        if (input.getDescription() != null) {
            category.setDescription(input.getDescription());
        }
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
