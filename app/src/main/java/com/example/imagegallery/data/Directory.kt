
package com.example.imagegallery.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "directories")
data class Directory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
