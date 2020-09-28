package com.example.contactbook

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val READ_CONTACTS_PERMISSION_REQUEST_ID = 1337
    }

    private var contactsList: MutableList<Contact> = emptyList<Contact>().toMutableList()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(TAG, "onRequestPermissionsResult")
        if (requestCode == READ_CONTACTS_PERMISSION_REQUEST_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateContactsList()
            } else {
                noContacts.text = getString(R.string.no_permission)
            }
        }
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun Context.fetchAllContracts() {
        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
            .use { cursor ->
                contactsList.clear()
                if (cursor == null) return
                while (cursor.moveToNext()) {
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                            ?: "N/A"
                    val phoneNumber =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            ?: "N/A"
                    contactsList.add(Contact(name, phoneNumber))
                }
            }
    }

    private fun updateContactsList() {
        fetchAllContracts()
        myRecyclerView.adapter?.notifyDataSetChanged()
        if (contactsList.isEmpty()) {
            noContacts.text = getString(R.string.no_contacts)
        } else {
            noContacts.visibility = View.GONE
        }
        Toast.makeText(
            this@MainActivity,
            resources.getQuantityString(
                R.plurals.contact_counts,
                contactsList.size,
                contactsList.size
            ),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "onCreate")
        val viewManager = LinearLayoutManager(this)

        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = ContactAdapter(contactsList) {
                dialPhoneNumber(it.number)
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                READ_CONTACTS_PERMISSION_REQUEST_ID
            )
        } else {
            updateContactsList()
        }

    }
}