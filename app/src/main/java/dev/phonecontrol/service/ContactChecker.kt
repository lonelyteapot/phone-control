package dev.phonecontrol.service

import android.Manifest
import android.content.Context
import android.provider.ContactsContract
import androidx.annotation.RequiresPermission

class ContactChecker(private val context: Context, private val phoneNumber: String) {

    @get:RequiresPermission(Manifest.permission.READ_CONTACTS)
    val isNumberInContacts: Boolean by lazy {
        // Query the contacts database to check if the phone number exists
        val contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val selection = "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?"
        val selectionArgs = arrayOf(phoneNumber)
        val cursor =
            context.contentResolver.query(contactUri, projection, selection, selectionArgs, null)

        // Check if the cursor has any results
        val result = (cursor?.count ?: 0) > 0

        // Close the cursor
        cursor?.close()

        result
    }
}