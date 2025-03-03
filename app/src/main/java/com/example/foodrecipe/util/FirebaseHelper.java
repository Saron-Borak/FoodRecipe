package com.example.foodrecipe.util;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.foodrecipe.model.Recipe;
import com.example.foodrecipe.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Helper class to manage Firebase interactions.
 */
public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    
    // Collection names
    private static final String USERS_COLLECTION = "users";
    private static final String RECIPES_COLLECTION = "recipes";
    private static final String FAVORITE_RECIPES_COLLECTION = "favoriteRecipes";
    
    // Firebase instances
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore mFirestore;
    private final FirebaseStorage mStorage;
    
    // Singleton instance
    private static FirebaseHelper instance;
    
    private FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }
    
    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }
    
    // Authentication methods
    
    /**
     * Get current authenticated user
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
    
    /**
     * Check if user is signed in
     */
    public boolean isUserSignedIn() {
        return mAuth.getCurrentUser() != null;
    }
    
    /**
     * Sign in with email and password
     */
    public Task<AuthResult> signInWithEmailAndPassword(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }
    
    /**
     * Create user with email and password
     */
    public Task<AuthResult> createUserWithEmailAndPassword(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }
    
    /**
     * Sign in with phone credential
     */
    public Task<AuthResult> signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        return mAuth.signInWithCredential(credential);
    }
    
    /**
     * Sign out current user
     */
    public void signOut() {
        mAuth.signOut();
    }
    
    /**
     * Update user profile information
     */
    public Task<Void> updateUserProfile(String displayName, Uri photoUri) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(photoUri)
                .build();
                
        return getCurrentUser().updateProfile(profileUpdates);
    }
    
    // Firestore User methods
    
    /**
     * Save user to Firestore
     */
    public Task<Void> saveUserToFirestore(User user) {
        return mFirestore.collection(USERS_COLLECTION)
                .document(user.getUid())
                .set(user);
    }
    
    /**
     * Get user data from Firestore
     */
    public Task<DocumentSnapshot> getUserData(String userId) {
        return mFirestore.collection(USERS_COLLECTION)
                .document(userId)
                .get();
    }
    
    /**
     * Update user data in Firestore
     */
    public Task<Void> updateUserData(String userId, Map<String, Object> updates) {
        return mFirestore.collection(USERS_COLLECTION)
                .document(userId)
                .update(updates);
    }
    
    // Recipe methods
    
    /**
     * Add new recipe to Firestore
     */
    public Task<DocumentReference> addRecipe(Recipe recipe) {
        return mFirestore.collection(RECIPES_COLLECTION)
                .add(recipe);
    }
    
    /**
     * Get recipe by ID
     */
    public Task<DocumentSnapshot> getRecipe(String recipeId) {
        return mFirestore.collection(RECIPES_COLLECTION)
                .document(recipeId)
                .get();
    }
    
    /**
     * Get all recipes
     */
    public Task<QuerySnapshot> getAllRecipes() {
        return mFirestore.collection(RECIPES_COLLECTION)
                .get();
    }
    
    /**
     * Get recipes by category
     */
    public Task<QuerySnapshot> getRecipesByCategory(String category) {
        return mFirestore.collection(RECIPES_COLLECTION)
                .whereEqualTo("category", category)
                .get();
    }
    
    /**
     * Get recipes by cooking time (less than or equal to specified minutes)
     */
    public Task<QuerySnapshot> getRecipesByCookingTime(int maxMinutes) {
        return mFirestore.collection(RECIPES_COLLECTION)
                .whereLessThanOrEqualTo("cookingTime", maxMinutes)
                .get();
    }
    
    /**
     * Get recipes by serving size
     */
    public Task<QuerySnapshot> getRecipesByServingSize(int servingSize) {
        return mFirestore.collection(RECIPES_COLLECTION)
                .whereEqualTo("servingSize", servingSize)
                .get();
    }
    
    /**
     * Search recipes by name (case-insensitive search)
     */
    public Task<QuerySnapshot> searchRecipesByName(String query) {
        String lowercaseQuery = query.toLowerCase();
        String uppercaseQuery = query.toUpperCase();
        
        return mFirestore.collection(RECIPES_COLLECTION)
                .orderBy("name")
                .startAt(lowercaseQuery)
                .endAt(lowercaseQuery + "\uf8ff")
                .get();
    }
    
    /**
     * Get recipes by IDs
     */
    public Task<List<Recipe>> getRecipesByIds(List<String> recipeIds) {
        if (recipeIds == null || recipeIds.isEmpty()) {
            return Tasks.forResult(new ArrayList<>());
        }
        
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String recipeId : recipeIds) {
            tasks.add(mFirestore.collection(RECIPES_COLLECTION).document(recipeId).get());
        }
        
        return Tasks.whenAllSuccess(tasks).continueWith(task -> {
            List<Object> results = task.getResult();
            List<Recipe> recipes = new ArrayList<>();
            
            for (Object result : results) {
                DocumentSnapshot document = (DocumentSnapshot) result;
                if (document.exists()) {
                    Recipe recipe = document.toObject(Recipe.class);
                    if (recipe != null) {
                        recipe.setId(document.getId());
                        recipes.add(recipe);
                    }
                }
            }
            
            return recipes;
        });
    }
    
    /**
     * Get favorite recipes for a user
     */
    public Task<QuerySnapshot> getFavoriteRecipes(String userId) {
        return mFirestore.collection(RECIPES_COLLECTION)
                .whereArrayContains("favoriteUsers", userId)
                .get();
    }
    
    /**
     * Add or remove recipe from favorites
     */
    public Task<Void> toggleFavoriteRecipe(String userId, String recipeId, boolean isFavorite) {
        // Update the user's favorite recipes list
        DocumentReference userRef = mFirestore.collection(USERS_COLLECTION).document(userId);
        
        if (isFavorite) {
            return userRef.update("favoriteRecipes", com.google.firebase.firestore.FieldValue.arrayUnion(recipeId));
        } else {
            return userRef.update("favoriteRecipes", com.google.firebase.firestore.FieldValue.arrayRemove(recipeId));
        }
    }
    
    /**
     * Update recipe notes
     */
    public Task<Void> updateRecipeNotes(String recipeId, String notes) {
        return mFirestore.collection(RECIPES_COLLECTION)
                .document(recipeId)
                .update("notes", notes);
    }
    
    // Storage methods
    
    /**
     * Upload recipe image to Firebase Storage
     */
    public UploadTask uploadRecipeImage(Uri imageUri, String recipeId) {
        StorageReference storageRef = mStorage.getReference();
        StorageReference imagesRef = storageRef.child("recipe_images/" + recipeId + ".jpg");
        
        return imagesRef.putFile(imageUri);
    }
    
    /**
     * Get recipe image download URL
     */
    public Task<Uri> getRecipeImageUrl(String recipeId) {
        StorageReference storageRef = mStorage.getReference();
        StorageReference imageRef = storageRef.child("recipe_images/" + recipeId + ".jpg");
        
        return imageRef.getDownloadUrl();
    }
    
    /**
     * Upload user profile image to Firebase Storage
     */
    public UploadTask uploadProfileImage(Uri imageUri, String userId) {
        StorageReference storageRef = mStorage.getReference();
        StorageReference profileImagesRef = storageRef.child("profile_images/" + userId + ".jpg");
        
        return profileImagesRef.putFile(imageUri);
    }
    
    /**
     * Get user profile image download URL
     */
    public Task<Uri> getProfileImageUrl(String userId) {
        StorageReference storageRef = mStorage.getReference();
        StorageReference profileImageRef = storageRef.child("profile_images/" + userId + ".jpg");
        
        return profileImageRef.getDownloadUrl();
    }
}
