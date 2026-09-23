package com.webshop.controller;

import com.webshop.dto.ProductInputDto;
import com.webshop.dto.ProductUpdateDto;
import com.webshop.model.Product;
import com.webshop.model.ProductCatalogView;
import com.webshop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
@Tag(name = "Products", description = "Endpoints for managing and querying products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "List products", description = "Retrieve products with optional filtering by category, supplier, or search keyword via v_product_catalog view")
    public ResponseEntity<List<ProductCatalogView>> listProducts(
            @RequestParam(name = "category_id", required = false) Long categoryId,
            @RequestParam(name = "supplier_id", required = false) Long supplierId,
            @RequestParam(name = "search", required = false) String search
    ) {
        return ResponseEntity.ok(productService.getProducts(categoryId, supplierId, search));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a single product view by ID")
    public ResponseEntity<ProductCatalogView> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductCatalogViewById(id));
    }

    @PostMapping
    @Operation(summary = "Create product", description = "Create a new catalog product")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductInputDto input) {
        Product created = productService.createProduct(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing catalog product")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateDto input) {
        Product updated = productService.updateProduct(id, input);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product from the catalog")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
