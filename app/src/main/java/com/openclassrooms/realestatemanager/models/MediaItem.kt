package com.openclassrooms.realestatemanager.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MediaItem(
    var url: String,
    val description: String?,
    val fileType: FileType
) : Parcelable