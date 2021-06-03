package com.openclassrooms.realestatemanager.services

import com.openclassrooms.realestatemanager.extensions.generateEstates
import com.openclassrooms.realestatemanager.models.Estate

class EstateApiServiceImpl : EstateApiService {
    private val estates : MutableList<Estate> = generateEstates().toMutableList()

    override fun getEstateList(): List<Estate> {
        return estates
    }

    override fun addEstate(estate: Estate) {
        estates.add(estate)
    }

    override fun updateEstate(estate: Estate) {
        val estateToUpdate = estates.find { it.id == estate.id }
        val estateIndex = estates.indexOf(estateToUpdate)
        estates[estateIndex] = estate
    }
}