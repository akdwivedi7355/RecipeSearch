package com.example.recipeapp._Ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Kitchen

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.recipeapp.data.Recipe
import com.example.recipeapp.data.RecipeViewModel
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun RecipeDetailBottomSheet(
    title: String,
    imageUrl: String,
    recipeViewModel: RecipeViewModel,
    onClose: () -> Unit,
    navController: NavHostController
) {
    val context = LocalContext.current
    var isFavorite by remember { mutableStateOf(false) }
    var currentSection by remember { mutableStateOf(Section.NONE) }
    val recipeDetails by recipeViewModel.recipeDetails.collectAsState()
    val ingredients by recipeViewModel.ingredients.collectAsState()
    val instructions by recipeViewModel.instructions.collectAsState()
    val equipment by recipeViewModel.equipment.collectAsState()
    val recipes by recipeViewModel.recipes.collectAsState()
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(recipeDetails) {
        coroutineScope.launch {
            try {
                recipeDetails?.id?.let { recipeViewModel.fetchRecipeDetails("babb0844766243ccb9cf4d2ce3fe113d", it) }
            } catch (e: Exception) {
                Toast.makeText(context, "Error fetching recipe details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(recipeViewModel.favorites.value) {
        isFavorite = recipeViewModel.favorites.value.contains(title)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp) // Add padding to avoid overlap with button
        ) {
            item {
                // Back and Favorite Icons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                when (currentSection) {
                                    Section.SIMILAR_RECIPE -> currentSection = Section.FULL_RECIPE
                                    Section.FULL_RECIPE -> currentSection = Section.INGREDIENTS
                                    Section.INGREDIENTS -> currentSection = Section.NONE
                                    Section.NONE -> onClose()
                                }
                            }
                    )

                    // Recipe Title
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f).offset(x = 16.dp, y = 16.dp)) // Offset to align text properly
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(bottom = 5.dp)
                            .clickable {
                                coroutineScope.launch {
                                    try {
                                        if (isFavorite) {
                                            recipeViewModel.removeRecipeItem(
                                                Recipe(
                                                    id = recipeDetails?.id ?: 0,
                                                    title = title,
                                                    image = imageUrl,
                                                )
                                            )
                                            isFavorite = false
                                            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                                        } else {
                                            recipeViewModel.addNewRecipeItem(
                                                Recipe(
                                                    id = recipeDetails?.id ?: 0,
                                                    title = title,
                                                    image = imageUrl,
                                                )
                                            )
                                            isFavorite = true
                                            Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error updating favorites: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    )
                }
            }

            item {
                if (currentSection == Section.NONE) {
                    // Initial Recipe Image and Info
                    Image(
                        painter = rememberImagePainter(data = imageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        RecipeInfoBox(label = "Ready in", value = "25 min")
                        RecipeInfoBox(label = "Servings", value = "10 min")
                    }
                }
            }

            item {
                Divider(color = Color.White)
            }

            // Ingredients Section
            if (currentSection == Section.INGREDIENTS || currentSection == Section.FULL_RECIPE || currentSection == Section.SIMILAR_RECIPE) {
                item {
                    SectionHeader(
                        title = "Ingredients",
                        isExpanded = currentSection == Section.INGREDIENTS,
                        onClick = { currentSection = if (currentSection == Section.INGREDIENTS) Section.NONE else Section.INGREDIENTS }
                    )
                }

                if (currentSection == Section.INGREDIENTS) {
                    if (ingredients.isEmpty()) {
                        item {
                            Text(
                                text = "Sorry, couldn't fetch the data",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.White
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    } else {
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(ingredients) { ingredient ->
                                    IngredientItem(name = ingredient.name, imageUrl = "https://spoonacular.com/cdn/ingredients_100x100/${ingredient.image}")
                                }
                            }
                        }
                    }
                }
            }

            item {
                Divider(color = Color.White)
            }

            // Full Recipe Section
            if (currentSection == Section.FULL_RECIPE || currentSection == Section.SIMILAR_RECIPE) {
                item {
                    SectionHeader(
                        title = "Full Recipe",
                        isExpanded = currentSection == Section.FULL_RECIPE,
                        onClick = { currentSection = if (currentSection == Section.FULL_RECIPE) Section.NONE else Section.FULL_RECIPE }
                    )
                }

                if (currentSection == Section.FULL_RECIPE) {
                    if (instructions.isEmpty()) {
                        item {
                            Text(
                                text = "Sorry, couldn't fetch the data",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.White
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    } else {
                        items(instructions) { instruction ->
                            Text(
                                text = instruction.step,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.White
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Equipment",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(equipment) { equipmentItem ->
                                EquipmentItem(name = equipmentItem.name)
                            }
                        }
                    }
                }
            }

            item {
                Divider(color = Color.White)
            }

            // Similar Recipe Section
            if (currentSection == Section.SIMILAR_RECIPE) {
                item {
                    SectionHeader(
                        title = "Similar recipes",
                        isExpanded = currentSection == Section.SIMILAR_RECIPE,
                        onClick = { currentSection = if (currentSection == Section.SIMILAR_RECIPE) Section.NONE else Section.SIMILAR_RECIPE }
                    )
                }

                if (currentSection == Section.SIMILAR_RECIPE) {
                    items(recipes) { recipe ->
                        ColumnRecipeCard(navController, recipe.title ?: "", "Ready in 25 min", recipe.image ?: "")
                    }
                }
            }
        }

        // Toggle button
        Button(
            onClick = {
                currentSection = when (currentSection) {
                    Section.NONE -> Section.INGREDIENTS
                    Section.INGREDIENTS -> Section.FULL_RECIPE
                    Section.FULL_RECIPE -> Section.SIMILAR_RECIPE
                    Section.SIMILAR_RECIPE -> Section.NONE
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFA500)), // Orange color
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.9f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Text(
                text = when (currentSection) {
                    Section.NONE -> "Get Ingredients"
                    Section.INGREDIENTS -> "Get Full Recipe"
                    Section.FULL_RECIPE -> "Get Similar Recipe"
                    Section.SIMILAR_RECIPE -> "Hide content"
                },
                style = TextStyle(color = Color.White)
            )
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}


enum class Section {
    NONE, INGREDIENTS, FULL_RECIPE, SIMILAR_RECIPE
}

@Composable
fun SectionHeader(title: String, isExpanded: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 12.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .clickable { onClick() }
        )
    }
}

@Composable
fun EquipmentItem(name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            Icon(
                imageVector = Icons.Default.Kitchen, // Use appropriate vector drawable
                contentDescription = name,
                tint = Color.Gray,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )
    }
}





