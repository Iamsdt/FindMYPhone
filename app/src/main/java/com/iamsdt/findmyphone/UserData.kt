package com.iamsdt.findmyphone

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseUser
import java.util.HashMap

/**
* Created by Shudipto Trafder on 8/27/2017.
* ${PACKAGE_NAME}
*/
class UserData(var context: Context) {

    var sharedPref:SharedPreferences ?= null

    init {
        this.sharedPref = context.getSharedPreferences(
                Constant.sharedPrefKey,Context.MODE_PRIVATE)
    }

    fun saveData(type:String,phoneNumber: String){
        val editor = sharedPref!!.edit()
        if (type == "phone"){
            editor.putString(Constant.sharedPrefPhoneValue,phoneNumber)
        } else{
            editor.putString(Constant.sharedPrefUser,phoneNumber)
        }

        editor.apply()
    }

    fun loadPhoneNumber():String{
        val phone = sharedPref!!.getString(Constant.sharedPrefPhoneValue,null)
        val user = sharedPref!!.getString(Constant.sharedPrefUser,null)


        if (phone == null || user == null){
            context.startActivity(Intent(context,LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        return phone
    }

    fun saveContactInfo(){
        var listOfTrackers=""
        for ((key,value) in myTracker){

            if (listOfTrackers.isEmpty()){
                listOfTrackers = key + "%" + value
            }else{
                listOfTrackers += "%$key%$value"
            }
        }

        if (listOfTrackers.isEmpty()){
            listOfTrackers ="empty"
        }


        val editor=sharedPref!!.edit()
        editor.putString("listOfTrackers",listOfTrackers)
        editor.apply()
    }


    fun loadContactInfo(){
        myTracker.clear()
        val listOfTrackers =sharedPref!!.getString("listOfTrackers","empty")

        if (listOfTrackers != "empty"){
            val usersInfo=listOfTrackers.split("%").toTypedArray()
            var i=0
            while(i<usersInfo.size){

                myTracker.put(usersInfo[i],usersInfo[i+1])
                i += 2
            }
        }
    }

    companion object {
        val myTracker:MutableMap<String,String> = HashMap()

        fun formatPhoneNumber(phoneNumber:String):String {
            var onlyNumber= phoneNumber.replace("[^0-9]".toRegex(),"")
            if (phoneNumber[0] == '+') {
                onlyNumber ="+"+ phoneNumber
                if (phoneNumber.length == 11){
                    //only for bd
                    onlyNumber ="+88"+ phoneNumber
                }
            }

            return  onlyNumber
        }
    }

}