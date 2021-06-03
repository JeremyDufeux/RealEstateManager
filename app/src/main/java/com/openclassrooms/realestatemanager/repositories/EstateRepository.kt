package com.openclassrooms.realestatemanager.repositories

import com.openclassrooms.realestatemanager.models.Estate
import com.openclassrooms.realestatemanager.services.EstateApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EstateRepository @Inject constructor(val estateApiService: EstateApiService){

    fun getEstateList(): List<Estate> {
        return estateApiService.getEstateList()
    }

    fun addEstate(estate: Estate) {
        estateApiService.addEstate(estate)
    }

    fun updateEstate(estate: Estate) {
        estateApiService.updateEstate(estate)
    }
}