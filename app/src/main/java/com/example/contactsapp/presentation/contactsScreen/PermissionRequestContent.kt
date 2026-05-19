package com.example.contactsapp.presentation.contactsScreen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PermissionRequestContent(
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Column (
        modifier = Modifier.fillMaxSize().wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onClick
        ) {
            Text(buttonText)
        }
    }
}