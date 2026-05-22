package com.example.composeapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Hunter(
    val id: String? = null,
    val hunter_name: String,
    val mobile_number: String,
    val fish_category: String,
    val created_at: String? = null
)

@Serializable
data class FishCatch(
    val id: String? = null,
    val hunter_id: String,
    val fish_category: String,
    val weight: Double,
    val price: Double,
    val created_at: String? = null
)

@Serializable
data class Sale(
    val id: String? = null,
    val fish_category: String,
    val weight: Double,
    val price: Double,
    val created_at: String? = null
)

@Serializable
data class Expense(
    val id: String? = null,
    val category: String,
    val amount: Double,
    val description: String,
    val created_at: String? = null
)

@Serializable
data class FishCategory(
    val id: String? = null,
    val category_name: String? = null,
    val type: String? = null
)

@Serializable
data class ExpenseCategory(
    val id: String? = null,
    val category_name: String? = null
)
