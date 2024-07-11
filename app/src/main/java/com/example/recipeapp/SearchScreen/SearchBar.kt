package com.example.recipeapp.SearchScreen

import android.R
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.recipeapp._Ui.RecipeDetailBottomSheet
import com.example.recipeapp.data.Recipe
import com.example.recipeapp.data.RecipeViewModel
import com.example.warehousetracebilityandroid.utils.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SearchBar(navController: NavHostController, context: Context, recipeViewModel: RecipeViewModel) {
    var searchText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, CircleShape)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                // Navigate to the search screen
                navController.navigate(Screen.SearchScreen.route)
            }
    ) {
        Row(
            Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search_category_default),
                contentDescription = null,
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(Modifier.weight(1f)) {
                if (searchText.isEmpty()) {
                    Text(text = "Search any recipe..", color = Color.Black)
                }
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
                    enabled = false, // Disable text input to prevent keyboard from showing
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(navController: NavHostController, context: Context, recipeViewModel: RecipeViewModel) {
    val focusRequester = remember { FocusRequester() }
    var searchText by remember { mutableStateOf("") }
    val searchResults by recipeViewModel.searchResults.collectAsState()
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed))

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            selectedRecipe?.let { recipe ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 400.dp, max = 490.dp) // Adjust the height as needed
                        .background(Color.White)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                ) {
                    RecipeDetailBottomSheet(
                        title = recipe.title ?: "No title",
                        imageUrl = recipe.image ?: "",
                        recipeViewModel = recipeViewModel,
                        onClose = {
                            scope.launch {
                                scaffoldState.bottomSheetState.collapse()
                            }
                        },
                        navController
                    )
                }
            }
        },
        sheetPeekHeight = 0.dp, // Adjust this if you want a visible peek height when collapsed
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, CircleShape)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search_category_default),
                        contentDescription = null,
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(Modifier.weight(1f)) {
                        if (searchText.isEmpty()) {
                            Text(text = "Search any recipe", color = Color.Gray)
                        }
                        BasicTextField(
                            value = searchText,
                            onValueChange = {
                                searchText = it
                                scope.launch {
                                    try {
                                        recipeViewModel.searchRecipes(searchText)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error searching recipes: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (searchText.isNotEmpty()) {
                LazyColumn {
                    items(searchResults) { recipe ->
                        SearchResultItem(recipe.title!!, recipe.image) {
                            focusManager.clearFocus() // Clear the focus
                            scope.launch {
                                selectedRecipe = recipe
                                scaffoldState.bottomSheetState.collapse()
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(title: String, imageUrl: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = rememberImagePainter(data = imageUrl ?: "default_image_url"),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
