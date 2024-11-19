package com.silicon.cure_sync

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(colors = topAppBarColors(
                containerColor = Color(0xFFE0F7FA)
            ),
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_cs1),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(60.dp)
                                .align(Alignment.CenterStart)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            "CureSync",
                            style = androidx.compose.ui.text.TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color(0xFFF7E300),
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(1f, 1f),
                                    blurRadius = 2f
                                )
                            )
                        )
                    }
                },
            )
        },
        containerColor = colorResource(R.color.bblue)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MedGreetingSection()
            MedDashboardGrid(
                items = listOf(
                    MedDashboardItem(R.drawable.ic_tablets, "Tablets"),
                    MedDashboardItem(R.drawable.ic_syrups, "Syrups"),
                    MedDashboardItem(R.drawable.ic_ointments, "Ointments"),
                    MedDashboardItem(R.drawable.ic_instruments, "Basic Instruments"),
                ),
                navController = navController,
            )
            Spacer(modifier = Modifier.weight(3f)) // Spacer to push the slogan to the bottom
            Text(
                text = "Your HEALTH, Our PRIORITY",
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color(0xFF006400), // Dark Green color
                    fontStyle = FontStyle.Italic,
                    shadow = Shadow(
                        color = Color.Gray,
                        offset = Offset(1f, 1f),
                        blurRadius = 2f
                    )
                ),
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center // Center align the text
            )
        }
    }
}

@Composable
fun MedGreetingSection() {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(text = "Hello", fontSize = 20.sp,fontWeight = FontWeight.Bold)
        Text(text = "Let's Shop!", fontSize = 30.sp,fontWeight = FontWeight.ExtraBold)
    }
}


@Composable
fun MedDashboardGrid(items: List<MedDashboardItem>, navController: NavController) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for (i in items.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MedDashboardIcon(item = items[i], onClick = {
                    MednavigateToScreen(navController, items[i].label)
                })
                if (i + 1 < items.size) {
                    MedDashboardIcon(item = items[i + 1], onClick = {
                        MednavigateToScreen(navController, items[i + 1].label)
                    })
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun MedDashboardIcon(item: MedDashboardItem, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.blue)
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp), // Adding elevation for shadow effect
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(16.dp)
            .width(150.dp)
            .height(150.dp)
            .border(BorderStroke(2.dp, Color.White), shape = RoundedCornerShape(10.dp)) // Adding white border
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = item.iconRes),
                contentDescription = item.label,
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.label,
                color = colorResource(R.color.black),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
fun MednavigateToScreen(navController: NavController, label: String) {
    when (label) {
        "Tablets" -> navController.navigate("tabletsScreen")
        "Syrups" -> navController.navigate("syrupsScreen")
        "Ointments" -> navController.navigate("ointmentsScreen")
        "Basic Instrument" -> navController.navigate("instrumentsScreen")
        else -> navController.navigate("Home") // Handle any other cases
    }
}
data class MedDashboardItem(val iconRes: Int, val label: String)