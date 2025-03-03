package com.example.foodrecipe.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodrecipe.model.Recipe;
import com.example.foodrecipe.repository.RecipeRepository;

import java.util.List;

/**
 * ViewModel for recipe-related operations.
 */
public class RecipeViewModel extends ViewModel {
    private final RecipeRepository recipeRepository;
    
    public RecipeViewModel() {
        recipeRepository = new RecipeRepository();
    }
    
    /**
     * Get all recipes
     */
    public LiveData<List<Recipe>> getAllRecipes() {
        return recipeRepository.getAllRecipes();
    }
    
    /**
     * Get recipes by category
     */
    public LiveData<List<Recipe>> getRecipesByCategory(String category) {
        return recipeRepository.getRecipesByCategory(category);
    }
    
    /**
     * Get recipes by cooking time
     */
    public LiveData<List<Recipe>> getRecipesByCookingTime(int maxMinutes) {
        return recipeRepository.getRecipesByCookingTime(maxMinutes);
    }
    
    /**
     * Get recipes by serving size
     */
    public LiveData<List<Recipe>> getRecipesByServingSize(int servingSize) {
        return recipeRepository.getRecipesByServingSize(servingSize);
    }
    
    /**
     * Search recipes by name
     */
    public LiveData<List<Recipe>> searchRecipesByName(String query) {
        return recipeRepository.searchRecipesByName(query);
    }
    
    /**
     * Get favorite recipes
     */
    public LiveData<List<Recipe>> getFavoriteRecipes(String userId) {
        return recipeRepository.getFavoriteRecipes(userId);
    }
    
    /**
     * Get recipe by ID
     */
    public LiveData<Recipe> getRecipeById(String recipeId) {
        return recipeRepository.getRecipeById(recipeId);
    }
    
    /**
     * Add a new recipe
     */
    public LiveData<String> addRecipe(Recipe recipe) {
        return recipeRepository.addRecipe(recipe);
    }
    
    /**
     * Upload recipe image
     */
    public LiveData<String> uploadRecipeImage(Uri imageUri, String recipeId) {
        return recipeRepository.uploadRecipeImage(imageUri, recipeId);
    }
    
    /**
     * Toggle favorite recipe
     */
    public LiveData<Boolean> toggleFavoriteRecipe(String userId, String recipeId, boolean isFavorite) {
        return recipeRepository.toggleFavoriteRecipe(userId, recipeId, isFavorite);
    }
    
    /**
     * Update recipe notes
     */
    public LiveData<Boolean> updateRecipeNotes(String recipeId, String notes) {
        return recipeRepository.updateRecipeNotes(recipeId, notes);
    }
}
