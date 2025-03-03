package com.example.foodrecipe.ui.recipedetail;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodrecipe.R;
import com.example.foodrecipe.adapter.RecipeIngredientsAdapter;
import com.example.foodrecipe.adapter.RecipeInstructionsAdapter;
import com.example.foodrecipe.model.Recipe;
import com.example.foodrecipe.viewmodel.RecipeViewModel;
import com.example.foodrecipe.viewmodel.UserViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;

/**
 * Activity to display detailed information about a recipe
 */
public class RecipeDetailActivity extends AppCompatActivity {

    // Intent extra for recipe ID
    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    private RecipeViewModel recipeViewModel;
    private UserViewModel userViewModel;
    
    private String recipeId;
    private Recipe currentRecipe;
    
    // UI components
    private ImageView recipeImageView;
    private TextView recipeNameTextView;
    private TextView recipeDescriptionTextView;
    private TextView cookingTimeTextView;
    private TextView servingSizeTextView;
    private ToggleButton favoriteToggle;
    private RecyclerView ingredientsRecyclerView;
    private RecyclerView instructionsRecyclerView;
    private FloatingActionButton shareButton;
    private TextView videoInstructionsTextView;
    private FrameLayout videoContainer;
    private YouTubePlayerView youTubePlayerView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        
        // Get recipe ID from intent
        recipeId = getIntent().getStringExtra(EXTRA_RECIPE_ID);
        if (recipeId == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize ViewModels
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.recipe_details);
        
        // Initialize UI components
        initializeViews();
        
        // Load recipe data
        loadRecipeData();
    }
    
    private void initializeViews() {
        recipeImageView = findViewById(R.id.image_recipe);
        recipeNameTextView = findViewById(R.id.text_recipe_name);
        recipeDescriptionTextView = findViewById(R.id.text_recipe_description);
        cookingTimeTextView = findViewById(R.id.text_cooking_time);
        servingSizeTextView = findViewById(R.id.text_serving_size);
        favoriteToggle = findViewById(R.id.toggle_favorite);
        shareButton = findViewById(R.id.fab_share);
        videoInstructionsTextView = findViewById(R.id.text_video_instructions);
        videoContainer = findViewById(R.id.video_container);
        
        // Set up RecyclerViews
        ingredientsRecyclerView = findViewById(R.id.recycler_view_ingredients);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ingredientsRecyclerView.setAdapter(new RecipeIngredientsAdapter(new ArrayList<Recipe.Ingredient>()));
        
        instructionsRecyclerView = findViewById(R.id.recycler_view_instructions);
        instructionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        instructionsRecyclerView.setAdapter(new RecipeInstructionsAdapter(new ArrayList<>()));
        
        // Set up click listeners
        favoriteToggle.setOnClickListener(v -> toggleFavorite());
        shareButton.setOnClickListener(v -> shareRecipe());
    }
    
    private void loadRecipeData() {
        recipeViewModel.getRecipeById(recipeId).observe(this, recipe -> {
            if (recipe != null) {
                currentRecipe = recipe;
                displayRecipeDetails(recipe);
                checkIfFavorite();
            } else {
                Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    
    private void displayRecipeDetails(Recipe recipe) {
        // Display recipe image
        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(recipe.getImageUrl())
                    .placeholder(R.drawable.ic_recipe_placeholder)
                    .error(R.drawable.ic_recipe_placeholder)
                    .centerCrop()
                    .into(recipeImageView);
        } else {
            recipeImageView.setImageResource(R.drawable.ic_recipe_placeholder);
        }
        
        // Set text views
        recipeNameTextView.setText(recipe.getName());
        recipeDescriptionTextView.setText(recipe.getDescription());
        cookingTimeTextView.setText(String.format("%d min", recipe.getCookingTimeMinutes()));
        servingSizeTextView.setText(String.format("%d servings", recipe.getServingSize()));
        
        // Update ingredients adapter
        if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
            RecipeIngredientsAdapter adapter = (RecipeIngredientsAdapter) ingredientsRecyclerView.getAdapter();
            if (adapter != null) {
                adapter.updateData(recipe.getIngredients());
            }
        }
        
        // Update instructions adapter
        if (recipe.getInstructions() != null && !recipe.getInstructions().isEmpty()) {
            RecipeInstructionsAdapter adapter = (RecipeInstructionsAdapter) instructionsRecyclerView.getAdapter();
            if (adapter != null) {
                adapter.updateData(recipe.getInstructions());
            }
        }
        
        // Setup video if available
        if (recipe.getVideoId() != null && !recipe.getVideoId().isEmpty()) {
            videoInstructionsTextView.setVisibility(View.VISIBLE);
            videoContainer.setVisibility(View.VISIBLE);
            setupYouTubePlayer(recipe.getVideoId());
        } else {
            videoInstructionsTextView.setVisibility(View.GONE);
            videoContainer.setVisibility(View.GONE);
        }
    }
    
    private void setupYouTubePlayer(String videoId) {
        // Remove any existing player view
        videoContainer.removeAllViews();
        
        // Create and add a new player view
        youTubePlayerView = new YouTubePlayerView(this);
        videoContainer.addView(youTubePlayerView);
        
        // Add the player view to lifecycle observers
        getLifecycle().addObserver(youTubePlayerView);
        
        // Set up the player
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                // Load the video but don't play it automatically
                youTubePlayer.cueVideo(videoId, 0);
            }
        });
    }
    
    private void checkIfFavorite() {
        userViewModel.isRecipeFavorite(recipeId).observe(this, isFavorite -> {
            favoriteToggle.setChecked(isFavorite != null && isFavorite);
        });
    }
    
    private void toggleFavorite() {
        boolean isFavorite = favoriteToggle.isChecked();
        userViewModel.toggleFavoriteRecipe(recipeId, isFavorite).observe(this, success -> {
            if (success) {
                int messageResId = isFavorite ? 
                        R.string.added_to_favorites : R.string.removed_from_favorites;
                Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
            } else {
                // Revert toggle if operation failed
                favoriteToggle.setChecked(!isFavorite);
                Toast.makeText(this, "Failed to update favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void shareRecipe() {
        // Implement share functionality
        // This is just a placeholder
        if (currentRecipe != null) {
            String shareText = "Check out this recipe: " + currentRecipe.getName() 
                    + "\n\n" + currentRecipe.getDescription();
            
            android.content.Intent shareIntent = new android.content.Intent();
            shareIntent.setAction(android.content.Intent.ACTION_SEND);
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            shareIntent.setType("text/plain");
            startActivity(android.content.Intent.createChooser(shareIntent, "Share via"));
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Properly release the YouTube player
        if (youTubePlayerView != null) {
            youTubePlayerView.release();
        }
    }
}
