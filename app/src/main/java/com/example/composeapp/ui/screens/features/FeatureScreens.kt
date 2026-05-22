package com.example.composeapp.ui.screens.features

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.composeapp.viewmodel.FishViewModel
import com.example.composeapp.ui.components.CategoryDropdown
import com.dilip.composeapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HunterRegistrationScreen(navController: NavController, viewModel: FishViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    val hunters by viewModel.hunters.collectAsState()
    val fishCategories by viewModel.fishCategories.collectAsState()
    val categoryNames = fishCategories.map { it.category_name ?: "Unknown" }

    LaunchedEffect(Unit) {
        viewModel.refreshAll()
    }

    LaunchedEffect(categoryNames) {
        if (selectedCategory.isEmpty() && categoryNames.isNotEmpty()) {
            selectedCategory = categoryNames.first()
        }
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
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.name)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text(stringResource(R.string.mobile_number)) }, modifier = Modifier.fillMaxWidth())
            
            Spacer(modifier = Modifier.height(8.dp))
            CategoryDropdown(stringResource(R.string.fish_category), categoryNames, selectedCategory) { selectedCategory = it }

            Button(onClick = { viewModel.addHunter(name, mobile, selectedCategory); name = ""; mobile = "" }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
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
                            Text("${stringResource(R.string.fish_category)}: ${hunter.fish_category}")
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
    var selectedCategory by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    
    val catches by viewModel.catches.collectAsState()
    val hunters by viewModel.hunters.collectAsState()
    val fishCategories by viewModel.fishCategories.collectAsState()
    
    val categoryNames = fishCategories.map { it.category_name ?: "Unknown" }

    LaunchedEffect(Unit) {
        viewModel.refreshAll()
    }

    LaunchedEffect(hunters, categoryNames) {
        if (selectedHunter.isEmpty() && hunters.isNotEmpty()) selectedHunter = hunters.first().id ?: ""
        if (selectedCategory.isEmpty() && categoryNames.isNotEmpty()) selectedCategory = categoryNames.first()
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

            Spacer(modifier = Modifier.height(8.dp))
            CategoryDropdown(stringResource(R.string.fish_category), categoryNames, selectedCategory) { selectedCategory = it }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text(stringResource(R.string.weight_kg)) }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text(stringResource(R.string.price)) }, modifier = Modifier.weight(1f))
            }
            Button(onClick = { viewModel.addCatch(selectedHunter, selectedCategory, weight.toDoubleOrNull() ?: 0.0, price.toDoubleOrNull() ?: 0.0); weight = ""; price = "" }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(stringResource(R.string.submit_catch))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(stringResource(R.string.catches), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(catches) { c ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            val hunter = hunters.find { it.id == c.hunter_id || it.hunter_name == c.hunter_id }
                            val displayHunterName = hunter?.hunter_name ?: c.hunter_id
                            
                            Text("${stringResource(R.string.select_hunter)}: $displayHunterName", fontWeight = FontWeight.Bold)
                            Text("${stringResource(R.string.fish_category)}: ${c.fish_category} | ${c.weight} kg | ₹${c.price}")
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
                OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text(stringResource(R.string.weight_kg)) }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text(stringResource(R.string.price)) }, modifier = Modifier.weight(1f))
            }
            Button(onClick = { viewModel.addSale(selectedCategory, weight.toDoubleOrNull() ?: 0.0, price.toDoubleOrNull() ?: 0.0); weight = ""; price = "" }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(stringResource(R.string.submit_sale))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(stringResource(R.string.recent_sales), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(sales) { s ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("${stringResource(R.string.fish_category)}: ${s.fish_category}", fontWeight = FontWeight.Bold)
                            Text("${stringResource(R.string.weight_kg)}: ${s.weight} kg | ${stringResource(R.string.price)}: ₹${s.price}")
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
            CategoryDropdown("Expense Category", categoryNames, selectedCategory) { selectedCategory = it }
            
            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text(stringResource(R.string.amount)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.description)) }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { viewModel.addExpense(selectedCategory, amount.toDoubleOrNull() ?: 0.0, description); amount = ""; description = "" }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(stringResource(R.string.submit_expense))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(stringResource(R.string.expense_list), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(expenses) { e ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Category: ${e.category}", fontWeight = FontWeight.Bold)
                            Text("${stringResource(R.string.amount)}: ₹${e.amount}")
                            Text("${stringResource(R.string.description)}: ${e.description}")
                        }
                    }
                }
            }
        }
    }
}
