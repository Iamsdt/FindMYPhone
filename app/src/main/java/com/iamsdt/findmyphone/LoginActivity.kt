package com.iamsdt.findmyphone

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val userData = UserData(this)

        login_btn.setOnClickListener {
            userData.saveData(login_et.text.toString().trim())
            finish()
        }
    }
}
