package com.example.foodrecipe.ui.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipe.R;
import com.example.foodrecipe.adapter.RecipeAdapter;
import com.example.foodrecipe.model.Recipe;
import com.example.foodrecipe.ui.recipedetail.RecipeDetailActivity;
import com.example.foodrecipe.viewmodel.RecipeViewModel;
import com.example.foodrecipe.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Fragment for displaying user's favorite recipes
 */
public class FavoritesFragment extends Fragment {
    
    private RecipeViewModel recipeViewModel;
    private UserViewModel userViewModel;
    private RecyclerView favoritesRecyclerView;
    private TextView emptyStateTextView;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        
        // Initialize ViewModels
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        
        // Initialize UI components
        initializeViews(view);
        
        // Load favorite recipes
        loadFavoriteRecipes();
        
        return view;
    }
    
    private void initializeViews(View view) {
        favoritesRecyclerView = view.findViewById(R.id.recycler_view_favorites);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        emptyStateTextView = view.findViewById(R.id.text_empty_state);
    }
    
    private void loadFavoriteRecipes() {
        // Get current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null 
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        
        if (userId != null) {
            recipeViewModel.getFavoriteRecipes(userId).observe(getViewLifecycleOwner(), recipes -> {
                if (recipes != null && !recipes.isEmpty()) {
                    // Show recipes
                    favoritesRecyclerView.setVisibility(View.VISIBLE);
                    emptyStateTextView.setVisibility(View.GONE);
                    
                    // Set adapter for favorites
                    RecipeAdapter adapter = new RecipeAdapter(recipes, recipe -> {
                        // Open recipe detail activity when a recipe is clicked
                        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
                        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
                        startActivity(intent);
                    });
                    favoritesRecyclerView.setAdapter(adapter);
                } else {
                    // Show empty state
                    favoritesRecyclerView.setVisibility(View.GONE);
                    emptyStateTextView.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
