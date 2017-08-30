package com.iamsdt.findmyphone

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import java.text.SimpleDateFormat
import java.util.*


class LoginActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null

    //complete post firebase signInAnonymously

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val userData = UserData(this)

        mAuth = FirebaseAuth.getInstance()

        mAuth!!.signInAnonymously()
                .addOnCompleteListener(this, { task ->
                    if (task.isSuccessful) {

                        val user = mAuth!!.currentUser
                        userData.saveData("user", user!!.uid)

                        Toast.makeText(this, "Authentication successful.",
                                Toast.LENGTH_SHORT).show()

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()

                    }
                })


        login_btn.setOnClickListener {

            var phone = login_et.text.toString().trim()
            phone = UserData.formatPhoneNumber(phone)

            userData.saveData("phone", phone)

            val df = SimpleDateFormat("dd/MMM/yy", Locale.ENGLISH)
            val date = Date()

            val database = FirebaseDatabase.getInstance().reference
            database.child("User").child(phone)
                    .child("request").setValue(df.format(date).toString())

            database.child("User").child(phone)
                    .child("finders").setValue(df.format(date).toString())

            finish()
        }
    }

}
