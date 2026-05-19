package com.example.contactsapp.data.utils

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.Settings
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull

fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )

    startActivity(intent)
}
fun <T> Cursor?.map(block: (Cursor) -> T): List<T>? {
    return this?.let {
        val result = mutableListOf<T>()
        if(moveToFirst()){
            do {
                result.add(block(this))
            }while (moveToNext())
        }
        result
    }
}

fun Cursor.getStringByName(columnName: String): String {
    return getString(getColumnIndexOrThrow(columnName))
}

fun Cursor.optStringByName(columnName: String): String? {
    val index = getColumnIndex(columnName)
    return getStringOrNull(index)
}

fun Cursor.optIntByName(columnName: String, defValue: Int = -1): Int {
    val index = getColumnIndex(columnName)
    return getIntOrNull(index) ?: defValue
}


