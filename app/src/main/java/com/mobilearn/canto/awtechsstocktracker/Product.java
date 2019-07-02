package com.mobilearn.canto.awtechsstocktracker;

public class Product {
    private String name;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String description;
    private String image;
    private String status;
    private String town_stock;
    private String warehouse_stock;
    private String updated_by;
    private String updated_on;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTown_stock() {
        return town_stock;
    }

    public void setTown_stock(String town_stock) {
        this.town_stock = town_stock;
    }

    public String getWarehouse_stock() {
        return warehouse_stock;
    }

    public void setWarehouse_stock(String warehouse_stock) {
        this.warehouse_stock = warehouse_stock;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }



}
