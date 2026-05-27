package com.example.composeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "local_hunters")
data class HunterEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val hunter_name: String?,
    val mobile_number: String?,
    val fish_category: List<String>?,
    val fish_rates: Map<String, Double>?,
    val created_at: String? = null,
    val isSynced: Boolean = false
)

@Entity(tableName = "local_catches")
data class FishCatchEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val hunter_id: String,
    val fish_category: String,
    val weight: Double,
    val price: Double,
    val created_at: String? = null,
    val isSynced: Boolean = false
)

@Entity(tableName = "local_sales")
data class SaleEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val fish_category: String,
    val weight: Double,
    val price: Double,
    val remarks: String? = null,
    val created_at: String? = null,
    val isSynced: Boolean = false
)

@Entity(tableName = "local_expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val category: String,
    val amount: Double,
    val description: String,
    val created_at: String? = null,
    val isSynced: Boolean = false
)

@Entity(tableName = "local_fish_categories")
data class FishCategoryEntity(
    @PrimaryKey val id: String,
    val category_name: String?,
    val type: String?
)

@Entity(tableName = "local_expense_categories")
data class ExpenseCategoryEntity(
    @PrimaryKey val id: String,
    val category_name: String?
)
