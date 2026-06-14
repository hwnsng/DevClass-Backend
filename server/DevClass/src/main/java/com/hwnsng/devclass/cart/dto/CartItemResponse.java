package com.hwnsng.devclass.cart.dto;

import com.hwnsng.devclass.cart.entity.CartItem;
import com.hwnsng.devclass.course.entity.Course;
import lombok.Getter;

@Getter
public class CartItemResponse {
    private final Long cartItemId;
    private final Long courseId;
    private final String title;
    private final int price;
    private final String thumbnailUrl;

    public CartItemResponse(CartItem item, Course course) {
        this.cartItemId   = item.getId();
        this.courseId     = course.getId();
        this.title        = course.getTitle();
        this.price        = course.getPrice();
        this.thumbnailUrl = course.getThumbnailUrl() != null
                ? "http://localhost:8080/uploads/" + course.getThumbnailUrl()
                : null;
    }
}
