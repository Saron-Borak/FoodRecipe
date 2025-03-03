package com.example.foodrecipe.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class representing a user in the Food Recipe app.
 */
public class User {
    private String uid;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;
    private List<String> favoriteRecipes;
    private Map<String, List<String>> userIngredients; // Ingredients the user has in stock
    private List<String> dietaryPreferences; // e.g., "vegetarian", "vegan", "gluten-free"
    private boolean notificationsEnabled;
    private long createdAt;
    private long lastLogin;

    // Default constructor required for Firestore
    public User() {
        favoriteRecipes = new ArrayList<>();
        userIngredients = new HashMap<>();
        dietaryPreferences = new ArrayList<>();
        notificationsEnabled = true;
    }

    // Constructor with required fields
    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.favoriteRecipes = new ArrayList<>();
        this.userIngredients = new HashMap<>();
        this.dietaryPreferences = new ArrayList<>();
        this.notificationsEnabled = true;
        this.createdAt = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
    }

    // Getters and setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public List<String> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public void setFavoriteRecipes(List<String> favoriteRecipes) {
        this.favoriteRecipes = favoriteRecipes;
    }

    public Map<String, List<String>> getUserIngredients() {
        return userIngredients;
    }

    public void setUserIngredients(Map<String, List<String>> userIngredients) {
        this.userIngredients = userIngredients;
    }

    public List<String> getDietaryPreferences() {
        return dietaryPreferences;
    }

    public void setDietaryPreferences(List<String> dietaryPreferences) {
        this.dietaryPreferences = dietaryPreferences;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Helper methods
    public void addFavoriteRecipe(String recipeId) {
        if (favoriteRecipes == null) {
            favoriteRecipes = new ArrayList<>();
        }
        if (!favoriteRecipes.contains(recipeId)) {
            favoriteRecipes.add(recipeId);
        }
    }

    public void removeFavoriteRecipe(String recipeId) {
        if (favoriteRecipes != null) {
            favoriteRecipes.remove(recipeId);
        }
    }

    public void addIngredient(String category, String ingredient) {
        if (userIngredients == null) {
            userIngredients = new HashMap<>();
        }
        
        List<String> ingredients = userIngredients.get(category);
        if (ingredients == null) {
            ingredients = new ArrayList<>();
            userIngredients.put(category, ingredients);
        }
        
        if (!ingredients.contains(ingredient)) {
            ingredients.add(ingredient);
        }
    }

    public void removeIngredient(String category, String ingredient) {
        if (userIngredients != null && userIngredients.containsKey(category)) {
            List<String> ingredients = userIngredients.get(category);
            if (ingredients != null) {
                ingredients.remove(ingredient);
                
                // Remove the category if it's now empty
                if (ingredients.isEmpty()) {
                    userIngredients.remove(category);
                }
            }
        }
    }

    public void addDietaryPreference(String preference) {
        if (dietaryPreferences == null) {
            dietaryPreferences = new ArrayList<>();
        }
        if (!dietaryPreferences.contains(preference)) {
            dietaryPreferences.add(preference);
        }
    }

    public void removeDietaryPreference(String preference) {
        if (dietaryPreferences != null) {
            dietaryPreferences.remove(preference);
        }
    }

    public void updateLoginTime() {
        this.lastLogin = System.currentTimeMillis();
    }
}
