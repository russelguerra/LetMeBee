package com.guerra.russel.letmebee.Collection;

public class Sizes {

    private String size;
    private int stock;
    private double price;
    private int priority;
    private String id;

    public Sizes() {
    }

    public Sizes(String name, int stock, double price, int priority, String id) {
        this.size = name;
        this.stock = stock;
        this.price = price;
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public String getSize() {
        return size;
    }

    public int getStock() {
        return stock;
    }

    public double getPrice() {
        return price;
    }

    public int getPriority() {
        return priority;
    }
}
