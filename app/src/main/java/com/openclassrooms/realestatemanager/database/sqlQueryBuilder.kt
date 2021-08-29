package com.openclassrooms.realestatemanager.database

import com.openclassrooms.realestatemanager.models.PropertyFilter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

fun constructSqlQuery(propertyFilter: PropertyFilter): String{

    val df = DecimalFormat("0", DecimalFormatSymbols(Locale.ENGLISH))
    df.maximumFractionDigits = 340

    var query = if(propertyFilter.mediasAmount > 0) {
        "SELECT *, COUNT(media_items.propertyId) as mediasAmount " +
        "FROM properties " +
        "LEFT JOIN media_items " +
        "ON (media_items.propertyId = properties.propertyId) " +
        "GROUP BY properties.propertyId " +
        "HAVING "
    } else {
        "SELECT * FROM properties " +
        "WHERE "
    }

    query += "price >= '${propertyFilter.selectedMinPrice.toBigDecimal().toPlainString()}' AND price <= '${propertyFilter.selectedMaxPrice.toBigDecimal().toPlainString()}' "

    query += "AND surface >= '${propertyFilter.selectedMinSurface.toBigDecimal().toPlainString()}' AND surface <= '${propertyFilter.selectedMaxSurface.toBigDecimal().toPlainString()}' "

    if(propertyFilter.propertyTypeList.isNotEmpty()) {
        query += "AND ("

        for (type in propertyFilter.propertyTypeList) {
            query += "type = '$type' OR "
        }
        query = query.dropLast(4)
        query += ") "
    }

    if (propertyFilter.pointOfInterestList.isNotEmpty()){
        for(poi in propertyFilter.pointOfInterestList) {
            query += "AND (" +
                    "EXISTS (SELECT * FROM properties_point_of_interest_cross_ref as poi " +
                    "WHERE properties.propertyId = poi.propertyId AND " +
                    "description = '$poi')) "
        }
    }

    if(propertyFilter.city.isNotEmpty()){
        query += "AND city LIKE '%${propertyFilter.city}%' "
    }

    if(propertyFilter.roomsAmount > 0){
        query += "AND roomsAmount >= '${propertyFilter.roomsAmount}' "
    }

    if(propertyFilter.bedroomsAmount > 0){
        query += "AND bedroomsAmount >= '${propertyFilter.bedroomsAmount}' "
    }

    if(propertyFilter.bathroomsAmount > 0){
        query += "AND bathroomsAmount >= '${propertyFilter.bathroomsAmount}' "
    }

    if(propertyFilter.mediasAmount > 0){
        query += "AND mediasAmount >= ${propertyFilter.mediasAmount} "
    }

    if(propertyFilter.available){
        query += "AND soldDate IS NULL "
        query += "AND postDate >= '${propertyFilter.postDate}' "
    }

    if(propertyFilter.sold){
        query += "AND soldDate IS NOT NULL "
        query += "AND soldDate >= '${propertyFilter.soldDate}' "
    }

    query += "ORDER BY properties.postDate"

    return query
}

/*
SELECT *, COUNT(media_items.propertyId) as mediaAmount
FROM properties
LEFT JOIN media_items
ON (media_items.propertyId = properties.propertyId)
GROUP BY properties.propertyId
HAVING mediaAmount >= 4
ORDER BY properties.propertyId
* */