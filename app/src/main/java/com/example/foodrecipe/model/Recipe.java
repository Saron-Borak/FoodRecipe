package com.example.foodrecipe.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Model class representing a recipe in the Food Recipe app.
 */
public class Recipe implements Serializable {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String videoId; // YouTube video ID
    private int cookingTime; // in minutes
    private int servingSize;
    private String category;
    private Map<String, String> nutritionValues; // e.g., {"calories": "250 kcal", "protein": "15g"}
    private List<Ingredient> ingredients;
    private List<String> instructions;
    private List<String> tags; // e.g., "vegetarian", "gluten-free"
    private String createdBy; // User ID who created this recipe
    private long createdAt;
    private long updatedAt;
    private int likesCount;
    private String notes; // User personal notes for the recipe

    // Default constructor required for Firestore
    public Recipe() {
        ingredients = new ArrayList<>();
        instructions = new ArrayList<>();
        tags = new ArrayList<>();
    }

    // Constructor with required fields
    public Recipe(String name, String description, int cookingTime, int servingSize, String category) {
        this.name = name;
        this.description = description;
        this.cookingTime = cookingTime;
        this.servingSize = servingSize;
        this.category = category;
        this.ingredients = new ArrayList<>();
        this.instructions = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.likesCount = 0;
    }

    // Getters and setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getCookingTimeMinutes() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public int getServingSize() {
        return servingSize;
    }

    public void setServingSize(int servingSize) {
        this.servingSize = servingSize;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, String> getNutritionValues() {
        return nutritionValues;
    }

    public void setNutritionValues(Map<String, String> nutritionValues) {
        this.nutritionValues = nutritionValues;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Helper methods
    public void addIngredient(Ingredient ingredient) {
        if (ingredients == null) {
            ingredients = new ArrayList<>();
        }
        ingredients.add(ingredient);
    }

    // Backwards compatibility for string-based ingredients
    public void addIngredientFromString(String ingredientStr) {
        if (ingredients == null) {
            ingredients = new ArrayList<>();
        }
        // Parse the ingredient string (assuming format like "2 tbsp olive oil")
        String[] parts = ingredientStr.split(" ", 2);
        String quantity = parts.length > 1 ? parts[0] : "";
        String name = parts.length > 1 ? parts[1] : ingredientStr;
        
        Ingredient ingredient = new Ingredient();
        ingredient.setQuantity(quantity);
        ingredient.setName(name);
        ingredient.setCategory("Other"); // Default category
        
        ingredients.add(ingredient);
    }

    public void addInstruction(String instruction) {
        if (instructions == null) {
            instructions = new ArrayList<>();
        }
        instructions.add(instruction);
    }

    public void addTag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
    }

    public void incrementLikesCount() {
        likesCount++;
    }

    public void decrementLikesCount() {
        if (likesCount > 0) {
            likesCount--;
        }
    }
    
    /**
     * Inner class representing an ingredient in a recipe
     */
    public static class Ingredient implements Serializable {
        private String name;
        private String quantity;
        private String unit;
        private String category;
        
        // Default constructor for Firestore
        public Ingredient() {
        }
        
        public Ingredient(String name, String quantity, String unit, String category) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
            this.category = category;
        }
        
        // Getters and setters
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
        
        public String getUnit() {
            return unit;
        }
        
        public void setUnit(String unit) {
            this.unit = unit;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (quantity != null && !quantity.isEmpty()) {
                sb.append(quantity);
                sb.append(" ");
            }
            if (unit != null && !unit.isEmpty()) {
                sb.append(unit);
                sb.append(" ");
            }
            sb.append(name);
            return sb.toString();
        }
    }
}
