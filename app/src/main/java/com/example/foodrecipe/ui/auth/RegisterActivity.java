package com.example.foodrecipe.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodrecipe.MainActivity;
import com.example.foodrecipe.R;
import com.example.foodrecipe.databinding.ActivityRegisterBinding;
import com.example.foodrecipe.viewmodel.UserViewModel;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    
    private ActivityRegisterBinding binding;
    private UserViewModel userViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        
        // Set up click listeners
        setupClickListeners();
    }
    
    private void setupClickListeners() {
        // Register button
        binding.buttonRegister.setOnClickListener(v -> {
            String name = binding.editTextName.getText().toString().trim();
            String email = binding.editTextEmail.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();
            String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();
            
            if (validateInput(name, email, password, confirmPassword)) {
                registerUser(name, email, password);
            }
        });
        
        // Login link
        binding.textViewLogin.setOnClickListener(v -> {
            finish(); // Go back to login screen
        });
    }
    
    private boolean validateInput(String name, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            binding.editTextName.setError("Name is required");
            return false;
        }
        
        if (TextUtils.isEmpty(email)) {
            binding.editTextEmail.setError("Email is required");
            return false;
        }
        
        if (TextUtils.isEmpty(password)) {
            binding.editTextPassword.setError("Password is required");
            return false;
        }
        
        if (password.length() < 6) {
            binding.editTextPassword.setError("Password must be at least 6 characters");
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            binding.editTextConfirmPassword.setError("Passwords do not match");
            return false;
        }
        
        return true;
    }
    
    private void registerUser(String name, String email, String password) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonRegister.setEnabled(false);
        
        userViewModel.createUserWithEmailAndPassword(email, password, name).observe(this, firebaseUser -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.buttonRegister.setEnabled(true);
            
            if (firebaseUser != null) {
                // Registration successful
                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            } else {
                // Registration failed
                Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
