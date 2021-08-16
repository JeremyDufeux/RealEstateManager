package com.openclassrooms.realestatemanager.ui.add

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.mappers.propertyToPropertyUiAddView
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.UserData
import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import com.openclassrooms.realestatemanager.models.enums.Unit
import com.openclassrooms.realestatemanager.models.ui.PropertyUiAddView
import com.openclassrooms.realestatemanager.modules.IoCoroutineScope
import com.openclassrooms.realestatemanager.repositories.PropertyUseCase
import com.openclassrooms.realestatemanager.repositories.UserDataRepository
import com.openclassrooms.realestatemanager.services.GeocoderClient
import com.openclassrooms.realestatemanager.utils.Utils.convertEurosToDollars
import com.openclassrooms.realestatemanager.utils.Utils.convertSquareMetersToSquareFoot
import com.openclassrooms.realestatemanager.utils.getGeoApifyUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

const val BUNDLE_KEY_ADD_ACTIVITY_PROPERTY_ID = "BUNDLE_KEY_ADD_ACTIVITY_PROPERTY_ID"

@HiltViewModel
class AddActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @IoCoroutineScope private val mIoScope: CoroutineScope,
    private val mPropertyUseCase: PropertyUseCase,
    private val mGeocoderClient: GeocoderClient,
    private val mUserDataRepository: UserDataRepository
) : ViewModel(){

    private var _propertyLiveData : MutableLiveData<PropertyUiAddView> = MutableLiveData()
    var propertyLiveData: LiveData<PropertyUiAddView> = _propertyLiveData

    private val _mediaListLiveData = MutableLiveData<List<MediaItem>>()
    val mediaListLiveData : LiveData<List<MediaItem>> = _mediaListLiveData

    var userDataLiveData: LiveData<UserData> = mUserDataRepository.userDataFlow.asLiveData(Dispatchers.IO)

    private var propertyId = savedStateHandle.get<String>(BUNDLE_KEY_ADD_ACTIVITY_PROPERTY_ID)
    private var editMode = false

    var propertyType : PropertyType = PropertyType.FLAT
    private var mMediaList : MutableList<MediaItem> = mutableListOf()
    var price: Double? = null
    var surface: Double? = null
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
                combine(mPropertyUseCase.getPropertyWithIdFlow(propertyId!!), mUserDataRepository.userDataFlow){ property, userData ->
                        propertyToPropertyUiAddView(property, userData)
                    }
                    .collect { property ->
                        propertyType = property.type
                        postDate = property.postDate
                        soldDate = property.soldDate
                        mMediaList = property.mediaList.toMutableList()
                        _mediaListLiveData.postValue(mMediaList)
                        _propertyLiveData.postValue(property)
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

    fun setPrice(newPrice: String) {
        price = if (newPrice != _propertyLiveData.value?.priceString){
            if(userDataLiveData.value?.currency == Currency.EURO) {
                convertEurosToDollars(newPrice.toDouble())
            } else {
                newPrice.toDouble()
            }
        } else {
            _propertyLiveData.value?.price
        }
    }

    fun setSurface(newSurface: String) {
        surface = if (newSurface != _propertyLiveData.value?.surfaceString){
            if(userDataLiveData.value?.unit == Unit.METRIC) {
                convertSquareMetersToSquareFoot(newSurface.toDouble())
            } else {
                newSurface.toDouble()
            }
        } else {
            _propertyLiveData.value?.surface
        }
    }
}