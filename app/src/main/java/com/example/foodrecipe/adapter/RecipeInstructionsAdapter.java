package com.example.foodrecipe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipe.R;

import java.util.List;

/**
 * Adapter for displaying recipe instructions in a RecyclerView
 */
public class RecipeInstructionsAdapter extends RecyclerView.Adapter<RecipeInstructionsAdapter.InstructionViewHolder> {
    
    private final List<String> instructions;
    
    public RecipeInstructionsAdapter(List<String> instructions) {
        this.instructions = instructions;
    }
    
    @NonNull
    @Override
    public InstructionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_instruction, parent, false);
        return new InstructionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull InstructionViewHolder holder, int position) {
        String instruction = instructions.get(position);
        holder.bind(position + 1, instruction);
    }
    
    @Override
    public int getItemCount() {
        return instructions != null ? instructions.size() : 0;
    }
    
    /**
     * Updates the adapter data
     */
    public void updateData(List<String> newInstructions) {
        instructions.clear();
        if (newInstructions != null) {
            instructions.addAll(newInstructions);
        }
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder for instruction items
     */
    public static class InstructionViewHolder extends RecyclerView.ViewHolder {
        private final TextView stepNumberTextView;
        private final TextView instructionTextView;
        
        public InstructionViewHolder(@NonNull View itemView) {
            super(itemView);
            stepNumberTextView = itemView.findViewById(R.id.text_step_number);
            instructionTextView = itemView.findViewById(R.id.text_instruction);
        }
        
        public void bind(int stepNumber, String instruction) {
            stepNumberTextView.setText(String.format("Step %d", stepNumber));
            instructionTextView.setText(instruction);
        }
    }
}
