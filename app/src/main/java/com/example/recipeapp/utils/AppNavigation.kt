package com.example.warehousetracebilityandroid.components



//import com.example.warehousetracebilityandroid.ui.settings.SelectDevices
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recipeapp.SearchScreen.SearchScreen
import com.example.recipeapp._Ui.HomeScreenRecipeDetails
import com.example.recipeapp._Ui.MainContentScreen
import com.example.recipeapp._Ui.SplashScreen
import com.example.recipeapp.data.RecipeApi
import com.example.recipeapp.data.RecipeViewModel
import com.example.recipeapp.data.RecipeViewModelFactory
import com.example.recipeapp.data.RetrofitClient
import com.example.warehousetracebilityandroid.utils.Screen

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("RememberReturnType", "SuspiciousIndentation")
@Composable
fun AppNavigation(
    navController: NavHostController,
)
{
    val context = LocalContext.current
    val navController = rememberNavController()

     val apiService: RecipeApi by lazy {
        RetrofitClient.instance.create(RecipeApi::class.java)
    }
    val viewModel: RecipeViewModel = viewModel(factory = RecipeViewModelFactory(context,apiService))



    NavHost(navController, startDestination = Screen.SplashScreen.route) {
        composable(Screen.MainScreen.route) {
            MainContentScreen(navController ,viewModel)
        }
        composable(Screen.SearchScreen.route) {
            SearchScreen(navController,context,viewModel)
        }
        composable(Screen.SplashScreen.route) {
            SplashScreen(navController)
        }

        composable(
            route = Screen.RecipeDetailScreen.route,
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("imageUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""

            HomeScreenRecipeDetails(title, imageUrl,viewModel)
        }

    }
}