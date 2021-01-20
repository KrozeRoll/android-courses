package com.example.myrecycleview

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.*
import kotlin.collections.ArrayList

data class Contact(val name: String, val phone: String)

fun Context.fetchAllContacts(): List<Contact> {
    contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        null,
        null,
        null
    )
        .use { cursor ->
            if (cursor == null) return emptyList()
            val builder = ArrayList<Contact>()
            while (cursor.moveToNext()) {
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)) ?: "N/A"
                val phoneNumber =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) ?: "N/A"

                builder.add(Contact(name, phoneNumber))
            }
            return builder
        }
}

class UserAdapter(
    val contacts: MutableList<Contact>,
    private val onClick: (Contact) -> Unit
): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private val itemsCopy = contacts.toMutableList()

    class UserViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
        fun bind(contact: Contact) {
            with(root) {
                name.text = contact.name
                phone.text = contact.phone
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val holder = UserViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        )
        holder.root.setOnClickListener {
            onClick(contacts[holder.adapterPosition])
        }
        //itemsCopy.addAll(contacts)
        return holder

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) = holder.bind(contacts[position])

    override fun getItemCount(): Int = contacts.size

    fun filter(text: String) {
        contacts.clear()
        if (text.isEmpty()) {
            contacts.addAll(itemsCopy)
        } else {
            val lowerText = text.toLowerCase(Locale.ROOT)
            for (item in itemsCopy) {
                if (item.name.toLowerCase(Locale.ROOT).contains(lowerText) || item.phone.toLowerCase(
                        Locale.ROOT
                    )
                        .contains(lowerText)
                ) {
                    contacts.add(item)
                }
            }
        }
    }
}

class MainActivity : AppCompatActivity() {
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val contactsList = this@MainActivity.fetchAllContacts() as MutableList<Contact>
                    makeRecycleView(contactsList)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.permissionErrorMessage),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    private fun makeRecycleView(contactsList: MutableList<Contact>) {
        val viewManager = LinearLayoutManager(this@MainActivity)
        val userAdapter = UserAdapter(contactsList) {
            val phone: String = it.phone
            if (!TextUtils.isEmpty(phone)) {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
            }
        }
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = userAdapter
        }
        Toast.makeText(
            this@MainActivity,
            resources.getQuantityString(
                R.plurals.loadMessage,
                contactsList.size,
                contactsList.size
            ),
            Toast.LENGTH_SHORT
        ).show()
        searchView.visibility = View.VISIBLE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                userAdapter.filter(newText!!)
                userAdapter.notifyDataSetChanged()
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                userAdapter.filter(query!!)
                userAdapter.notifyDataSetChanged()
                return true
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_CONTACTS
            )
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_CONTACTS),
                1
            )
        } else {
            val contactsList = this@MainActivity.fetchAllContacts() as MutableList<Contact>
            makeRecycleView(contactsList)
        }
    }
}