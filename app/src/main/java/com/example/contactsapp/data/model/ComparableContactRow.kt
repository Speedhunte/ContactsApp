package com.example.contactsapp.data.model

data class ComparableContactRow(
    val dataId: String,
    val contactId: String,
    val rawContactId: String,
    val displayName: String?,
    val photoUri: String?,
    val mimeType: String,
    val data1: String?,
    val data2: String?,
    val data3: String?
)