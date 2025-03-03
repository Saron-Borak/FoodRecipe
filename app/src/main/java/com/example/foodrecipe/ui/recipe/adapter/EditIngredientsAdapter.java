package com.example.foodrecipe.ui.recipe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipe.R;
import com.example.foodrecipe.model.Recipe;

import java.util.List;

public class EditIngredientsAdapter extends RecyclerView.Adapter<EditIngredientsAdapter.IngredientViewHolder> {

    private List<Recipe.Ingredient> ingredients;
    private OnItemDeleteListener deleteListener;

    public interface OnItemDeleteListener {
        void onDeleteClick(int position);
    }

    public EditIngredientsAdapter(List<Recipe.Ingredient> ingredients, OnItemDeleteListener deleteListener) {
        this.ingredients = ingredients;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Recipe.Ingredient ingredient = ingredients.get(position);
        holder.nameTextView.setText(ingredient.getName());
        
        // Create details text (quantity + unit)
        StringBuilder details = new StringBuilder();
        
        if (ingredient.getQuantity() != null && !ingredient.getQuantity().isEmpty()) {
            details.append(ingredient.getQuantity());
        }
        
        if (ingredient.getUnit() != null && !ingredient.getUnit().isEmpty()) {
            if (details.length() > 0) {
                details.append(" ");
            }
            details.append(ingredient.getUnit());
        }
        
        if (ingredient.getCategory() != null && !ingredient.getCategory().isEmpty()) {
            if (details.length() > 0) {
                details.append(" • ");
            }
            details.append(ingredient.getCategory());
        }
        
        holder.detailTextView.setText(details.toString());
        
        // Set delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients != null ? ingredients.size() : 0;
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView detailTextView;
        ImageButton deleteButton;

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_ingredient_name);
            detailTextView = itemView.findViewById(R.id.text_ingredient_detail);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}
