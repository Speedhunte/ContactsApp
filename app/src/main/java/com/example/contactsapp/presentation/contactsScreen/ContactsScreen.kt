package com.example.contactsapp.presentation.contactsScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.data.utils.openAppSettings
import com.example.contactsapp.presentation.contactsScreen.components.ContactAvatar
import com.example.contactsapp.presentation.contactsScreen.components.ContactInitialHeader
import com.example.contactsapp.presentation.viewModel.ContactsUiEvent
import com.example.contactsapp.presentation.viewModel.ContactsUiState
import com.example.contactsapp.presentation.viewModel.ContactsViewModel
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.example.contactsapp.R

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ContactsCleanerScreen(
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val snackBarHostState = remember {
        SnackbarHostState()
    }



    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ContactsUiEvent.DuplicateContactsDeleted -> {
                    snackBarHostState.showSnackbar(message = "Повторяющиеся контакты удалены успешно")
                }

                ContactsUiEvent.DuplicateContactsNotFound -> {
                    snackBarHostState.showSnackbar(message = "Повторяющиеся контакты не найдены")
                }

                is ContactsUiEvent.Error -> {
                    snackBarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    var hasRequestedPermissions by rememberSaveable {
        mutableStateOf(false)
    }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS
        )
    )

    val permissionUiState = permissionsState.toContactsPermissionUiState(
        hasRequestedPermissions = hasRequestedPermissions
    )

    LaunchedEffect(permissionUiState) {
        if (permissionUiState == ContactsPermissionUiState.Granted &&
            uiState.contacts.isEmpty()
        ) {
            viewModel.loadContacts()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.top_bar_title),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {


            when (permissionUiState) {
                ContactsPermissionUiState.Granted -> {
                    ContactsScreen(state = uiState, onCleanClick = viewModel::deleteDuplicates)
                }

                ContactsPermissionUiState.NotRequested -> {
                    PermissionRequestContent(
                        title = stringResource(id = R.string.permission_not_requested_title),
                        description = stringResource(id = R.string.permission_not_requested_desc),
                        buttonText = stringResource(id = R.string.permission_btn_allow),
                        onClick = {
                            permissionsState.launchMultiplePermissionRequest()
                            hasRequestedPermissions = true
                        }
                    )
                }

                ContactsPermissionUiState.RationaleRequired -> {
                    PermissionRequestContent(
                        title = stringResource(id = R.string.permission_rationale_title),
                        description = stringResource(id = R.string.permission_rationale_desc),
                        buttonText = stringResource(id = R.string.permission_btn_try_again),
                        onClick = {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                    )
                }

                ContactsPermissionUiState.SettingsRequired -> {
                    PermissionRequestContent(
                        title = stringResource(id = R.string.permission_settings_title),
                        description = stringResource(id = R.string.permission_settings_desc),
                        buttonText = stringResource(id = R.string.permission_btn_open_settings),
                        onClick = {
                            context.openAppSettings()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ContactsScreen(
    state: ContactsUiState,
    onCleanClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.contacts.isEmpty() -> {
                Text(
                    text = stringResource(id = R.string.contacts_empty),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            else -> {
                ContactsList(
                    contacts = state.contacts,
                    onDeleteDuplicates = onCleanClick
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContactsList(
    contacts: Map<Char, List<Contact>>,
    onDeleteDuplicates: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            contacts.forEach { (initial, contactsByInitial) ->
                stickyHeader(
                    key = "header_$initial"
                ) {
                    ContactInitialHeader(
                        initial = initial
                    )
                }

                items(
                    items = contactsByInitial,
                ) { contact ->
                    ContactItem(
                        contact = contact
                    )
                }
            }
        }

        Button(
            onClick = onDeleteDuplicates
        ) {
            Text(
                text = stringResource(id = R.string.btn_delete_duplicates)
            )
        }
    }
}

@Composable
private fun ContactItem(
    contact: Contact
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactAvatar(
            name = contact.name,
            photoUri = contact.photoUri
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = contact.mainPhoneNumber,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    HorizontalDivider()
}
