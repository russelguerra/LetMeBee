package com.guerra.russel.letmebee.Collection;

public class OtherProducts {
    private String name;
    private int stock;
    private double price;
    private String id;
    private int order;

    public OtherProducts() {
    }

    public OtherProducts(String name, int stock, double price, String id, int order) {
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.id = id;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

    public double getPrice() {
        return price;
    }

    public String getId() {
        return id;
    }

    public int getOrder() {
        return order;
    }
}
