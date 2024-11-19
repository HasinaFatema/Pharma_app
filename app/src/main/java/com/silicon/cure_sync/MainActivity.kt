package com.silicon.cure_sync

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initializing Firebase and checking if collections are populated
        val db = FirebaseFirestore.getInstance()
        db.collection("medicines").get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    createCollections()  // CALL createCollections() ONLY IF DOCUMENTS ARE EMPTY
                }
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Error checking collections", e)
            }
        setContent{
            MainApp()
        }
    }
}
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val tabletList = remember { mutableStateListOf<Medicine>() }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController=navController) }
        composable("signup") { SignupScreen(navController) }
        composable("otp") { OtpScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
        composable("reset_password/{verificationId}") { backStackEntry ->
            var verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
            ResetPasswordScreen(navController, verificationId)
        }
        composable("password_reset_success") { PasswordResetSuccessScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("medicine") { MedicineScreen(navController) }
        composable("tabletsScreen") { TabletsScreen(navController) }
        composable("tabletDetail/{tabletName}") { backStackEntry ->
            val tabletName = backStackEntry.arguments?.getString("tabletName") ?: "Unknown" // *Edited line*
            val tablet = tabletList.find { it.name == tabletName }
            if (tablet != null) {
                TabletDetailScreen(tabletName,navController)
            } else {
                // Handle error: tablet not found
                Text(text = "Tablet not found", modifier = Modifier.padding(16.dp))
            }
        }
        composable("syrupsScreen") { SyrupScreen(navController) }
        composable("ointmentsScreen") { OintmentsScreen(navController) }
        composable("instrumentsScreen") { InstrumentsScreen(navController) }
        composable("doctor/{phoneNumber}") { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            DoctorSuggestionScreen(navController, phoneNumber)
        }
        composable("profile") { ProfileScreen(navController) }
        composable("editProfile") { EditProfileScreen(navController) }

        composable("addToCart") { AddToCartScreen(navController) }
        composable("cartSummary") { CartSummaryScreen(navController) }
    }
}









