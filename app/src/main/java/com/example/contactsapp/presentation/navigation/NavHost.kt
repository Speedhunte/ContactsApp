package com.example.contactsapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.contactsapp.presentation.contactsScreen.ContactsCleanerScreen
import com.example.contactsapp.presentation.contactsScreen.ContactsScreen
import kotlinx.serialization.Serializable

@Serializable
data object ContactsScreen




@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
){
    val navController = rememberNavController()

    NavHost(navController=navController, startDestination = ContactsScreen
    ) {

        composable<ContactsScreen> {
            ContactsCleanerScreen()
        }
    }
}
