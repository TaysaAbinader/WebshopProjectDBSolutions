package com.webshop.controller;

import com.webshop.dto.ProductInputDto;
import com.webshop.dto.ProductUpdateDto;
import com.webshop.model.Product;
import com.webshop.model.ProductCatalogView;
import com.webshop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductCatalogView>> listProducts(
            @RequestParam(name = "category_id", required = false) Long categoryIdSnake,
            @RequestParam(name = "categoryId", required = false) Long categoryIdCamel,
            @RequestParam(name = "supplier_id", required = false) Long supplierIdSnake,
            @RequestParam(name = "supplierId", required = false) Long supplierIdCamel,
            @RequestParam(name = "search", required = false) String search
    ) {
        Long categoryId = categoryIdSnake != null ? categoryIdSnake : categoryIdCamel;
        Long supplierId = supplierIdSnake != null ? supplierIdSnake : supplierIdCamel;
        return ResponseEntity.ok(productService.getProducts(categoryId, supplierId, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCatalogView> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductCatalogViewById(id));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductInputDto input) {
        Product created = productService.createProduct(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateDto input) {
        Product updated = productService.updateProduct(id, input);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
