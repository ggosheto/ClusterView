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
}