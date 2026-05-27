package com.example.composeapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.data.model.*
import com.example.composeapp.data.remote.supabase
import com.example.composeapp.data.repository.FisheryRepository
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow

class FishViewModel(private val repository: FisheryRepository) : ViewModel() {

    val hunters: StateFlow<List<Hunter>> = repository.hunters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val catches: StateFlow<List<FishCatch>> = repository.catches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sales: StateFlow<List<Sale>> = repository.sales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<Expense>> = repository.expenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fishCategories: StateFlow<List<FishCategory>> = repository.fishCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseCategories: StateFlow<List<ExpenseCategory>> = repository.expenseCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        Log.d("FishViewModel", "Init: Syncing data")
        viewModelScope.launch {
            repository.syncAll()
        }
    }

    fun refreshAll() {
        viewModelScope.launch {
            repository.syncAll()
        }
    }

    fun getHunters() {
        viewModelScope.launch {
            repository.fetchHunters()
        }
    }

    fun addHunter(name: String, mobile: String, categories: List<String>, rates: Map<String, Double>) {
        viewModelScope.launch {
            try {
                _error.value = null
                val hunter = Hunter(
                    hunter_name = name, 
                    mobile_number = mobile, 
                    fish_category = categories,
                    fish_rates = rates
                )
                repository.addHunter(hunter)
            } catch (e: Exception) { 
                Log.e("FishViewModel", "Registration failed", e)
                _error.value = e.message ?: "Registration failed"
            }
        }
    }

    fun addCatches(hunterName: String, catches: List<Pair<String, Pair<Double, Double>>>) {
        viewModelScope.launch {
            try {
                val domainCatches = catches.map { (category, data) ->
                    FishCatch(hunter_id = hunterName, fish_category = category, weight = data.first, price = data.second)
                }
                repository.addCatches(domainCatches)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun addSale(category: String, weight: Double, price: Double, remarks: String? = null) {
        viewModelScope.launch {
            try {
                val sale = Sale(fish_category = category, weight = weight, price = price, remarks = remarks)
                repository.addSale(sale)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun addExpense(category: String, amount: Double, description: String) {
        viewModelScope.launch {
            try {
                val expense = Expense(category = category, amount = amount, description = description)
                repository.addExpense(expense)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
