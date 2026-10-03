package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CanvasAspectRatio

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val aspectRatio: String = "9:16",
    val backgroundColorHex: String = "#000000",
    val durationMs: Long = 0L,
    val thumbnailUri: String? = null,
    val previewDrawableResName: String? = null,
    val projectJson: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)
