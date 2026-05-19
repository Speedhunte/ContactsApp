package com.example.contactsapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.contactsapp.IContactsAidlService
import com.example.contactsapp.data.model.ComparableContactRow
import com.example.contactsapp.data.repository.DeleteDuplicateContactsStatus
import com.example.contactsapp.domain.ContactsProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class ContactsAidlService : Service() {

    @Inject
    lateinit var contactsProvider: ContactsProvider

    private val binder = object : IContactsAidlService.Stub() {
        override fun deleteDuplicateContacts(): Int {

            return runBlocking(Dispatchers.IO) {
                try {
                    val rows = contactsProvider.fetchComparableContactRows()

                    val contactsRows = rows
                        .groupBy { row -> row.contactId }
                        .values
                        .toList()

                    val duplicateGroups = contactsRows
                        .groupBy { contactRows -> buildContactKey(contactRows) }
                        .values
                        .filter { group -> group.size > 1 }

                    if (duplicateGroups.isEmpty()) {
                        return@runBlocking DeleteDuplicateContactsStatus.NOT_FOUND
                    }

                    val rawContactIdsToDelete = duplicateGroups
                        .flatMap { group -> group.drop(1) }
                        .flatMap { contactRows -> contactRows.map { row -> row.rawContactId } }
                        .distinct()

                    if (rawContactIdsToDelete.isEmpty()) {
                        return@runBlocking DeleteDuplicateContactsStatus.NOT_FOUND
                    }

                    contactsProvider.deleteRawContacts(rawContactIdsToDelete)

                    DeleteDuplicateContactsStatus.SUCCESS
                } catch (e: Exception) {
                    Log.d("mytag", e.message.toString())
                    DeleteDuplicateContactsStatus.ERROR
                }
            }
        }
    }

    private fun buildContactKey(rows: List<ComparableContactRow>): String {
        val firstRow = rows.first()
        val contactFields = rows
            .map { row -> "${row.mimeType}|${row.data1}|${row.data2}|${row.data3}" }
            .distinct()
            .sorted()
            .joinToString(separator = "#")

        return "${firstRow.displayName}|${firstRow.photoUri}|$contactFields"
    }

    override fun onBind(intent: Intent?): IBinder = binder
}

