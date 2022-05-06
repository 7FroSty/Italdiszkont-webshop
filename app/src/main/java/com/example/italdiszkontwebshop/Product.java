package com.example.italdiszkontwebshop;

public class Product {
    private String id;
    private String name;
    private String info;
    private float alcoholContent;
    private int unitPrice;
    private int price;
    private int image;
    private int cartedCount;

    public Product() {}

    public Product(String name, String info, float alcoholContent, int unitPrice, int price, int image, int cartedCount) {
        this.name = name;
        this.info = info;
        this.alcoholContent = alcoholContent;
        this.unitPrice = unitPrice;
        this.price = price;
        this.image = image;
        this.cartedCount = cartedCount;
    }

    public String getName() {
        return name;
    }
    public String getInfo() {
        return info;
    }
    public float getAlcoholContent() {
        return alcoholContent;
    }
    public int getUnitPrice() {
        return unitPrice;
    }
    public int getPrice() {
        return price;
    }
    public int getImage() {
        return image;
    }
    public int getCartedCount() {
        return cartedCount;
    }

    public String _getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
