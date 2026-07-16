package com.example.imagegallery.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dirId: Long,
    val filePath: String,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
