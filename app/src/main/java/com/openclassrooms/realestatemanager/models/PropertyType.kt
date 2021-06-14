package com.openclassrooms.realestatemanager.models

enum class PropertyType(val description : String) {
    FLAT("Flat"),
    HOUSE("House"),
    DUPLEX("Duplex"),
    TRIPLEX("Triplex"),
    PENTHOUSE("Penthouse"),
    MANOR("Manor"),
    LAND("Land"),
    RANCH("Ranch"),
    PRIVATE_ISLAND("Private Island");

    override fun toString(): String {
        return description
    }
}