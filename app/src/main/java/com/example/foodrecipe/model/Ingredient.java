package com.example.foodrecipe.model;

/**
 * Model class for recipe ingredients
 */
public class Ingredient {
    private String id;
    private String name;
    private String quantity;
    private String category;
    
    // Required empty constructor for Firebase
    public Ingredient() {
    }
    
    public Ingredient(String id, String name, String quantity, String category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getQuantity() {
        return quantity;
    }
    
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
}
