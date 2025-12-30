package com.clusterview.demo

data class FileEntry(
    val id: Int = 0,
    val name: String,
    val path: String,
    val extension: String,
    val size: Long,
    val lastModified: Long,
    val clusterId: Int? = null
)

data class Cluster(
    val id: Int = 0,
    val name: String,
    val color: String
)