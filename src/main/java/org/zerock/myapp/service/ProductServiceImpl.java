package org.zerock.myapp.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.myapp.domain.*;
import org.zerock.myapp.mapper.ProductMapper;

import java.util.List;

@Log4j2
@RequiredArgsConstructor

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductMapper productMapper;

    private Integer perPage = 8;

    @Override
    public List<StoreKategoriesVO> findKategorieList(){
        return this.productMapper.findKategorieList();
    }

    @Override
    public List<StoreVO> findProductList(Long kategorieId) {
        return this.productMapper.findProductList(kategorieId);
    }

    @Override
    public StoreVO findProduct(Long id) {
        return this.productMapper.findProduct(id);
    }

    @Override
    public StoreVO findProductId(Long adminId, String title) {
        return this.productMapper.selectProductId(adminId, title);
    }

    @Override
    public Integer createProduct(Long adminId, StoreDTO product) {
        log.trace("createProduct({}, {}) invoked.",adminId, product);
        product.setAdminId(adminId);

        return productMapper.insertProduct(product);
    }

    @Override
    public Integer updateProduct(Long adminId, StoreDTO product) {
        product.setAdminId(adminId);

        return productMapper.updateProduct(product);
    }

    @Override
    public Integer writePhotoReview(Long memberId, PhotoReviewDTO review) {
        log.trace("writePhotoReview({}, {}) invoked.", memberId, review);

        review.setMemberId(memberId);
        return this.productMapper.insertPhotoReview(review);
    }

    @Override
    public Integer deleteProduct(Long id) {
        return productMapper.deleteProduct(id);
    }

    @Override
    public List<PhotoReviewVO> findPhotoReviewsByStoreId(Long id) {
        return productMapper.selectPhotoReviewsByStoreId(id);
    }

    @Override
    public PhotoReviewVO findPhotoReview(Long productId, Long reviewId, Long memberId) {
        return productMapper.selectPhotoReview(productId, reviewId, memberId);
    }

    @Override
    public Integer modifyPhotoReview(Long memberId, PhotoReviewDTO review) {
        log.trace("modifyPhotoReview({}, {}) invoked.", memberId, review);

        review.setMemberId(memberId);

        return this.productMapper.updatePhotoReview(review);
    }

    @Override
    public Integer deletePhotoReview(Long reviewId) {
        return this.productMapper.deletePhotoReview(reviewId);
    }

} // end class
