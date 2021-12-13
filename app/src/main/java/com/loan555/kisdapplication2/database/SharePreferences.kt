package com.loan555.kisdapplication2.database

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object AppPreferences {
    private const val NAME = "SpinKotlin"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences
    private val TOKEN = Pair("token", "")
    private val ID = Pair("_idNguoiDung", "")
    private val PASSWORD = Pair("password", "")
    private val USER_NAME = Pair("name", "")
    private val ID_LOAINGUOIDUNG = Pair("_id", "")

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    var password: String?
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getString(PASSWORD.first, PASSWORD.second)
        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            this.putString(PASSWORD.first, value)
        }
    var userName: String?
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getString(USER_NAME.first, USER_NAME.second)
        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            this.putString(USER_NAME.first, value)
        }

    var idLoaiNguoiDung: String?
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getString(ID_LOAINGUOIDUNG.first, ID_LOAINGUOIDUNG.second)
        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            this.putString(ID_LOAINGUOIDUNG.first, value)
        }
    var token: String?
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getString(TOKEN.first, TOKEN.second)
        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            this.putString(TOKEN.first, value)
        }
    var idNguoiDung: String?
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getString(ID.first, ID.second)
        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            this.putString(ID.first, value)
        }
}