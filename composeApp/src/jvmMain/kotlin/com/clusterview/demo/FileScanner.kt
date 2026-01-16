package com.clusterview.demo

import java.io.File
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

object FileScanner {

    fun scanFolder(rootPath: String, onProgress: (Int) -> Unit) {
        val root = Paths.get(rootPath)
        val fileList = mutableListOf<FileEntry>()
        var count = 0

        Files.walkFileTree(root, object : SimpleFileVisitor<Path>() {
            override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult {
                val file = path.toFile()
                if (!file.isHidden) {
                    fileList.add(
                        FileEntry(
                            name = file.name,
                            path = file.absolutePath,
                            extension = file.extension.lowercase(),
                            size = file.length(),
                            lastModified = file.lastModified()
                        )
                    )
                    count++

                    // Batch save every 500 files to keep memory low
                    if (fileList.size >= 500) {
                        DatabaseManager.batchInsertFiles(fileList)
                        fileList.clear()
                        onProgress(count)
                    }
                }
                return FileVisitResult.CONTINUE
            }

            override fun visitFileFailed(file: Path, exc: java.io.IOException): FileVisitResult {
                return FileVisitResult.SKIP_SUBTREE // Skip folders we can't access
            }
        })

        // Insert remaining files
        if (fileList.isNotEmpty()) {
            DatabaseManager.batchInsertFiles(fileList)
            onProgress(count)
        }
    }
}