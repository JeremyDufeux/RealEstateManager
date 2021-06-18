package com.openclassrooms.realestatemanager.extensions

import java.text.DateFormat
import java.util.*

fun formatCalendarToString(date : Date) : String {
    return DateFormat.getDateInstance(DateFormat.LONG).format(date)
}