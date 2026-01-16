package com.clusterview.demo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application



fun main() = application {
    // Initialize DB before window starts
    DatabaseManager.initDatabase()

    // Test Insert (You can remove this after checking)
    DatabaseManager.insertFile(FileEntry(name = "Test.txt", path = "C:/Test.txt", extension = "txt", size = 1024, lastModified = 0))
    println("Files in DB: ${DatabaseManager.getAllFiles().size}")

    Window(onCloseRequest = ::exitApplication, title = "ClusterView") {
        App()
    }
}