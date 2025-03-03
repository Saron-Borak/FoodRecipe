package com.example.foodrecipe.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodrecipe.MainActivity;
import com.example.foodrecipe.R;
import com.example.foodrecipe.databinding.ActivityLoginBinding;
import com.example.foodrecipe.repository.UserRepository;
import com.example.foodrecipe.viewmodel.UserViewModel;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    
    private ActivityLoginBinding binding;
    private UserViewModel userViewModel;
    private FirebaseAuth mAuth;
    
    // For phone auth
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        
        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        
        // Set up click listeners
        setupClickListeners();
        
        // Set up phone auth callbacks
        setupPhoneAuthCallbacks();
        
        // Check if user is already signed in
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
    
    private void setupClickListeners() {
        // Login button
        binding.buttonLogin.setOnClickListener(v -> {
            String email = binding.editTextEmail.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();
            
            if (validateEmailPassword(email, password)) {
                loginWithEmail(email, password);
            }
        });
        
        // Register link
        binding.textViewRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
        
        // Switch to phone auth
        binding.textViewPhoneAuth.setOnClickListener(v -> {
            toggleLoginMethod(true);
        });
        
        // Switch to email auth
        binding.textViewEmailAuth.setOnClickListener(v -> {
            toggleLoginMethod(false);
        });
        
        // Phone verification button
        binding.buttonSendVerification.setOnClickListener(v -> {
            String phoneNumber = binding.editTextPhone.getText().toString().trim();
            
            if (validatePhoneNumber(phoneNumber)) {
                startPhoneVerification(phoneNumber);
            }
        });
        
        // OTP verification button
        binding.buttonVerifyOtp.setOnClickListener(v -> {
            String otp = binding.editTextOtp.getText().toString().trim();
            
            if (validateOtp(otp)) {
                verifyOtp(otp);
            }
        });
        
        // Resend OTP button
        binding.buttonResendOtp.setOnClickListener(v -> {
            String phoneNumber = binding.editTextPhone.getText().toString().trim();
            
            if (validatePhoneNumber(phoneNumber)) {
                resendVerificationCode(phoneNumber, mResendToken);
            }
        });
    }
    
    private void setupPhoneAuthCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: " + phoneAuthCredential);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }
            
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(LoginActivity.this, "Verification failed: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                
                binding.progressBar.setVisibility(View.GONE);
                binding.buttonSendVerification.setEnabled(true);
            }
            
            @Override
            public void onCodeSent(@NonNull String verificationId, 
                                  @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent: " + verificationId);
                
                // Save verification ID and resending token
                mVerificationId = verificationId;
                mResendToken = token;
                
                // Show OTP fields
                binding.phoneNumberLayout.setVisibility(View.GONE);
                binding.otpLayout.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                
                Toast.makeText(LoginActivity.this, "Verification code sent", 
                        Toast.LENGTH_SHORT).show();
            }
        };
    }
    
    private void toggleLoginMethod(boolean usePhone) {
        if (usePhone) {
            binding.emailPasswordLayout.setVisibility(View.GONE);
            binding.phoneAuthLayout.setVisibility(View.VISIBLE);
            binding.phoneNumberLayout.setVisibility(View.VISIBLE);
            binding.otpLayout.setVisibility(View.GONE);
        } else {
            binding.emailPasswordLayout.setVisibility(View.VISIBLE);
            binding.phoneAuthLayout.setVisibility(View.GONE);
        }
    }
    
    private boolean validateEmailPassword(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            binding.editTextEmail.setError("Email is required");
            return false;
        }
        
        if (TextUtils.isEmpty(password)) {
            binding.editTextPassword.setError("Password is required");
            return false;
        }
        
        return true;
    }
    
    private boolean validatePhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.editTextPhone.setError("Phone number is required");
            return false;
        }
        
        // Add more validation as needed
        
        return true;
    }
    
    private boolean validateOtp(String otp) {
        if (TextUtils.isEmpty(otp)) {
            binding.editTextOtp.setError("OTP is required");
            return false;
        }
        
        if (otp.length() < 6) {
            binding.editTextOtp.setError("OTP must be 6 digits");
            return false;
        }
        
        return true;
    }
    
    private void loginWithEmail(String email, String password) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonLogin.setEnabled(false);
        
        userViewModel.signInWithEmailAndPassword(email, password).observe(this, firebaseUser -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.buttonLogin.setEnabled(true);
            
            if (firebaseUser != null) {
                // Login successful
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                // Login failed
                Toast.makeText(LoginActivity.this, "Authentication failed.", 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void startPhoneVerification(String phoneNumber) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonSendVerification.setEnabled(false);
        
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    
    private void verifyOtp(String code) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonVerifyOtp.setEnabled(false);
        
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
    
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonResendOtp.setEnabled(false);
        
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        // Use default name for first-time users
        String defaultName = "User"; // This could be changed later in profile settings
        
        userViewModel.signInWithPhoneCredential(credential, defaultName).observe(this, firebaseUser -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.buttonVerifyOtp.setEnabled(true);
            binding.buttonResendOtp.setEnabled(true);
            
            if (firebaseUser != null) {
                // Login successful
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                // Login failed
                Toast.makeText(LoginActivity.this, "Phone authentication failed.", 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
