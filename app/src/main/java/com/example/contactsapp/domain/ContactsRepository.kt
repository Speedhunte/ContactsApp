package com.example.contactsapp.domain

import com.example.contactsapp.domain.model.Contact

interface ContactsRepository {
    suspend fun getContacts(): List<Contact>
    suspend fun deleteDuplicateContacts(): Int
}