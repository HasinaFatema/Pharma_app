@file:OptIn(ExperimentalMaterial3Api::class)

package com.silicon.cure_sync

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.FirebaseFirestore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabletsScreen(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val tabletList = remember { mutableStateListOf<Medicine>() }
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(true) }

    // Fetch tablets from Firestore
    LaunchedEffect(Unit) {
        db.collection("medicines").document("tablets").collection("items")
            .get()
            .addOnSuccessListener { documents ->
                tabletList.clear()
                val tablets = documents.mapNotNull { document ->
                    document.toObject(Medicine::class.java)
                }

                // Eliminate duplicates based on tablet's name
                tabletList.addAll(tablets.distinctBy { it.name }) // Use distinctBy to remove duplicates based on name
                isLoading.value = false
            }
            .addOnFailureListener { exception ->
                isLoading.value = false
                Toast.makeText(context, "Error getting documents: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE0F7FA)),
                title = { Text("Tablets") },
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                if (isLoading.value) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (tabletList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No tablets found.", fontSize = 18.sp)
                    }
                } else {
                    // Correct the items to use the correct type (Medicine)
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 120.dp),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tabletList) { tablet -> // Changed line
                            TabletItem(
                                tablet = tablet,
                                onClick = {
                                    // Safely handle nullable 'name' field and navigate to the detail screen
                                    tablet.name?.let { tabletName ->  // Changed line
                                        navController.navigate("tabletDetail/$tabletName")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun TabletItem(tablet: Medicine, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.blue)),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(16.dp)
            .width(150.dp)
            .height(200.dp)
            .border(BorderStroke(2.dp, Color.White), shape = RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Assuming tablet has an image URL or resource ID
            val imagePainter = rememberImagePainter(tablet.imageUrls)
            Image(
                painter = imagePainter,
                contentDescription = "Tablet image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 8.dp)
            )
            Text(text = tablet.name ?: "Unknown Tablet", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Price: \$${tablet.price ?: "N/A"}", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabletDetailScreen(tabletName: String, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val tablet = remember { mutableStateOf<Medicine?>(null) }
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(true) }

    val pagerState = rememberPagerState(
        pageCount = { tablet.value?.imageUrls?.size ?: 0 } // Lambda for dynamic page count
    )

    // Fetch tablet details from Firestore
    LaunchedEffect(tabletName) {
        // Log tablet name to debug
        Log.d("TabletDetailScreen", "Fetching tablet details for name: $tabletName")

        db.collection("medicines").document("tablets").collection("items")
            .whereEqualTo("name", tabletName.lowercase())  // Ensure the query matches the case
            .get()
            .addOnSuccessListener { documents ->
                Log.d("TabletDetailScreen", "Documents fetched: ${documents.size()}")

                if (!documents.isEmpty) {
                    tablet.value = documents.first().toObject(Medicine::class.java)
                    isLoading.value = false
                } else {
                    // Show toast when no tablet is found
                    Toast.makeText(context, "No tablet found with name $tabletName", Toast.LENGTH_SHORT).show()
                    isLoading.value = false
                }
            }
            .addOnFailureListener { exception ->
                // Show toast in case of failure
                Toast.makeText(context, "Error getting document: ${exception.message}", Toast.LENGTH_SHORT).show()
                isLoading.value = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tablet Details") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isLoading.value) {
                    // Display loading indicator while fetching data
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    tablet.value?.let { tablet ->
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Display tablet images using HorizontalPager
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            ) { page ->
                                val imageUrl = tablet.imageUrls.getOrNull(page)
                                if (imageUrl != null) {
                                    Image(
                                        painter = rememberImagePainter(imageUrl),
                                        contentDescription = "Image $page",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Name: ${tablet.name}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Description: ${tablet.description ?: "N/A"}", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Price: \$${tablet.price ?: "N/A"}", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Age Group: ${tablet.ageGroup ?: "N/A"}", fontSize = 16.sp)

                            Spacer(modifier = Modifier.height(16.dp))

                            // Add to cart button
                            Button(
                                onClick = {
                                    navController.navigate("addToCart/${tablet.name}")
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Add to Cart")
                            }
                        }
                    } ?: Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Display message if no tablet is found
                        Text(text = "No details found.", fontSize = 18.sp)
                    }
                }
            }
        }
    )
}
