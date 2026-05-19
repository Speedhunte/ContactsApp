package com.example.contactsapp.data.model


data class PhoneContactRow(
    val contactId: String,
    val rawContactId: String,
    val displayName: String,
    val phoneNumber: String,
    val normalizedNumber: String?,
    val isPrimary: Boolean,
    val isSuperPrimary: Boolean,
    val photoThumbnailUri: String?,
    val phoneType: Int
)


