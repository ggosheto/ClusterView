package com.clusterview.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignUpView(onSignUpSuccess: () -> Unit, onNavigateToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val OxfordBlue = Color(0, 33, 71)
    val Tan = Color(210, 180, 140)

    Box(modifier = Modifier.fillMaxSize().background(OxfordBlue), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.width(450.dp).padding(16.dp),
            shape = RoundedCornerShape(24.dp), // More rounded for modern look
            backgroundColor = Color(10, 43, 81), // Slightly lighter blue for depth
            elevation = 12.dp
        ) {
            Column(Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("CREATE ACCOUNT", color = Tan, style = MaterialTheme.typography.h4, fontWeight = FontWeight.ExtraBold)
                Text("Join the ClusterView ecosystem", color = Color.Gray, style = MaterialTheme.typography.caption)

                Spacer(Modifier.height(32.dp))

                // Modern Outlined Text Fields with Tan accents
                FuturisticTextField(value = email, onValueChange = { email = it }, label = "Email Address")
                Spacer(Modifier.height(16.dp))
                FuturisticTextField(value = password, onValueChange = { password = it }, label = "Password", isPassword = true)
                Spacer(Modifier.height(16.dp))
                FuturisticTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = "Confirm Password", isPassword = true)

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        if(password == confirmPassword && email.isNotEmpty()) {
                            // 1. Save to Database
                            val success = DatabaseManager.registerUser(email, password)
                            if (success) {
                                onSignUpSuccess() // Navigates back to Login
                            }
                            else{
                                Exception("User already exists")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Tan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("REGISTER", color = OxfordBlue, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }

                TextButton(onClick = onNavigateToLogin) {
                    Text("Already have an account? Sign In", color = Tan.copy(alpha = 0.7f))
                }
            }
        }
    }
}

private fun DatabaseManager.registerUser(email: String, password: String) {}
