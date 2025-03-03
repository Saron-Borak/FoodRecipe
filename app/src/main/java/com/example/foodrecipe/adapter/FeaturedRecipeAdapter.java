package com.example.foodrecipe.adapter;

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
 * Adapter for displaying featured recipes in a RecyclerView
 */
public class FeaturedRecipeAdapter extends RecyclerView.Adapter<FeaturedRecipeAdapter.FeaturedRecipeViewHolder> {
    
    private final List<Recipe> featuredRecipes;
    private final OnFeaturedRecipeClickListener listener;
    
    public FeaturedRecipeAdapter(List<Recipe> featuredRecipes, OnFeaturedRecipeClickListener listener) {
        this.featuredRecipes = featuredRecipes;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public FeaturedRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_featured, parent, false);
        return new FeaturedRecipeViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull FeaturedRecipeViewHolder holder, int position) {
        Recipe recipe = featuredRecipes.get(position);
        holder.bind(recipe, listener);
    }
    
    @Override
    public int getItemCount() {
        return featuredRecipes != null ? featuredRecipes.size() : 0;
    }
    
    /**
     * Updates the adapter data
     */
    public void updateData(List<Recipe> newRecipes) {
        featuredRecipes.clear();
        if (newRecipes != null) {
            featuredRecipes.addAll(newRecipes);
        }
        notifyDataSetChanged();
    }
    
    /**
     * Interface for handling featured recipe clicks
     */
    public interface OnFeaturedRecipeClickListener {
        void onFeaturedRecipeClick(Recipe recipe);
    }
    
    /**
     * ViewHolder for Featured Recipe items
     */
    public static class FeaturedRecipeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView recipeImageView;
        private final TextView recipeNameTextView;
        private final TextView recipeDescriptionTextView;
        private final TextView cookingTimeTextView;
        private final TextView servingSizeTextView;
        
        public FeaturedRecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImageView = itemView.findViewById(R.id.image_recipe);
            recipeNameTextView = itemView.findViewById(R.id.text_recipe_name);
            recipeDescriptionTextView = itemView.findViewById(R.id.text_recipe_description);
            cookingTimeTextView = itemView.findViewById(R.id.text_cooking_time);
            servingSizeTextView = itemView.findViewById(R.id.text_serving_size);
        }
        
        public void bind(Recipe recipe, OnFeaturedRecipeClickListener listener) {
            recipeNameTextView.setText(recipe.getName());
            
            // Truncate description if it's too long
            String description = recipe.getDescription();
            if (description != null && description.length() > 100) {
                description = description.substring(0, 97) + "...";
            }
            recipeDescriptionTextView.setText(description);
            
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
                    listener.onFeaturedRecipeClick(recipe);
                }
            });
        }
    }
}
