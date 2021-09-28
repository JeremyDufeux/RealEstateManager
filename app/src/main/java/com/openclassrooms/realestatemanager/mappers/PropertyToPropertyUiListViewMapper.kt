package com.openclassrooms.realestatemanager.mappers

import android.content.Context
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.ui.PropertyUiListView
import com.openclassrooms.realestatemanager.utils.Utils.convertDollarsToEuros
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PropertyToPropertyUiListViewMapper @Inject constructor(
    @ApplicationContext private val mContext: Context) {

    fun map(properties: List<Property>, currency: Currency): List<PropertyUiListView> {

        val propertiesUi = mutableListOf<PropertyUiListView>()

        for (property in properties.sortedByDescending{ it.postDate }) {
            val priceString = if (property.price != null) {
                if (currency == Currency.DOLLAR) {
                    String.format(
                        "%s%,d",
                        mContext.getString(Currency.DOLLAR.symbolResId),
                        property.price?.toInt()
                    )
                } else {
                    String.format(
                        "%,d%s",
                        convertDollarsToEuros(property.price!!).toInt(),
                        mContext.getString(Currency.EURO.symbolResId)
                    )
                }
            } else {
                ""
            }
            val sold = property.soldDate != null

            propertiesUi.add(
                PropertyUiListView(
                    id = property.id,
                    type = property.type,
                    price = property.price,
                    priceString = priceString,
                    city = property.city,
                    pictureUrl = property.mediaList[0].url,
                    sold = sold
                )
            )
        }
        return propertiesUi
    }
}