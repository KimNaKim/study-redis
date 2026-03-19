package com.example.redis_sampling.controller;

import com.example.redis_sampling.domain.Product;
import com.example.redis_sampling.domain.ProductRepository;
import com.example.redis_sampling.dto.CacheResponse;
import com.example.redis_sampling.dto.ProductDto;
import com.example.redis_sampling.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final HealthEndpoint healthEndpoint;

    /**
     * 메인 대시보드 페이지
     */
    @GetMapping("/")
    public String index(Model model) {
        // [Phase 1 통합] Redis 헬스 체크
        Status redisStatus;
        try {
            HealthComponent health = healthEndpoint.healthForPath("redis");
            redisStatus = (health != null) ? health.getStatus() : Status.UNKNOWN;
        } catch (Exception e) {
            redisStatus = Status.DOWN;
        }
        model.addAttribute("redisStatus", redisStatus.getCode());
        model.addAttribute("isRedisUp", Status.UP.equals(redisStatus));

        // [Phase 3] 유니크 방문자 집계
        productService.trackVisitor("guest");
        
        List<Product> products = productRepository.findAll();
        List<ProductDto> productDtos = products.stream()
                .map(p -> {
                    var likeInfo = productService.getLikeInfo(p.getId(), "guest");
                    return new ProductDto(p).updateLikeInfo((Boolean) likeInfo.get("isLiked"), (Long) likeInfo.get("likeCount"));
                })
                .collect(Collectors.toList());

        model.addAttribute("products", productDtos);
        
        // [Phase 3] 최근 본 상품 및 방문자 수 추가
        model.addAttribute("recentProducts", productService.getRecentProducts());
        model.addAttribute("visitorCount", productService.getTodayVisitorCount());

        // [Phase 4] 실시간 인기 랭킹 추가
        model.addAttribute("ranking", productService.getTopRankedProducts(5));

        return "product-list";
    }

    /**
     * [Phase 4] 실시간 인기 랭킹 API (JSON)
     */
    @GetMapping("/api/products/ranking")
    @ResponseBody
    public List<Map<String, Object>> getRankingApi() {
        return productService.getTopRankedProducts(5);
    }

    /**
     * [Phase 3] 최근 본 상품 목록 API (JSON)
     */
    @GetMapping("/api/products/recent")
    @ResponseBody
    public List<ProductDto> getRecentProductsApi() {
        return productService.getRecentProducts();
    }

    /**
     * [Phase 3] 좋아요 토글 API
     */
    @PostMapping("/api/products/{id}/like")
    @ResponseBody
    public ResponseEntity<Void> toggleLike(@PathVariable Long id) {
        productService.toggleLike(id, "guest");
        return ResponseEntity.ok().build();
    }

    /**
     * [Phase 2] Hashes 기반 상세 조회 API
     */
    @GetMapping("/api/products/hash/{id}")
    @ResponseBody
    public CacheResponse<ProductDto> getProductHashApi(@PathVariable Long id) {
        return productService.getProductFromHash(id);
    }

    /**
     * [Phase 1] Strings 기반 상세 조회 API
     */
    @GetMapping("/api/products/{id}")
    @ResponseBody
    public CacheResponse<ProductDto> getProductApi(@PathVariable Long id) {
        return productService.getProductWithCaching(id);
    }

    /**
     * [Phase 2] 가격 부분 업데이트 API
     */
    @PatchMapping("/api/products/{id}/price")
    @ResponseBody
    public ResponseEntity<String> updatePrice(@PathVariable Long id, @RequestParam Long price) {
        productService.updateProductPrice(id, price);
        return ResponseEntity.ok("Price updated");
    }

    /**
     * [Phase 2] 원자적 재고 차감 API
     */
    @PostMapping("/api/products/{id}/decrease-stock")
    @ResponseBody
    public ResponseEntity<String> decreaseStock(@PathVariable Long id, @RequestParam Long quantity) {
        productService.decreaseStock(id, quantity);
        return ResponseEntity.ok("Stock decreased");
    }

    /**
     * [Phase 2] 캐시 수동 삭제 API
     */
    @DeleteMapping("/api/products/{id}/cache")
    @ResponseBody
    public ResponseEntity<String> clearCache(@PathVariable Long id, @RequestParam String type) {
        productService.clearCache(id, type);
        return ResponseEntity.ok("Cache cleared");
    }

    /**
     * [Phase 2] 성능 벤치마크 테스트 API
     */
    @GetMapping("/api/benchmark/{id}")
    @ResponseBody
    public Map<String, Object> runBenchmark(@PathVariable Long id) {
        return productService.runBenchmark(id);
    }

    @GetMapping("/products")
    public String productListPage(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "product-list";
    }
}
