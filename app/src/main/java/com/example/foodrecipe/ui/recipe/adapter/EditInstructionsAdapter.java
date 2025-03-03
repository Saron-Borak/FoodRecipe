package com.example.foodrecipe.ui.recipe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipe.R;

import java.util.List;

public class EditInstructionsAdapter extends RecyclerView.Adapter<EditInstructionsAdapter.InstructionViewHolder> {

    private List<String> instructions;
    private OnItemDeleteListener deleteListener;

    public interface OnItemDeleteListener {
        void onDeleteClick(int position);
    }

    public EditInstructionsAdapter(List<String> instructions, OnItemDeleteListener deleteListener) {
        this.instructions = instructions;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public InstructionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit_instruction, parent, false);
        return new InstructionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionViewHolder holder, int position) {
        String instruction = instructions.get(position);
        
        // Set step number (1-based)
        holder.stepNumberTextView.setText(String.valueOf(position + 1));
        
        // Set instruction text
        holder.instructionTextView.setText(instruction);
        
        // Set delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return instructions != null ? instructions.size() : 0;
    }

    static class InstructionViewHolder extends RecyclerView.ViewHolder {
        TextView stepNumberTextView;
        TextView instructionTextView;
        ImageButton deleteButton;

        InstructionViewHolder(@NonNull View itemView) {
            super(itemView);
            stepNumberTextView = itemView.findViewById(R.id.text_step_number);
            instructionTextView = itemView.findViewById(R.id.text_instruction);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}
