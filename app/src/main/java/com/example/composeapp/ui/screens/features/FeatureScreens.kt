package com.example.composeapp.ui.screens.features

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.composeapp.viewmodel.FishViewModel
import com.example.composeapp.ui.components.CategoryDropdown
import com.example.composeapp.ui.components.ErrorMessage
import com.example.composeapp.ui.components.getTranslatedCategory
import com.example.composeapp.ui.components.formatToIST
import com.dilip.composeapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HunterRegistrationScreen(navController: NavController, viewModel: FishViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    val selectedCategories = remember { mutableStateListOf<String>() }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val hunters by viewModel.hunters.collectAsState()
    val fishCategories by viewModel.fishCategories.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshAll()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reg_hunter)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = name, 
                onValueChange = { name = it; errorMessage = null }, 
                label = { Text(stringResource(R.string.name)) }, 
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = mobile, 
                onValueChange = { 
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        mobile = it
                        errorMessage = null
                    }
                }, 
                label = { Text(stringResource(R.string.mobile_number)) }, 
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.fish_category), style = MaterialTheme.typography.titleSmall)
            
            // Multiple Selection via Checkboxes (2 per row)
            fishCategories.chunked(2).forEach { rowCategories ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    rowCategories.forEach { category ->
                        val catName = category.category_name ?: "Unknown"
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (selectedCategories.contains(catName)) {
                                        selectedCategories.remove(catName)
                                    } else {
                                        selectedCategories.add(catName)
                                    }
                                }
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = selectedCategories.contains(catName),
                                onCheckedChange = { checked ->
                                    if (checked) selectedCategories.add(catName) else selectedCategories.remove(catName)
                                }
                            )
                            Text(getTranslatedCategory(catName), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    // Fill space if only one item in row
                    if (rowCategories.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            errorMessage?.let { ErrorMessage(it) }

            Button(
                onClick = { 
                    if (name.isBlank() || mobile.isBlank()) {
                        errorMessage = "All fields are required"
                    } else if (mobile.length != 10) {
                        errorMessage = "Please enter a valid 10-digit mobile number"
                    } else if (selectedCategories.isEmpty()) {
                        errorMessage = "Please select at least one category"
                    } else {
                        viewModel.addHunter(name, mobile, selectedCategories.toList())
                        name = ""
                        mobile = ""
                        selectedCategories.clear()
                        errorMessage = null 
                    }
                }, 
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(stringResource(R.string.register))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(stringResource(R.string.registered_hunters), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(hunters) { hunter ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("${stringResource(R.string.name)}: ${hunter.hunter_name}", fontWeight = FontWeight.Bold)
                            Text("${stringResource(R.string.mobile_number)}: ${hunter.mobile_number}")
                            val translatedCategories = hunter.fish_category.map { getTranslatedCategory(it) }
                            Text("${stringResource(R.string.fish_category)}: ${translatedCategories.joinToString(", ")}")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCatchScreen(navController: NavController, viewModel: FishViewModel = viewModel()) {
    var selectedHunter by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val catches by viewModel.catches.collectAsState()
    val hunters by viewModel.hunters.collectAsState()
    val fishCategories by viewModel.fishCategories.collectAsState()
    
    // State to store weights and prices for each category
    val weights = remember { mutableStateMapOf<String, String>() }
    val prices = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(Unit) {
        viewModel.getHunters()
        viewModel.refreshAll()
    }

    LaunchedEffect(hunters) {
        if (selectedHunter.isEmpty() && hunters.isNotEmpty()) {
            selectedHunter = hunters.first().id ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.submit_catch)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            // Hunter Dropdown
            var hunterDropdownExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = hunterDropdownExpanded,
                onExpandedChange = { hunterDropdownExpanded = !hunterDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = hunters.find { it.id == selectedHunter }?.hunter_name ?: stringResource(R.string.select_hunter),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.select_hunter)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = hunterDropdownExpanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = hunterDropdownExpanded,
                    onDismissRequest = { hunterDropdownExpanded = false }
                ) {
                    hunters.forEach { hunter ->
                        DropdownMenuItem(
                            text = { Text(hunter.hunter_name) },
                            onClick = {
                                selectedHunter = hunter.id ?: ""
                                hunterDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Categories with inputs in a LazyColumn (Form part)
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text(stringResource(R.string.fish_categories_title), style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    items(fishCategories) { category ->
                        val catId = category.id ?: ""
                        val catName = category.category_name ?: "Unknown"
                        
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(getTranslatedCategory(catName), modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.bodyMedium)
                            
                            OutlinedTextField(
                                value = weights[catId] ?: "",
                                onValueChange = { if(it.length <= 8) weights[catId] = it },
                                label = { Text(stringResource(R.string.wt_label)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall
                            )
                            
                            OutlinedTextField(
                                value = prices[catId] ?: "",
                                onValueChange = { if(it.length <= 8) prices[catId] = it },
                                label = { Text(stringResource(R.string.price)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    
                    item {
                        errorMessage?.let { ErrorMessage(it) }

                        Button(
                            onClick = { 
                                if (selectedHunter.isBlank()) {
                                    errorMessage = "Please select a hunter"
                                    return@Button
                                }
                                
                                val batchCatches = mutableListOf<Pair<String, Pair<Double, Double>>>()
                                var validationError: String? = null
                                
                                fishCategories.forEach { category ->
                                    val catId = category.id ?: ""
                                    val wStr = weights[catId] ?: ""
                                    val pStr = prices[catId] ?: ""
                                    
                                    if (wStr.isNotBlank() || pStr.isNotBlank()) {
                                        val w = wStr.toDoubleOrNull()
                                        val p = pStr.toDoubleOrNull()
                                        
                                        if (w == null || w > 99999) {
                                            validationError = "Invalid weight for ${category.category_name}"
                                        } else if (p == null || p > 99999) {
                                            validationError = "Invalid price for ${category.category_name}"
                                        } else {
                                            batchCatches.add(category.category_name!! to (w to p))
                                        }
                                    }
                                }
                                
                                if (validationError != null) {
                                    errorMessage = validationError
                                } else if (batchCatches.isEmpty()) {
                                    errorMessage = "Please enter at least one catch"
                                } else {
                                    viewModel.addCatches(selectedHunter, batchCatches)
                                    weights.clear()
                                    prices.clear()
                                    errorMessage = null
                                }
                            }, 
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                        ) {
                            Text(stringResource(R.string.submit_catch))
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                        Text(stringResource(R.string.catches), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }

                    // Group catches by hunter and time (assuming same timestamp for batch insert)
                    val groupedCatches = catches.groupBy { it.hunter_id to it.created_at }
                    
                    items(groupedCatches.keys.toList()) { key ->
                        val group = groupedCatches[key] ?: emptyList()
                        val hunterId = key.first
                        val timestamp = key.second
                        
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                val hunter = hunters.find { it.id == hunterId || it.hunter_name == hunterId }
                                val displayHunterName = hunter?.hunter_name ?: hunterId
                                
                                Text("${stringResource(R.string.select_hunter)}: $displayHunterName", fontWeight = FontWeight.Bold)
                                if (timestamp != null) {
                                    Text("${stringResource(R.string.time_label)}: ${formatToIST(timestamp)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)
                                
                                group.forEach { c ->
                                    Text(
                                        text = "${getTranslatedCategory(c.fish_category)}: ${c.weight} kg | ₹${c.price}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(navController: NavController, viewModel: FishViewModel = viewModel()) {
    var selectedCategory by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val sales by viewModel.sales.collectAsState()
    val fishCategories by viewModel.fishCategories.collectAsState()
    val categoryNames = fishCategories.map { it.category_name ?: "Unknown" }

    LaunchedEffect(Unit) {
        viewModel.refreshAll()
    }

    LaunchedEffect(categoryNames) {
        if (selectedCategory.isEmpty() && categoryNames.isNotEmpty()) selectedCategory = categoryNames.first()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.sales)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            CategoryDropdown(stringResource(R.string.fish_category), categoryNames, selectedCategory) { selectedCategory = it }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = weight, 
                    onValueChange = { if(it.length <= 8) { weight = it; errorMessage = null } }, 
                    label = { Text(stringResource(R.string.weight_kg)) }, 
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = price, 
                    onValueChange = { if(it.length <= 8) { price = it; errorMessage = null } }, 
                    label = { Text(stringResource(R.string.price)) }, 
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = remarks,
                onValueChange = { remarks = it },
                label = { Text(stringResource(R.string.remarks)) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                singleLine = true
            )

            errorMessage?.let { ErrorMessage(it) }

            Button(
                onClick = { 
                    val w = weight.toDoubleOrNull()
                    val p = price.toDoubleOrNull()
                    
                    if (weight.isBlank() || price.isBlank()) {
                        errorMessage = "Please fill all fields"
                    } else if (w == null || w > 99999) {
                        errorMessage = "Invalid weight (max 99,999 kg)"
                    } else if (p == null || p > 99999) {
                        errorMessage = "Invalid price (max 99,999)"
                    } else {
                        viewModel.addSale(selectedCategory, w, p, remarks.ifBlank { null })
                        weight = ""
                        price = ""
                        remarks = ""
                        errorMessage = null
                    }
                }, 
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(stringResource(R.string.submit_sale))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(stringResource(R.string.recent_sales), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(sales) { s ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("${stringResource(R.string.fish_category)}: ${getTranslatedCategory(s.fish_category)}", fontWeight = FontWeight.Bold)
                            if (s.created_at != null) {
                                Text("${stringResource(R.string.time_label)}: ${formatToIST(s.created_at)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Text("${stringResource(R.string.weight_kg)}: ${s.weight} kg | ${stringResource(R.string.price)}: ₹${s.price}")
                            if (!s.remarks.isNullOrBlank()) {
                                Text("${stringResource(R.string.remarks)}: ${s.remarks}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(navController: NavController, viewModel: FishViewModel = viewModel()) {
    var selectedCategory by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val expenses by viewModel.expenses.collectAsState()
    val expenseCategories by viewModel.expenseCategories.collectAsState()
    val categoryNames = expenseCategories.map { it.category_name ?: "Unknown" }

    LaunchedEffect(Unit) {
        viewModel.refreshAll()
    }

    LaunchedEffect(categoryNames) {
        if (selectedCategory.isEmpty() && categoryNames.isNotEmpty()) selectedCategory = categoryNames.first()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.expense)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            CategoryDropdown(stringResource(R.string.expense), categoryNames, selectedCategory) { selectedCategory = it }
            
            OutlinedTextField(
                value = amount, 
                onValueChange = { if(it.length <= 8) { amount = it; errorMessage = null } }, 
                label = { Text(stringResource(R.string.amount)) }, 
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(
                value = description, 
                onValueChange = { description = it; errorMessage = null }, 
                label = { Text(stringResource(R.string.description)) }, 
                modifier = Modifier.fillMaxWidth()
            )

            errorMessage?.let { ErrorMessage(it) }

            Button(
                onClick = { 
                    val a = amount.toDoubleOrNull()
                    if (amount.isBlank() || description.isBlank()) {
                        errorMessage = "All fields are required"
                    } else if (a == null || a > 99999) {
                        errorMessage = "Invalid amount (max 99,999)"
                    } else {
                        viewModel.addExpense(selectedCategory, a, description)
                        amount = ""
                        description = ""
                        errorMessage = null
                    }
                }, 
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(stringResource(R.string.submit_expense))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(stringResource(R.string.expense_list), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(expenses) { e ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Category: ${getTranslatedCategory(e.category)}", fontWeight = FontWeight.Bold)
                            if (e.created_at != null) {
                                Text("${stringResource(R.string.time_label)}: ${formatToIST(e.created_at)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Text("${stringResource(R.string.amount)}: ₹${e.amount}")
                            Text("${stringResource(R.string.description)}: ${e.description}")
                        }
                    }
                }
            }
        }
    }
}
