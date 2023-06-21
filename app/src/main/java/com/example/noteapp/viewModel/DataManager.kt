package com.example.noteapp.viewModel

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class DataManager(context: Context) {
    private var appSharedPrefs: SharedPreferences? = context.getSharedPreferences("noteapp_prefs",MODE_PRIVATE)
    private var prefsEditor: SharedPreferences.Editor? = appSharedPrefs!!.edit()

    //get value
    fun getValue_int(intKeyValue: String?): Int {
        return appSharedPrefs!!.getInt(intKeyValue, 0)
    }

    fun getValue_string(stringKeyValue: String?): String? {
        return appSharedPrefs!!.getString(stringKeyValue, "")
    }

    fun getValue_stringSet(stringKeyValue: String?):Set<String>?{
        return appSharedPrefs!!.getStringSet(stringKeyValue,  mutableSetOf<String>())
    }

    fun getValue_boolean(stringKeyValue: String?): Boolean? {
        return appSharedPrefs!!.getBoolean(stringKeyValue, false)
    }

    //setvalue

    //setvalue
    fun setValue_int(intKeyValue: String?, _intValue: Int) {
        prefsEditor!!.putInt(intKeyValue, _intValue).commit()
    }

    fun setValue_string(stringKeyValue: String?, _stringValue: String?) {
        prefsEditor!!.putString(stringKeyValue, _stringValue).commit()
    }

    fun setValue_stringSet(stringKeyValue: String?, _stringSetValue: Set<String>?){
        prefsEditor!!.putStringSet(stringKeyValue,_stringSetValue).commit()
    }

    fun setValue_boolean(stringKeyValue: String?, _bool: Boolean?) {
        prefsEditor!!.putBoolean(stringKeyValue, _bool!!).commit()
    }

    fun setValue_int(intKeyValue: String?) {
        prefsEditor!!.putInt(intKeyValue, 0).commit()
    }

    fun clearData() {
        prefsEditor!!.clear().commit()
    }
}