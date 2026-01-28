package com.clusterview.demo

data class FileEntry(
    val id: Int = 0,
    val name: String,
    val path: String,
    val extension: String,
    val size: Long,
    val lastModified: Long,
    val clusterId: Int? = null,
    var isStarred: Boolean = false
)

data class Cluster(
    val id: Int = 0,
    val name: String,
    val color: String
)

data class User(
    val id: Int,
    val email: String,
    val username: String,
    val passwordHash: String
)