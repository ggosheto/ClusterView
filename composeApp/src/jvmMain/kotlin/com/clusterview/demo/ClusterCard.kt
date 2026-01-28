package com.clusterview.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.ui.unit.dp

/*
@Composable
fun ClusterCard(summary: ClusterSummary, onClick: () -> Unit) {
    // Define your custom colors locally if not using a Theme file
    val OxfordBlue = Color(0, 33, 71)
    val Tan = Color(210, 180, 140)

    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() }
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Label Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(OxfordBlue, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = summary.name.uppercase(),
                        color = Tan,
                        style = MaterialTheme.typography.caption,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Middle Content
            Column {
                Text(
                    text = summary.name,
                    color = OxfordBlue,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "${summary.fileCount} items found",
                    color = Color.Gray,
                    style = MaterialTheme.typography.body2
                )
            }

            // Footer Accent: Progress bar in Tan
            LinearProgressIndicator(
                progress = 1f, // We can link this to a real % later
                color = Tan,
                backgroundColor = Tan.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
            )
        }
    }
}*/

@Composable
fun ClusterCard(cluster: ClusterSummary, onClick: () -> Unit) {
    val OxfordBlue = Color(0, 33, 71)
    val Tan = Color(210, 180, 140)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = 3.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text(cluster.name.uppercase(), fontWeight = FontWeight.Black, color = OxfordBlue)
                Icon(Icons.Default.Folder, null, tint = Tan)
            }

            Spacer(Modifier.height(16.dp))

            // Fixed visualization logic
            MiniDistributionBar(cluster.id)

            Spacer(Modifier.height(12.dp))
            Text("VIEW CLUSTER", style = MaterialTheme.typography.overline, color = Tan, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MiniDistributionBar(clusterId: Int) {
    // We use explicit typing here to stop the "Cannot infer type" errors
    val files: List<FileEntry> = remember(clusterId) { DatabaseManager.getFilesByCluster(clusterId) }
    val totalSize: Long = files.sumOf { it.size }

    if (files.isEmpty() || totalSize == 0L) return

    val stats = files.groupBy { it.extension.lowercase() }
        .mapValues { entry ->
            val extensionFiles: List<FileEntry> = entry.value
            extensionFiles.sumOf { it.size }.toFloat() / totalSize
        }
        .toList()
        .sortedByDescending { it.second }
        .take(3)

    val colors = listOf(Color(0xFFD2B48C), Color(0xFF5DADE2), Color(0xFF58D68D))

    Row(Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(Color.LightGray.copy(0.2f))) {
        stats.forEachIndexed { i, pair ->
            Box(Modifier.weight(pair.second.coerceAtLeast(0.01f)).fillMaxHeight().background(colors.getOrElse(i) { Color.Gray }))
        }
    }
}
