package com.example.recipeapp.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApi {
    @GET("recipes/random")
    suspend fun getRandomRecipes(
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int
    ): RecipeResponse


    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String
    ): RecipeSearchResponse

    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String
    ): RecipeDetailResponse

    @GET("recipes/{id}/ingredientWidget.json")
    suspend fun getRecipeIngredientsByID(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String
    ): IngredientsResponse

    @GET("recipes/{id}/analyzedInstructions")
    suspend fun getAnalyzedRecipeInstructions(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String
    ): List<Instruction>

    @GET("recipes/{id}/equipmentWidget.json")
    suspend fun getRecipeEquipmentByID(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String
    ): EquipmentResponse
}