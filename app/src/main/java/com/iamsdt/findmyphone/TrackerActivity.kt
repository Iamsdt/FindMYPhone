package com.iamsdt.findmyphone

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_tracker.*

class TrackerActivity : AppCompatActivity(), Adapter.ClickListener {


    val list = ArrayList<UserContract>()
    val CONTACT_CODE: Int = 121

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)

        val layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        tracker_recyclerView.layoutManager = layoutManager
        dummyData()
        tracker_recyclerView.adapter = Adapter(list, this)

    }


    fun dummyData() {
        list.add(UserContract("Shudipto", "01732033963"))
        list.add(UserContract("Shudipto1", "01732033963"))
        list.add(UserContract("Shudipto2", "01732033963"))
        list.add(UserContract("Shudipto3", "01732033963"))
        list.add(UserContract("Shudipto4", "01732033963"))
        list.add(UserContract("Shudipto5", "01732033963"))

    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this, "position $position", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tracker_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.add_tracker -> {
                addContact()
                return true
            }

            R.id.finish_tracker -> {
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)

        }
    }

    fun addContact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS),
                        CONTACT_CODE)

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            CONTACT_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickContact()
                } else {
                    Toast.makeText(this, "Cannot access to contact. please allow the permission", Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
    val PCIK_CODE=1234
    fun pickContact(){
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, PCIK_CODE)
    }


}
