package com.example.foodrecipe.ui.ingredients;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipe.R;
import com.example.foodrecipe.adapter.IngredientsAdapter;
import com.example.foodrecipe.adapter.RecipeAdapter;
import com.example.foodrecipe.model.Recipe;
import com.example.foodrecipe.model.User;
import com.example.foodrecipe.ui.recipedetail.RecipeDetailActivity;
import com.example.foodrecipe.viewmodel.RecipeViewModel;
import com.example.foodrecipe.viewmodel.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment for managing user's ingredients and suggesting recipes based on them
 */
public class IngredientsFragment extends Fragment {
    
    private UserViewModel userViewModel;
    private RecipeViewModel recipeViewModel;
    private RecyclerView ingredientsRecyclerView;
    private RecyclerView suggestedRecipesRecyclerView;
    private Button addIngredientButton;
    
    private Map<String, List<String>> userIngredients = new HashMap<>();
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);
        
        // Initialize ViewModels
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        
        // Initialize UI components
        initializeViews(view);
        
        // Setup listeners
        setupListeners();
        
        // Load user ingredients
        loadUserData();
        
        return view;
    }
    
    private void initializeViews(View view) {
        ingredientsRecyclerView = view.findViewById(R.id.recycler_view_ingredients);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        suggestedRecipesRecyclerView = view.findViewById(R.id.recycler_view_suggested_recipes);
        suggestedRecipesRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        
        addIngredientButton = view.findViewById(R.id.button_add_ingredient);
    }
    
    private void setupListeners() {
        addIngredientButton.setOnClickListener(v -> showAddIngredientDialog());
    }
    
    private void loadUserData() {
        userViewModel.getUserData().observe(getViewLifecycleOwner(), user -> {
            if (user != null && user.getUserIngredients() != null) {
                userIngredients = user.getUserIngredients();
                updateIngredientsAdapter();
                loadSuggestedRecipes();
            } else {
                userIngredients = new HashMap<>();
                updateIngredientsAdapter();
            }
        });
    }
    
    private void updateIngredientsAdapter() {
        // Create a flat list of ingredients for the adapter
        List<IngredientItem> ingredientItems = new ArrayList<>();
        
        for (Map.Entry<String, List<String>> entry : userIngredients.entrySet()) {
            String category = entry.getKey();
            List<String> ingredients = entry.getValue();
            
            for (String ingredient : ingredients) {
                ingredientItems.add(new IngredientItem(category, ingredient));
            }
        }
        
        // Set adapter for ingredients
        IngredientsAdapter adapter = new IngredientsAdapter(ingredientItems, this::removeIngredient);
        ingredientsRecyclerView.setAdapter(adapter);
    }
    
    private void loadSuggestedRecipes() {
        // Get list of all user ingredients for filtering
        List<String> allIngredients = new ArrayList<>();
        for (List<String> ingredients : userIngredients.values()) {
            allIngredients.addAll(ingredients);
        }
        
        if (!allIngredients.isEmpty()) {
            recipeViewModel.getAllRecipes().observe(getViewLifecycleOwner(), recipes -> {
                // Simple filtering logic - recipes that contain at least one user ingredient
                List<Recipe> suggested = new ArrayList<>();
                
                for (Recipe recipe : recipes) {
                    List<String> recipeIngredientNames = new ArrayList<>();
                    if (recipe.getIngredients() != null) {
                        for (Recipe.Ingredient ingredient : recipe.getIngredients()) {
                            recipeIngredientNames.add(ingredient.getName().toLowerCase());
                        }
                    }
                    
                    // Check if any user ingredient is in this recipe
                    boolean hasMatchingIngredient = false;
                    for (String userIngredient : allIngredients) {
                        if (recipeIngredientNames.contains(userIngredient.toLowerCase())) {
                            hasMatchingIngredient = true;
                            break;
                        }
                    }
                    
                    if (hasMatchingIngredient) {
                        suggested.add(recipe);
                    }
                }
                
                // Set adapter for suggested recipes
                if (!suggested.isEmpty()) {
                    RecipeAdapter adapter = new RecipeAdapter(requireContext(), suggested, recipe -> {
                        // Open recipe detail when clicked
                        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
                        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
                        startActivity(intent);
                    });
                    suggestedRecipesRecyclerView.setAdapter(adapter);
                    suggestedRecipesRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    suggestedRecipesRecyclerView.setVisibility(View.GONE);
                }
            });
        } else {
            suggestedRecipesRecyclerView.setVisibility(View.GONE);
        }
    }
    
    private void showAddIngredientDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_ingredient, null);
        
        Spinner categorySpinner = dialogView.findViewById(R.id.spinner_category);
        EditText ingredientEditText = dialogView.findViewById(R.id.edit_text_ingredient);
        Button addButton = dialogView.findViewById(R.id.button_add);
        Button cancelButton = dialogView.findViewById(R.id.button_cancel);
        
        // Set up category spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.ingredient_categories,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        
        // Set up button listeners
        addButton.setOnClickListener(v -> {
            String category = categorySpinner.getSelectedItem().toString();
            String ingredient = ingredientEditText.getText().toString().trim();
            
            if (!ingredient.isEmpty()) {
                addIngredient(category, ingredient);
                dialog.dismiss();
            } else {
                ingredientEditText.setError(getString(R.string.error_ingredient_required));
            }
        });
        
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        
        dialog.setContentView(dialogView);
        dialog.show();
    }
    
    private void addIngredient(String category, String ingredient) {
        userViewModel.updateUserIngredients(category, ingredient, true)
                .observe(getViewLifecycleOwner(), success -> {
                    if (success != null && success) {
                        Toast.makeText(requireContext(), 
                                getString(R.string.ingredient_added), 
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void removeIngredient(String category, String ingredient) {
        userViewModel.updateUserIngredients(category, ingredient, false)
                .observe(getViewLifecycleOwner(), success -> {
                    if (success != null && success) {
                        Toast.makeText(requireContext(), 
                                getString(R.string.ingredient_removed), 
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    /**
     * Helper class to hold ingredient data for the adapter
     */
    public static class IngredientItem {
        private String category;
        private String name;
        
        public IngredientItem(String category, String name) {
            this.category = category;
            this.name = name;
        }
        
        public String getCategory() {
            return category;
        }
        
        public String getName() {
            return name;
        }
    }
}
