package com.example.foodrecipe.ui.recipe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipe.R;
import com.example.foodrecipe.model.Recipe;
import com.example.foodrecipe.ui.recipe.adapter.EditIngredientsAdapter;
import com.example.foodrecipe.ui.recipe.adapter.EditInstructionsAdapter;
import com.example.foodrecipe.ui.recipe.dialog.AddIngredientDialog;
import com.example.foodrecipe.ui.recipe.dialog.AddInstructionDialog;
import com.example.foodrecipe.util.FirebaseStorageHelper;
import com.example.foodrecipe.viewmodel.RecipeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AddRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextInputEditText recipeNameEditText;
    private TextInputEditText recipeDescriptionEditText;
    private TextInputEditText cookingTimeEditText;
    private TextInputEditText servingSizeEditText;
    private TextInputEditText videoIdEditText;
    private AutoCompleteTextView categoryAutoComplete;
    private ImageView recipeImageView;
    private Button uploadImageButton;
    private Button addIngredientButton;
    private Button addInstructionButton;
    private Button saveRecipeButton;
    private RecyclerView ingredientsRecyclerView;
    private RecyclerView instructionsRecyclerView;
    private FloatingActionButton fabAddRecipe;

    private EditIngredientsAdapter ingredientsAdapter;
    private EditInstructionsAdapter instructionsAdapter;
    
    private List<Recipe.Ingredient> ingredients = new ArrayList<>();
    private List<String> instructions = new ArrayList<>();
    
    private Uri selectedImageUri = null;
    private RecipeViewModel recipeViewModel;
    private FirebaseStorageHelper storageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        
        initializeViews();
        setupToolbar();
        setupViewModel();
        setupAdapters();
        setupCategoryDropdown();
        setupListeners();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        recipeNameEditText = findViewById(R.id.edit_recipe_name);
        recipeDescriptionEditText = findViewById(R.id.edit_recipe_description);
        cookingTimeEditText = findViewById(R.id.edit_cooking_time);
        servingSizeEditText = findViewById(R.id.edit_serving_size);
        videoIdEditText = findViewById(R.id.edit_video_id);
        categoryAutoComplete = findViewById(R.id.autoCompleteCategory);
        recipeImageView = findViewById(R.id.image_recipe);
        uploadImageButton = findViewById(R.id.button_upload_image);
        addIngredientButton = findViewById(R.id.button_add_ingredient);
        addInstructionButton = findViewById(R.id.button_add_instruction);
        saveRecipeButton = findViewById(R.id.button_save_recipe);
        ingredientsRecyclerView = findViewById(R.id.recycler_ingredients);
        instructionsRecyclerView = findViewById(R.id.recycler_instructions);
        fabAddRecipe = findViewById(R.id.fab_add_recipe);

        // Initialize Firebase Storage helper
        storageHelper = new FirebaseStorageHelper();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setupViewModel() {
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
    }

    private void setupAdapters() {
        // Setup ingredients adapter
        ingredientsAdapter = new EditIngredientsAdapter(ingredients, position -> {
            ingredients.remove(position);
            ingredientsAdapter.notifyItemRemoved(position);
        });
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);

        // Setup instructions adapter
        instructionsAdapter = new EditInstructionsAdapter(instructions, position -> {
            instructions.remove(position);
            instructionsAdapter.notifyItemRemoved(position);
            // Update step numbers
            for (int i = position; i < instructions.size(); i++) {
                instructionsAdapter.notifyItemChanged(i);
            }
        });
        instructionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        instructionsRecyclerView.setAdapter(instructionsAdapter);
    }

    private void setupCategoryDropdown() {
        String[] categories = {
            getString(R.string.category_breakfast),
            getString(R.string.category_lunch),
            getString(R.string.category_dinner),
            getString(R.string.category_dessert),
            getString(R.string.category_snack),
            getString(R.string.category_drink)
        };
        
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
            this, 
            android.R.layout.simple_dropdown_item_1line, 
            categories
        );
        
        categoryAutoComplete.setAdapter(categoryAdapter);
    }

    private void setupListeners() {
        // Upload image button
        uploadImageButton.setOnClickListener(v -> openImagePicker());
        
        // Add ingredient button
        addIngredientButton.setOnClickListener(v -> showAddIngredientDialog());
        
        // Add instruction button
        addInstructionButton.setOnClickListener(v -> showAddInstructionDialog());
        
        // Save recipe button
        saveRecipeButton.setOnClickListener(v -> validateAndSaveRecipe());
        
        // Floating action button
        fabAddRecipe.setOnClickListener(v -> validateAndSaveRecipe());
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Recipe Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Picasso.get().load(selectedImageUri).into(recipeImageView);
        }
    }

    private void showAddIngredientDialog() {
        AddIngredientDialog dialog = new AddIngredientDialog(this, (name, quantity, unit, category) -> {
            Recipe.Ingredient ingredient = new Recipe.Ingredient(name, quantity, unit, category);
            ingredients.add(ingredient);
            ingredientsAdapter.notifyItemInserted(ingredients.size() - 1);
        });
        dialog.show();
    }

    private void showAddInstructionDialog() {
        AddInstructionDialog dialog = new AddInstructionDialog(this, instruction -> {
            instructions.add(instruction);
            instructionsAdapter.notifyItemInserted(instructions.size() - 1);
        });
        dialog.show();
    }

    private boolean validateInputs() {
        boolean isValid = true;
        
        // Check recipe name
        if (TextUtils.isEmpty(recipeNameEditText.getText())) {
            recipeNameEditText.setError(getString(R.string.required_field));
            isValid = false;
        }
        
        // Check recipe description
        if (TextUtils.isEmpty(recipeDescriptionEditText.getText())) {
            recipeDescriptionEditText.setError(getString(R.string.required_field));
            isValid = false;
        }
        
        // Check cooking time
        if (TextUtils.isEmpty(cookingTimeEditText.getText())) {
            cookingTimeEditText.setError(getString(R.string.required_field));
            isValid = false;
        }
        
        // Check serving size
        if (TextUtils.isEmpty(servingSizeEditText.getText())) {
            servingSizeEditText.setError(getString(R.string.required_field));
            isValid = false;
        }
        
        // Check category
        if (TextUtils.isEmpty(categoryAutoComplete.getText())) {
            TextInputLayout dropdown = findViewById(R.id.dropdown_category);
            dropdown.setError(getString(R.string.required_field));
            isValid = false;
        }
        
        // Check if there are ingredients
        if (ingredients.isEmpty()) {
            Toast.makeText(this, "Please add at least one ingredient", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        // Check if there are instructions
        if (instructions.isEmpty()) {
            Toast.makeText(this, "Please add at least one instruction", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        return isValid;
    }

    private void validateAndSaveRecipe() {
        if (!validateInputs()) {
            return;
        }
        
        // Show loading indicator
        // progressBar.setVisibility(View.VISIBLE);
        
        final Recipe recipe = createRecipeFromInputs();
        
        // If there's an image, upload it first
        if (selectedImageUri != null) {
            String imagePath = "recipe_images/" + UUID.randomUUID().toString();
            storageHelper.uploadImage(selectedImageUri, imagePath, new FirebaseStorageHelper.OnImageUploadListener() {
                @Override
                public void onSuccess(String imageUrl) {
                    recipe.setImageUrl(imageUrl);
                    saveRecipeToFirebase(recipe);
                }

                @Override
                public void onFailure(Exception e) {
                    // progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddRecipeActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // No image to upload, just save the recipe
            saveRecipeToFirebase(recipe);
        }
    }

    private Recipe createRecipeFromInputs() {
        String name = recipeNameEditText.getText().toString().trim();
        String description = recipeDescriptionEditText.getText().toString().trim();
        int cookingTime = Integer.parseInt(cookingTimeEditText.getText().toString().trim());
        int servingSize = Integer.parseInt(servingSizeEditText.getText().toString().trim());
        String category = categoryAutoComplete.getText().toString().trim();
        String videoId = videoIdEditText.getText() != null ? 
                         videoIdEditText.getText().toString().trim() : "";
        
        // Get current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : "";
        
        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setDescription(description);
        recipe.setCookingTime(cookingTime);
        recipe.setServingSize(servingSize);
        recipe.setCategory(category);
        recipe.setVideoId(videoId);
        recipe.setCreatedBy(userId);
        recipe.setIngredients(new ArrayList<>(ingredients));
        recipe.setInstructions(new ArrayList<>(instructions));
        
        return recipe;
    }

    private void saveRecipeToFirebase(Recipe recipe) {
        recipeViewModel.addRecipe(recipe).observe(this, recipeId -> {
            // progressBar.setVisibility(View.GONE);
            
            if (recipeId != null) {
                Toast.makeText(this, R.string.recipe_added_successfully, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.recipe_add_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
