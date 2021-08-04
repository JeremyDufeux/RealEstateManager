package com.openclassrooms.realestatemanager.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import com.openclassrooms.realestatemanager.modules.IoCoroutineScope
import com.openclassrooms.realestatemanager.repositories.PropertyRepository
import com.openclassrooms.realestatemanager.services.GeocoderClient
import com.openclassrooms.realestatemanager.utils.getGeoApifyUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class AddActivityViewModel @Inject constructor(
    @IoCoroutineScope private val mIoScope: CoroutineScope,
    private val mPropertyRepository: PropertyRepository,
    private val mGeocoderClient: GeocoderClient
) : ViewModel(){

    var propertyType : PropertyType = PropertyType.FLAT
    private var mMediaList : MutableList<MediaItem> = mutableListOf()
    var price: Long = 0
    var surface: Int = 0
    var rooms: Int = 0
    var bathrooms: Int = 0
    var bedrooms: Int = 0
    var description = String()
    var address1 = String()
    var address2 = String()
    var city = String()
    var postalCode = String()
    var country = String()
    private var latitude : Double = 0.0
    private var longitude : Double = 0.0
    var agent = String()
    var mPointOfInterestList : MutableList<PointOfInterest> = ArrayList()

    private val _mediaListLiveData = MutableLiveData<MutableList<MediaItem>>()
    val mediaListLiveData : LiveData<MutableList<MediaItem>> = _mediaListLiveData
    
    fun saveProperty() {
        mIoScope.launch {
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
                mediaList = mMediaList,
                address = address1,
                city = city,
                postalCode = postalCode,
                country = country,
                latitude = latitude,
                longitude = longitude,
                mapPictureUrl = getGeoApifyUrl(latitude, longitude),
                pointOfInterestList = mPointOfInterestList,
                available = true,
                postDate = Calendar.getInstance().timeInMillis,
                soldDate = null,
                agentName = agent
            )
            mPropertyRepository.addPropertyAndFetch(property)
        }
    }

    fun addMediaUri(mediaItem: MediaItem) {
        viewModelScope.launch(Dispatchers.Default) {
            mMediaList.add(mediaItem)
            _mediaListLiveData.postValue(mMediaList)
        }
    }

    fun removeMediaAtPosition(position: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            mMediaList.removeAt(position)
            _mediaListLiveData.postValue(mMediaList)
        }
    }
}