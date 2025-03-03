package com.example.foodrecipe.repository;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.foodrecipe.model.Recipe;
import com.example.foodrecipe.util.FirebaseHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing Recipe data.
 */
public class RecipeRepository {
    private static final String TAG = "RecipeRepository";
    
    private final FirebaseHelper firebaseHelper;
    
    public RecipeRepository() {
        firebaseHelper = FirebaseHelper.getInstance();
    }
    
    /**
     * Get all recipes
     */
    public LiveData<List<Recipe>> getAllRecipes() {
        MutableLiveData<List<Recipe>> recipesLiveData = new MutableLiveData<>();
        
        firebaseHelper.getAllRecipes()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> recipes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        recipes.add(recipe);
                    }
                    recipesLiveData.setValue(recipes);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting recipes", e);
                    recipesLiveData.setValue(new ArrayList<>());
                });
                
        return recipesLiveData;
    }
    
    /**
     * Get recipes by category
     */
    public LiveData<List<Recipe>> getRecipesByCategory(String category) {
        MutableLiveData<List<Recipe>> recipesLiveData = new MutableLiveData<>();
        
        firebaseHelper.getRecipesByCategory(category)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> recipes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        recipes.add(recipe);
                    }
                    recipesLiveData.setValue(recipes);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting recipes by category", e);
                    recipesLiveData.setValue(new ArrayList<>());
                });
                
        return recipesLiveData;
    }
    
    /**
     * Get recipes by cooking time
     */
    public LiveData<List<Recipe>> getRecipesByCookingTime(int maxMinutes) {
        MutableLiveData<List<Recipe>> recipesLiveData = new MutableLiveData<>();
        
        firebaseHelper.getRecipesByCookingTime(maxMinutes)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> recipes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        recipes.add(recipe);
                    }
                    recipesLiveData.setValue(recipes);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting recipes by cooking time", e);
                    recipesLiveData.setValue(new ArrayList<>());
                });
                
        return recipesLiveData;
    }
    
    /**
     * Get recipes by serving size
     */
    public LiveData<List<Recipe>> getRecipesByServingSize(int servingSize) {
        MutableLiveData<List<Recipe>> recipesLiveData = new MutableLiveData<>();
        
        firebaseHelper.getRecipesByServingSize(servingSize)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> recipes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        recipes.add(recipe);
                    }
                    recipesLiveData.setValue(recipes);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting recipes by serving size", e);
                    recipesLiveData.setValue(new ArrayList<>());
                });
                
        return recipesLiveData;
    }
    
    /**
     * Search recipes by name
     */
    public LiveData<List<Recipe>> searchRecipesByName(String query) {
        MutableLiveData<List<Recipe>> recipesLiveData = new MutableLiveData<>();
        
        firebaseHelper.searchRecipesByName(query)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> recipes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        recipes.add(recipe);
                    }
                    recipesLiveData.setValue(recipes);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error searching recipes by name", e);
                    recipesLiveData.setValue(new ArrayList<>());
                });
                
        return recipesLiveData;
    }
    
    /**
     * Get favorite recipes
     */
    public LiveData<List<Recipe>> getFavoriteRecipes(String userId) {
        MutableLiveData<List<Recipe>> recipesLiveData = new MutableLiveData<>();
        
        firebaseHelper.getFavoriteRecipes(userId)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> recipes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        recipes.add(recipe);
                    }
                    recipesLiveData.setValue(recipes);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting favorite recipes", e);
                    recipesLiveData.setValue(new ArrayList<>());
                });
                
        return recipesLiveData;
    }
    
    /**
     * Get recipe by ID
     */
    public LiveData<Recipe> getRecipeById(String recipeId) {
        MutableLiveData<Recipe> recipeLiveData = new MutableLiveData<>();
        
        firebaseHelper.getRecipe(recipeId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Recipe recipe = documentSnapshot.toObject(Recipe.class);
                        if (recipe != null) {
                            recipe.setId(documentSnapshot.getId());
                        }
                        recipeLiveData.setValue(recipe);
                    } else {
                        recipeLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting recipe by ID", e);
                    recipeLiveData.setValue(null);
                });
                
        return recipeLiveData;
    }
    
    /**
     * Add a new recipe
     */
    public LiveData<String> addRecipe(Recipe recipe) {
        MutableLiveData<String> recipeIdLiveData = new MutableLiveData<>();
        
        firebaseHelper.addRecipe(recipe)
                .addOnSuccessListener(documentReference -> {
                    String recipeId = documentReference.getId();
                    recipe.setId(recipeId);
                    // Update the recipe with the ID
                    documentReference.set(recipe)
                            .addOnSuccessListener(aVoid -> recipeIdLiveData.setValue(recipeId))
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error updating recipe with ID", e);
                                recipeIdLiveData.setValue(null);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding recipe", e);
                    recipeIdLiveData.setValue(null);
                });
                
        return recipeIdLiveData;
    }
    
    /**
     * Upload recipe image
     */
    public LiveData<String> uploadRecipeImage(Uri imageUri, String recipeId) {
        MutableLiveData<String> imageUrlLiveData = new MutableLiveData<>();
        
        UploadTask uploadTask = firebaseHelper.uploadRecipeImage(imageUri, recipeId);
        uploadTask.addOnSuccessListener(taskSnapshot -> 
            firebaseHelper.getRecipeImageUrl(recipeId)
                .addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    // Update recipe with image URL
                    firebaseHelper.getRecipe(recipeId)
                            .addOnSuccessListener(documentSnapshot -> {
                                Recipe recipe = documentSnapshot.toObject(Recipe.class);
                                if (recipe != null) {
                                    recipe.setId(recipeId);
                                    recipe.setImageUrl(imageUrl);
                                    documentSnapshot.getReference().set(recipe)
                                            .addOnSuccessListener(aVoid -> imageUrlLiveData.setValue(imageUrl))
                                            .addOnFailureListener(e -> {
                                                Log.e(TAG, "Error updating recipe with image URL", e);
                                                imageUrlLiveData.setValue(null);
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error getting recipe for image URL update", e);
                                imageUrlLiveData.setValue(null);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting image download URL", e);
                    imageUrlLiveData.setValue(null);
                })
        ).addOnFailureListener(e -> {
            Log.e(TAG, "Error uploading recipe image", e);
            imageUrlLiveData.setValue(null);
        });
        
        return imageUrlLiveData;
    }
    
    /**
     * Toggle favorite recipe
     */
    public LiveData<Boolean> toggleFavoriteRecipe(String userId, String recipeId, boolean isFavorite) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        firebaseHelper.toggleFavoriteRecipe(userId, recipeId, isFavorite)
                .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error toggling favorite recipe", e);
                    resultLiveData.setValue(false);
                });
                
        return resultLiveData;
    }
    
    /**
     * Update recipe notes
     */
    public LiveData<Boolean> updateRecipeNotes(String recipeId, String notes) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        firebaseHelper.updateRecipeNotes(recipeId, notes)
                .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating recipe notes", e);
                    resultLiveData.setValue(false);
                });
                
        return resultLiveData;
    }
}
