package com.example.contactsapp.presentation.contactsScreen

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

sealed interface ContactsPermissionUiState {
    data object Granted : ContactsPermissionUiState
    data object NotRequested : ContactsPermissionUiState
    data object RationaleRequired : ContactsPermissionUiState
    data object SettingsRequired : ContactsPermissionUiState
}


@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.toContactsPermissionUiState(
    hasRequestedPermissions: Boolean
): ContactsPermissionUiState {
    return when {
        allPermissionsGranted -> {
            ContactsPermissionUiState.Granted
        }

        !hasRequestedPermissions -> {
            ContactsPermissionUiState.NotRequested
        }

        shouldShowRationale -> {
            ContactsPermissionUiState.RationaleRequired
        }

        else -> {
            ContactsPermissionUiState.SettingsRequired
        }
    }
}