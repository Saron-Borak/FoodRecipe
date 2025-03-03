package com.example.foodrecipe.ui.recipe.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.foodrecipe.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;

public class AddIngredientDialog extends Dialog {

    public interface OnIngredientAddedListener {
        void onIngredientAdded(String name, String quantity, String unit, String category);
    }

    private final OnIngredientAddedListener listener;
    private TextInputEditText ingredientEditText;
    private TextInputEditText quantityEditText;
    private TextInputEditText unitEditText;
    private Spinner categorySpinner;
    private Button addButton;
    private Button cancelButton;

    public AddIngredientDialog(@NonNull Context context, OnIngredientAddedListener listener) {
        super(context);
        this.listener = listener;
        
        // Remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // Set the layout
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_recipe_ingredient, null);
        setContentView(view);
        
        // Initialize views
        ingredientEditText = view.findViewById(R.id.edit_text_ingredient);
        quantityEditText = view.findViewById(R.id.edit_text_quantity);
        unitEditText = view.findViewById(R.id.edit_text_unit);
        categorySpinner = view.findViewById(R.id.spinner_category);
        addButton = view.findViewById(R.id.button_add);
        cancelButton = view.findViewById(R.id.button_cancel);
        
        // Setup category spinner
        setupCategorySpinner(context);
        
        // Setup listeners
        setupListeners();
    }

    private void setupCategorySpinner(Context context) {
        List<String> categories = Arrays.asList(
            context.getString(R.string.category_meat),
            context.getString(R.string.category_seafood),
            context.getString(R.string.category_vegetable),
            context.getString(R.string.category_fruit),
            context.getString(R.string.category_dairy),
            context.getString(R.string.category_grain),
            context.getString(R.string.category_herb),
            context.getString(R.string.category_spice),
            context.getString(R.string.category_sauce),
            context.getString(R.string.category_other)
        );
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            context, 
            android.R.layout.simple_spinner_item, 
            categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        // Cancel button
        cancelButton.setOnClickListener(v -> dismiss());
        
        // Add button
        addButton.setOnClickListener(v -> {
            String name = ingredientEditText.getText().toString().trim();
            String quantity = quantityEditText.getText().toString().trim();
            String unit = unitEditText.getText().toString().trim();
            String category = categorySpinner.getSelectedItem().toString();
            
            // Validate name
            if (TextUtils.isEmpty(name)) {
                ingredientEditText.setError(getContext().getString(R.string.required_field));
                return;
            }
            
            // Validate quantity
            if (TextUtils.isEmpty(quantity)) {
                quantityEditText.setError(getContext().getString(R.string.required_field));
                return;
            }
            
            // Notify listener
            if (listener != null) {
                listener.onIngredientAdded(name, quantity, unit, category);
            }
            
            // Dismiss dialog
            dismiss();
        });
    }
}
