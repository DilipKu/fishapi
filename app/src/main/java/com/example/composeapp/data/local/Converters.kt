package com.example.composeapp.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromList(value: List<String>?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toList(value: String?): List<String>? {
        return value?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromMap(value: Map<String, Double>?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toMap(value: String?): Map<String, Double>? {
        return value?.let { Json.decodeFromString(it) }
    }
}
