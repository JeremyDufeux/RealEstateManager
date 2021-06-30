package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.widget.Toast

fun showToast(context: Context, messageResId: Int) {
    Toast.makeText(context, messageResId, Toast.LENGTH_LONG).show()
}