package com.example.recipeapp._Ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.recipeapp.R
import com.example.warehousetracebilityandroid.utils.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder

@Composable
fun ColumnRecipeCard(navController: NavHostController, title: String, time: String, imageUrl: String) {
    val coroutineScope = rememberCoroutineScope()

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 8.dp)
            .clickable {
                coroutineScope.launch {
                    try {
                        val encodedImageUrl = withContext(Dispatchers.IO) {
                            URLEncoder.encode(imageUrl, "UTF-8")
                        }
                        navController.navigate(
                            Screen.RecipeDetailScreen.createRoute(
                                title,
                                encodedImageUrl
                            )
                        )
                    } catch (e: Exception) {
                        // Handle encoding exception
                        Log.e("ColumnRecipeCard", "Error encoding URL: ${e.message}")
                    }
                }
            }
    ) {
        Row {
            Image(
                painter = rememberImagePainter(
                    data = imageUrl,
                    builder = {
                        error(R.drawable.food) // Replace with your drawable resource
                    }
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
            )
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                Text(text = if (title.length > 28) title.substring(0, 28) else title)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = time)
            }
        }
    }
}

@Composable
fun RowRecipeCard(navController: NavHostController, title: String, imageUrl: String) {
    val coroutineScope = rememberCoroutineScope()

    androidx.compose.material3.Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .width(150.dp)
            .height(180.dp)
            .padding(8.dp)
            .clickable {
                coroutineScope.launch {
                    try {
                        val encodedImageUrl = withContext(Dispatchers.IO) {
                            URLEncoder.encode(imageUrl, "UTF-8")
                        }
                        navController.navigate(
                            Screen.RecipeDetailScreen.createRoute(
                                title,
                                encodedImageUrl
                            )
                        )
                    } catch (e: Exception) {
                        // Handle encoding exception
                        Log.e("RowRecipeCard", "Error encoding URL: ${e.message}")
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = rememberImagePainter(
                    data = imageUrl,
                    builder = {
                        error(R.drawable.food)
                    }
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                androidx.compose.material3.Text(
                    text = if (title.length > 10) title.substring(0, 10) else title,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                androidx.compose.material3.Text(
                    text = "Ready in 25 min",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.White
                    )
                )
            }
        }
    }
}
