package com.example.contactsapp.domain.model

data class Contact(
    val id: String,
    val rawContactId: String,
    val name: String,
    val mainPhoneNumber: String,
    val normalizedMainPhoneNumber: String?,
    val photoUri: String?
)
