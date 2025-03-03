package com.example.foodrecipe.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodrecipe.model.Recipe;
import com.example.foodrecipe.model.User;
import com.example.foodrecipe.repository.UserRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;

import java.util.List;

/**
 * ViewModel for user-related operations.
 */
public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;
    
    public UserViewModel() {
        userRepository = new UserRepository();
    }
    
    /**
     * Sign in with email and password
     */
    public LiveData<FirebaseUser> signInWithEmailAndPassword(String email, String password) {
        return userRepository.signInWithEmailAndPassword(email, password);
    }
    
    /**
     * Create a new user with email and password
     */
    public LiveData<FirebaseUser> createUserWithEmailAndPassword(String email, String password, String name) {
        return userRepository.createUserWithEmailAndPassword(email, password, name);
    }
    
    /**
     * Sign in with phone credential
     */
    public LiveData<FirebaseUser> signInWithPhoneCredential(PhoneAuthCredential credential, String name) {
        return userRepository.signInWithPhoneCredential(credential, name);
    }
    
    /**
     * Sign out current user
     */
    public void signOut() {
        userRepository.signOut();
    }
    
    /**
     * Get current user
     */
    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }
    
    /**
     * Get user data from Firestore
     */
    public LiveData<User> getUserData() {
        return userRepository.getUserData();
    }
    
    /**
     * Update user profile
     */
    public LiveData<Boolean> updateUserProfile(String name, Uri photoUri) {
        return userRepository.updateUserProfile(name, photoUri);
    }
    
    /**
     * Upload user profile image
     */
    public LiveData<String> uploadProfileImage(Uri imageUri) {
        return userRepository.uploadProfileImage(imageUri);
    }
    
    /**
     * Update user ingredients
     */
    public LiveData<Boolean> updateUserIngredients(String category, String ingredient, boolean add) {
        return userRepository.updateUserIngredients(category, ingredient, add);
    }
    
    /**
     * Update dietary preferences
     */
    public LiveData<Boolean> updateDietaryPreference(String preference, boolean add) {
        return userRepository.updateDietaryPreference(preference, add);
    }
    
    /**
     * Toggle notification settings
     */
    public LiveData<Boolean> toggleNotifications(boolean enable) {
        return userRepository.toggleNotifications(enable);
    }
    
    /**
     * Update notification preference
     */
    public LiveData<Boolean> updateNotificationPreference(boolean enabled) {
        return userRepository.toggleNotifications(enabled);
    }
    
    /**
     * Update user preferences (dietary preferences and notifications)
     */
    public LiveData<Boolean> updateUserPreferences(List<String> dietaryPreferences, boolean notificationsEnabled) {
        return userRepository.updateUserPreferences(dietaryPreferences, notificationsEnabled);
    }
    
    /**
     * Add a recipe to user's favorites
     */
    public LiveData<Boolean> addToFavorites(String recipeId) {
        return userRepository.toggleFavoriteRecipe(recipeId, true);
    }
    
    /**
     * Remove a recipe from user's favorites
     */
    public LiveData<Boolean> removeFromFavorites(String recipeId) {
        return userRepository.toggleFavoriteRecipe(recipeId, false);
    }
    
    /**
     * Toggle a recipe in user's favorites
     */
    public LiveData<Boolean> toggleFavoriteRecipe(String recipeId, boolean addToFavorites) {
        return userRepository.toggleFavoriteRecipe(recipeId, addToFavorites);
    }
    
    /**
     * Check if a recipe is in user's favorites
     */
    public LiveData<Boolean> isRecipeFavorite(String recipeId) {
        return userRepository.isRecipeFavorite(recipeId);
    }
    
    /**
     * Get all favorited recipes
     */
    public LiveData<List<Recipe>> getFavoriteRecipes() {
        return userRepository.getFavoriteRecipes();
    }
}
