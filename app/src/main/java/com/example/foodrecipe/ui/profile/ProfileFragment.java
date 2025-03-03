package com.example.foodrecipe.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.foodrecipe.R;
import com.example.foodrecipe.model.User;
import com.example.foodrecipe.ui.auth.LoginActivity;
import com.example.foodrecipe.viewmodel.UserViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying and editing user profile
 */
public class ProfileFragment extends Fragment {
    
    private UserViewModel userViewModel;
    private ImageView profileImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private Button editProfileButton;
    private Button updatePhotoButton;
    private ChipGroup dietaryPreferencesChipGroup;
    private Switch notificationsSwitch;
    private Button logoutButton;
    
    private Uri selectedImageUri;
    
    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    profileImageView.setImageURI(uri);
                    uploadProfileImage(uri);
                }
            });
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        // Initialize ViewModel
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        
        // Initialize UI components
        initializeViews(view);
        
        // Setup listeners
        setupListeners();
        
        // Load user data
        loadUserData();
        
        return view;
    }
    
    private void initializeViews(View view) {
        profileImageView = view.findViewById(R.id.image_profile);
        nameTextView = view.findViewById(R.id.text_name);
        emailTextView = view.findViewById(R.id.text_email);
        editProfileButton = view.findViewById(R.id.button_edit_profile);
        updatePhotoButton = view.findViewById(R.id.button_update_photo);
        dietaryPreferencesChipGroup = view.findViewById(R.id.chip_group_dietary_preferences);
        notificationsSwitch = view.findViewById(R.id.switch_notifications);
        logoutButton = view.findViewById(R.id.button_logout);
        
        // Setup dietary preference chips
        setupDietaryPreferenceChips();
    }
    
    private void setupDietaryPreferenceChips() {
        String[] preferences = {
                getString(R.string.vegetarian),
                getString(R.string.vegan),
                getString(R.string.gluten_free),
                getString(R.string.dairy_free),
                getString(R.string.low_carb),
                getString(R.string.keto),
                getString(R.string.paleo)
        };
        
        for (String preference : preferences) {
            Chip chip = new Chip(requireContext());
            chip.setText(preference);
            chip.setCheckable(true);
            dietaryPreferencesChipGroup.addView(chip);
        }
    }
    
    private void setupListeners() {
        // Update profile photo button click
        updatePhotoButton.setOnClickListener(v -> getContent.launch("image/*"));
        
        // Edit profile button click
        editProfileButton.setOnClickListener(v -> saveProfile());
        
        // Logout button click
        logoutButton.setOnClickListener(v -> logout());
        
        // Notification switch change
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> 
                userViewModel.updateNotificationPreference(isChecked));
        
        // Dietary preference chip selection changes
        for (int i = 0; i < dietaryPreferencesChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) dietaryPreferencesChipGroup.getChildAt(i);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> updateDietaryPreferences());
        }
    }
    
    private void loadUserData() {
        userViewModel.getUserData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                updateUI(user);
            }
        });
    }
    
    private void updateUI(User user) {
        // Set user name and email
        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());
        
        // Load profile image
        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(user.getProfileImageUrl())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .circleCrop()
                    .into(profileImageView);
        }
        
        // Set notification preference
        notificationsSwitch.setChecked(user.isNotificationsEnabled());
        
        // Set dietary preferences
        if (user.getDietaryPreferences() != null) {
            for (int i = 0; i < dietaryPreferencesChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) dietaryPreferencesChipGroup.getChildAt(i);
                chip.setChecked(user.getDietaryPreferences().contains(chip.getText().toString()));
            }
        }
    }
    
    private void saveProfile() {
        // Get dietary preferences
        List<String> dietaryPreferences = new ArrayList<>();
        for (int i = 0; i < dietaryPreferencesChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) dietaryPreferencesChipGroup.getChildAt(i);
            if (chip.isChecked()) {
                dietaryPreferences.add(chip.getText().toString());
            }
        }
        
        // Update user data
        userViewModel.updateUserPreferences(dietaryPreferences, notificationsSwitch.isChecked())
                .observe(getViewLifecycleOwner(), success -> {
                    if (success != null && success) {
                        Toast.makeText(requireContext(), 
                                getString(R.string.profile_updated), 
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void uploadProfileImage(Uri imageUri) {
        userViewModel.uploadProfileImage(imageUri).observe(getViewLifecycleOwner(), imageUrl -> {
            if (imageUrl == null || imageUrl.isEmpty()) {
                Toast.makeText(requireContext(), 
                        getString(R.string.error_uploading_image), 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateDietaryPreferences() {
        // This will be called when chips are toggled
        // We'll save preferences when the user clicks the save button
    }
    
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(requireContext(), LoginActivity.class));
        requireActivity().finish();
    }
}
