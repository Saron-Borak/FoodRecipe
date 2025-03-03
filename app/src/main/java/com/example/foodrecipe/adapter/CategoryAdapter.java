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
import com.example.foodrecipe.model.Category;

import java.util.List;

/**
 * Adapter for displaying recipe categories in a RecyclerView
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    
    private final List<Category> categories;
    private final OnCategoryClickListener listener;
    
    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category, listener);
    }
    
    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }
    
    /**
     * Updates the adapter data
     */
    public void updateData(List<Category> newCategories) {
        categories.clear();
        if (newCategories != null) {
            categories.addAll(newCategories);
        }
        notifyDataSetChanged();
    }
    
    /**
     * Interface for handling category clicks
     */
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
    
    /**
     * ViewHolder for Category items
     */
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView categoryImageView;
        private final TextView categoryNameTextView;
        
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImageView = itemView.findViewById(R.id.image_category);
            categoryNameTextView = itemView.findViewById(R.id.text_category_name);
        }
        
        public void bind(Category category, OnCategoryClickListener listener) {
            categoryNameTextView.setText(category.getName());
            
            // Load image using Glide
            if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(category.getImageUrl())
                        .placeholder(R.drawable.ic_category_placeholder)
                        .error(R.drawable.ic_category_placeholder)
                        .centerCrop()
                        .into(categoryImageView);
            } else {
                categoryImageView.setImageResource(R.drawable.ic_category_placeholder);
            }
            
            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }
    }
}
