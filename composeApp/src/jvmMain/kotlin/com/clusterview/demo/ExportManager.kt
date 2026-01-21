package com.clusterview.demo

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

object ExportManager {
    fun exportToFolder(clusterName: String, files: List<FileEntry>) {
        // 1. Define the destination (usually a 'Clusters' folder on the Desktop or project root)
        val exportRoot = java.io.File("Exported_Clusters")
        val clusterFolder = java.io.File(exportRoot, clusterName)

        try {
            // 2. THIS IS THE KEY FIX: Create the directories if they don't exist
            if (!clusterFolder.exists()) {
                val created = clusterFolder.mkdirs()
                println("Folder creation status: $created")
            }

            // 3. Copy each file into the new folder
            files.forEach { fileEntry ->
                val source = java.io.File(fileEntry.path)
                val destination = java.io.File(clusterFolder, source.name)

                if (source.exists()) {
                    source.copyTo(destination, overwrite = true)
                }
            }

            println("Export Complete: ${files.size} files moved to ${clusterFolder.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}