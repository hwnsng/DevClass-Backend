package com.hwnsng.devclass.cart.service;

import com.hwnsng.devclass.cart.dto.CartItemResponse;
import com.hwnsng.devclass.cart.entity.CartItem;
import com.hwnsng.devclass.cart.repository.CartItemRepository;
import com.hwnsng.devclass.common.exception.CustomException;
import com.hwnsng.devclass.course.entity.Course;
import com.hwnsng.devclass.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CourseRepository   courseRepository;

    public List<CartItemResponse> getCart(Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .map(item -> {
                    Course course = courseRepository.findById(item.getCourseId())
                            .orElseThrow(() -> new CustomException(
                                    HttpStatus.NOT_FOUND, "NOT_FOUND", "강의를 찾을 수 없습니다."));
                    return new CartItemResponse(item, course);
                })
                .toList();
    }

    @Transactional
    public CartItemResponse addToCart(Long userId, Long courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 강의입니다."));

        if (cartItemRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new CustomException(
                    HttpStatus.CONFLICT, "ALREADY_IN_CART", "이미 장바구니에 담긴 강의입니다.");
        }

        CartItem item = cartItemRepository.save(CartItem.create(userId, courseId));
        Course course = courseRepository.findById(courseId).orElseThrow();
        return new CartItemResponse(item, course);
    }

    @Transactional
    public void removeFromCart(Long cartItemId, Long userId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND, "NOT_FOUND", "장바구니 항목을 찾을 수 없습니다."));

        if (!item.getUserId().equals(userId)) {
            throw new CustomException(
                    HttpStatus.FORBIDDEN, "FORBIDDEN", "본인의 장바구니 항목만 삭제할 수 있습니다.");
        }

        cartItemRepository.delete(item);
    }

    @Transactional
    public void removeItemsByCourseIds(Long userId, List<Long> courseIds) {
        courseIds.forEach(cid -> cartItemRepository.deleteByUserIdAndCourseId(userId, cid));
    }
}
