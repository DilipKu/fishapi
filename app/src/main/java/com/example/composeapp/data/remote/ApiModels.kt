package com.example.composeapp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class HunterApi(
    val id: String? = null,
    val hunter_name: String? = "",
    val mobile_number: String? = "",
    val fish_category: List<String>? = emptyList(),
    val fish_rates: Map<String, Double>? = emptyMap(),
    val created_at: String? = null
)

@Serializable
data class FishCatchApi(
    val id: String? = null,
    val hunter_id: String,
    val fish_category: String,
    val weight: Double,
    val price: Double,
    val created_at: String? = null
)

@Serializable
data class SaleApi(
    val id: String? = null,
    val fish_category: String,
    val weight: Double,
    val price: Double,
    val remarks: String? = null,
    val created_at: String? = null
)

@Serializable
data class ExpenseApi(
    val id: String? = null,
    val category: String,
    val amount: Double,
    val description: String,
    val hunter_id: String? = null,
    val created_at: String? = null
)

@Serializable
data class FishCategoryApi(
    val id: String? = null,
    val category_name: String? = null,
    val type: String? = null
)

@Serializable
data class ExpenseCategoryApi(
    val id: String? = null,
    val category_name: String? = null
)
