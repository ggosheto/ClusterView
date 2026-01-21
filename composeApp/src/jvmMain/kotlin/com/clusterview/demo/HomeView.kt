package com.clusterview.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeView(onImportClick: () -> Unit) {
    // Uses the SpaceBackground brush defined in your Theme.kt
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBackground),
        contentAlignment = Alignment.Center
    ) {
        // --- CINEMATIC BACKGROUND DECORATION ---
        // This giant "CV" adds that high-end WLT design depth
        Text(
            text = "CV",
            modifier = Modifier
                .alpha(0.04f)
                .offset(y = (-40).dp),
            fontSize = 380.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- SUB-HEADER ---
            Text(
                text = "DATABASE SYSTEM",
                color = GalacticTan.copy(alpha = 0.5f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp
            )

            Spacer(Modifier.height(12.dp))

            // --- MAIN HERO TITLE ---
            Text(
                text = "CLUSTER VIEW",
                color = Color.White,
                fontSize = 58.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-2).sp
            )

            Spacer(Modifier.height(60.dp))

            // --- THE INTERACTIVE SCAN BUTTON ---
            // Designed to look like a glowing holographic interface
            Surface(
                onClick = onImportClick,
                shape = RoundedCornerShape(50),
                color = Color.Transparent,
                modifier = Modifier
                    .width(300.dp)
                    .height(64.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(GalacticTan.copy(alpha = 0.6f), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(50)
                    )
            ) {
                Box(
                    modifier = Modifier.background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.05f), Color.Transparent)
                        )
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = GalacticTan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = "INITIALIZE DIRECTORY SCAN",
                            color = GalacticTan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- FOOTER CAPTION ---
            Text(
                text = "READY FOR ARCHIVE INGESTION",
                color = Color.White.copy(alpha = 0.2f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 3.sp
            )
        }

        // --- DECORATIVE CORNER ELEMENT (OPTIONAL) ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(40.dp)
                .size(100.dp)
                .alpha(0.1f)
                .border(1.dp, Color.White, RoundedCornerShape(topStart = 100.dp))
        )
    }
}