package com.iamsdt.findmyphone

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

/**
* Created by Shudipto Trafder on 8/27/2017.
* ${PACKAGE_NAME}
*/
class UserData(context: Context) {

    var sharedPref:SharedPreferences ?= null
    var context: Context ?= context

    init {
        this.sharedPref = context.getSharedPreferences(
                Constant.sharedPrefKey,Context.MODE_PRIVATE)
    }

    fun saveData(phoneNumber: String){
        val editor = sharedPref!!.edit()
        editor.putString(Constant.sharedPrefPhoneValue,phoneNumber)
        editor.apply()
    }

    fun loadPhoneNumber():String{
        val phone = sharedPref!!.getString(Constant.sharedPrefPhoneValue,null)

        if (phone == null){
            context!!.startActivity(Intent(context,LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        return phone
    }

}