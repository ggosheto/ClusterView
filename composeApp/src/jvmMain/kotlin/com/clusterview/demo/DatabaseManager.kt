package com.clusterview.demo

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

object DatabaseManager {
    private const val DB_URL = "jdbc:sqlite:clusterview.db"

    // Hold one active connection instead of opening/closing constantly
    private var connection: Connection? = null

    fun getConnection(): Connection {
        if (connection == null || connection!!.isClosed) {
            connection = DriverManager.getConnection(DB_URL)
        }
        return connection!!
    }

    // Call this only when the app completely closes
    fun closeDatabase() {
        connection?.close()
    }

    fun initDatabase() {
        getConnection().use { conn ->
            val statement = conn.createStatement()
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_file_name ON files(name)")

            // Create Clusters Table
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS clusters (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    color TEXT
                )
            """.trimIndent())

            // Create Files Table
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS files (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    path TEXT UNIQUE NOT NULL,
                    extension TEXT,
                    size INTEGER,
                    last_modified INTEGER,
                    cluster_id INTEGER,
                    FOREIGN KEY (cluster_id) REFERENCES clusters(id)
                )
            """.trimIndent())
        }
    }

    fun insertFile(file: FileEntry) {
        val sql = "INSERT OR REPLACE INTO files (name, path, extension, size, last_modified, cluster_id) VALUES (?, ?, ?, ?, ?, ?)"
        getConnection().use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setString(1, file.name)
                pstmt.setString(2, file.path)
                pstmt.setString(3, file.extension)
                pstmt.setLong(4, file.size)
                pstmt.setLong(5, file.lastModified)
                pstmt.setObject(6, file.clusterId)
                pstmt.executeUpdate()
            }
        }
    }

    fun getAllFiles(): List<FileEntry> {
        val files = mutableListOf<FileEntry>()
        getConnection().use { conn ->
            val rs = conn.createStatement().executeQuery("SELECT * FROM files")
            while (rs.next()) {
                files.add(FileEntry(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("path"),
                    rs.getString("extension"),
                    rs.getLong("size"),
                    rs.getLong("last_modified"),
                    rs.getInt("cluster_id")
                ))
            }
        }
        return files
    }

    fun batchInsertFiles(files: List<FileEntry>) {
        val sql = "INSERT OR REPLACE INTO files (name, path, extension, size, last_modified, cluster_id) VALUES (?, ?, ?, ?, ?, ?)"
        getConnection().use { conn ->
            conn.autoCommit = false // Start transaction
            try {
                conn.prepareStatement(sql).use { pstmt ->
                    for (file in files) {
                        pstmt.setString(1, file.name)
                        pstmt.setString(2, file.path)
                        pstmt.setString(3, file.extension)
                        pstmt.setLong(4, file.size)
                        pstmt.setLong(5, file.lastModified)
                        pstmt.setObject(6, file.clusterId)
                        pstmt.addBatch()
                    }
                    pstmt.executeBatch()
                }
                conn.commit() // Commit all at once
            } catch (e: Exception) {
                conn.rollback()
                throw e
            }
        }
    }

    // 1. This is the data model for the UI


    // 2. This function talks to the DB to get the clusters + file counts
    // Example: Correct Resource Cleanup for getClusterSummaries
    fun getClusterSummaries(): List<ClusterSummary> {
        val summaries = mutableListOf<ClusterSummary>()
        val sql = "SELECT c.id, c.name, c.color, COUNT(f.id) as file_count FROM clusters c LEFT JOIN files f ON c.id = f.cluster_id GROUP BY c.id"

        // 1. Connection is opened and automatically closed
        getConnection().use { conn ->
            // 2. Statement is opened and automatically closed
            conn.createStatement().use { stmt ->
                // 3. ResultSet is opened and automatically closed
                stmt.executeQuery(sql).use { rs ->
                    while (rs.next()) {
                        summaries.add(ClusterSummary(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("color") ?: "#D2B48C",
                            rs.getInt("file_count")
                        ))
                    }
                }
            }
        }
        return summaries
    }

    fun clearAllData() {
        getConnection().use { conn ->
            val statement = conn.createStatement()
            statement.executeUpdate("DELETE FROM files")
            statement.executeUpdate("DELETE FROM clusters")
            // This resets the auto-increment IDs to 1
            statement.executeUpdate("DELETE FROM sqlite_sequence WHERE name='files' OR name='clusters'")
        }
    }

    fun getFilesByCluster(clusterId: Int): List<FileEntry> {
        val files = mutableListOf<FileEntry>()
        val sql = "SELECT * FROM files WHERE cluster_id = ?"

        getConnection().use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setInt(1, clusterId)
                val rs = pstmt.executeQuery()
                while (rs.next()) {
                    files.add(FileEntry(
                        id = rs.getInt("id"),
                        name = rs.getString("name"),
                        path = rs.getString("path"),
                        extension = rs.getString("extension"),
                        size = rs.getLong("size"),
                        lastModified = rs.getLong("last_modified"),
                        clusterId = rs.getInt("cluster_id")
                    ))
                }
            }
        }
        return files
    }

    fun searchFilesInCluster(clusterId: Int, query: String): List<FileEntry> {
        val files = mutableListOf<FileEntry>()
        val sql = "SELECT * FROM files WHERE cluster_id = ? AND name LIKE ?"

        getConnection().use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setInt(1, clusterId)
                pstmt.setString(2, "%$query%") // The % signs allow for "fuzzy" matching
                val rs = pstmt.executeQuery()
                while (rs.next()) {
                    files.add(FileEntry(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("path"),
                        rs.getString("extension"),
                        rs.getLong("size"),
                        rs.getLong("last_modified"),
                        rs.getInt("cluster_id")
                    ))
                }
            }
        }
        return files
    }

    fun getClusterIdForExtension(extension: String): Int {
        return when (extension.lowercase()) {
            "jpg", "png", "gif", "svg" -> 1 // Images
            "pdf", "docx", "txt", "xlsx" -> 2 // Documents
            "mp4", "mov", "avi" -> 3 // Video
            "mp3", "wav", "flac" -> 4 // Audio
            else -> 5 // Miscellaneous
        }
    }

    fun seedClusters() {
        val categories = listOf(
            "Images" to "#FFD2B48C",    // Galactic Tan
            "Documents" to "#FF4FC3F7", // Tech Blue
            "Video" to "#FFBA68C8",     // Nebula Purple
            "Audio" to "#FFFF8A65",     // Sun Flare Orange
            "Archive" to "#FF90A4AE"    // Space Gray
        )

        getConnection().use { conn ->
            val sql = "INSERT OR IGNORE INTO clusters (id, name, color) VALUES (?, ?, ?)"
            conn.prepareStatement(sql).use { pstmt ->
                categories.forEachIndexed { index, pair ->
                    pstmt.setInt(1, index + 1)
                    pstmt.setString(2, pair.first)
                    pstmt.setString(3, pair.second)
                    pstmt.addBatch()
                }
                pstmt.executeBatch()
            }
        }
    }

    fun getClusterStats(clusterId: Int): Pair<Int, Long> {
        var count = 0
        var totalSize = 0L
        val sql = "SELECT COUNT(*), SUM(size) FROM files WHERE cluster_id = ?"

        getConnection().use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setInt(1, clusterId)
                val rs = pstmt.executeQuery()
                if (rs.next()) {
                    count = rs.getInt(1)
                    totalSize = rs.getLong(2)
                }
            }
        }
        return Pair(count, totalSize)
    }

    /*fun updateFileStar(fileId: Int, currentStatus: Boolean) {
        val status = if (isStarred) 1 else 0
        val sql = "UPDATE files SET is_starred = ? WHERE id = ?"
        getConnection().use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setInt(1, newStatus)
                pstmt.setInt(2, fileId)
                pstmt.executeUpdate()
            }
        }
    }*/
}

