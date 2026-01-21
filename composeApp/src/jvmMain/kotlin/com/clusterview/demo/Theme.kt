package com.clusterview.demo // Ensure this matches your package name

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset

// 1. CINEMATIC COLORS
val DeepSpace = Color(0xFF00050A)    // The near-black "void"
val OxfordBlue = Color(0xFF002147)   // Your core brand blue
val GalacticTan = Color(0xFFD2B48C)  // Your "starlight" accent
val NebulaGlow = Color(0xFF001A33)    // Subtle highlight for depth

// 2. THE DYNAMIC BACKGROUND BRUSH
// This creates the "Nebula" effect from your reference image
val SpaceBackground = Brush.radialGradient(
    0.0f to NebulaGlow,
    1.0f to DeepSpace,
    center = Offset(0.5f, 0.2f), // Shifts the "glow" slightly to the top
    radius = 1500f
)

// 3. GLASSMORPHISM HELPER
val GlassWhite = Color.White.copy(alpha = 0.05f)
val GlassBorder = Color.White.copy(alpha = 0.15f)