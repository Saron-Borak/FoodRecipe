package com.example.foodrecipe.ui.recipe.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.foodrecipe.R;
import com.google.android.material.textfield.TextInputEditText;

public class AddInstructionDialog extends Dialog {

    public interface OnInstructionAddedListener {
        void onInstructionAdded(String instruction);
    }

    private final OnInstructionAddedListener listener;
    private TextInputEditText instructionEditText;
    private Button addButton;
    private Button cancelButton;

    public AddInstructionDialog(@NonNull Context context, OnInstructionAddedListener listener) {
        super(context);
        this.listener = listener;
        
        // Remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // Set the layout
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_instruction, null);
        setContentView(view);
        
        // Initialize views
        instructionEditText = view.findViewById(R.id.edit_text_instruction);
        addButton = view.findViewById(R.id.button_add);
        cancelButton = view.findViewById(R.id.button_cancel);
        
        // Setup listeners
        setupListeners();
    }

    private void setupListeners() {
        // Cancel button
        cancelButton.setOnClickListener(v -> dismiss());
        
        // Add button
        addButton.setOnClickListener(v -> {
            String instruction = instructionEditText.getText().toString().trim();
            
            // Validate instruction
            if (TextUtils.isEmpty(instruction)) {
                instructionEditText.setError(getContext().getString(R.string.required_field));
                return;
            }
            
            // Notify listener
            if (listener != null) {
                listener.onInstructionAdded(instruction);
            }
            
            // Dismiss dialog
            dismiss();
        });
    }
}
