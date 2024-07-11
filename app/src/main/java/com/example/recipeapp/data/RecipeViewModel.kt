package com.example.recipeapp.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.utils.PreferencesHelper
import com.google.android.gms.ads.AdRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecipeViewModel(private val context: Context, private val recipeApi: RecipeApi) : ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes


    var scrollPosition = mutableStateOf(0) // MutableState for scroll position

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites

    private val _favorites2 = MutableStateFlow<List<Recipe>>(emptyList())
    val favorites2: StateFlow<List<Recipe>> = _favorites2.asStateFlow()

    private val _favoriteRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val favoriteRecipes: StateFlow<List<Recipe>> = _favoriteRecipes

    private val _searchResults = MutableStateFlow<List<Recipe>>(emptyList())
    val searchResults: StateFlow<List<Recipe>> = _searchResults

    private val _selectedRecipe = MutableStateFlow<Recipe?>(null)
    val selectedRecipe: StateFlow<Recipe?> = _selectedRecipe

    private val _recipeDetails = MutableStateFlow<RecipeDetailResponse?>(null)
    val recipeDetails: StateFlow<RecipeDetailResponse?> = _recipeDetails

    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients: StateFlow<List<Ingredient>> = _ingredients

    private val _instructions = MutableStateFlow<List<Step>>(emptyList())
    val instructions: StateFlow<List<Step>> = _instructions

    private val _equipment = MutableStateFlow<List<Equipment>>(emptyList())
    val equipment: StateFlow<List<Equipment>> = _equipment

    private val _adRequest = MutableStateFlow<AdRequest?>(null)
    val adRequest: StateFlow<AdRequest?> = _adRequest.asStateFlow()


    private var isDataLoaded = false



    fun fetchRecipes(apiKey: String, number: Int) {
        if (isDataLoaded) return // Ensure data is fetched only once

        viewModelScope.launch {
            try {
                val response = recipeApi.getRandomRecipes(apiKey, number)
                _recipes.value = response.recipes ?: emptyList()
                isDataLoaded = true

//                PreferencesHelper.saveRecipes(context, response.recipes ?: emptyList())
                Log.d("RandomResponse", response.toString())

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            try {
                val response = recipeApi.searchRecipes(apiKey = "babb0844766243ccb9cf4d2ce3fe113d", query = query)
                _searchResults.value = response.results ?: emptyList()
                Log.d("SearchResponse", response.toString())


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchRecipeDetails(apiKey: String, recipeId: Int) {
        viewModelScope.launch {
            try {
                val details = recipeApi.getRecipeInformation(recipeId, apiKey)
                _recipeDetails.value = details

                val ingredientsResponse = recipeApi.getRecipeIngredientsByID(recipeId, apiKey)
                _ingredients.value = ingredientsResponse.ingredients

                val instructionsResponse = recipeApi.getAnalyzedRecipeInstructions(recipeId, apiKey)
                _instructions.value = instructionsResponse.flatMap { it.steps }

                val equipmentResponse = recipeApi.getRecipeEquipmentByID(recipeId, apiKey)
                _equipment.value = equipmentResponse.equipment
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addNewRecipeItem(recipe: Recipe) {
        _favorites2.value += recipe
        PreferencesHelper.saveFavorite(context, recipe.title)

    }

    fun removeRecipeItem(recipe: Recipe) {
        _favorites2.value -= recipe
        PreferencesHelper.removeFavorite(context, recipe.title)

    }


}
