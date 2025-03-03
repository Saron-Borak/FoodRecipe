package com.example.foodrecipe.repository;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.foodrecipe.model.Recipe;
import com.example.foodrecipe.model.User;
import com.example.foodrecipe.util.FirebaseHelper;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository for managing User data.
 */
public class UserRepository {
    private static final String TAG = "UserRepository";
    
    private final FirebaseHelper firebaseHelper;
    
    public UserRepository() {
        firebaseHelper = FirebaseHelper.getInstance();
    }
    
    /**
     * Sign in with email and password
     */
    public LiveData<FirebaseUser> signInWithEmailAndPassword(String email, String password) {
        MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
        
        firebaseHelper.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    userLiveData.setValue(user);
                    
                    // Update user's last login time
                    if (user != null) {
                        firebaseHelper.getUserData(user.getUid())
                                .addOnSuccessListener(documentSnapshot -> {
                                    User userData = documentSnapshot.toObject(User.class);
                                    if (userData != null) {
                                        userData.updateLoginTime();
                                        firebaseHelper.saveUserToFirestore(userData);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error signing in with email and password", e);
                    userLiveData.setValue(null);
                });
                
        return userLiveData;
    }
    
    /**
     * Create user with email and password
     */
    public LiveData<FirebaseUser> createUserWithEmailAndPassword(String email, String password, String name) {
        MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
        
        firebaseHelper.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        // Update profile
                        firebaseHelper.updateUserProfile(name, null)
                                .addOnSuccessListener(aVoid -> {
                                    // Create user in Firestore
                                    User user = new User(firebaseUser.getUid(), name, email);
                                    firebaseHelper.saveUserToFirestore(user)
                                            .addOnSuccessListener(aVoid1 -> userLiveData.setValue(firebaseUser))
                                            .addOnFailureListener(e -> {
                                                Log.e(TAG, "Error saving user to Firestore", e);
                                                userLiveData.setValue(null);
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error updating user profile", e);
                                    userLiveData.setValue(null);
                                });
                    } else {
                        userLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating user with email and password", e);
                    userLiveData.setValue(null);
                });
                
        return userLiveData;
    }
    
    /**
     * Sign in with phone credential
     */
    public LiveData<FirebaseUser> signInWithPhoneCredential(PhoneAuthCredential credential, String name) {
        MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
        
        firebaseHelper.signInWithPhoneAuthCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        // Check if this is an existing user
                        firebaseHelper.getUserData(firebaseUser.getUid())
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        // Existing user - update last login
                                        User user = documentSnapshot.toObject(User.class);
                                        if (user != null) {
                                            user.updateLoginTime();
                                            firebaseHelper.saveUserToFirestore(user)
                                                    .addOnSuccessListener(aVoid -> userLiveData.setValue(firebaseUser));
                                        } else {
                                            userLiveData.setValue(firebaseUser);
                                        }
                                    } else {
                                        // New user - create profile
                                        firebaseHelper.updateUserProfile(name, null)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Create user in Firestore
                                                    User user = new User(firebaseUser.getUid(), name, null);
                                                    user.setPhoneNumber(firebaseUser.getPhoneNumber());
                                                    
                                                    firebaseHelper.saveUserToFirestore(user)
                                                            .addOnSuccessListener(aVoid1 -> userLiveData.setValue(firebaseUser))
                                                            .addOnFailureListener(e -> {
                                                                Log.e(TAG, "Error saving user to Firestore", e);
                                                                userLiveData.setValue(null);
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e(TAG, "Error updating user profile", e);
                                                    userLiveData.setValue(null);
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error checking user existence", e);
                                    userLiveData.setValue(null);
                                });
                    } else {
                        userLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error signing in with phone credential", e);
                    userLiveData.setValue(null);
                });
                
        return userLiveData;
    }
    
    /**
     * Sign out
     */
    public void signOut() {
        firebaseHelper.signOut();
    }
    
    /**
     * Get current user
     */
    public FirebaseUser getCurrentUser() {
        return firebaseHelper.getCurrentUser();
    }
    
    /**
     * Get user data
     */
    public LiveData<User> getUserData() {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        
        FirebaseUser currentUser = firebaseHelper.getCurrentUser();
        if (currentUser != null) {
            firebaseHelper.getUserData(currentUser.getUid())
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        userLiveData.setValue(user);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting user data", e);
                        userLiveData.setValue(null);
                    });
        } else {
            userLiveData.setValue(null);
        }
        
        return userLiveData;
    }
    
    /**
     * Update user profile
     */
    public LiveData<Boolean> updateUserProfile(String name, Uri photoUri) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        firebaseHelper.updateUserProfile(name, photoUri)
                .addOnSuccessListener(aVoid -> {
                    // Update user in Firestore
                    FirebaseUser currentUser = firebaseHelper.getCurrentUser();
                    if (currentUser != null) {
                        firebaseHelper.getUserData(currentUser.getUid())
                                .addOnSuccessListener(documentSnapshot -> {
                                    User user = documentSnapshot.toObject(User.class);
                                    if (user != null) {
                                        user.setName(name);
                                        if (photoUri != null) {
                                            user.setProfileImageUrl(photoUri.toString());
                                        }
                                        
                                        firebaseHelper.saveUserToFirestore(user)
                                                .addOnSuccessListener(aVoid1 -> resultLiveData.setValue(true))
                                                .addOnFailureListener(e -> {
                                                    Log.e(TAG, "Error updating user in Firestore", e);
                                                    resultLiveData.setValue(false);
                                                });
                                    } else {
                                        resultLiveData.setValue(false);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error getting user data for update", e);
                                    resultLiveData.setValue(false);
                                });
                    } else {
                        resultLiveData.setValue(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating user profile", e);
                    resultLiveData.setValue(false);
                });
                
        return resultLiveData;
    }
    
    /**
     * Upload profile image
     */
    public LiveData<String> uploadProfileImage(Uri imageUri) {
        MutableLiveData<String> imageUrlLiveData = new MutableLiveData<>();
        
        FirebaseUser currentUser = firebaseHelper.getCurrentUser();
        if (currentUser != null) {
            UploadTask uploadTask = firebaseHelper.uploadProfileImage(imageUri, currentUser.getUid());
            uploadTask.addOnSuccessListener(taskSnapshot -> 
                firebaseHelper.getProfileImageUrl(currentUser.getUid())
                    .addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        // Update user with image URL
                        firebaseHelper.getUserData(currentUser.getUid())
                                .addOnSuccessListener(documentSnapshot -> {
                                    User user = documentSnapshot.toObject(User.class);
                                    if (user != null) {
                                        user.setProfileImageUrl(imageUrl);
                                        firebaseHelper.saveUserToFirestore(user)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Update user profile
                                                    firebaseHelper.updateUserProfile(user.getName(), uri)
                                                            .addOnSuccessListener(aVoid1 -> imageUrlLiveData.setValue(imageUrl))
                                                            .addOnFailureListener(e -> {
                                                                Log.e(TAG, "Error updating user profile with image", e);
                                                                imageUrlLiveData.setValue(null);
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e(TAG, "Error updating user with image URL", e);
                                                    imageUrlLiveData.setValue(null);
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error getting user for image URL update", e);
                                    imageUrlLiveData.setValue(null);
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting image download URL", e);
                        imageUrlLiveData.setValue(null);
                    })
            ).addOnFailureListener(e -> {
                Log.e(TAG, "Error uploading profile image", e);
                imageUrlLiveData.setValue(null);
            });
        } else {
            imageUrlLiveData.setValue(null);
        }
        
        return imageUrlLiveData;
    }
    
    /**
     * Update user ingredients
     */
    public LiveData<Boolean> updateUserIngredients(String category, String ingredient, boolean add) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        FirebaseUser currentUser = firebaseHelper.getCurrentUser();
        if (currentUser != null) {
            firebaseHelper.getUserData(currentUser.getUid())
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            if (add) {
                                user.addIngredient(category, ingredient);
                            } else {
                                user.removeIngredient(category, ingredient);
                            }
                            
                            firebaseHelper.saveUserToFirestore(user)
                                    .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error updating user ingredients", e);
                                        resultLiveData.setValue(false);
                                    });
                        } else {
                            resultLiveData.setValue(false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting user for ingredient update", e);
                        resultLiveData.setValue(false);
                    });
        } else {
            resultLiveData.setValue(false);
        }
        
        return resultLiveData;
    }
    
    /**
     * Update dietary preferences
     */
    public LiveData<Boolean> updateDietaryPreference(String preference, boolean add) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        FirebaseUser currentUser = firebaseHelper.getCurrentUser();
        if (currentUser != null) {
            firebaseHelper.getUserData(currentUser.getUid())
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            if (add) {
                                user.addDietaryPreference(preference);
                            } else {
                                user.removeDietaryPreference(preference);
                            }
                            
                            firebaseHelper.saveUserToFirestore(user)
                                    .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error updating dietary preferences", e);
                                        resultLiveData.setValue(false);
                                    });
                        } else {
                            resultLiveData.setValue(false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting user for dietary preference update", e);
                        resultLiveData.setValue(false);
                    });
        } else {
            resultLiveData.setValue(false);
        }
        
        return resultLiveData;
    }
    
    /**
     * Toggle notification settings
     */
    public LiveData<Boolean> toggleNotifications(boolean enable) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        FirebaseUser currentUser = firebaseHelper.getCurrentUser();
        if (currentUser != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("notificationsEnabled", enable);
            
            firebaseHelper.updateUserData(currentUser.getUid(), updates)
                    .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating notification settings", e);
                        resultLiveData.setValue(false);
                    });
        } else {
            resultLiveData.setValue(false);
        }
        
        return resultLiveData;
    }
    
    /**
     * Update user preferences (dietary preferences and notifications)
     */
    public LiveData<Boolean> updateUserPreferences(List<String> dietaryPreferences, boolean notificationsEnabled) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        FirebaseUser currentUser = firebaseHelper.getCurrentUser();
        if (currentUser != null) {
            firebaseHelper.getUserData(currentUser.getUid())
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            // Update dietary preferences
                            user.setDietaryPreferences(dietaryPreferences);
                            
                            // Update notification setting
                            user.setNotificationsEnabled(notificationsEnabled);
                            
                            // Save updated user
                            firebaseHelper.saveUserToFirestore(user)
                                    .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error updating user preferences", e);
                                        resultLiveData.setValue(false);
                                    });
                        } else {
                            resultLiveData.setValue(false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting user for preferences update", e);
                        resultLiveData.setValue(false);
                    });
        } else {
            resultLiveData.setValue(false);
        }
        
        return resultLiveData;
    }
    
    /**
     * Toggle favorite recipe
     */
    public LiveData<Boolean> toggleFavoriteRecipe(String recipeId, boolean addToFavorites) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        FirebaseUser currentUser = firebaseHelper.getCurrentUser();
        if (currentUser != null) {
            firebaseHelper.getUserData(currentUser.getUid())
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            List<String> favorites = user.getFavoriteRecipes();
                            if (favorites == null) {
                                favorites = new ArrayList<>();
                            }
                            
                            if (addToFavorites && !favorites.contains(recipeId)) {
                                // Add to favorites
                                favorites.add(recipeId);
                            } else if (!addToFavorites && favorites.contains(recipeId)) {
                                // Remove from favorites
                                favorites.remove(recipeId);
                            }
                            
                            user.setFavoriteRecipes(favorites);
                            
                            // Save updated user
                            firebaseHelper.saveUserToFirestore(user)
                                    .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error updating favorites", e);
                                        resultLiveData.setValue(false);
                                    });
                        } else {
                            resultLiveData.setValue(false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting user for favorites update", e);
                        resultLiveData.setValue(false);
                    });
        } else {
            resultLiveData.setValue(false);
        }
        
        return resultLiveData;
    }
    
    /**
     * Check if recipe is in favorites
     */
    public LiveData<Boolean> isRecipeFavorite(String recipeId) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        FirebaseUser currentUser = firebaseHelper.getCurrentUser();
        if (currentUser != null) {
            firebaseHelper.getUserData(currentUser.getUid())
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null && user.getFavoriteRecipes() != null) {
                            resultLiveData.setValue(user.getFavoriteRecipes().contains(recipeId));
                        } else {
                            resultLiveData.setValue(false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error checking if recipe is favorite", e);
                        resultLiveData.setValue(false);
                    });
        } else {
            resultLiveData.setValue(false);
        }
        
        return resultLiveData;
    }
    
    /**
     * Get favorite recipes
     */
    public LiveData<List<Recipe>> getFavoriteRecipes() {
        MutableLiveData<List<Recipe>> recipesLiveData = new MutableLiveData<>();
        
        FirebaseUser currentUser = firebaseHelper.getCurrentUser();
        if (currentUser != null) {
            firebaseHelper.getUserData(currentUser.getUid())
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null && user.getFavoriteRecipes() != null && !user.getFavoriteRecipes().isEmpty()) {
                            List<String> favoriteIds = user.getFavoriteRecipes();
                            
                            // Get recipes by IDs
                            firebaseHelper.getRecipesByIds(favoriteIds)
                                    .addOnSuccessListener(recipes -> {
                                        recipesLiveData.setValue(recipes);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error getting favorite recipes", e);
                                        recipesLiveData.setValue(new ArrayList<>());
                                    });
                        } else {
                            recipesLiveData.setValue(new ArrayList<>());
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting user for favorites", e);
                        recipesLiveData.setValue(new ArrayList<>());
                    });
        } else {
            recipesLiveData.setValue(new ArrayList<>());
        }
        
        return recipesLiveData;
    }
}
