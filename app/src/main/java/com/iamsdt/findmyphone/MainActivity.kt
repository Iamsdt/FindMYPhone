package com.iamsdt.findmyphone

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), Adapter.ClickListener {


    var database: DatabaseReference? = null
    val listOfContact = ArrayList<UserContract>()

    var adapter: Adapter? = null
    var userData: UserData? = null

    val CONTACT_CODE = 123
    val LOCATION_CODE = 133

    var myLocation: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userData = UserData(this)
        //userData!!.loadPhoneNumber()

        val layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        main_recyclerView.layoutManager = layoutManager

        adapter = Adapter(listOfContact, this)
        main_recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance().reference

    }

    override fun onResume() {
        super.onResume()

        refresh()

        checkContactPermission()
        checkLocationPermission()
    }

    fun refresh() {

        if (userData!!.loadPhoneNumber() == "") {
            return
        }

        database!!.child("User").child(userData!!.loadPhoneNumber())
                .child("finders")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        try {
                            val td: HashMap<String, Any> = dataSnapshot!!.value as HashMap<String, Any>

                            listOfContact.clear()

                            if (td == null) {
                                listOfContact.add(UserContract("NO_USERS", "nothing"))
                                adapter!!.notifyDataSetChanged()
                                return
                            }

                            for (key in td.keys) {
                                val name = listOfContacts[key]
                                listOfContact.add(UserContract(name.toString(), key))

                            }

                            adapter!!.notifyDataSetChanged()
                        } catch (ex: Exception) {
//                            listOfContact.clear()
//                            listOfContact.add(UserContract("NO_USERS","nothing"))
//                            adapter!!.notifyDataSetChanged()
//                            return
                        }

                    }

                })

    }

    override fun onItemClick(position: Int) {
        Toast.makeText(applicationContext, "pos$position", Toast.LENGTH_SHORT).show()
    }

    fun checkContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS),
                        CONTACT_CODE)

            } else {
                loadContract()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            CONTACT_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadContract()
                } else {
                    Toast.makeText(this, "Cannot access to contact. please allow the permission", Toast.LENGTH_LONG).show()
                }
            }

            LOCATION_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    Toast.makeText(this, "Cannot access to contact. please allow the permission", Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    val listOfContacts = HashMap<String, String>()
    fun loadContract() {
        try {
            listOfContacts.clear()

            val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null)
            cursor.moveToFirst()
            do {
                val name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                listOfContacts.put(UserData.formatPhoneNumber(phoneNumber), name)

            } while (cursor.moveToNext())

            cursor.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.add -> {
                startActivity(Intent(this, TrackerActivity::class.java))
                return true
            }

            R.id.help -> {
                //todo help
                return true
            }

            else -> return super.onOptionsItemSelected(item)

        }
    }

    fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_CODE)

            }

            getUserLocation()

        }
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        val locationListener = MyLocationListener()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(
                //provider
                LocationManager.GPS_PROVIDER,
                //min update
                3,
                //distance
                3f,
                //location listener
                locationListener
        )

        val phone = userData!!.loadPhoneNumber()

        database!!.child("User").child(phone)
                .child("request")
                .addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {

                        if (myLocation == null) return

                        database!!.child("User").child(phone)
                                .child("request").child("location")
                                .child("lat").setValue(myLocation!!.latitude)

                        database!!.child("User").child(phone)
                                .child("request").child("location")
                                .child("lon").setValue(myLocation!!.longitude)

                        val df = SimpleDateFormat("dd/MMM/yy", Locale.ENGLISH)
                        val date = Date()

                        database!!.child("User").child(phone)
                                .child("request").child("location")
                                .child("lastOnline").setValue(df.format(date).toString())

                    }

                })

    }

    inner class MyLocationListener : LocationListener {

        constructor() : super() {
            myLocation = Location("shudipto")
            myLocation!!.latitude = 0.0
            myLocation!!.longitude = 0.0
        }

        override fun onLocationChanged(location: Location?) {
            myLocation = location
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onProviderDisabled(p0: String?) {
        }

    }
}
