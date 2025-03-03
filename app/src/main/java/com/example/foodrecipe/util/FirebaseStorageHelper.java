package com.example.foodrecipe.util;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseStorageHelper {
    private static final String TAG = "FirebaseStorageHelper";
    private final FirebaseStorage storage;

    public interface OnImageUploadListener {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
    }

    public FirebaseStorageHelper() {
        storage = FirebaseStorage.getInstance();
    }

    public void uploadImage(Uri imageUri, String path, OnImageUploadListener listener) {
        if (imageUri == null) {
            listener.onFailure(new IllegalArgumentException("Image URI cannot be null"));
            return;
        }

        StorageReference storageRef = storage.getReference().child(path);
        
        UploadTask uploadTask = storageRef.putFile(imageUri);
        
        uploadTask.addOnSuccessListener(taskSnapshot -> 
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Log.d(TAG, "Image uploaded successfully. URL: " + downloadUrl);
                listener.onSuccess(downloadUrl);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to get download URL", e);
                listener.onFailure(e);
            })
        ).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to upload image", e);
            listener.onFailure(e);
        });
    }

    public void deleteImage(String path) {
        if (path == null || path.isEmpty()) {
            return;
        }

        StorageReference storageRef = storage.getReference().child(path);
        
        storageRef.delete().addOnSuccessListener(aVoid -> 
            Log.d(TAG, "Image deleted successfully: " + path)
        ).addOnFailureListener(e -> 
            Log.e(TAG, "Failed to delete image: " + path, e)
        );
    }
}
