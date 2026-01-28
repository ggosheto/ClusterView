package com.clusterview.demo

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// Main Entry Point for Home
@Composable
fun HomeView(
    user: User?,
    clusters: List<ClusterSummary>,
    onImportClick: () -> Unit,
    onClusterClick: (ClusterSummary) -> Unit,
    onLogout: () -> Unit // This MUST match the parameter used in the call above
) {
    val OxfordBlue = Color(0, 33, 71)
    val Tan = Color(210, 180, 140)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome, ${user?.username ?: "User"}", color = Color.White) },
                backgroundColor = OxfordBlue,
                actions = {
                    // Logout Button in the top right
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, "Logout", tint = Tan)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onImportClick, backgroundColor = Tan) {
                Icon(Icons.Default.Add, "Import", tint = OxfordBlue)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("Your Clusters", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            if (clusters.isEmpty()) {
                Text("No clusters found. Click '+' to import a folder.")
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 300.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(clusters) { cluster ->
                        ClusterCard(cluster) { onClusterClick(cluster) }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyLandingPage(onImportClick: () -> Unit, brandColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.CloudUpload, null, tint = brandColor.copy(0.1f), modifier = Modifier.size(140.dp))
        Text("Welcome to ClusterView", style = MaterialTheme.typography.h4, color = brandColor, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onImportClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(210, 180, 140)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("CHOOSE FOLDER", color = brandColor, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun DashboardContent(
    clusters: List<ClusterSummary>,
    onImportClick: () -> Unit,
    onClusterClick: (ClusterSummary) -> Unit,
    brandColor: Color
) {
    Column(Modifier.fillMaxSize().padding(32.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("Dashboard", style = MaterialTheme.typography.h4, fontWeight = FontWeight.Black, color = brandColor)
            OutlinedButton(onClick = onImportClick) { Text("IMPORT NEW", color = brandColor) }
        }
        Spacer(Modifier.height(32.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 280.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(clusters) { cluster ->
                ClusterCard(cluster) { onClusterClick(cluster) }
            }
        }
    }
}