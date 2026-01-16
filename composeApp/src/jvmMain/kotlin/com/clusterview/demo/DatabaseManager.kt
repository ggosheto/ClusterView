package com.clusterview.demo

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

object DatabaseManager {
    private const val DB_URL = "jdbc:sqlite:clusterview.db"

    fun getConnection(): Connection = DriverManager.getConnection(DB_URL)

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
}