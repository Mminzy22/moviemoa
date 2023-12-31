package org.zerock.myapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.myapp.domain.PhotoReviewDTO;
import org.zerock.myapp.domain.PhotoReviewVO;
import org.zerock.myapp.domain.StoreKategoriesVO;
import org.zerock.myapp.domain.StoreVO;
import org.zerock.myapp.service.ProductService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor

@RequestMapping("/store/*")
@Controller
public class StoreController {

    private final ProductService productService;

// "kategorie" 요청 매개변수를 통해 선택된 카테고리에 해당하는 제품 목록을 가져옴
// 선택된 카테고리가 없으면 기본값으로 "1"을 사용함
    @GetMapping("/tickets")
    public String storeTicketsView(@RequestParam(value="kategorie", required = false, defaultValue="1")Long kategorieId, Model model) {
        log.trace("storeTicketsView({}) invoked.", kategorieId);

        // 카테고리 목록과 선택된 카테고리에 해당하는 제품 목록을 조회
        List<StoreKategoriesVO> kategorieList = this.productService.findKategorieList();
        List<StoreVO> productList = this.productService.findProductList(kategorieId);

        model.addAttribute("kategorieList", kategorieList);
        model.addAttribute("productList", productList);
        model.addAttribute("kategorie", kategorieId);

        return "/store/tickets";
    } // storeTicketsView

// "id" 경로 변수를 통해 조회할 제품의 ID를 받음
    @GetMapping("/detailProduct/{id}")
    public String detailProductView(@PathVariable(value = "id") Long id, Model model) {
        log.trace("detailProductView({}) invoked.", id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.isAuthenticated() &&
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            String username = authentication.getName();
            Long memberId = Long.valueOf(username);
            model.addAttribute("currentUserId", memberId);
        }

        List<StoreKategoriesVO> kategorieList = this.productService.findKategorieList();

        // 제품 ID에 해당하는 제품의 상세 정보를 조회
        StoreVO product = this.productService.findProduct(id);
        List<PhotoReviewVO> reviews = this.productService.findPhotoReviewsByStoreId(id);

        model.addAttribute("kategorieList", kategorieList);
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);

        return "/store/detailProduct";
    } //detailProductView

//    @DeleteMapping("/product")
//    public String detailProductDelete() {
//        log.trace("detailProductDelete() invoked.");
//
//        return "redirect:/store/tickets";
//    } // detailProductDelete

    @GetMapping("/photoReview")
    public String photoReplyView(Long id, Model model){
        log.trace("photoReplyView({}) invoked.", id);

        model.addAttribute("productId", id);
        return "/store/photoReply";
    } // photoReplyView

    @PostMapping("/photoReview")
    public String photoReplyWrite(PhotoReviewDTO review){
        log.trace("photoReplyWrite({}) invoked.", review);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long memberId = Long.valueOf(username);

        this.productService.writePhotoReview(memberId, review);

        return "redirect:/store/detailProduct/"+review.getProductId();
    } // photoReplyWrite

    @GetMapping("/modify/photoReview")
    public String photoReplyUpdateView(@RequestParam("id") Long productId,
                                       @RequestParam("rid") Long reviewId, Model model){
        log.trace("photoReplyUpdateView({}, {}) invoked.", productId, reviewId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long memberId = Long.valueOf(username);

        PhotoReviewVO photoReview = this.productService.findPhotoReview(productId, reviewId, memberId);

        model.addAttribute("review", photoReview);

        return "/store/updatePhotoReply";
    }

    @PutMapping("/photoReview")
    public String photoReplyUpdate(PhotoReviewDTO review){
        log.trace("photoReplyUpdate({}) invoked.", review);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long memberId = Long.valueOf(username);

        this.productService.modifyPhotoReview(memberId, review);

        return "redirect:/store/detailProduct/"+review.getProductId();
    } // photoReplyUpdate

    @DeleteMapping("/photoReview/{productId}/{reviewId}")
    public ResponseEntity<String> photoReplyDelete(@PathVariable Long productId, @PathVariable Long reviewId) {
        log.trace("photoReplyDelete({}, {}) invoked.", productId, reviewId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            Integer affectedRows = this.productService.deletePhotoReview(reviewId);
            String responseJson;
            if (affectedRows == 1) {
                responseJson = "{\"result\": 1}";
            } else {
                responseJson = "{\"result\": 0}";
            }
            return ResponseEntity.ok(responseJson);
        }

        return ResponseEntity.status(401).build();
    } // photoReplyDelete

} // end class
