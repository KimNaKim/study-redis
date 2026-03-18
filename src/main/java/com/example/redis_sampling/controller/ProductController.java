package com.example.redis_sampling.controller;

import com.example.redis_sampling.domain.Product;
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
        // 샘플 데이터가 없으면 자동 생성
        if (productRepository.count() == 0) {
            productRepository.save(new Product("Redis Machine", 150000.0, "High Performance Caching"));
            productRepository.save(new Product("Spring Book", 35000.0, "Mastering Spring Boot 3"));
            productRepository.save(new Product("Gaming Mouse", 89000.0, "Ultra fast response"));
        }
        model.addAttribute("products", productRepository.findAll());
        return "product-list";
    }

    @GetMapping("/api/products/{id}")
    @ResponseBody
    public CacheResponse<ProductDto> getProductApi(@PathVariable Long id) {
        return productService.getProductWithCaching(id);
    }
}
