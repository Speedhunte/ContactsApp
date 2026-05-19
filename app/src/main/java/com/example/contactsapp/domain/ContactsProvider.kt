package com.example.contactsapp.domain

import com.example.contactsapp.data.model.ComparableContactRow
import com.example.contactsapp.domain.model.Contact

interface ContactsProvider {


    suspend fun getContacts(): List<Contact>

    suspend fun deleteRawContacts(rawContactIds: List<String>)
    fun fetchComparableContactRows(): List<ComparableContactRow>

}