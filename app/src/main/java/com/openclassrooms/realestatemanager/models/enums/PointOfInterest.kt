package com.openclassrooms.realestatemanager.models.enums

import com.openclassrooms.realestatemanager.R

enum class PointOfInterest(val description : Int,
                           val icon :Int) {
    SCHOOL(R.string.poi_school, R.drawable.ic_school),
    GROCERY(R.string.poi_grocery, R.drawable.ic_grocery),
    PARK(R.string.poi_park, R.drawable.ic_park),
    PUBLIC_TRANSPORT(R.string.poi_public_transport, R.drawable.ic_bus),
    SWIMMING_POOL(R.string.poi_swimming_pool, R.drawable.ic_pool),
    FITNESS_CLUB(R.string.poi_fitness_club, R.drawable.ic_fitness),
    PARKING(R.string.poi_parking, R.drawable.ic_parking)
}