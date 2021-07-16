package com.openclassrooms.realestatemanager.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.models.*
import com.openclassrooms.realestatemanager.repositories.PropertyRepository
import com.openclassrooms.realestatemanager.utils.GeocoderClient
import com.openclassrooms.realestatemanager.utils.getGeoApifyUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

@HiltViewModel
class AddActivityViewModel @Inject constructor(
    private val mPropertyRepository: PropertyRepository,
    private val mGeocoderClient: GeocoderClient
) : ViewModel(){

    var propertyType : PropertyType = PropertyType.FLAT
    var mediaList : MutableList<MediaItem> = mutableListOf()
    var price by Delegates.notNull<Long>()
    var surface by Delegates.notNull<Int>()
    var rooms by Delegates.notNull<Int>()
    var bathrooms by Delegates.notNull<Int>()
    var bedrooms by Delegates.notNull<Int>()
    var description = String()
    var address1 = String()
    var address2 = String()
    var city = String()
    var postalCode = String()
    var country = String()
    var latitude : Double = 0.0
    var longitude : Double = 0.0
    var agent = String()
    var pointOfInterestList : MutableList<PointsOfInterest> = ArrayList()

    private val _mediaListLiveData = MutableLiveData<MutableList<MediaItem>>()
    val mediaListLiveData : LiveData<MutableList<MediaItem>> = _mediaListLiveData
    
    suspend fun saveProperty() = withContext(Dispatchers.IO) {
        val latLng =
            mGeocoderClient.getPropertyLocation(address1, address2, city, postalCode, country)
        if (latLng != null) {
            latitude = latLng.latitude
            longitude = latLng.longitude
        }

        if (address2.isNotEmpty()) address1 += "\n$address2"

        val property = Property(
            id = UUID.randomUUID().toString(),
            type = propertyType,
            price = price,
            surface = "$surface sq ft",
            roomsAmount = rooms,
            bathroomsAmount = bathrooms,
            bedroomsAmount = bedrooms,
            description = description,
            mediaList = mediaList,
            address = address1,
            city = city,
            postalCode = postalCode,
            country = country,
            latitude = latitude,
            longitude = longitude,
            mapPictureUrl = getGeoApifyUrl(latitude, longitude),
            pointOfInterest = pointOfInterestList,
            available = true,
            saleDate = Calendar.getInstance().timeInMillis,
            dateOfSale = null,
            agentName = agent
        )
        mPropertyRepository.addPropertyAndFetch(property)
    }

    fun addMediaUri(uri: String, description: String?, fileType: FileType) {
        viewModelScope.launch(Dispatchers.IO) {

            mediaList.add(MediaItem(uri, description, fileType))

            _mediaListLiveData.postValue(mediaList)
        }
    }
}