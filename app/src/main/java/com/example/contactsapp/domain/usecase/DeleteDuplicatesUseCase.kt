package com.example.contactsapp.domain.usecase

import com.example.contactsapp.domain.ContactsProvider
import com.example.contactsapp.domain.ContactsRepository
import javax.inject.Inject

class DeleteDuplicatesUseCase @Inject constructor(
    private val repo: ContactsRepository
) {
    suspend operator fun invoke() = repo.deleteDuplicateContacts()
}