package com.example.contactsapp.data.mapper

import android.database.Cursor
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.*
import com.example.contactsapp.data.model.ComparableContactRow
import com.example.contactsapp.data.model.PhoneContactRow
import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.data.utils.getStringByName
import com.example.contactsapp.data.utils.optIntByName
import com.example.contactsapp.data.utils.optStringByName


fun Cursor.asPhoneContactRow(): PhoneContactRow {
    return PhoneContactRow(
        contactId = getStringByName(Phone.CONTACT_ID),
        rawContactId = getStringByName(Phone.RAW_CONTACT_ID),
        displayName = getStringByName(Phone.DISPLAY_NAME_PRIMARY),
        phoneNumber = getStringByName(Phone.NUMBER),
        normalizedNumber = optStringByName(Phone.NORMALIZED_NUMBER),
        isPrimary = optIntByName(Phone.IS_PRIMARY, 0) == 1,
        isSuperPrimary = optIntByName(Phone.IS_SUPER_PRIMARY, 0) == 1,
        photoThumbnailUri = optStringByName(Phone.PHOTO_THUMBNAIL_URI),
        phoneType = optIntByName(Phone.TYPE, Phone.TYPE_OTHER)
    )
}

fun List<PhoneContactRow>.toContacts(): List<Contact> {
    return groupBy { it.contactId }
        .map { (contactId, rows) ->
            val mainPhoneRow = rows.firstOrNull {
                it.phoneType == Phone.TYPE_MOBILE && it.isSuperPrimary
            } ?: rows.firstOrNull {
                it.phoneType == Phone.TYPE_MOBILE && it.isPrimary
            } ?: rows.firstOrNull {
                it.phoneType == Phone.TYPE_MOBILE
            } ?: rows.firstOrNull {
                it.isSuperPrimary
            } ?: rows.firstOrNull {
                it.isPrimary
            } ?: rows.first()

            Contact(
                id = contactId,
                rawContactId = mainPhoneRow.rawContactId,
                name = mainPhoneRow.displayName,
                mainPhoneNumber = mainPhoneRow.phoneNumber,
                normalizedMainPhoneNumber = mainPhoneRow.normalizedNumber,
                photoUri = mainPhoneRow.photoThumbnailUri
            )
        }
}


fun Cursor.asComparableContactRow(): ComparableContactRow {
    return ComparableContactRow(
        dataId = getStringByName(ContactsContract.Data._ID),
        contactId = getStringByName(ContactsContract.Data.CONTACT_ID),
        rawContactId = getStringByName(ContactsContract.Data.RAW_CONTACT_ID),
        displayName = optStringByName(ContactsContract.Data.DISPLAY_NAME_PRIMARY),
        photoUri = optStringByName(ContactsContract.Data.PHOTO_THUMBNAIL_URI),
        mimeType = getStringByName(ContactsContract.Data.MIMETYPE),
        data1 = optStringByName(ContactsContract.Data.DATA1),
        data2 = optStringByName(ContactsContract.Data.DATA2),
        data3 = optStringByName(ContactsContract.Data.DATA3)
    )
}