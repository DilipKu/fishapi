package com.example.composeapp.data.repository

import android.util.Log
import com.example.composeapp.data.local.dao.FisheryDao
import com.example.composeapp.data.mapper.*
import com.example.composeapp.data.model.*
import com.example.composeapp.data.remote.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FisheryRepository(private val fisheryDao: FisheryDao) {

    val hunters: Flow<List<Hunter>> = fisheryDao.getAllHunters().map { list -> list.map { it.toDomain() } }
    val catches: Flow<List<FishCatch>> = fisheryDao.getAllCatches().map { list -> list.map { it.toDomain() } }
    val sales: Flow<List<Sale>> = fisheryDao.getAllSales().map { list -> list.map { it.toDomain() } }
    val expenses: Flow<List<Expense>> = fisheryDao.getAllExpenses().map { list -> list.map { it.toDomain() } }
    val fishCategories: Flow<List<FishCategory>> = fisheryDao.getFishCategories().map { list -> list.map { it.toDomain() } }
    val expenseCategories: Flow<List<ExpenseCategory>> = fisheryDao.getExpenseCategories().map { list -> list.map { it.toDomain() } }

    suspend fun addHunter(hunter: Hunter) {
        fisheryDao.insertHunter(hunter.toEntity(isSynced = false))
        syncHunters()
    }

    suspend fun addCatch(fishCatch: FishCatch) {
        fisheryDao.insertCatch(fishCatch.toEntity(isSynced = false))
        syncCatches()
    }

    suspend fun addCatches(catches: List<FishCatch>) {
        catches.forEach { fisheryDao.insertCatch(it.toEntity(isSynced = false)) }
        syncCatches()
    }

    suspend fun addSale(sale: Sale) {
        fisheryDao.insertSale(sale.toEntity(isSynced = false))
        syncSales()
    }

    suspend fun addExpense(expense: Expense) {
        fisheryDao.insertExpense(expense.toEntity(isSynced = false))
        syncExpenses()
    }

    suspend fun refreshCategories() {
        try {
            val fList = supabase.from("fish_categories").select().decodeList<FishCategory>()
            if (fList.isNotEmpty()) fisheryDao.insertFishCategories(fList.map { it.toEntity() })

            val eList = supabase.from("expense_categories").select().decodeList<ExpenseCategory>()
            if (eList.isNotEmpty()) fisheryDao.insertExpenseCategories(eList.map { it.toEntity() })
        } catch (e: Exception) {
            Log.e("FisheryRepository", "Refresh categories failed", e)
        }
    }

    suspend fun fetchHunters() {
        try {
            val remoteHunters = supabase.from("hunters").select {
                order("created_at", Order.DESCENDING)
            }.decodeList<Hunter>()
            remoteHunters.forEach { fisheryDao.insertHunter(it.toEntity(isSynced = true)) }
        } catch (e: Exception) {
            Log.e("FisheryRepository", "Fetch hunters failed", e)
        }
    }

    suspend fun fetchCatches() {
        try {
            val remoteCatches = supabase.from("catch_fish").select {
                order("created_at", Order.DESCENDING)
            }.decodeList<FishCatch>()
            remoteCatches.forEach { fisheryDao.insertCatch(it.toEntity(isSynced = true)) }
        } catch (e: Exception) {
            Log.e("FisheryRepository", "Fetch catches failed", e)
        }
    }

    suspend fun fetchSales() {
        try {
            val remoteSales = supabase.from("sales").select {
                order("created_at", Order.DESCENDING)
            }.decodeList<Sale>()
            remoteSales.forEach { fisheryDao.insertSale(it.toEntity(isSynced = true)) }
        } catch (e: Exception) {
            Log.e("FisheryRepository", "Fetch sales failed", e)
        }
    }

    suspend fun fetchExpenses() {
        try {
            val remoteExpenses = supabase.from("expenses").select {
                order("created_at", Order.DESCENDING)
            }.decodeList<Expense>()
            remoteExpenses.forEach { fisheryDao.insertExpense(it.toEntity(isSynced = true)) }
        } catch (e: Exception) {
            Log.e("FisheryRepository", "Fetch expenses failed", e)
        }
    }

    // Sync Unsynced Data
    suspend fun syncHunters() {
        val unsynced = fisheryDao.getUnsyncedHunters()
        unsynced.forEach { entity ->
            try {
                val domain = entity.toDomain()
                supabase.from("hunters").insert(domain)
                fisheryDao.markHunterSynced(entity.id)
            } catch (e: Exception) {
                Log.e("FisheryRepository", "Sync hunter failed", e)
            }
        }
    }

    suspend fun syncCatches() {
        val unsynced = fisheryDao.getUnsyncedCatches()
        unsynced.forEach { entity ->
            try {
                val domain = entity.toDomain()
                supabase.from("catch_fish").insert(domain)
                fisheryDao.markCatchSynced(entity.id)
            } catch (e: Exception) {
                Log.e("FisheryRepository", "Sync catch failed", e)
            }
        }
    }

    suspend fun syncSales() {
        val unsynced = fisheryDao.getUnsyncedSales()
        unsynced.forEach { entity ->
            try {
                val domain = entity.toDomain()
                supabase.from("sales").insert(domain)
                fisheryDao.markSaleSynced(entity.id)
            } catch (e: Exception) {
                Log.e("FisheryRepository", "Sync sale failed", e)
            }
        }
    }

    suspend fun syncExpenses() {
        val unsynced = fisheryDao.getUnsyncedExpenses()
        unsynced.forEach { entity ->
            try {
                val domain = entity.toDomain()
                supabase.from("expenses").insert(domain)
                fisheryDao.markExpenseSynced(entity.id)
            } catch (e: Exception) {
                Log.e("FisheryRepository", "Sync expense failed", e)
            }
        }
    }

    suspend fun syncAll() {
        syncHunters()
        syncCatches()
        syncSales()
        syncExpenses()
        fetchHunters()
        fetchCatches()
        fetchSales()
        fetchExpenses()
        refreshCategories()
    }
}
