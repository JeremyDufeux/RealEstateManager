package com.openclassrooms.realestatemanager.ui.add

import android.util.Log
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.extensions.geoApifyUrl
import com.openclassrooms.realestatemanager.models.PointsOfInterest
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.PropertyType
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

private const val TAG = "AddActivityViewModel"

@HiltViewModel
class AddActivityViewModel @Inject constructor() : ViewModel(){

    var propertyType : PropertyType = PropertyType.FLAT
    var mediaList : List<Pair<String, String>> = emptyList()
    var price by Delegates.notNull<Int>()
    var surface by Delegates.notNull<Int>()
    var room by Delegates.notNull<Int>()
    var bathroom by Delegates.notNull<Int>()
    var bedroom by Delegates.notNull<Int>()
    var description = String()
    var address1 = String()
    var address2 = String()
    var city = String()
    var postalCode = String()
    var country = String()
    var agent = String()
    var pointOfInterestList : MutableList<PointsOfInterest> = ArrayList()
    
    fun saveProperty(){
        if(address2.isNotEmpty()){
            address1 += "\n$address2"
        }
        val surfaceString = "$surface sq ft"

        val property = Property(
                UUID.randomUUID().toString(),
                propertyType,
                price,
                surfaceString,
                room,
                bathroom,
                bedroom,
                description,
                mediaList, // Todo
                address1,
                city,
                postalCode,
                country,
                0.0,    // Todo
                0.0,    // Todo
                geoApifyUrl,    // Todo
                pointOfInterestList,
                true,
                Calendar.getInstance(),
                null,
                agent
                )
        Log.d(TAG, "saveProperty: $property")
    }
}