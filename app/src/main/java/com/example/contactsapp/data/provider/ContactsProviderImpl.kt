package com.example.contactsapp.data.provider

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.*
import com.example.contactsapp.data.mapper.asComparableContactRow
import com.example.contactsapp.data.mapper.asPhoneContactRow
import com.example.contactsapp.data.mapper.toContacts
import com.example.contactsapp.data.model.ComparableContactRow
import com.example.contactsapp.domain.ContactsProvider
import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.data.utils.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsProviderImpl(
    private val contentResolver: ContentResolver,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ContactsProvider{

    private fun query(
        uri: Uri,
        projection: Array<String>,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sort: String? = null
    ): Cursor? {
        return contentResolver.query(uri, projection, selection, selectionArgs, sort)
    }

     override suspend fun getContacts(): List<Contact> = withContext(dispatcher){
        fetchContacts() ?: emptyList()
    }

    private fun fetchContacts(): List<Contact>? {
        val cursor: Cursor?= query(
            uri = Phone.CONTENT_URI,
            projection = CONTACTS_WITH_PHONE_PROJECTION,
            selection = null,
            selectionArgs = null,
            sort = null
        )

        val res = cursor?.map { it.asPhoneContactRow() }?.toContacts()
        cursor?.close()
        return res
    }

     override fun fetchComparableContactRows(): List<ComparableContactRow> {
        val cursor =  query(
            uri = ContactsContract.Data.CONTENT_URI,
            projection = COMPARABLE_CONTACT_PROJECTION,
            selection = COMPARABLE_CONTACT_SELECTION,
            selectionArgs = COMPARABLE_CONTACT_SELECTION_ARGS,
            sort = "${ContactsContract.Data.CONTACT_ID} ASC"
        )
         val res = cursor.map { it.asComparableContactRow() }?:emptyList()
         cursor?.close()
         return res
    }
     override suspend fun deleteRawContacts(rawContactIds: List<String>) =
        withContext(dispatcher) {
            deleteRawContactsInternal(rawContactIds)
        }

    private fun deleteRawContactsInternal(rawContactIds: List<String>) {
        if (rawContactIds.isEmpty()) return

        val operations = rawContactIds.map { rawContactId ->
            ContentProviderOperation
                .newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(
                    "${ContactsContract.RawContacts._ID} = ?",
                    arrayOf(rawContactId)
                )
                .build()
        }

        contentResolver.applyBatch(
            ContactsContract.AUTHORITY,
            ArrayList(operations)
        )
    }



    companion object {
        private  val CONTACTS_WITH_PHONE_PROJECTION = arrayOf(
            Phone.CONTACT_ID,
            Phone.RAW_CONTACT_ID,
            Phone.DISPLAY_NAME_PRIMARY,
            Phone.NUMBER,
            Phone.NORMALIZED_NUMBER,
            Phone.IS_PRIMARY,
            Phone.IS_SUPER_PRIMARY,
            Phone.PHOTO_THUMBNAIL_URI,
            Phone.TYPE
        )
        private val COMPARABLE_CONTACT_PROJECTION = arrayOf(
            ContactsContract.Data._ID,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.RAW_CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.Data.PHOTO_THUMBNAIL_URI,
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.DATA1,
            ContactsContract.Data.DATA2,
            ContactsContract.Data.DATA3
        )

        private const val COMPARABLE_CONTACT_SELECTION =
            "${ContactsContract.Data.MIMETYPE} IN (?, ?, ?, ?)"

        private val COMPARABLE_CONTACT_SELECTION_ARGS = arrayOf(
            StructuredName.CONTENT_ITEM_TYPE,
            Phone.CONTENT_ITEM_TYPE,
            Email.CONTENT_ITEM_TYPE,
            Website.CONTENT_ITEM_TYPE
        )
    }

}


