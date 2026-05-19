package com.example.contactsapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactsapp.data.repository.ContactsRepositoryImpl
import com.example.contactsapp.data.repository.DeleteDuplicateContactsStatus
import com.example.contactsapp.domain.usecase.DeleteDuplicatesUseCase

import com.example.contactsapp.domain.usecase.GetContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import javax.inject.Inject
import kotlin.toString


@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val getContactsUseCase: GetContactsUseCase,
    private val deleteDuplicatesUseCase: DeleteDuplicatesUseCase
): ViewModel() {

    private val _state = MutableStateFlow(ContactsUiState())
    val state = _state.asStateFlow()


    private val _events = MutableSharedFlow<ContactsUiEvent>()
    val events = _events.asSharedFlow()



    fun loadContacts() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }
                runCatching{
                    getContactsUseCase()
                }.onSuccess {contacts ->
                    _state.update {
                        it.copy(
                            contacts = contacts,
                            isLoading = false
                        )
                    }

                }.onFailure {error->
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    _events.emit(ContactsUiEvent.Error(error.message.toString()))
                }
        }

    }

    fun deleteDuplicates() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val statusCode = deleteDuplicatesUseCase()

            when (statusCode) {
                DeleteDuplicateContactsStatus.SUCCESS -> {
                    loadContacts()

                    _events.emit(ContactsUiEvent.DuplicateContactsDeleted)
                }

                DeleteDuplicateContactsStatus.NOT_FOUND -> {
                    _state.update {
                        it.copy(isLoading = false)
                    }
                    _events.emit(ContactsUiEvent.DuplicateContactsNotFound)
                }

                else -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                    _events.emit(
                        ContactsUiEvent.Error(
                            message = ERROR_MESSAGE
                        )
                    )
                }
            }
        }
    }

    companion object{
        const val ERROR_MESSAGE = "Произошла ошибка при удалении контактов через AIDL"
    }

}
