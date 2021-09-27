package com.example.a02;

import java.io.Serializable;

public class StoreData implements Serializable {
    private String StoreKinds;
    private String StoreName;
    private String StoreAddress;
    private String StoreNo;
    private String StoreViews;
    private String StoreRating;
    public String getStoreKinds() {
        return StoreKinds;
    }
    public void setStoreKinds(String storeKinds) {
        StoreKinds = storeKinds;
    }
    public String getStoreName() {
        return StoreName;
    }
    public void setStoreName(String storeName) {
        StoreName = storeName;
    }
    public String getStoreAddress() {
        return StoreAddress;
    }
    public void setStoreAddress(String storeAddress) {
        StoreAddress = storeAddress;
    }
    public String getStoreNo() {
        return StoreNo;
    }
    public void setStoreNo(String storeNo) {
        StoreNo = storeNo;
    }
    public String getStoreViews() {
        return StoreViews;
    }
    public void setStoreViews(String storeViews) {
        StoreViews = storeViews;
    }
    public String getStoreRating() {
        return StoreRating;
    }
    public void setStoreRating(String storeRating) {
        StoreRating = storeRating;
    }
}
