package com.example.contactsapp.data.repository

import com.example.contactsapp.client.ContactsAidlClient
import com.example.contactsapp.domain.ContactsProvider
import com.example.contactsapp.domain.ContactsRepository
import com.example.contactsapp.domain.model.Contact
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ContactsRepositoryImpl @Inject constructor(
    private val contactsProvider: ContactsProvider,
    private val aidlServiceManager: ContactsAidlClient
): ContactsRepository {

    override suspend fun getContacts(): List<Contact> {
        return contactsProvider.getContacts()
    }

    override suspend fun deleteDuplicateContacts(): Int {
            return try {
                aidlServiceManager.deleteDuplicateContacts()
            } catch (e: Exception) {
                DeleteDuplicateContactsStatus.ERROR
            }
    }

}