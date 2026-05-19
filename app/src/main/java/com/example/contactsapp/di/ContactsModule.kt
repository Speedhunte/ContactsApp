package com.example.contactsapp.di

import android.content.Context
import com.example.contactsapp.data.provider.ContactsProviderImpl
import com.example.contactsapp.data.repository.ContactsRepositoryImpl
import com.example.contactsapp.domain.ContactsProvider
import com.example.contactsapp.domain.ContactsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ContactsModule {

    @Provides
    @Singleton
    fun provideContactsProvider(@ApplicationContext context: Context): ContactsProvider {
        return ContactsProviderImpl(
            contentResolver = context.contentResolver,
            dispatcher = Dispatchers.IO
        )
    }

}

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindContactsRepository(
        impl: ContactsRepositoryImpl
    ): ContactsRepository
}