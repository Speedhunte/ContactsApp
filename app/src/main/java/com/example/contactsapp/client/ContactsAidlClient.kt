package com.example.contactsapp.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.contactsapp.IContactsAidlService
import com.example.contactsapp.data.repository.DeleteDuplicateContactsStatus
import com.example.contactsapp.service.ContactsAidlService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsAidlClient @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var service: IContactsAidlService? = null

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(
            name: ComponentName?,
            binder: IBinder?
        ) {
            service = IContactsAidlService.Stub.asInterface(binder)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
        }
    }

    fun bind() {
        val intent = Intent(context, ContactsAidlService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbind() {
        context.unbindService(connection)
        service = null
    }

    suspend fun deleteDuplicateContacts(): Int =
        withContext(Dispatchers.IO) {
            val status = service?.deleteDuplicateContacts() ?: DeleteDuplicateContactsStatus.ERROR
            return@withContext status
        }
}