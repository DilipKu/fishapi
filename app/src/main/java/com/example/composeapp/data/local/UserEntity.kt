package com.example.composeapp.data.local


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")        // ← creates "users" table in SQLite
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,                // ← auto increment ID
    val name: String,
    val email: String,
    val age: Int,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Long = System.currentTimeMillis()
)
