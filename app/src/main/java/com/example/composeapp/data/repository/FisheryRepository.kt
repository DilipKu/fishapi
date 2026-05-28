package com.example.composeapp.data.repository

import android.util.Log
import com.example.composeapp.data.local.dao.FisheryDao
import com.example.composeapp.data.mapper.*
import com.example.composeapp.data.model.*
import com.example.composeapp.data.remote.*
import com.example.composeapp.data.remote.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FisheryRepository(private val fisheryDao: FisheryDao) {

    init {
        Log.d("FisheryRepository", "FisheryRepository initialized")
    }

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
            val fList = supabase.from("fish_categories").select().decodeList<FishCategoryApi>()
            if (fList.isNotEmpty()) fisheryDao.insertFishCategories(fList.map { it.toDomain().toEntity() })

            val eList = supabase.from("expense_categories").select().decodeList<ExpenseCategoryApi>()
            if (eList.isNotEmpty()) fisheryDao.insertExpenseCategories(eList.map { it.toDomain().toEntity() })
        } catch (e: Exception) {
            Log.e("FisheryRepository", "Refresh categories failed", e)
        }
    }

    suspend fun fetchHunters() {
        try {
            val remoteHunters = supabase.from("hunters").select {
                order("created_at", Order.DESCENDING)
            }.decodeList<HunterApi>()
            remoteHunters.forEach { fisheryDao.insertHunter(it.toDomain(isSynced = true).toEntity(isSynced = true)) }
        } catch (e: Exception) {
            Log.e("FisheryRepository", "Fetch hunters failed", e)
        }
    }

    suspend fun fetchCatches() {
        try {
            val remoteCatches = supabase.from("catch_fish").select {
                order("created_at", Order.DESCENDING)
            }.decodeList<FishCatchApi>()
            remoteCatches.forEach { fisheryDao.insertCatch(it.toDomain(isSynced = true).toEntity(isSynced = true)) }
        } catch (e: Exception) {
            Log.e("FisheryRepository", "Fetch catches failed", e)
        }
    }

    suspend fun fetchSales() {
        try {
            val remoteSales = supabase.from("sales").select {
                order("created_at", Order.DESCENDING)
            }.decodeList<SaleApi>()
            remoteSales.forEach { fisheryDao.insertSale(it.toDomain(isSynced = true).toEntity(isSynced = true)) }
        } catch (e: Exception) {
            Log.e("FisheryRepository", "Fetch sales failed", e)
        }
    }

    suspend fun fetchExpenses() {
        try {
            val remoteExpenses = supabase.from("expenses").select {
                order("created_at", Order.DESCENDING)
            }.decodeList<ExpenseApi>()
            remoteExpenses.forEach { fisheryDao.insertExpense(it.toDomain(isSynced = true).toEntity(isSynced = true)) }
        } catch (e: Exception) {
            Log.e("FisheryRepository", "Fetch expenses failed", e)
        }
    }

    // Sync Unsynced Data
    suspend fun syncHunters() {
        val unsynced = fisheryDao.getUnsyncedHunters()
        Log.d("FisheryRepository", "Syncing hunters: ${unsynced.size} items found")
        unsynced.forEach { entity ->
            try {
                val apiModel = entity.toDomain().toApi()
                Log.d("FisheryRepository", "Attempting to sync hunter: ${apiModel.hunter_name}")
                supabase.from("hunters").upsert(apiModel) {
                    onConflict = "id"
                }
                fisheryDao.markHunterSynced(entity.id)
                Log.d("FisheryRepository", "Hunter synced successfully: ${entity.id}")
            } catch (e: Exception) {
                Log.e("FisheryRepository", "Sync hunter failed for ${entity.id}: ${e.message}")
            }
        }
    }

    suspend fun syncCatches() {
        val unsynced = fisheryDao.getUnsyncedCatches()
        Log.d("FisheryRepository", "Syncing catches: ${unsynced.size} items found")
        unsynced.forEach { entity ->
            try {
                val apiModel = entity.toDomain().toApi()
                supabase.from("catch_fish").upsert(apiModel) {
                    onConflict = "id"
                }
                fisheryDao.markCatchSynced(entity.id)
                Log.d("FisheryRepository", "Catch synced successfully: ${entity.id}")
            } catch (e: Exception) {
                Log.e("FisheryRepository", "Sync catch failed: ${e.message}")
            }
        }
    }

    suspend fun syncSales() {
        val unsynced = fisheryDao.getUnsyncedSales()
        Log.d("FisheryRepository", "Syncing sales: ${unsynced.size} items found")
        unsynced.forEach { entity ->
            try {
                val apiModel = entity.toDomain().toApi()
                supabase.from("sales").upsert(apiModel) {
                    onConflict = "id"
                }
                fisheryDao.markSaleSynced(entity.id)
                Log.d("FisheryRepository", "Sale synced successfully: ${entity.id}")
            } catch (e: Exception) {
                Log.e("FisheryRepository", "Sync sale failed: ${e.message}")
            }
        }
    }

    suspend fun syncExpenses() {
        val unsynced = fisheryDao.getUnsyncedExpenses()
        Log.d("FisheryRepository", "Syncing expenses: ${unsynced.size} items found")
        unsynced.forEach { entity ->
            try {
                val apiModel = entity.toDomain().toApi()
                supabase.from("expenses").upsert(apiModel) {
                    onConflict = "id"
                }
                fisheryDao.markExpenseSynced(entity.id)
                Log.d("FisheryRepository", "Expense synced successfully: ${entity.id}")
            } catch (e: Exception) {
                Log.e("FisheryRepository", "Sync expense failed: ${e.message}")
            }
        }
    }

    suspend fun syncAll() {
        try {
            syncHunters()
            syncCatches()
            syncSales()
            syncExpenses()
            fetchHunters()
            fetchCatches()
            fetchSales()
            fetchExpenses()
            refreshCategories()
        } catch (e: Exception) {
            Log.e("FisheryRepository", "syncAll global failure", e)
        }
    }
}
