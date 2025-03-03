package com.example.foodrecipe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipe.R;
import com.example.foodrecipe.model.Recipe;

import java.util.List;

/**
 * Adapter for displaying recipe ingredients in a RecyclerView
 */
public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecipeIngredientsAdapter.IngredientViewHolder> {
    
    private final List<Recipe.Ingredient> ingredients;
    
    public RecipeIngredientsAdapter(List<Recipe.Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
    
    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Recipe.Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }
    
    @Override
    public int getItemCount() {
        return ingredients != null ? ingredients.size() : 0;
    }
    
    /**
     * Updates the adapter data
     */
    public void updateData(List<Recipe.Ingredient> newIngredients) {
        ingredients.clear();
        if (newIngredients != null) {
            ingredients.addAll(newIngredients);
        }
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder for ingredient items
     */
    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final TextView quantityTextView;
        private final TextView nameTextView;
        
        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            quantityTextView = itemView.findViewById(R.id.text_ingredient_quantity);
            nameTextView = itemView.findViewById(R.id.text_ingredient_name);
        }
        
        public void bind(Recipe.Ingredient ingredient) {
            String quantityText = ingredient.getQuantity();
            if (ingredient.getUnit() != null && !ingredient.getUnit().isEmpty()) {
                quantityText += " " + ingredient.getUnit();
            }
            quantityTextView.setText(quantityText);
            nameTextView.setText(ingredient.getName());
        }
    }
}
