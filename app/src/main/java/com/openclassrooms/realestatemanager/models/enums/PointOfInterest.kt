package com.openclassrooms.realestatemanager.models.enums

import com.openclassrooms.realestatemanager.R

enum class PointOfInterest(val description : String,
                           val icon :Int) {
    SCHOOL("School", R.drawable.ic_school),
    GROCERY("Grocery", R.drawable.ic_grocery),
    PARK("Park", R.drawable.ic_park),
    PUBLIC_TRANSPORT("Public transport", R.drawable.ic_bus),
    SWIMMING_POOL("Swimming pool", R.drawable.ic_pool),
    FITNESS_CLUB("Fitness club", R.drawable.ic_fitness)
}