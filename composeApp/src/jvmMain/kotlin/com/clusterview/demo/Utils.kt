package com.clusterview.demo

import java.awt.Desktop
//import java.awt.FileDialog
//import java.awt.Frame
import javax.swing.JFileChooser
import java.io.File
import java.io.PrintWriter

fun openFolderPicker(): String? {
    val chooser = JFileChooser()
    // THIS IS THE CRITICAL LINE: Force it to select directories only
    chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    chooser.dialogTitle = "Select Folder to Index"

    val result = chooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        chooser.selectedFile.absolutePath
    } else null
}

fun openFileInSystem(filePath: String) {
    try {
        val file = File(filePath)
        if (file.exists()) {
            // This triggers the default OS application for the file type
            Desktop.getDesktop().open(file)
        } else {
            println("Error: File not found at $filePath")
        }
    } catch (e: Exception) {
        println("Could not open file: ${e.message}")
    }
}

fun openFolderInSystem(filePath: String) {
    try {
        val file = File(filePath)
        val parentDir = file.parentFile
        if (parentDir != null && parentDir.exists()) {
            Desktop.getDesktop().open(parentDir)
        }
    } catch (e: Exception) {
        println("Could not open folder: ${e.message}")
    }
}

fun exportToCSV(files: List<FileEntry>, clusterName: String) {
    val chooser = JFileChooser()
    chooser.dialogTitle = "Save Export As"
    // Set a default file name
    chooser.selectedFile = File("${clusterName.replace(" ", "_")}_Export.csv")

    // FIX: Changed APPROVE_Dialog to APPROVE_OPTION
    val result = chooser.showSaveDialog(null)

    if (result == JFileChooser.APPROVE_OPTION) {
        try {
            val saveFile = chooser.selectedFile
            PrintWriter(saveFile).use { writer ->
                // CSV Header - Professional formatting
                writer.println("ID,Name,Extension,Size_Bytes,Path")

                // Data Rows
                files.forEach { file ->
                    // We use replace(",", "") to ensure commas in filenames don't break the CSV format
                    val cleanName = file.name.replace(",", "")
                    val cleanPath = file.path.replace(",", "")
                    writer.println("${file.id},$cleanName,${file.extension},${file.size},$cleanPath")
                }
            }
            println("Export successful: ${saveFile.absolutePath}")
        } catch (e: Exception) {
            println("Export failed: ${e.message}")
            e.printStackTrace()
        }
    }
}