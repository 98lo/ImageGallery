package com.example.imagegallery.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert
    suspend fun insertDirectory(dir: Directory): Long

    @Update
    suspend fun updateDirectory(dir: Directory)

    @Delete
    suspend fun deleteDirectory(dir: Directory)

    @Query("SELECT * FROM directories ORDER BY createdAt DESC")
    fun getAllDirectories(): Flow<List<Directory>>

    @Query("SELECT * FROM directories WHERE name LIKE '%' || :keyword || '%' ORDER BY createdAt DESC")
    fun searchDirectories(keyword: String): Flow<List<Directory>>

    @Insert
    suspend fun insertImage(image: ImageItem): Long

    @Update
    suspend fun updateImage(image: ImageItem)

    @Delete
    suspend fun deleteImage(image: ImageItem)

    @Query("SELECT * FROM images WHERE dirId = :dirId ORDER BY createdAt DESC")
    fun getImagesForDirectory(dirId: Long): Flow<List<ImageItem>>

    @Query("DELETE FROM images WHERE dirId = :dirId")
    suspend fun deleteImagesForDirectory(dirId: Long)
}
