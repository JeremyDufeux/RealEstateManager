package com.openclassrooms.realestatemanager.models.databaseEntites

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.realestatemanager.models.enums.FileType
import com.openclassrooms.realestatemanager.models.enums.ServerState
import kotlinx.parcelize.Parcelize

@Entity(tableName = "media_items")
@Parcelize
data class MediaItemEntity(
    @PrimaryKey
    val mediaId: String,
    val propertyId: String,
    var url: String,
    var description: String,
    val fileType: FileType,
    var serverState: ServerState
) : Parcelable