package com.openclassrooms.realestatemanager.models.enums

import com.openclassrooms.realestatemanager.R

enum class PropertyType(val description: Int) {
    FLAT(R.string.pt_flat),
    HOUSE(R.string.pt_house),
    DUPLEX(R.string.pt_duplex),
    TRIPLEX(R.string.pt_triplex),
    PENTHOUSE(R.string.pt_penthouse),
    MANOR(R.string.pt_manor),
    LAND(R.string.pt_land),
    RANCH(R.string.pt_ranch),
    PRIVATE_ISLAND(R.string.pt_private_island);
}