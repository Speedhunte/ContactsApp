package com.example.contactsapp.presentation.viewModel

import com.example.contactsapp.domain.model.Contact

sealed interface ContactsUiEvent {

    data object DuplicateContactsDeleted: ContactsUiEvent

    data object DuplicateContactsNotFound : ContactsUiEvent

    data class Error(
        val message: String
    ) : ContactsUiEvent
}


data class ContactsUiState(
    val contacts: Map<Char, List<Contact>> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
) {
    val hasContacts: Boolean
        get() = contacts.isNotEmpty()

}