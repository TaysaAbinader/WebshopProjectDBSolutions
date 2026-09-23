package com.webshop.service;

import com.webshop.dto.ProductInputDto;
import com.webshop.dto.ProductUpdateDto;
import com.webshop.exception.ResourceNotFoundException;
import com.webshop.model.Product;
import com.webshop.model.ProductCatalogView;
import com.webshop.repository.CategoryRepository;
import com.webshop.repository.ProductCatalogViewRepository;
import com.webshop.repository.ProductRepository;
import com.webshop.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCatalogViewRepository productCatalogViewRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public ProductService(
            ProductRepository productRepository,
            ProductCatalogViewRepository productCatalogViewRepository,
            CategoryRepository categoryRepository,
            SupplierRepository supplierRepository
    ) {
        this.productRepository = productRepository;
        this.productCatalogViewRepository = productCatalogViewRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductCatalogView> getProducts(Long categoryId, Long supplierId, String search) {
        return productCatalogViewRepository.searchCatalog(categoryId, supplierId, search);
    }

    @Transactional(readOnly = true)
    public ProductCatalogView getProductCatalogViewById(Long id) {
        return productCatalogViewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    public Product createProduct(ProductInputDto input) {
        if (!categoryRepository.existsById(input.getCategoryId())) {
            throw new ResourceNotFoundException("Category not found with ID: " + input.getCategoryId());
        }
        if (!supplierRepository.existsById(input.getSupplierId())) {
            throw new ResourceNotFoundException("Supplier not found with ID: " + input.getSupplierId());
        }

        Product product = new Product();
        product.setName(input.getName());
        product.setDescription(input.getDescription());
        product.setPrice(input.getPrice());
        product.setStockQuantity(input.getStockQuantity());
        product.setCategoryId(input.getCategoryId());
        product.setSupplierId(input.getSupplierId());

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductUpdateDto input) {
        Product product = getProductById(id);

        if (input.getName() != null) {
            product.setName(input.getName());
        }
        if (input.getDescription() != null) {
            product.setDescription(input.getDescription());
        }
        if (input.getPrice() != null) {
            product.setPrice(input.getPrice());
        }
        if (input.getStockQuantity() != null) {
            product.setStockQuantity(input.getStockQuantity());
        }
        if (input.getCategoryId() != null) {
            if (!categoryRepository.existsById(input.getCategoryId())) {
                throw new ResourceNotFoundException("Category not found with ID: " + input.getCategoryId());
            }
            product.setCategoryId(input.getCategoryId());
        }
        if (input.getSupplierId() != null) {
            if (!supplierRepository.existsById(input.getSupplierId())) {
                throw new ResourceNotFoundException("Supplier not found with ID: " + input.getSupplierId());
            }
            product.setSupplierId(input.getSupplierId());
        }

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}
