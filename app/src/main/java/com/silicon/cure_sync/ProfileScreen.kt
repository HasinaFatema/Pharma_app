package com.silicon.cure_sync

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
@Composable
fun ProfileScreen(navController: NavController) {
    var firstName by remember { mutableStateOf("First Name") } // Updated: Set default value
    var lastName by remember { mutableStateOf("Last Name") } // Updated: Set default value
    var birthday by remember { mutableStateOf("01/01/2000") } // Updated: Set default value
    var gender by remember { mutableStateOf("Gender") } // Updated: Set default value
    var email by remember { mutableStateOf("example@email.com") } // Updated: Set default value
    var location by remember { mutableStateOf("Location") } // Updated: Set default value
    var profilePicUrl by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance().reference

    // Listen for updated profile data
    val updatedProfile = navController.currentBackStackEntry?.savedStateHandle?.get<Map<String, String>>("updatedProfile")
    LaunchedEffect(updatedProfile) {
        updatedProfile?.let { data ->
            firstName = data["firstName"] ?: firstName
            lastName = data["lastName"] ?: lastName
            birthday = data["birthday"] ?: birthday
            gender = data["gender"] ?: gender
            email = data["email"] ?: email
            location = data["location"] ?: location
            profilePicUrl = data["profilePictureUrl"] ?: profilePicUrl
        }
    }

    // Fetch user data from Firestore
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            try {
                val document = db.collection("users").document(userId).get().await()
                if (document.exists()) {
                    val data = document.data
                    firstName = data?.get("firstName")?.toString() ?: "First Name" // Updated: Set default value
                    lastName = data?.get("lastName")?.toString() ?: "Last Name" // Updated: Set default value
                    birthday = data?.get("birthday")?.toString() ?: "01/01/2000" // Updated: Set default value
                    gender = data?.get("gender")?.toString() ?: "Gender" // Updated: Set default value
                    email = data?.get("email")?.toString() ?: "example@email.com" // Updated: Set default value
                    location = data?.get("location")?.toString() ?: "Location" // Updated: Set default value

                    // Fetch profile picture URL
                    val profilePicPath = data?.get("profilePicture")?.toString() // Updated: Corrected field name
                    if (profilePicPath != null) {
                        val imageRef = storage.child(profilePicPath)
                        profilePicUrl = imageRef.downloadUrl.await().toString()
                    }
                }
            } catch (e: Exception) {
                // Handle error
                Log.e("ProfileScreen", "Error fetching user data", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
            .padding(16.dp)
    ) {
        // Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = profilePicUrl.ifEmpty { R.drawable.ic_profile1 }, // Updated: Use default profile picture if URL is empty
                        builder = {
                            crossfade(true)
                            placeholder(R.drawable.ic_profile1) // Placeholder image
                        }
                    ),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, CircleShape)
                )
                Text(
                    text = "$firstName $lastName",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    navController.navigate("editProfile") // Navigate to the Edit Profile screen
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66D3A3))
            ) {
                Text(text = "Edit Profile", color = Color.White)
            }
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") { // Navigate to the login screen
                        popUpTo("login") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
            ) {
                Text(text = "Sign Out", color = Color.White)
            }
        }

        // Editable Text Fields
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            CustomTextField(firstName, "First Name")
            Spacer(modifier = Modifier.height(10.dp))
            CustomTextField(lastName, "Last Name")
            Spacer(modifier = Modifier.height(10.dp))
            CustomTextField(birthday, "Birthday")
            Spacer(modifier = Modifier.height(10.dp))
            CustomTextField(gender, "Gender")
            Spacer(modifier = Modifier.height(10.dp))
            CustomTextField(email, "Email")
            Spacer(modifier = Modifier.height(10.dp))
            CustomTextField(location, "Location")
        }
    }
}
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var profilePictureUrl by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance().reference

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                uploadImageToFirebase(uri) { imageUrl ->
                    updateProfilePictureUrl(imageUrl)
                    profilePictureUrl = imageUrl
                }
            }
        }
    )

    // Fetch user data from Firestore
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            try {
                val document = db.collection("users").document(userId).get().await()
                if (document.exists()) {
                    val data = document.data
                    firstName = data?.get("firstName")?.toString() ?: ""
                    lastName = data?.get("lastName")?.toString() ?: ""
                    birthday = data?.get("birthday")?.toString() ?: ""
                    gender = data?.get("gender")?.toString() ?: ""
                    email = data?.get("email")?.toString() ?: ""
                    location = data?.get("location")?.toString() ?: ""
                    // Fetch profile picture URL
                    val profilePicUrl = data?.get("profilePicture")?.toString()
                    if (profilePicUrl != null) {
                        profilePictureUrl = profilePicUrl // Edited: Update the profilePictureUrl state
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Error fetching user data", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Edit Profile",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Profile Picture Section
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Gray, shape = CircleShape)
                .padding(8.dp)
                .clickable { launcher.launch("image/*") }
        ) {
            val painter = rememberImagePainter( // Edited: Composable call to load the image
                data = profilePictureUrl,
                builder = {
                    crossfade(true)
                    placeholder(R.drawable.ic_profile1) // Placeholder image
                    error(R.drawable.ic_profile1)
                }
            )

            Image(
                painter = painter,
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Editable Text Fields
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            OutlinedTextField(
                value = birthday,
                onValueChange = { birthday = it },
                label = { Text("Birthday") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            OutlinedTextField(
                value = gender,
                onValueChange = { gender = it },
                label = { Text("Gender") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Update Firestore with new data
                updateProfile(firstName, lastName, birthday, gender, email, location)

                // Pass the updated data back to the ProfileScreen
                navController.previousBackStackEntry?.savedStateHandle?.set("updatedProfile", mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "birthday" to birthday,
                    "gender" to gender,
                    "email" to email,
                    "location" to location,
                    "profilePictureUrl" to profilePictureUrl
                ))

                navController.popBackStack() // Navigate back to the previous screen
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66D3A3))
        ) {
            Text(text = "Save Changes", color = Color.White)
        }
    }
}

private fun uploadImageToFirebase(uri: Uri, onSuccess: (String) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val imageRef = storageRef.child("profile_pictures/$userId.jpg")

    imageRef.putFile(uri)
        .addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                onSuccess(downloadUrl.toString())
            }
        }
        .addOnFailureListener {
            // Handle error
            Log.e("EditProfileScreen", "Error uploading image", it)
        }
}

private fun updateProfile(firstName: String, lastName: String, birthday: String, gender: String, email: String, location: String) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val userId = auth.currentUser?.uid
    if (userId != null) {
        val userUpdates = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "birthday" to birthday,
            "gender" to gender,
            "email" to email,
            "location" to location
        )

        db.collection("users").document(userId)
            .update(userUpdates as Map<String, Any>)
            .addOnSuccessListener {
                Log.d("EditProfileScreen", "Profile successfully updated")
            }
            .addOnFailureListener { e ->
                Log.e("EditProfileScreen", "Error updating profile", e)
            }
    }
}

private fun updateProfilePictureUrl(imageUrl: String) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val userId = auth.currentUser?.uid
    if (userId != null) {
        db.collection("users").document(userId)
            .update("profilePicture", imageUrl)
            .addOnSuccessListener {
                Log.d("EditProfileScreen", "Profile picture URL successfully updated")
            }
            .addOnFailureListener { e ->
                Log.e("EditProfileScreen", "Error updating profile picture URL", e)
            }
    }
}

@Composable
fun CustomTextField(value: String, label: String) {
    Box(
        modifier = Modifier
            .border(1.dp, Color.Blue, RoundedCornerShape(4.dp))
            .background(Color.White, RoundedCornerShape(4.dp))
    ) {
        TextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            enabled = false, // Make the TextField read-only
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}