package com.example.foodrecipe.model;

/**
 * Model class for recipe categories
 */
public class Category {
    private String id;
    private String name;
    private String imageUrl;
    
    // Required empty constructor for Firebase
    public Category() {
    }
    
    public Category(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
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
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
