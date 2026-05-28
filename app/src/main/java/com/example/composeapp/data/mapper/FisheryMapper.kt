package com.example.composeapp.data.mapper

import com.example.composeapp.data.local.entity.*
import com.example.composeapp.data.model.*
import com.example.composeapp.data.remote.*
import java.text.SimpleDateFormat
import java.util.*

private fun getCurrentTimestamp(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date())
}

// Hunter
fun HunterEntity.toDomain() = Hunter(
    id = id,
    hunter_name = hunter_name,
    mobile_number = mobile_number,
    fish_category = fish_category,
    fish_rates = fish_rates,
    created_at = created_at,
    isSynced = isSynced
)

fun Hunter.toEntity(isSynced: Boolean = false) = HunterEntity(
    id = id ?: UUID.randomUUID().toString(),
    hunter_name = hunter_name,
    mobile_number = mobile_number,
    fish_category = fish_category,
    fish_rates = fish_rates,
    created_at = created_at ?: getCurrentTimestamp(),
    isSynced = isSynced
)

fun Hunter.toApi() = HunterApi(
    id = id,
    hunter_name = hunter_name,
    mobile_number = mobile_number,
    fish_category = fish_category,
    fish_rates = fish_rates,
    created_at = created_at
)

fun HunterApi.toDomain(isSynced: Boolean = true) = Hunter(
    id = id,
    hunter_name = hunter_name,
    mobile_number = mobile_number,
    fish_category = fish_category,
    fish_rates = fish_rates,
    created_at = created_at,
    isSynced = isSynced
)

// Fish Catch
fun FishCatchEntity.toDomain() = FishCatch(
    id = id,
    hunter_id = hunter_id,
    fish_category = fish_category,
    weight = weight,
    price = price,
    created_at = created_at,
    isSynced = isSynced
)

fun FishCatch.toEntity(isSynced: Boolean = false) = FishCatchEntity(
    id = id ?: UUID.randomUUID().toString(),
    hunter_id = hunter_id,
    fish_category = fish_category,
    weight = weight,
    price = price,
    created_at = created_at ?: getCurrentTimestamp(),
    isSynced = isSynced
)

fun FishCatch.toApi() = FishCatchApi(
    id = id,
    hunter_id = hunter_id,
    fish_category = fish_category,
    weight = weight,
    price = price,
    created_at = created_at
)

fun FishCatchApi.toDomain(isSynced: Boolean = true) = FishCatch(
    id = id,
    hunter_id = hunter_id,
    fish_category = fish_category,
    weight = weight,
    price = price,
    created_at = created_at,
    isSynced = isSynced
)

// Sale
fun SaleEntity.toDomain() = Sale(
    id = id,
    fish_category = fish_category,
    weight = weight,
    price = price,
    remarks = remarks,
    created_at = created_at,
    isSynced = isSynced
)

fun Sale.toEntity(isSynced: Boolean = false) = SaleEntity(
    id = id ?: UUID.randomUUID().toString(),
    fish_category = fish_category,
    weight = weight,
    price = price,
    remarks = remarks,
    created_at = created_at ?: getCurrentTimestamp(),
    isSynced = isSynced
)

fun Sale.toApi() = SaleApi(
    id = id,
    fish_category = fish_category,
    weight = weight,
    price = price,
    remarks = remarks,
    created_at = created_at
)

fun SaleApi.toDomain(isSynced: Boolean = true) = Sale(
    id = id,
    fish_category = fish_category,
    weight = weight,
    price = price,
    remarks = remarks,
    created_at = created_at,
    isSynced = isSynced
)

// Expense
fun ExpenseEntity.toDomain() = Expense(
    id = id,
    category = category,
    amount = amount,
    description = description,
    hunter_id = hunter_id,
    created_at = created_at,
    isSynced = isSynced
)

fun Expense.toEntity(isSynced: Boolean = false) = ExpenseEntity(
    id = id ?: UUID.randomUUID().toString(),
    category = category,
    amount = amount,
    description = description,
    hunter_id = hunter_id,
    created_at = created_at ?: getCurrentTimestamp(),
    isSynced = isSynced
)

fun Expense.toApi() = ExpenseApi(
    id = id,
    category = category,
    amount = amount,
    description = description,
    hunter_id = hunter_id,
    created_at = created_at
)

fun ExpenseApi.toDomain(isSynced: Boolean = true) = Expense(
    id = id,
    category = category,
    amount = amount,
    description = description,
    hunter_id = hunter_id,
    created_at = created_at,
    isSynced = isSynced
)

// Fish Category
fun FishCategoryEntity.toDomain() = FishCategory(
    id = id,
    category_name = category_name,
    type = type
)

fun FishCategoryApi.toDomain() = FishCategory(
    id = id,
    category_name = category_name,
    type = type
)

fun FishCategory.toEntity() = FishCategoryEntity(
    id = id ?: UUID.randomUUID().toString(),
    category_name = category_name,
    type = type
)

// Expense Category
fun ExpenseCategoryEntity.toDomain() = ExpenseCategory(
    id = id,
    category_name = category_name
)

fun ExpenseCategoryApi.toDomain() = ExpenseCategory(
    id = id,
    category_name = category_name
)

fun ExpenseCategory.toEntity() = ExpenseCategoryEntity(
    id = id ?: UUID.randomUUID().toString(),
    category_name = category_name
)
