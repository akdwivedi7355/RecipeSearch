package com.example.warehousetracebilityandroid.utils

sealed class Screen(val route: String) {
    object MainScreen : Screen("MainScreen")
    object SearchScreen : Screen("SearchScreen")
    object SplashScreen : Screen("SplashScreen")
    object RecipeDetailScreen : Screen("recipe_detail_screen/{title}/{imageUrl}") {
        fun createRoute(title: String, imageUrl: String) = "recipe_detail_screen/$title/$imageUrl"
    }

    object RecipeDetailBottomSheetScreen : Screen("recipe_detail_screen/{title}/{imageUrl}") {
        fun createRoute(title: String, imageUrl: String) = "recipe_detail_screen/$title/$imageUrl"
    }

}
