package com.swenson.android.task.data.helper

import android.content.Context
import android.content.SharedPreferences
import com.swenson.android.task.data.response.RepoResponseItem
import com.swenson.android.task.network.Constants.PREFERENCE_NAME
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ComplexPreferencesImpl(context: Context) {
    var sharedPreferences: SharedPreferences
    private val gson: Gson = Gson()
    private val editor: SharedPreferences.Editor

    init {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun saveArrayList(list: java.util.ArrayList<RepoResponseItem>, key: String) {
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    fun getArrayList(key: String): java.util.ArrayList<RepoResponseItem>? {
        val json: String? = sharedPreferences.getString(key, null)
        val type = object : TypeToken<java.util.ArrayList<RepoResponseItem>>() {}.type
        return gson.fromJson(json, type)
    }
}