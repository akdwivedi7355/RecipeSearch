package com.example.recipeapp._Ui

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.recipeapp.R
import com.example.recipeapp.SearchScreen.SearchBar

import com.example.recipeapp.data.Recipe
import com.example.recipeapp.data.RecipeViewModel
import com.example.warehousetracebilityandroid.utils.Screen
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import kotlinx.coroutines.delay


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainContentScreen(navController: NavHostController, recipeViewModel: RecipeViewModel) {
    val items = listOf("Home", "Favourite")
    var selectedItem by remember { mutableStateOf(0) }
    val context = LocalContext.current

    BackHandler {
        (context as? ComponentActivity)?.finish()
    }
    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color.White
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = selectedItem == index
                    val color = if (isSelected) Color(0xFFFFFFFF) else Color.Gray

                    BottomNavigationItem(
                        icon = {
                            Icon(
                                imageVector = if (index == 0) Icons.Default.Home else Icons.Default.Favorite,
                                contentDescription = "Expand",
                                modifier = Modifier.size(24.dp),
                                tint = color
                            )
                        },
                        label = {
                            Text(item, color = color)
                        },
                        modifier = Modifier.background(Color.Black),
                        selected = isSelected,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) {
        when (selectedItem) {
            0 -> HomeScreen(navController, recipeViewModel)
            1 -> FavouriteScreen(recipeViewModel, navController)
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController, recipeViewModel: RecipeViewModel) {
    val scrollState = rememberLazyListState(recipeViewModel.scrollPosition.value)
    val recipes by recipeViewModel.recipes.collectAsState()
    val context = LocalContext.current

    val isLoading = recipes.isEmpty()

    LaunchedEffect(Unit) {
        try {
            recipeViewModel.fetchRecipes("babb0844766243ccb9cf4d2ce3fe113d", 25)
        } catch (e: Exception) {
            Toast.makeText(context, "Error fetching recipes: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(scrollState.firstVisibleItemIndex) {
        onDispose {
            recipeViewModel.scrollPosition.value = scrollState.firstVisibleItemIndex
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "👋 Hey There, Welcome Back",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        )
        Text(
            text = "Discover tasty and healthy recipes",
            Modifier.padding(start = 35.dp),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        SearchBar(navController, context,recipeViewModel)

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            LoadingIndicator()
        } else {
            RecipeContent(recipes, navController)
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Loading",
                color = Color.White,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal)
            )
        }
    }
}

@Composable
fun RecipeContent(recipes: List<Recipe>, navController: NavHostController) {
    Text(
        text = "Popular Recipes",
        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
    )

    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(recipes) { recipe ->
            RowRecipeCard(navController, recipe.title ?: "", recipe.image ?: "")
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "All Recipes",
        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
    )

    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(recipes) { index, recipe ->
            ColumnRecipeCard(navController, recipe.title ?: "", "Ready in 25 min", recipe.image ?: "")

            if (index > 0 && index % 2 == 0) {
                Spacer(modifier = Modifier.height(8.dp))
                AdView(adRequest = remember { AdRequest.Builder().build() }, adUnitId = "ca-app-pub-3940256099942544/6300978111")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FavouriteScreen(recipeViewModel: RecipeViewModel, navController: NavHostController) {
    val favoriteRecipes by recipeViewModel.favorites2.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 4.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column {
            Text(
                text = "Favourite Recipes",
                modifier = Modifier.padding(4.dp),
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(favoriteRecipes) { recipe ->
                    ColumnRecipeCard(
                        title = recipe.title ?: "Unknown",
                        time = "Ready in 25 min",
                        imageUrl = recipe.image ?: "default_image_url",
                        navController = navController,
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
        ) {
            Image(
                painter = painterResource(id = R.drawable.splashimage),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate(Screen.MainScreen.route)
    }
}

@Composable
fun AdView(adRequest: AdRequest, adUnitId: String) {
    val context = LocalContext.current
    var adLoadFailed by remember { mutableStateOf(false) }

    if (adLoadFailed) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Failed to load the ad", color = Color.Black)
        }
    } else {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    adListener = object : AdListener() {
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            adLoadFailed = true
                        }
                    }
                    loadAd(adRequest)
                }
            }
        )
    }
}
