package com.example.recipeapp.data

data class RecipeResponse(val recipes: List<Recipe>)
data class RecipeDetailResponse(val id: Int, val title: String, val image: String)
data class IngredientsResponse(val ingredients: List<Ingredient>)
data class Instruction(val steps: List<Step>)
data class Step(val number: Int, val step: String)
data class EquipmentResponse(val equipment: List<Equipment>)
data class Recipe(val id: Int, val title: String?, val image: String?)
data class Ingredient(val name: String, val image: String)
data class Equipment(val name: String, val image: String)
data class RecipeSearchResponse(
    val results: List<Recipe>?
)