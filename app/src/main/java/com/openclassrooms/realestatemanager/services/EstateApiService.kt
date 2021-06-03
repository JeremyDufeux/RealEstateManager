package com.openclassrooms.realestatemanager.services

import com.openclassrooms.realestatemanager.models.Estate

interface EstateApiService {
    fun getEstateList() : List<Estate>

    fun addEstate(estate: Estate)

    fun updateEstate(estate: Estate)
}