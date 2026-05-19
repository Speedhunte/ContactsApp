package com.example.contactsapp.domain.usecase

import com.example.contactsapp.domain.ContactsRepository
import javax.inject.Inject
import kotlin.collections.groupBy

class GetContactsUseCase @Inject constructor(
    private val repo: ContactsRepository

) {
    suspend operator fun invoke() = repo.getContacts().groupBy { it.name.first()}.toSortedMap()
}