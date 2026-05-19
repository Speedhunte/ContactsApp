package com.example.contactsapp

import android.app.Application
import com.example.contactsapp.client.ContactsAidlClient
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ContactsApplication: Application() {

}