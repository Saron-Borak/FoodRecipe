package com.example.foodrecipe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipe.R;
import com.example.foodrecipe.ui.ingredients.IngredientsFragment;

import java.util.List;

/**
 * Adapter for displaying user's ingredients in a RecyclerView
 */
public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {
    
    private final List<IngredientsFragment.IngredientItem> ingredients;
    private final OnIngredientActionListener listener;
    
    public IngredientsAdapter(List<IngredientsFragment.IngredientItem> ingredients, OnIngredientActionListener listener) {
        this.ingredients = ingredients;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        IngredientsFragment.IngredientItem ingredient = ingredients.get(position);
        holder.bind(ingredient, listener);
    }
    
    @Override
    public int getItemCount() {
        return ingredients != null ? ingredients.size() : 0;
    }
    
    /**
     * Updates the adapter data
     */
    public void updateData(List<IngredientsFragment.IngredientItem> newIngredients) {
        ingredients.clear();
        if (newIngredients != null) {
            ingredients.addAll(newIngredients);
        }
        notifyDataSetChanged();
    }
    
    /**
     * Interface for handling ingredient actions like removal
     */
    public interface OnIngredientActionListener {
        void onRemoveIngredient(String category, String ingredient);
    }
    
    /**
     * ViewHolder for Ingredient items
     */
    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final TextView ingredientNameTextView;
        private final TextView categoryTextView;
        private final ImageButton removeButton;
        
        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientNameTextView = itemView.findViewById(R.id.text_ingredient_name);
            categoryTextView = itemView.findViewById(R.id.text_ingredient_category);
            removeButton = itemView.findViewById(R.id.button_remove);
        }
        
        public void bind(IngredientsFragment.IngredientItem ingredient, OnIngredientActionListener listener) {
            ingredientNameTextView.setText(ingredient.getName());
            categoryTextView.setText(ingredient.getCategory());
            
            // Set click listener for remove button
            removeButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveIngredient(ingredient.getCategory(), ingredient.getName());
                }
            });
        }
    }
}
