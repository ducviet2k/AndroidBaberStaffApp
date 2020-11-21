package com.example.androidbaberstaffapp.Model;

import androidx.annotation.NonNull;

public class CartItem {

    private String producId;

    private String productName;

    private String productImage;

    private Long productPrice;

    private int productQuantity;

    private String userPhone;



    public String getProducId() {
        return producId;
    }

    public void setProducId( String producId) {
        this.producId = producId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Long productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
