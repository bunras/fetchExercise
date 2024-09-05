package com.example.fetchexercise

import android.os.Bundle
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.fetchexercise.ui.theme.FetchExerciseTheme

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

//retrofit imports
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FetchExercise()
        }
    }
}

@Composable
fun FetchExercise() {
    var readItems by remember { mutableStateOf(emptyList<Item>()) }
    var fCategories by remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(Unit) {
        RetrofitClient.instance.getItems().enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                if (response.isSuccessful) {
                    val items = response.body()?.filter { !it.name.isNullOrEmpty() }
                    val sItems = items?.sortedWith(compareBy<Item> { it.listId }
                        .thenBy { extractNumber(it.name ?: "") }) ?: emptyList()

                    readItems = sItems
                }
            }

            override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                //For testing purposes
                Log.e("MainActivity", "API CALL FAILED: ${t.message}")
            }
        })
    }

    FetchExerciseTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (listId in 1..4) {
                    val fItems = readItems.filter { it.listId == listId }
                    item {
                        CategorySection(
                            listId = listId,
                            nItems = fItems,
                            isExpanded = fCategories.contains(listId),
                            onToggleExpand = {
                                fCategories = if (fCategories.contains(listId)) {
                                    fCategories - listId
                                } else {
                                    fCategories + listId
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

val customFontFamily = FontFamily(
    Font(R.font.exo2regular, FontWeight.Normal),
    Font(R.font.exo2bold, FontWeight.Bold)
)

@Composable
fun CategorySection(listId: Int, nItems: List<Item>, isExpanded: Boolean, onToggleExpand: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() }
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFF7043), Color(0xFFFFA726)), //Fetch colors!
                        start = androidx.compose.ui.geometry.Offset.Zero,
                        end = androidx.compose.ui.geometry.Offset.Infinite
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "List ID: $listId",
                fontFamily = customFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (isExpanded) {
            Column(modifier = Modifier.padding(start = 16.dp)) {
                nItems.forEach { item ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "ID: ${item.id}",
                                fontFamily = customFontFamily,
                                modifier = Modifier
                                    .weight(1f)
                                    .alignByBaseline(),
                            )

                            Text(
                                text = "Name: ${item.name}",
                                fontFamily = customFontFamily,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .alignByBaseline(),
                                textAlign = TextAlign.Start
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            thickness = 1.dp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

fun extractNumber(name: String): Int {
    val parts = name.split(" ")
    val lastPart = parts.lastOrNull()
    val number = lastPart?.toIntOrNull() ?: 0
    return number
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FetchExerciseTheme {
        LazyColumn {
            items(listOf(Item(1, 1, "Item 1"), Item(2, 1, "Item 2"))) { item ->
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "ID: ${item.id}",
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Name: ${item.name}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
