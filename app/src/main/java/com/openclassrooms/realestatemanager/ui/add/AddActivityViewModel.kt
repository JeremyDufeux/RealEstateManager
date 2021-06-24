package com.openclassrooms.realestatemanager.ui.add

import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.extensions.getGeoApifyUrl
import com.openclassrooms.realestatemanager.models.PointsOfInterest
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.PropertyType
import com.openclassrooms.realestatemanager.repositories.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


private const val TAG = "AddActivityViewModel"

@HiltViewModel
class AddActivityViewModel @Inject constructor(private val mPropertyRepository: PropertyRepository) : ViewModel(){

    var propertyType : PropertyType = PropertyType.FLAT
    var mediaList : HashMap<String, String?> = hashMapOf()
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
    
    fun saveProperty(){
        if(address2.isNotEmpty()) address1 += "\n$address2"

        val property = Property(
                id = UUID.randomUUID().toString(),
                type = propertyType,
                price = price,
                surface = "$surface sq ft",
                roomsAmount = rooms,
                bathroomsAmount = bathrooms,
                bedroomsAmount = bedrooms,
                description = description,
                mediaUriList = mediaList, // Todo
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
        mPropertyRepository.addProperty(property)
    }
}