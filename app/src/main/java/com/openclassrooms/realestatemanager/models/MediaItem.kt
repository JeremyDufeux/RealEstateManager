package com.openclassrooms.realestatemanager.models

import android.os.Parcelable
import com.openclassrooms.realestatemanager.models.enums.FileType
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaItem(
    var id: String = "",
    var propertyId: String = "",
    var url: String = "",
    var description: String = "",
    var fileType: FileType = FileType.PICTURE
) : Parcelable