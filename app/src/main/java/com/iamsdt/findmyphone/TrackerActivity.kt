package com.iamsdt.findmyphone

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_tracker.*


class TrackerActivity : AppCompatActivity(), Adapter.ClickListener {

    //todo post on blogger about recycler view and contact

    val list = ArrayList<UserContract>()
    val CONTACT_CODE: Int = 121

    var adapter:Adapter ?= null
    var userData:UserData ?= null

    var database:DatabaseReference? = null

    //todo fix number 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)

        userData = UserData(this)
        userData!!.loadContactInfo()

        val layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        tracker_recyclerView.layoutManager = layoutManager

        refreshData()
        adapter = Adapter(list, this)
        tracker_recyclerView.adapter = adapter


        database = FirebaseDatabase.getInstance().reference

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            // Called when a user swipes left or right on a ViewHolder
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {

                val userinfo = list[viewHolder.adapterPosition]
                val name = userinfo.name
                UserData.myTracker.remove(name)

                database!!.child("User").child(userinfo.phone)
                        .child("finders").child(userData!!.loadPhoneNumber())
                        .removeValue()

                refreshData()
            }
        }).attachToRecyclerView(tracker_recyclerView)

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

            } else{
                pickContact()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when(requestCode){
            PCIK_CODE -> {
                if (resultCode == Activity.RESULT_OK){
                    val contactData = data!!.data

                    val cursor = contentResolver.query(contactData,null,null,null,null)

                    if (cursor.moveToFirst()){

                        val id = cursor.getString(cursor.getColumnIndex(
                                ContactsContract.Contacts._ID))

                        val hasPhoneNumber = cursor.getString(cursor.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER))

                        if(hasPhoneNumber == "1"){
                            val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,null,null)

                            phones.moveToFirst()
                            var phoneNumber = phones.getString(phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.DATA1))

                            val name = cursor.getString(cursor.getColumnIndex(
                                    ContactsContract.Contacts.DISPLAY_NAME))

                            //complete fix number
                            phoneNumber = UserData.formatPhoneNumber(phoneNumber)

                            UserData.myTracker.put(name,phoneNumber)
                            userData!!.saveContactInfo()
                            refreshData()


                            //save to user database
                            database!!.child("User").child(phoneNumber)
                                    .child("finders").child(userData!!.loadPhoneNumber())
                                    .setValue(true)



                            phones.close()
                        }
                    }

                    cursor.close()
                }
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }

    fun refreshData(){
        list.clear()
        for ((key,value) in UserData.myTracker){
            list.add(UserContract(key,value))
        }
        adapter?.notifyDataSetChanged()
    }

}
