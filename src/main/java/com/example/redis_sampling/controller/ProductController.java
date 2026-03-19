package com.example.redis_sampling.controller;

import com.example.redis_sampling.domain.ProductRepository;
import com.example.redis_sampling.dto.CacheResponse;
import com.example.redis_sampling.dto.ProductDto;
import com.example.redis_sampling.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

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

    /**
     * [Phase 1] Strings 기반 API
     */
    @GetMapping("/api/products/{id}")
    @ResponseBody
    public CacheResponse<ProductDto> getProductApi(@PathVariable Long id) {
        return productService.getProductWithCaching(id);
    }

    /**
     * [Phase 2] Hashes 기반 API 추가
     */
    @GetMapping("/api/products/hash/{id}")
    @ResponseBody
    public CacheResponse<ProductDto> getProductHashApi(@PathVariable Long id) {
        return productService.getProductFromHash(id);
    }
}
