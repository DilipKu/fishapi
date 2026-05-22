package com.example.composeapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dilip.composeapp.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AppLogo(size: androidx.compose.ui.unit.Dp) {
    Image(
        painter = painterResource(id = R.drawable.rio_app_icon),
        contentDescription = "App Logo",
        modifier = Modifier.size(size)
    )
}

@Composable
fun ErrorMessage(message: String) {
    val displayMessage = remember(message) {
        if (message.contains("\n")) {
            val lines = message.lineSequence()
                .filter { it.isNotBlank() && !it.startsWith("URL:") && !it.startsWith("Headers:") && !it.startsWith("Http Method:") }
                .toList()
            
            lines.find { it.contains(":") || it.length > 20 } ?: lines.firstOrNull() ?: "An error occurred"
        } else {
            message
        }
    }
    Text(
        text = displayMessage,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    label: String,
    options: List<String>,
    selectedOption: String, // This should be the raw (English) value
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = if (selectedOption.isEmpty()) label else getTranslatedCategory(selectedOption),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(getTranslatedCategory(option)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun getTranslatedCategory(category: String): String {
    return when (category.lowercase()) {
        "major" -> stringResource(R.string.major)
        "minor" -> stringResource(R.string.minor)
        "chikna" -> stringResource(R.string.chikna)
        "tilapiya" -> stringResource(R.string.tilapiya)
        "miscellaneous" -> stringResource(R.string.miscellaneous)
        "fixed company" -> stringResource(R.string.fixed_company)
        "fisherman" -> stringResource(R.string.fisherman)
        "transport" -> stringResource(R.string.transport)
        else -> category
    }
}

fun formatToIST(utcString: String?): String {
    if (utcString.isNullOrBlank()) return ""
    return try {
        // Supabase usually provides ISO 8601 strings
        // We parse it and convert to IST
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        
        val date = inputFormat.parse(utcString)
        val outputFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        
        if (date != null) outputFormat.format(date) else utcString
    } catch (e: Exception) {
        utcString
    }
}
