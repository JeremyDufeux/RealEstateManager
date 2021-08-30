package com.openclassrooms.realestatemanager.utils

import android.content.Context
import com.openclassrooms.realestatemanager.R
import java.lang.reflect.Field

fun getStringResourceId(context: Context, stringToSearch: String): Int {
    val fields: Array<Field> = R.string::class.java.fields
    for (field in fields) {
        val id = field.getInt(field)
        val str = context.getString(id)
        if (str == stringToSearch) {
            return id
        }
    }
    return -1
}