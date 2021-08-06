package com.openclassrooms.realestatemanager.ui.add

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import com.openclassrooms.realestatemanager.modules.IoCoroutineScope
import com.openclassrooms.realestatemanager.repositories.PropertyUseCase
import com.openclassrooms.realestatemanager.services.GeocoderClient
import com.openclassrooms.realestatemanager.ui.details.BUNDLE_KEY_PROPERTY_ID
import com.openclassrooms.realestatemanager.utils.getGeoApifyUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class AddActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @IoCoroutineScope private val mIoScope: CoroutineScope,
    private val mPropertyUseCase: PropertyUseCase,
    private val mGeocoderClient: GeocoderClient
) : ViewModel(){

    private var _propertyLiveData : MutableLiveData<Property> = MutableLiveData()
    var propertyLiveData: LiveData<Property> = _propertyLiveData

    private val _mediaListLiveData = MutableLiveData<List<MediaItem>>()
    val mediaListLiveData : LiveData<List<MediaItem>> = _mediaListLiveData

    private var propertyId = savedStateHandle.get<String>(BUNDLE_KEY_PROPERTY_ID)
    private lateinit var oldProperty: Property
    private var editMode = false

    var propertyType : PropertyType = PropertyType.FLAT
    private var mMediaList : MutableList<MediaItem> = mutableListOf()
    var price: Long = 0
    var surface: Int = 0
    var rooms: Int = 0
    var bathrooms: Int = 0
    var bedrooms: Int = 0
    var description = String()
    var addressLine1 = String()
    var addressLine2 = String()
    var city = String()
    var postalCode = String()
    var country = String()
    private var latitude : Double = 0.0
    private var longitude : Double = 0.0
    var agent = String()
    var mPointOfInterestList : MutableList<PointOfInterest> = ArrayList()


    init {
        if (propertyId != null) {
            editMode = true
            viewModelScope.launch {
                mPropertyUseCase.getPropertyWithId(propertyId!!).collect { property ->
                    oldProperty = property
                    _propertyLiveData.postValue(property)
                    mMediaList = property.mediaList.toMutableList()
                    _mediaListLiveData.postValue(mMediaList)
                    this.cancel()
                }
            }
        } else {
            propertyId = UUID.randomUUID().toString()
        }
    }

    fun saveProperty() {
        mIoScope.launch {
            val latLng =
                mGeocoderClient.getPropertyLocation(addressLine1, addressLine2, city, postalCode, country)
            if (latLng != null) {
                latitude = latLng.latitude
                longitude = latLng.longitude
            }

            val property = Property(
                id = propertyId!!,
                type = propertyType,
                price = price,
                surface = surface,
                roomsAmount = rooms,
                bathroomsAmount = bathrooms,
                bedroomsAmount = bedrooms,
                description = description,
                mediaList = mMediaList,
                addressLine1 = addressLine1,
                addressLine2 = addressLine2,
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
            if(editMode){
                mPropertyUseCase.updateProperty(oldProperty, property)
            }else {
                mPropertyUseCase.addProperty(property)
            }
        }
    }

    fun addMediaUri(mediaItem: MediaItem) {
        viewModelScope.launch(Dispatchers.Default) {
            mediaItem.propertyId = propertyId!!
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