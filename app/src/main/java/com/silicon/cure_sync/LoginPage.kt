package com.silicon.cure_sync

import android.app.Activity
import android.content.Context
import java.util.concurrent.TimeUnit
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import java.util.UUID



@Composable
fun LoginScreen(navController: NavHostController) {
    var phoneNumberOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val isFormValid = remember(phoneNumberOrEmail, password) {
        phoneNumberOrEmail.isNotBlank() && password.isNotBlank()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Changed background color to white
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo_cs1),
                    contentDescription = "App Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 0.dp)
                )

                // CureSync Text
                Text(
                    text = "CureSync",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(0xFFF7E300), // Yellow color
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    ),
                    modifier = Modifier.padding(bottom = 16.dp) // Add padding below the text
                )

                // Slogan
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

                // Login Heading
                Text(
                    text = "Login",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF405EA4) // Blue color
                    ),
                    modifier = Modifier.padding(bottom = 16.dp) // Padding below
                )

                // Phone Number or Email Text Field
                OutlinedTextField(
                    value = phoneNumberOrEmail,
                    onValueChange = { phoneNumberOrEmail = it },
                    label = { Text("Phone Number / Email", color = Color.Black) },
                    textStyle = TextStyle(color = Color.Black),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Password Text Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = Color.Black) },
                    textStyle = TextStyle(color = Color.Black),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color.Black
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Forget Password Text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("forgot_password") // Navigate to ForgotPasswordScreen
                        },
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Forget ",
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Password?",
                        color = Color(0xFF006400), // Dark Green color
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Login Button
                Button(
                    onClick = { signIn(phoneNumberOrEmail, password, navController,context) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormValid) Color(0xFF405EA4) else Color(0xFFB0BEC5), // Button color
                        contentColor = if (isFormValid) Color.White else Color(0xFF405EA4) // Text color
                    ),
                    enabled = isFormValid,
                    border = if (!isFormValid) BorderStroke(2.dp, Color(0xFF405EA4)) else null,
                    elevation = if (!isFormValid) ButtonDefaults.elevatedButtonElevation(8.dp) else ButtonDefaults.elevatedButtonElevation(
                        0.dp
                    ),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Login",
                        fontSize = 16.sp,
                        color = if (isFormValid) Color.White else Color(0xFF405EA4),
                        style = TextStyle(
                            shadow = if (!isFormValid) Shadow(
                                color = Color(0xFF405EA4),
                                offset = Offset(1f, 1f),
                                blurRadius = 2f
                            ) else null
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Signup Text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Do not have an account? ",
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Signup",
                        color = Color(0xFF405EA4),
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            navController.navigate("signup")
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun SignupScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordsMatch by remember { mutableStateOf(true) }
    val context = LocalContext.current

    val isFormValid = remember(name, mobileNumber, email, password, confirmPassword) {
        name.isNotBlank() &&
                mobileNumber.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank()
    }

    LaunchedEffect(password, confirmPassword) {
        passwordsMatch = password == confirmPassword
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)), // Light Blue color
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo_cs1),
                    contentDescription = "App Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 0.dp)
                )

                Text(
                    text = "CureSync",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(0xFFF7E300),
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    ),
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Black)) {
                                append("Name ")
                            }
                            withStyle(style = SpanStyle(color = Color.Red)) {
                                append("*")
                            }
                        })
                    },
                    textStyle = TextStyle(color = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = {
                        Text(buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Black)) {
                                append("Mobile Number ")
                            }
                            withStyle(style = SpanStyle(color = Color.Red)) {
                                append("*")
                            }
                        })
                    },
                    textStyle = TextStyle(color = Color.Black),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Black)) {
                                append("Email ")
                            }
                            withStyle(style = SpanStyle(color = Color.Red)) {
                                append("*")
                            }
                        })
                    },
                    textStyle = TextStyle(color = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                var passwordVisible by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Black)) {
                                append("Password ")
                            }
                            withStyle(style = SpanStyle(color = Color.Red)) {
                                append("*")
                            }
                        })
                    },
                    textStyle = TextStyle(color = Color.Black),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color.Black // Eye icon in black
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                var confirmPasswordVisible by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = {
                        Text(buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Black)) {
                                append("Confirm Password ")
                            }
                            withStyle(style = SpanStyle(color = Color.Red)) {
                                append("*")
                            }
                        })
                    },
                    textStyle = TextStyle(color = Color.Black),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = Color.Black
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Show error message if passwords don't match
                if (!passwordsMatch && confirmPassword.isNotBlank()) {
                    Text(
                        text = "Passwords do not match",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (isFormValid && passwordsMatch) {
                            signUp(name, mobileNumber, email, password, navController, context)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormValid && passwordsMatch) Color(0xFF405EA4) else Color(0xFFB0BEC5), // Button color
                        contentColor = if (isFormValid && passwordsMatch) Color.White else Color(0xFF405EA4) // Text color
                    ),
                    enabled = isFormValid,
                    border = if (!isFormValid || !passwordsMatch) BorderStroke(2.dp, Color(0xFF405EA4)) else null, // Border color for the faded button
                    elevation = if (!isFormValid || !passwordsMatch) ButtonDefaults.elevatedButtonElevation(8.dp) else ButtonDefaults.elevatedButtonElevation(0.dp), // Shadow
                    modifier = Modifier
                        .padding(vertical = 8.dp) // Add vertical padding
                ) {
                    Text(
                        text = "Signup",
                        fontSize = 16.sp,
                        color = if (isFormValid && passwordsMatch) Color.White else Color(0xFF405EA4), // Text color based on button state
                        style = TextStyle(
                            shadow = if (!isFormValid || !passwordsMatch) Shadow(
                                color = Color(0xFF405EA4), // Shadow color
                                offset = Offset(1f, 1f),
                                blurRadius = 2f
                            ) else null
                        )
                    )
                }
            }
        }
    }
}



@Composable
fun OtpScreen(navController: NavController) {
    var otp by remember { mutableStateOf("") }
    var isOtpValid by remember { mutableStateOf(true) } // Track OTP validity
    var otpErrorMessage by remember { mutableStateOf("") } // Error message state
    var verificationId by remember { mutableStateOf<String?>(null) }
    var phoneNumber by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    val isPhoneNumberValid = phoneNumber.length == 10
    val isOtpInputValid = otp.length == 6  // Assuming OTP is 6 digits long
    val context = LocalContext.current

    val callbacks = remember { // Changed: Wrapped in remember
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Handle automatic OTP verification
                signInWithPhoneAuthCredential(credential, navController)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                otpErrorMessage = e.message ?: "Verification failed. Please try again."
                isOtpValid = false
            }

            override fun onCodeSent(
                sentVerificationId: String, // Changed: verificationId renamed to sentVerificationId
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                verificationId = sentVerificationId // Changed: Directly assigned to verificationId
                isOtpSent = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Set background color to white
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)), // Light Blue color
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo_cs1),
                    contentDescription = "App Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 16.dp)
                )

                // OTP Verification Text
                Text(
                    text = "OTP Verification",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(0xFF405EA4) // Blue color
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // OTP Instruction
                Text(
                    text = "Enter the 6-digit code sent to your phone",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                // Phone Number Input Field
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Enter Phone Number", color = Color.Black) },
                    textStyle = TextStyle(color = Color.Black),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Send OTP Button
                Button(
                    onClick = {
                        if (isPhoneNumberValid) {
                            sendOtp("+91$phoneNumber", context, callbacks)
                            isOtpSent = true
                        } else {
                            otpErrorMessage = "Please enter a valid phone number."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPhoneNumberValid) Color(0xFF405EA4) else Color(
                            0xFFB0BEC5
                        ),
                        contentColor = if (isPhoneNumberValid) Color.White else Color(0xFF405EA4)
                    ),
                    enabled = isPhoneNumberValid,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Send OTP",
                        fontSize = 16.sp,
                        color = if (isPhoneNumberValid) Color.White else Color(0xFF405EA4)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isOtpSent) {
                    // OTP Input Field
                    OutlinedTextField(
                        value = otp,
                        onValueChange = { otp = it },
                        label = { Text("Enter OTP", color = Color.Black) },
                        textStyle = TextStyle(color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))


                    // Show error message if OTP is invalid
                    if (!isOtpValid && otp.isNotBlank()) {
                        Text(
                            text = otpErrorMessage,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Resend OTP Text
                    Text(
                        text = "Didn't receive the code? Resend OTP",
                        color = Color(0xFF006400), // Dark Green color
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Verify Button
                    Button(
                        onClick = {
                            // Handle OTP verification action
                            if (isOtpInputValid) {
                                verificationId?.let {
                                    verifyOtp(it, otp, navController)
                                } ?: run {
                                    otpErrorMessage = "Error verifying OTP. Please try again."
                                    isOtpValid = false
                                }
                            } else {
                                otpErrorMessage = "Please enter a valid 6-digit OTP."
                                isOtpValid = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOtpInputValid) Color(0xFF405EA4) else Color(
                                0xFFB0BEC5
                            ), // Button color
                            contentColor = if (isOtpInputValid) Color.White else Color(0xFF405EA4) // Text color
                        ),
                        enabled = isOtpInputValid,
                        border = BorderStroke(
                            2.dp,
                            if (isOtpInputValid) Color.Transparent else Color.Black // Red border if invalid
                        ),
                        elevation = if (isOtpInputValid) {
                            ButtonDefaults.elevatedButtonElevation(4.dp) // Default elevation
                        } else {
                            ButtonDefaults.elevatedButtonElevation(8.dp) // Elevated shadow if invalid
                        },
                        modifier = Modifier
                            .padding(vertical = 8.dp)

                    ) {
                        Text(
                            text = "Verify",
                            fontSize = 16.sp,
                            color = if (isOtpInputValid) Color.White else Color(0xFF405EA4)
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun ForgotPasswordScreen(navController: NavController) {
        var phoneNumber by remember { mutableStateOf("") }
        var phoneNumberErrorMessage by remember { mutableStateOf("") }

        val context = LocalContext.current as Activity
        val isPhoneNumberValid = phoneNumber.length in 10..15 // Validate phone number length

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White), // Set background color to white
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)), // Light Blue color
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo
                    Image(
                        painter = painterResource(id = R.drawable.logo_cs1),
                        contentDescription = "App Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp)
                    )

                    // Forgot Password Title
                    Text(
                        text = "Forgot Password",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF405EA4) // Blue color
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Instructions
                    Text(
                        text = "Enter your phone number to receive a password reset code",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Phone Number Input Field
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Phone Number", color = Color.Black) },
                        textStyle = TextStyle(color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Show error message if phone number is invalid
                    if (phoneNumberErrorMessage.isNotEmpty()) {
                        Text(
                            text = phoneNumberErrorMessage,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reset Code Button
                    Button(
                        onClick = {
                            if (isPhoneNumberValid) {
                                val formattedPhoneNumber = "+91$phoneNumber"
                                // Start the phone number verification process
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    formattedPhoneNumber,        // Phone number to verify
                                    60,                 // Timeout duration
                                    TimeUnit.SECONDS,   // Unit of timeout
                                    context as Activity,  // Activity (for callback binding)
                                    object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                            // This is called when the verification is done automatically (e.g., instant verification)
                                        }

                                        override fun onVerificationFailed(e: FirebaseException) {
                                            // Handle the error
                                            phoneNumberErrorMessage = "Failed to send verification code: ${e.message}"
                                        }

                                        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                                            // Save the verification ID and resending token for later use
                                            navController.navigate("reset_password/$verificationId")
                                        }
                                    }
                                )
                            } else {
                                phoneNumberErrorMessage = "Please enter a valid phone number."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPhoneNumberValid) Color(0xFF405EA4) else Color(
                                0xFFB0BEC5
                            ), // Button color
                            contentColor = if (isPhoneNumberValid) Color.White else Color(0xFF405EA4) // Text color
                        ),
                        enabled = isPhoneNumberValid,
                        border = if (!isPhoneNumberValid) BorderStroke(
                            2.dp,
                            Color(0xFF405EA4)
                        ) else null, // Border color for the faded button
                        elevation = if (!isPhoneNumberValid) ButtonDefaults.elevatedButtonElevation(
                            8.dp
                        ) else ButtonDefaults.elevatedButtonElevation(0.dp), // Shadow
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Reset Code",
                            fontSize = 16.sp,
                            color = if (isPhoneNumberValid) Color.White else Color(0xFF405EA4)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Back to Login Button
                    Button(
                        onClick = { navController.navigate("login") }, // Correct destination
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF405EA4),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Back to Login",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }


@Composable
fun ResetPasswordScreen(navController: NavController, verificationId: String ) {
        var otp by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var otpErrorMessage by remember { mutableStateOf("") }
        var passwordErrorMessage by remember { mutableStateOf("") }

        val context = LocalContext.current

        val isOtpValid = otp.length == 6
        val isPasswordValid = newPassword.length >= 6
        val arePasswordsMatching = newPassword == confirmPassword

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White), // Set background color to white
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)), // Light Blue color
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo
                    Image(
                        painter = painterResource(id = R.drawable.logo_cs1),
                        contentDescription = "App Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp)
                    )

                    // Reset Password Title
                    Text(
                        text = "Reset Your Password",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF405EA4) // Blue color
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Instructions
                    Text(
                        text = "Enter the 6-digit code sent to your phone.",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // OTP Input Field
                    OutlinedTextField(
                        value = otp,
                        onValueChange = { otp = it },
                        label = { Text("Enter OTP", color = Color.Black) },
                        textStyle = TextStyle(color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Show error message if OTP is invalid
                    if (!isOtpValid && otp.isNotBlank()) {
                        Text(
                            text = otpErrorMessage.ifBlank { "Invalid OTP. Please try again." },
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // New Password Input Field
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password", color = Color.Black) },
                        textStyle = TextStyle(color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Confirm Password Input Field
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password", color = Color.Black) },
                        textStyle = TextStyle(color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Show error messages if passwords are not matching
                    if (!arePasswordsMatching && confirmPassword.isNotBlank()) {
                        Text(
                            text = passwordErrorMessage.ifBlank { "Passwords do not match. Please try again." },
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (isOtpValid && isPasswordValid && arePasswordsMatching) {
                                //  Firebase password reset logic
                                val credential = PhoneAuthProvider.getCredential(
                                    verificationId,
                                    otp
                                )  // Get credential using OTP
                                FirebaseAuth.getInstance()
                                    .signInWithCredential(credential)  //  Sign in with credential
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // Sign in success
                                            FirebaseAuth.getInstance().currentUser?.let { user ->
                                                user.updatePassword(newPassword)  // ( Update the password
                                                    .addOnCompleteListener { passwordTask ->
                                                        if (passwordTask.isSuccessful) {
                                                            // Password reset success
                                                            Toast.makeText(
                                                                context,
                                                                "Password reset successful.",  //  Toast message for success
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            navController.navigate("password_reset_success") {
                                                                popUpTo("reset_password") {
                                                                    inclusive = true
                                                                }
                                                            }
                                                        } else {
                                                            // Password reset failed
                                                            Toast.makeText(
                                                                context,
                                                                "Failed to reset password. Please try again.",  //  Toast message for failure
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    }
                                            }
                                        } else {
                                            // Sign in failed
                                            if (task.exception is FirebaseAuthInvalidCredentialsException) {  //  Handle invalid OTP
                                                otpErrorMessage = "Invalid OTP. Please try again."
                                            } else {
                                                otpErrorMessage = "Failed to verify OTP. Please try again."  //  Handle OTP verification failure
                                            }
                                        }
                                    }
                            } else {
                                otpErrorMessage =
                                    if (!isOtpValid) "Please enter a valid 6-digit OTP." else ""
                                passwordErrorMessage =
                                    if (!isPasswordValid) "Password must be at least 6 characters long." else if (!arePasswordsMatching) "Passwords do not match." else ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOtpValid && isPasswordValid && arePasswordsMatching) Color(
                                0xFF405EA4
                            ) else Color(0xFFB0BEC5), // Button color
                            contentColor = if (isOtpValid && isPasswordValid && arePasswordsMatching) Color.White else Color(
                                0xFF405EA4
                            ) // Text color
                        ),
                        enabled = isOtpValid && isPasswordValid && arePasswordsMatching,
                        border = if (!(isOtpValid && isPasswordValid && arePasswordsMatching)) BorderStroke(
                            2.dp,
                            Color(0xFF405EA4)
                        ) else null, // Border color for the faded button
                        elevation = if (!(isOtpValid && isPasswordValid && arePasswordsMatching)) ButtonDefaults.elevatedButtonElevation(
                            8.dp
                        ) else ButtonDefaults.elevatedButtonElevation(0.dp), // Shadow
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Submit",
                            fontSize = 16.sp,
                            color = if (isOtpValid && isPasswordValid && arePasswordsMatching) Color.White else Color(
                                0xFF405EA4
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Back to Login Button
                    Button(
                        onClick = { navController.navigate("login") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF405EA4),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Back to Login",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }


@Composable
fun PasswordResetSuccessScreen(navController: NavController) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White), // Set background color to white
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)), // Light Blue color
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo
                    Image(
                        painter = painterResource(id = R.drawable.logo_cs1),
                        contentDescription = "App Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp)
                    )

                    // Success Title
                    Text(
                        text = "Password Reset Successful",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF405EA4) // Blue color
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Success Message
                    Text(
                        text = "Your password has been successfully reset. You can now log in with your new password.",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Back to Login Button
                    Button(
                        onClick = {
                            // Clear navigation stack and navigate to login screen
                            navController.popBackStack("login", inclusive = false)
                            navController.navigate("login")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF405EA4),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Back to Login",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }

val auth = FirebaseAuth.getInstance()
fun signUp(name: String, mobileNumber: String, email: String, password: String, navController: NavController, context: Context) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener { task ->
            if (task != null) {
                val user = auth.currentUser
                if (user != null) {
                    println("User Created with UID: ${user.uid}")

                    // Create a Firestore instance
                    val db = FirebaseFirestore.getInstance()

                    // Create a new user with the provided information
                    val userMap = hashMapOf(
                        "name" to name,
                        "mobileNumber" to mobileNumber,
                        "email" to email,
                        "uid" to user.uid
                    )

                    // Add a new document with the user's UID as the document ID
                    db.collection("users").document(user.uid)
                        .set(userMap)
                        .addOnSuccessListener {
                            println("User data added to Firestore")

                            // Navigate to the next screen (e.g., OTP verification, home, etc.)
                            navController.navigate("otp") {
                                popUpTo("signup") { inclusive = true }
                            }
                        }
                        .addOnFailureListener { exception ->
                            println("Error adding user data to Firestore: ${exception.message}")
                            Toast.makeText(context, "Failed to save user data: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
        }
        .addOnFailureListener { exception ->
            println("Sign up failed: ${exception.message}")
            Toast.makeText(context, "Sign up failed: ${exception.message}", Toast.LENGTH_LONG).show()
        }
}
fun signIn(phoneNumberOrEmail: String, password: String, navController: NavHostController,context: Context) {
    println("Attempting to sign in with email: $phoneNumberOrEmail") // Logging **MARKED LINE**

    if (phoneNumberOrEmail.contains("@")) {
        auth.signInWithEmailAndPassword(phoneNumberOrEmail, password)
            .addOnSuccessListener { task ->
                if (task.user != null) {
                    println("User Logged In")
                    val user = auth.currentUser
                    println("User UID: ${user?.uid}")

                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    println("User is null after login") // Logging **MARKED LINE**
                }
            }
            .addOnFailureListener { exception ->
                println("Login failed: ${exception.message}") // Logging **MARKED LINE**
                // Optionally, display a message to the user
                Toast.makeText(context, "Login failed: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    } else {
        println("Login with phone number not supported, please use an email address.") // Logging **MARKED LINE**
    }
}

    fun sendOtp(
        phoneNumber: String,
        context: Context,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(verificationId: String, otp: String, navController: NavController) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithPhoneAuthCredential(credential, navController)
    }

    fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        navController: NavController
    ) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Handle successful OTP verification, navigate to the next screen
                    navController.navigate("home")
                } else {
                    // Handle OTP verification failure
                    Log.e("OtpScreen", "Sign-in failed", task.exception)
                    // Handle exception (e.g., show a message to the user)
                }
            }
    }