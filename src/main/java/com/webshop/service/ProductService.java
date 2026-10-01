package com.webshop.service;

import com.webshop.dto.ProductInputDto;
import com.webshop.dto.ProductUpdateDto;
import com.webshop.exception.ResourceNotFoundException;
import com.webshop.model.Category;
import com.webshop.model.Product;
import com.webshop.model.ProductCatalogView;
import com.webshop.model.Supplier;
import com.webshop.repository.CategoryRepository;
import com.webshop.repository.ProductCatalogViewRepository;
import com.webshop.repository.ProductRepository;
import com.webshop.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        Category category = categoryRepository.findById(input.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + input.getCategoryId()));
        Supplier supplier = getSupplierOrThrow(input.getSupplierId());

        Product product = new Product();
        product.setName(input.getName());
        product.setDescription(input.getDescription());
        product.setPrice(input.getPrice());
        product.setStockQuantity(input.getStockQuantity());
        category.addProduct(product);
        product.setSupplierId(input.getSupplierId());
        product.addSupplier(supplier);

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
            Category category = categoryRepository.findById(input.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + input.getCategoryId()));
            product.setCategory(category);
        }
        if (input.getSupplierId() != null) {
            Supplier supplier = getSupplierOrThrow(input.getSupplierId());
            product.setSupplierId(input.getSupplierId());
            product.addSupplier(supplier);
        }

        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Supplier> getSuppliersOfProduct(Long productId) {
        return new ArrayList<>(getProductById(productId).getSuppliers());
    }

    public List<Supplier> addSupplierToProduct(Long productId, Long supplierId) {
        Product product = getProductById(productId);
        product.addSupplier(getSupplierOrThrow(supplierId));
        return new ArrayList<>(productRepository.save(product).getSuppliers());
    }

    public void removeSupplierFromProduct(Long productId, Long supplierId) {
        Product product = getProductById(productId);
        product.removeSupplier(getSupplierOrThrow(supplierId));
        productRepository.save(product);
    }

    private Supplier getSupplierOrThrow(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + supplierId));
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}
