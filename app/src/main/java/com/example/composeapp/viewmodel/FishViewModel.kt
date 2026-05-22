package com.example.composeapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.data.model.*
import com.example.composeapp.data.remote.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FishViewModel : ViewModel() {

    private val _hunters = MutableStateFlow<List<Hunter>>(emptyList())
    val hunters: StateFlow<List<Hunter>> = _hunters

    private val _catches = MutableStateFlow<List<FishCatch>>(emptyList())
    val catches: StateFlow<List<FishCatch>> = _catches

    private val _sales = MutableStateFlow<List<Sale>>(emptyList())
    val sales: StateFlow<List<Sale>> = _sales

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    private val _fishCategories = MutableStateFlow<List<FishCategory>>(emptyList())
    val fishCategories: StateFlow<List<FishCategory>> = _fishCategories

    private val _expenseCategories = MutableStateFlow<List<ExpenseCategory>>(emptyList())
    val expenseCategories: StateFlow<List<ExpenseCategory>> = _expenseCategories

    init {
        Log.d("FishViewModel", "Init: Refreshing all data")
        refreshAll()
    }

    fun refreshAll() {
        getHunters()
        getCatches()
        getSales()
        getExpenses()
        getFishCategories()
        getExpenseCategories()
    }

    fun getFishCategories() {
        viewModelScope.launch {
            try {
                val list = supabase.from("fish_categories").select().decodeList<FishCategory>()
                Log.d("FishViewModel", "Fish Categories fetched: ${list.size}")
                
                if (list.isEmpty()) {
                    Log.w("FishViewModel", "fish_categories table is EMPTY. Using fallbacks.")
                    _fishCategories.value = listOf(
                        FishCategory(category_name = "Major", type = "fish"),
                        FishCategory(category_name = "Minor", type = "fish"),
                        FishCategory(category_name = "Chikna", type = "fish"),
                        FishCategory(category_name = "Tilapiya", type = "fish"),
                        FishCategory(category_name = "Miscellaneous", type = "fish")
                    )
                } else {
                    _fishCategories.value = list
                }
            } catch (e: Exception) { 
                Log.e("FishViewModel", "Error fetching fish categories", e)
                _fishCategories.value = listOf(
                    FishCategory(category_name = "Major", type = "fish"),
                    FishCategory(category_name = "Minor", type = "fish"),
                    FishCategory(category_name = "Chikna", type = "fish"),
                    FishCategory(category_name = "Tilapiya", type = "fish"),
                    FishCategory(category_name = "Miscellaneous", type = "fish")
                )
            }
        }
    }

    fun getExpenseCategories() {
        viewModelScope.launch {
            try {
                val list = supabase.from("expense_categories").select().decodeList<ExpenseCategory>()
                Log.d("FishViewModel", "Expense Categories fetched: ${list.size}")
                
                if (list.isEmpty()) {
                    Log.w("FishViewModel", "expense_categories table is EMPTY. Using fallbacks.")
                    _expenseCategories.value = listOf(
                        ExpenseCategory(category_name = "Fixed Company"),
                        ExpenseCategory(category_name = "Fisherman"),
                        ExpenseCategory(category_name = "Transport"),
                        ExpenseCategory(category_name = "Miscellaneous")
                    )
                } else {
                    _expenseCategories.value = list
                }
            } catch (e: Exception) { 
                Log.e("FishViewModel", "Error fetching expense categories", e)
                _expenseCategories.value = listOf(
                    ExpenseCategory(category_name = "Fixed Company"),
                    ExpenseCategory(category_name = "Fisherman"),
                    ExpenseCategory(category_name = "Transport"),
                    ExpenseCategory(category_name = "Miscellaneous")
                )
            }
        }
    }

    fun getHunters() {
        viewModelScope.launch {
            try {
                _hunters.value = supabase.from("hunters")
                    .select {
                        order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                    }
                    .decodeList<Hunter>()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun addHunter(name: String, mobile: String, category: String) {
        viewModelScope.launch {
            try {
                val hunter = Hunter(hunter_name = name, mobile_number = mobile, fish_category = category)
                supabase.from("hunters").insert(hunter)
                getHunters()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun getCatches() {
        viewModelScope.launch {
            try {
                _catches.value = supabase.from("catch_fish")
                    .select {
                        order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                    }
                    .decodeList<FishCatch>()
            } catch (e: Exception) { 
                Log.e("FishViewModel", "Error fetching catches", e)
            }
        }
    }

    fun addCatch(hunterName: String, category: String, weight: Double, price: Double) {
        viewModelScope.launch {
            try {
                val fishCatch = FishCatch(hunter_id = hunterName, fish_category = category, weight = weight, price = price)
                supabase.from("catch_fish").insert(fishCatch)
                getCatches()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun getSales() {
        viewModelScope.launch {
            try {
                _sales.value = supabase.from("sales")
                    .select {
                        order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                    }
                    .decodeList<Sale>()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun addSale(category: String, weight: Double, price: Double) {
        viewModelScope.launch {
            try {
                val sale = Sale(fish_category = category, weight = weight, price = price)
                supabase.from("sales").insert(sale)
                getSales()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun getExpenses() {
        viewModelScope.launch {
            try {
                _expenses.value = supabase.from("expenses")
                    .select {
                        order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                    }
                    .decodeList<Expense>()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun addExpense(category: String, amount: Double, description: String) {
        viewModelScope.launch {
            try {
                val expense = Expense(category = category, amount = amount, description = description)
                supabase.from("expenses").insert(expense)
                getExpenses()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}
