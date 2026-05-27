package com.example.composeapp.data.local.dao

import androidx.room.*
import com.example.composeapp.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FisheryDao {

    // Hunters
    @Query("SELECT * FROM local_hunters ORDER BY isSynced ASC, created_at DESC")
    fun getAllHunters(): Flow<List<HunterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHunter(hunter: HunterEntity)

    @Query("SELECT * FROM local_hunters WHERE isSynced = 0")
    suspend fun getUnsyncedHunters(): List<HunterEntity>

    @Update
    suspend fun updateHunter(hunter: HunterEntity)

    // Catches
    @Query("SELECT * FROM local_catches ORDER BY isSynced ASC, created_at DESC")
    fun getAllCatches(): Flow<List<FishCatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatch(fishCatch: FishCatchEntity)

    @Query("SELECT * FROM local_catches WHERE isSynced = 0")
    suspend fun getUnsyncedCatches(): List<FishCatchEntity>

    // Sales
    @Query("SELECT * FROM local_sales ORDER BY isSynced ASC, created_at DESC")
    fun getAllSales(): Flow<List<SaleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SaleEntity)

    @Query("SELECT * FROM local_sales WHERE isSynced = 0")
    suspend fun getUnsyncedSales(): List<SaleEntity>

    // Expenses
    @Query("SELECT * FROM local_expenses ORDER BY isSynced ASC, created_at DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM local_expenses WHERE isSynced = 0")
    suspend fun getUnsyncedExpenses(): List<ExpenseEntity>

    // Categories
    @Query("SELECT * FROM local_fish_categories")
    fun getFishCategories(): Flow<List<FishCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFishCategories(categories: List<FishCategoryEntity>)

    @Query("SELECT * FROM local_expense_categories")
    fun getExpenseCategories(): Flow<List<ExpenseCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseCategories(categories: List<ExpenseCategoryEntity>)
    
    @Query("UPDATE local_hunters SET isSynced = 1 WHERE id = :id")
    suspend fun markHunterSynced(id: String)

    @Query("UPDATE local_catches SET isSynced = 1 WHERE id = :id")
    suspend fun markCatchSynced(id: String)

    @Query("UPDATE local_sales SET isSynced = 1 WHERE id = :id")
    suspend fun markSaleSynced(id: String)

    @Query("UPDATE local_expenses SET isSynced = 1 WHERE id = :id")
    suspend fun markExpenseSynced(id: String)
}
