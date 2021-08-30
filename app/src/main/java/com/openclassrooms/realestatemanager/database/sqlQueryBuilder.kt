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

    var addAnd = false

    if(propertyFilter.minPrice != 0L) {
        if(addAnd) query += "AND "
        addAnd = true

        query += "price >= '${propertyFilter.minPrice.toBigDecimal().toPlainString()}' "
    }

    if(propertyFilter.maxPrice != 0L){
        if(addAnd) query += "AND "
        addAnd = true

        query += "price <= '${propertyFilter.maxPrice.toBigDecimal().toPlainString()}' "
    }

    if (propertyFilter.minSurface != 0L){
        if(addAnd) query += "AND "
        addAnd = true

        query += "surface >= '${propertyFilter.minSurface.toBigDecimal().toPlainString()}' "
    }

    if(propertyFilter.maxSurface != 0L){
        if(addAnd) query += "AND "
        addAnd = true

        query += "surface <= '${propertyFilter.maxSurface.toBigDecimal().toPlainString()}' "
    }

    if(propertyFilter.propertyTypeList.isNotEmpty()) {
        if(addAnd) query += "AND "
        addAnd = true

        query += "("

        for (type in propertyFilter.propertyTypeList) {
            query += "type = '$type' OR "
        }
        query = query.dropLast(4)
        query += ") "
    }

    if (propertyFilter.pointOfInterestList.isNotEmpty()){
        for(poi in propertyFilter.pointOfInterestList) {
            if(addAnd) query += "AND "
            addAnd = true

            query += "(" +
                    "EXISTS (SELECT * FROM properties_point_of_interest_cross_ref as poi " +
                    "WHERE properties.propertyId = poi.propertyId AND " +
                    "description = '$poi')) "
        }
    }

    if(propertyFilter.city.isNotEmpty()){
        if(addAnd) query += "AND "
        addAnd = true

        query += "city LIKE '%${propertyFilter.city}%' "
    }

    if(propertyFilter.roomsAmount > 0){
        if(addAnd) query += "AND "
        addAnd = true

        query += "roomsAmount >= '${propertyFilter.roomsAmount}' "
    }

    if(propertyFilter.bedroomsAmount > 0){
        if(addAnd) query += "AND "
        addAnd = true

        query += "bedroomsAmount >= '${propertyFilter.bedroomsAmount}' "
    }

    if(propertyFilter.bathroomsAmount > 0){
        if(addAnd) query += "AND "
        addAnd = true

        query += "bathroomsAmount >= '${propertyFilter.bathroomsAmount}' "
    }

    if(propertyFilter.mediasAmount > 0){
        if(addAnd) query += "AND "
        addAnd = true

        query += "mediasAmount >= ${propertyFilter.mediasAmount} "
    }

    if(propertyFilter.available){
        if(addAnd) query += "AND "
        addAnd = true

        query += "soldDate IS NULL "
        query += "AND postDate >= '${propertyFilter.postDate}' "
    }

    if(propertyFilter.sold){
        if(addAnd) query += "AND "

        query += "soldDate IS NOT NULL "
        query += "AND soldDate >= '${propertyFilter.soldDate}' "
    }

    query += "ORDER BY properties.postDate"

    return query
}