package com.openclassrooms.realestatemanager.models

data class MediaItem(
    var url: String,
    val description: String?,
    val fileType: FileType
)