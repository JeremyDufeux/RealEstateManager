package com.openclassrooms.realestatemanager.ui.add

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.mappers.propertyToPropertyUiAddView
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import com.openclassrooms.realestatemanager.models.ui.PropertyUiAddView
import com.openclassrooms.realestatemanager.modules.IoCoroutineScope
import com.openclassrooms.realestatemanager.repositories.PropertyUseCase
import com.openclassrooms.realestatemanager.services.GeocoderClient
import com.openclassrooms.realestatemanager.ui.details.BUNDLE_KEY_PROPERTY_ID
import com.openclassrooms.realestatemanager.utils.getGeoApifyUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    private var _propertyLiveData : MutableLiveData<PropertyUiAddView> = MutableLiveData()
    var propertyLiveData: LiveData<PropertyUiAddView> = _propertyLiveData

    private val _mediaListLiveData = MutableLiveData<List<MediaItem>>()
    val mediaListLiveData : LiveData<List<MediaItem>> = _mediaListLiveData

    private var propertyId = savedStateHandle.get<String>(BUNDLE_KEY_PROPERTY_ID)
    private var editMode = false

    var propertyType : PropertyType = PropertyType.FLAT
    private var mMediaList : MutableList<MediaItem> = mutableListOf()
    var price: Long? = null
    var surface: Int? = null
    var rooms: Int? = null
    var bathrooms: Int? = null
    var bedrooms: Int? = null
    var description = String()
    var addressLine1 = String()
    var addressLine2 = String()
    var city = String()
    var postalCode = String()
    var country = String()
    var agent = String()
    var postDate: Long? = null
    var soldDate: Long? = null
    var mPointOfInterestList : MutableList<PointOfInterest> = ArrayList()

    init {
        if (propertyId != null) {
            editMode = true
            viewModelScope.launch(Dispatchers.IO) {
                mPropertyUseCase.getPropertyWithIdFlow(propertyId!!).collect { property ->
                    _propertyLiveData.postValue(propertyToPropertyUiAddView(property))
                    postDate = property.postDate
                    mMediaList = property.mediaList.toMutableList()
                    _mediaListLiveData.postValue(mMediaList)
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
                latitude = latLng?.latitude,
                longitude = latLng?.longitude,
                mapPictureUrl = getGeoApifyUrl(latLng?.latitude, latLng?.longitude),
                pointOfInterestList = mPointOfInterestList,
                postDate = postDate ?: Calendar.getInstance().timeInMillis,
                soldDate = soldDate,
                agentName = agent
            )
            if(editMode){
                mPropertyUseCase.updateProperty(property)
            }else {
                mPropertyUseCase.addProperty(property)
            }
        }
    }

    fun addMedia(mediaItem: MediaItem) {
        viewModelScope.launch(Dispatchers.Default) {
            mediaItem.propertyId = propertyId!!
            mMediaList.add(mediaItem)
            mMediaList.sortBy { it.id }
            _mediaListLiveData.postValue(mMediaList)
        }
    }

    fun removeMediaAtPosition(position: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            mMediaList.removeAt(position)
            _mediaListLiveData.postValue(mMediaList)
        }
    }

    fun updateMedia(mediaItem: MediaItem) {
        viewModelScope.launch(Dispatchers.Default) {
            val index = mMediaList.indexOfFirst{ it.id == mediaItem.id }
            mMediaList[index] = mediaItem

            _mediaListLiveData.postValue(mMediaList)
        }
    }
}