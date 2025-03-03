package com.example.foodrecipe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodrecipe.R;
import com.example.foodrecipe.model.Recipe;

import java.util.List;

/**
 * Adapter for displaying recipes in a RecyclerView
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    
    private final List<Recipe> recipes;
    private final Context context;
    private final OnRecipeClickListener listener;
    
    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
        this.context = null;
    }
    
    public RecipeAdapter(Context context, List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.context = context;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe, listener);
    }
    
    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }
    
    /**
     * Updates the adapter data
     */
    public void updateData(List<Recipe> newRecipes) {
        recipes.clear();
        if (newRecipes != null) {
            recipes.addAll(newRecipes);
        }
        notifyDataSetChanged();
    }
    
    /**
     * Interface for handling recipe clicks
     */
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }
    
    /**
     * ViewHolder for Recipe items
     */
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView recipeImageView;
        private final TextView recipeNameTextView;
        private final TextView cookingTimeTextView;
        private final TextView servingSizeTextView;
        
        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImageView = itemView.findViewById(R.id.image_recipe);
            recipeNameTextView = itemView.findViewById(R.id.text_recipe_name);
            cookingTimeTextView = itemView.findViewById(R.id.text_cooking_time);
            servingSizeTextView = itemView.findViewById(R.id.text_serving_size);
        }
        
        public void bind(Recipe recipe, OnRecipeClickListener listener) {
            recipeNameTextView.setText(recipe.getName());
            cookingTimeTextView.setText(String.format("%d min", recipe.getCookingTimeMinutes()));
            servingSizeTextView.setText(String.format("%d servings", recipe.getServingSize()));
            
            // Load image using Glide
            if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(recipe.getImageUrl())
                        .placeholder(R.drawable.ic_recipe_placeholder)
                        .error(R.drawable.ic_recipe_placeholder)
                        .centerCrop()
                        .into(recipeImageView);
            } else {
                recipeImageView.setImageResource(R.drawable.ic_recipe_placeholder);
            }
            
            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRecipeClick(recipe);
                }
            });
        }
    }
}
