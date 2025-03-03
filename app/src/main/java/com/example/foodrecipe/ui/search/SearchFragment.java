package com.example.foodrecipe.ui.search;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipe.R;
import com.example.foodrecipe.adapter.RecipeAdapter;
import com.example.foodrecipe.model.Recipe;
import com.example.foodrecipe.ui.recipedetail.RecipeDetailActivity;
import com.example.foodrecipe.viewmodel.RecipeViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for searching and filtering recipes
 */
public class SearchFragment extends Fragment {
    
    private RecipeViewModel recipeViewModel;
    private EditText searchEditText;
    private ImageButton searchButton;
    private ChipGroup categoryChipGroup;
    private Button filterTimeButton;
    private Button filterServingButton;
    private Button applyFiltersButton;
    private Button clearFiltersButton;
    private RecyclerView searchResultsRecyclerView;
    private RecipeAdapter recipeAdapter;
    
    // Filter values
    private int maxCookingTime = 120; // default 2 hours max
    private int servingSize = 4; // default 4 servings
    private List<String> selectedCategories = new ArrayList<>();
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        
        // Initialize ViewModel
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        
        // Initialize UI components
        initializeViews(view);
        
        // Setup listeners
        setupListeners();
        
        return view;
    }
    
    private void initializeViews(View view) {
        searchEditText = view.findViewById(R.id.edit_text_search);
        searchButton = view.findViewById(R.id.button_search);
        categoryChipGroup = view.findViewById(R.id.chip_group_category);
        filterTimeButton = view.findViewById(R.id.button_filter_time);
        filterServingButton = view.findViewById(R.id.button_filter_serving);
        applyFiltersButton = view.findViewById(R.id.button_apply_filters);
        clearFiltersButton = view.findViewById(R.id.button_clear_filters);
        searchResultsRecyclerView = view.findViewById(R.id.recycler_view_search_results);
        
        // Set up RecyclerView with a grid layout
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        
        // Initialize recipe adapter with empty list
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            // Open recipe detail activity when a recipe is clicked
            Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        });
        searchResultsRecyclerView.setAdapter(recipeAdapter);
        
        // Initialize category chips
        setupCategoryChips();
        
        // Load all recipes initially
        loadAllRecipes();
    }
    
    private void setupCategoryChips() {
        // Add category chips dynamically
        String[] categories = {
                getString(R.string.category_breakfast),
                getString(R.string.category_lunch),
                getString(R.string.category_dinner),
                getString(R.string.category_dessert),
                getString(R.string.category_snack),
                getString(R.string.category_drink)
        };
        
        for (String category : categories) {
            Chip chip = new Chip(requireContext());
            chip.setText(category);
            chip.setCheckable(true);
            categoryChipGroup.addView(chip);
        }
    }
    
    private void setupListeners() {
        // Search button click
        searchButton.setOnClickListener(v -> performSearch());
        
        // Apply filters button
        applyFiltersButton.setOnClickListener(v -> applyFilters());
        
        // Clear filters button
        clearFiltersButton.setOnClickListener(v -> clearFilters());
        
        // Time filter button
        filterTimeButton.setOnClickListener(v -> showTimeFilterDialog());
        
        // Serving filter button
        filterServingButton.setOnClickListener(v -> showServingFilterDialog());
        
        // Category chip selection listener
        categoryChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            selectedCategories.clear();
            for (int id : checkedIds) {
                Chip chip = group.findViewById(id);
                if (chip != null) {
                    selectedCategories.add(chip.getText().toString());
                }
            }
        });
    }
    
    private void loadAllRecipes() {
        recipeViewModel.getAllRecipes().observe(
                getViewLifecycleOwner(),
                recipes -> {
                    if (recipes != null && !recipes.isEmpty()) {
                        recipeAdapter.updateData(recipes);
                        searchResultsRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        searchResultsRecyclerView.setVisibility(View.GONE);
                    }
                });
    }
    
    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            recipeViewModel.searchRecipesByName(query).observe(
                    getViewLifecycleOwner(),
                    recipes -> {
                        if (recipes != null && !recipes.isEmpty()) {
                            recipeAdapter.updateData(recipes);
                            searchResultsRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            recipeAdapter.updateData(new ArrayList<>());
                            searchResultsRecyclerView.setVisibility(View.GONE);
                        }
                    });
        }
    }
    
    private void applyFilters() {
        // Get all recipes and apply filters manually
        recipeViewModel.getAllRecipes().observe(
                getViewLifecycleOwner(),
                allRecipes -> {
                    if (allRecipes != null && !allRecipes.isEmpty()) {
                        List<Recipe> filteredRecipes = new ArrayList<>();
                        
                        // Apply filtering logic
                        for (Recipe recipe : allRecipes) {
                            boolean passesTimeFilter = recipe.getCookingTimeMinutes() <= maxCookingTime;
                            boolean passesServingFilter = recipe.getServingSize() == servingSize;
                            boolean passesCategoryFilter = selectedCategories.isEmpty() || 
                                    selectedCategories.contains(recipe.getCategory());
                            
                            if (passesTimeFilter && passesServingFilter && passesCategoryFilter) {
                                filteredRecipes.add(recipe);
                            }
                        }
                        
                        // Update the adapter with filtered results
                        if (!filteredRecipes.isEmpty()) {
                            recipeAdapter.updateData(filteredRecipes);
                            searchResultsRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            recipeAdapter.updateData(new ArrayList<>());
                            searchResultsRecyclerView.setVisibility(View.GONE);
                        }
                    }
                });
    }
    
    private void clearFilters() {
        // Clear all filter selections
        searchEditText.setText("");
        categoryChipGroup.clearCheck();
        maxCookingTime = 120;
        servingSize = 4;
        selectedCategories.clear();
        
        // Update filter button texts
        filterTimeButton.setText(getString(R.string.cooking_time_filter, maxCookingTime));
        filterServingButton.setText(getString(R.string.serving_size_filter, servingSize));
        
        // Reset to show all recipes
        loadAllRecipes();
    }
    
    private void showTimeFilterDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_time_filter, null);
        
        SeekBar seekBar = dialogView.findViewById(R.id.seekbar_time);
        TextView valueTextView = dialogView.findViewById(R.id.text_time_value);
        
        // Set initial value
        seekBar.setProgress(maxCookingTime);
        valueTextView.setText(getString(R.string.minutes_value, maxCookingTime));
        
        // Setup seek bar listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Ensure minimum of 10 minutes
                int value = Math.max(10, progress);
                valueTextView.setText(getString(R.string.minutes_value, value));
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.cooking_time_filter_title)
                .setView(dialogView)
                .setPositiveButton(R.string.apply, (dialog, which) -> {
                    maxCookingTime = Math.max(10, seekBar.getProgress());
                    filterTimeButton.setText(getString(R.string.cooking_time_filter, maxCookingTime));
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
    
    private void showServingFilterDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_serving_filter, null);
        
        SeekBar seekBar = dialogView.findViewById(R.id.seekbar_serving);
        TextView valueTextView = dialogView.findViewById(R.id.text_serving_value);
        
        // Set initial value (within range 1-12)
        seekBar.setMax(12);
        seekBar.setProgress(servingSize);
        valueTextView.setText(String.valueOf(servingSize));
        
        // Setup seek bar listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Ensure minimum of 1 serving
                int value = Math.max(1, progress);
                valueTextView.setText(String.valueOf(value));
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.serving_size_filter_title)
                .setView(dialogView)
                .setPositiveButton(R.string.apply, (dialog, which) -> {
                    servingSize = Math.max(1, seekBar.getProgress());
                    filterServingButton.setText(getString(R.string.serving_size_filter, servingSize));
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
