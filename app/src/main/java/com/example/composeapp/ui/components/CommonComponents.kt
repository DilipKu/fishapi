package com.example.composeapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dilip.composeapp.R

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
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
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
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
