package com.clusterview.demo

import java.io.File
import java.io.PrintWriter
import java.awt.Desktop
import androidx.compose.ui.graphics.Color
import javax.swing.JFileChooser

fun openFolderPicker(): String? {
    val chooser = JFileChooser()
    chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    val result = chooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) chooser.selectedFile.absolutePath else null
}

fun saveFilesToCSV(files: List<FileEntry>, fileName: String) {
    try {
        val userHome = System.getProperty("user.home")
        val desktopPath = File(userHome, "Desktop")
        val finalFile = File(desktopPath, fileName)

        PrintWriter(finalFile).use { writer ->
            writer.println("ID,Name,Extension,Size_Bytes,Path")
            files.forEach { file ->
                writer.println("${file.id},${file.name.replace(",", "")},${file.extension},${file.size},${file.path.replace(",", "")}")
            }
        }
        if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(desktopPath)
    } catch (e: Exception) { e.printStackTrace() }
}

fun getIconColorForExtension(ext: String): Color {
    return when (ext.lowercase()) {
        "pdf" -> Color(0xFFD32F2F)
        "jpg", "png", "jpeg" -> Color(0xFF388E3C)
        else -> Color(0xFF607D8B)
    }
}