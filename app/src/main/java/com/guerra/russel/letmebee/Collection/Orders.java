package com.guerra.russel.letmebee.Collection;

public class Orders {
    private String by;
    private String dateOrdered;
    private String name;
    private String size;
    private int quantity;
    private double price;
    private int status;
    private String dateApproved;
    private String dateDelivered;
    private String signature;
    private String id;

    public Orders() {
    }

    public Orders(String by, String dateOrdered, String name, String size, int quantity, double price,
                  int status, String dateApproved, String dateDelivered, String signature, String id) {
        this.by = by;
        this.dateOrdered = dateOrdered;
        this.name = name;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.dateApproved = dateApproved;
        this.dateDelivered = dateDelivered;
        this.signature = signature;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getBy() {
        return by;
    }

    public String getDateOrdered() {
        return dateOrdered;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public int getStatus() {
        return status;
    }

    public String getDateApproved() {
        return dateApproved;
    }

    public String getDateDelivered() {
        return dateDelivered;
    }

    public String getSignature() {
        return signature;
    }
}
