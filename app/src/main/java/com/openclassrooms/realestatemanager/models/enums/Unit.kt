package com.openclassrooms.realestatemanager.models.enums

import com.openclassrooms.realestatemanager.R

enum class Unit(val unitNameResId: Int, val abbreviationRsId: Int) {
    IMPERIAL(R.string.imperial, R.string.sq_ft),
    METRIC(R.string.metric, R.string.m2);
}