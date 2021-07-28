package com.openclassrooms.realestatemanager.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaItem(
    val id: String,
    var url: String,
    var description: String?,
    val fileType: FileType
) : Parcelable