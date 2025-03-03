package com.example.foodrecipe;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.foodrecipe.ui.favorites.FavoritesFragment;
import com.example.foodrecipe.ui.home.HomeFragment;
import com.example.foodrecipe.ui.ingredients.IngredientsFragment;
import com.example.foodrecipe.ui.profile.ProfileFragment;
import com.example.foodrecipe.ui.search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Check if user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // User not logged in, redirect to login
            // This is a safety check in case someone tries to access MainActivity directly
            finish();
            return;
        }
        
        // Initialize bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);
        
        // Set initial fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
        
        // Handle incoming deep links or notifications
        handleIntent();
    }
    
    private void handleIntent() {
        // Check if we were launched with a specific recipe ID
        if (getIntent().hasExtra("recipeId")) {
            String recipeId = getIntent().getStringExtra("recipeId");
            // TODO: Navigate to recipe detail fragment
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (itemId == R.id.nav_search) {
            selectedFragment = new SearchFragment();
        } else if (itemId == R.id.nav_favorites) {
            selectedFragment = new FavoritesFragment();
        } else if (itemId == R.id.nav_ingredients) {
            selectedFragment = new IngredientsFragment();
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        }
        
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        }
        
        return false;
    }
}