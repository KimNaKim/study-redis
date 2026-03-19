package com.example.redis_sampling.controller;

import com.example.redis_sampling.domain.ProductRepository;
import com.example.redis_sampling.dto.CacheResponse;
import com.example.redis_sampling.dto.ProductDto;
import com.example.redis_sampling.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @GetMapping("/products")
    public String productListPage(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "product-list";
    }

    @GetMapping("/api/products/{id}")
    @ResponseBody
    public CacheResponse<ProductDto> getProductApi(@PathVariable Long id) {
        return productService.getProductWithCaching(id);
    }

    @GetMapping("/api/products/hash/{id}")
    @ResponseBody
    public CacheResponse<ProductDto> getProductHashApi(@PathVariable Long id) {
        return productService.getProductFromHash(id);
    }

    /**
     * [Phase 2] 가격 수정 API
     */
    @PatchMapping("/api/products/{id}/price")
    @ResponseBody
    public ResponseEntity<String> updatePrice(@PathVariable Long id, @RequestParam Long price) {
        productService.updateProductPrice(id, price);
        return ResponseEntity.ok("Price updated");
    }

    /**
     * [Phase 2] 재고 차감 API
     */
    @PostMapping("/api/products/{id}/decrease-stock")
    @ResponseBody
    public ResponseEntity<String> decreaseStock(@PathVariable Long id, @RequestParam Long quantity) {
        productService.decreaseStock(id, quantity);
        return ResponseEntity.ok("Stock decreased");
    }
}
