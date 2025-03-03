package com.example.foodrecipe.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipe.R;
import com.example.foodrecipe.adapter.CategoryAdapter;
import com.example.foodrecipe.adapter.FeaturedRecipeAdapter;
import com.example.foodrecipe.adapter.RecipeAdapter;
import com.example.foodrecipe.model.Category;
import com.example.foodrecipe.model.Recipe;
import com.example.foodrecipe.ui.recipe.AddRecipeActivity;
import com.example.foodrecipe.ui.recipedetail.RecipeDetailActivity;
import com.example.foodrecipe.viewmodel.RecipeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for the Home screen showing different recipe categories and recommendations
 */
public class HomeFragment extends Fragment implements 
        FeaturedRecipeAdapter.OnFeaturedRecipeClickListener,
        CategoryAdapter.OnCategoryClickListener,
        RecipeAdapter.OnRecipeClickListener {
    
    private RecipeViewModel recipeViewModel;
    private RecyclerView featuredRecyclerView;
    private RecyclerView categoriesRecyclerView;
    private RecyclerView recentRecyclerView;
    private FloatingActionButton fabAddRecipe;
    
    private FeaturedRecipeAdapter featuredRecipeAdapter;
    private CategoryAdapter categoryAdapter;
    private RecipeAdapter recentRecipeAdapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Initialize ViewModel
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        
        // Initialize UI components
        initializeViews(view);
        
        // Initialize adapters
        initializeAdapters();
        
        // Set up FAB click listener
        fabAddRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddRecipeActivity.class);
            startActivity(intent);
        });
        
        // Load data
        loadData();
        
        return view;
    }
    
    private void initializeViews(View view) {
        featuredRecyclerView = view.findViewById(R.id.recycler_view_featured);
        featuredRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        
        categoriesRecyclerView = view.findViewById(R.id.recycler_view_categories);
        categoriesRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        
        recentRecyclerView = view.findViewById(R.id.recycler_view_recent);
        recentRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext()));
                
        fabAddRecipe = view.findViewById(R.id.fab_add_recipe);
    }
    
    private void initializeAdapters() {
        // Initialize with empty lists and set listeners
        featuredRecipeAdapter = new FeaturedRecipeAdapter(new ArrayList<>(), this);
        featuredRecyclerView.setAdapter(featuredRecipeAdapter);
        
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this);
        categoriesRecyclerView.setAdapter(categoryAdapter);
        
        recentRecipeAdapter = new RecipeAdapter(new ArrayList<>(), this);
        recentRecyclerView.setAdapter(recentRecipeAdapter);
    }
    
    private void loadData() {
        // Load featured recipes
        recipeViewModel.getAllRecipes().observe(getViewLifecycleOwner(), recipes -> {
            if (recipes != null && !recipes.isEmpty()) {
                // For featured, just take the first few recipes
                List<Recipe> featuredRecipes = recipes.size() > 5 ? 
                        recipes.subList(0, 5) : new ArrayList<>(recipes);
                featuredRecipeAdapter.updateData(featuredRecipes);
                
                // For recent, show all recipes
                recentRecipeAdapter.updateData(recipes);
            }
        });
        
        // Load categories - This would typically come from the repository
        // but for now we'll create a static list
        categoryAdapter.updateData(getStaticCategories());
    }
    
    /**
     * Create a static list of categories for testing
     * In a real app, these would come from the backend
     */
    private List<Category> getStaticCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("breakfast", getString(R.string.category_breakfast), ""));
        categories.add(new Category("lunch", getString(R.string.category_lunch), ""));
        categories.add(new Category("dinner", getString(R.string.category_dinner), ""));
        categories.add(new Category("dessert", getString(R.string.category_dessert), ""));
        categories.add(new Category("snack", getString(R.string.category_snack), ""));
        categories.add(new Category("drink", getString(R.string.category_drink), ""));
        return categories;
    }
    
    /**
     * Handle featured recipe click
     */
    @Override
    public void onFeaturedRecipeClick(Recipe recipe) {
        navigateToRecipeDetail(recipe);
    }
    
    /**
     * Handle category click
     */
    @Override
    public void onCategoryClick(Category category) {
        // Load recipes for this category
        recipeViewModel.getRecipesByCategory(category.getId())
                .observe(getViewLifecycleOwner(), recipes -> {
                    // Could navigate to a category-specific view or update the current view
                    // For now, we'll just update the recent recipes list
                    if (recipes != null) {
                        recentRecipeAdapter.updateData(recipes);
                    }
                });
    }
    
    /**
     * Handle recipe click
     */
    @Override
    public void onRecipeClick(Recipe recipe) {
        navigateToRecipeDetail(recipe);
    }
    
    /**
     * Navigate to recipe detail screen
     */
    private void navigateToRecipeDetail(Recipe recipe) {
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
        intent.putExtra("recipe_id", recipe.getId());
        startActivity(intent);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Reload data when returning to this fragment (e.g., after adding a recipe)
        loadData();
    }
}
