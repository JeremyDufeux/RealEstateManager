package com.openclassrooms.realestatemanager.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.openclassrooms.realestatemanager.R

@SuppressLint("CustomViewStyleable")
class PropertyCardView(context: Context, attrs: AttributeSet): CardView(context, attrs) {

    private val soldTv: TextView
    private val propertyPicture: ImageView

    init {
        inflate(context, R.layout.property_cardview, this)

        soldTv = findViewById(R.id.property_cardview_sold_tv)
        propertyPicture = findViewById(R.id.property_cardview_property_picture_iv)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.propertyCardView)

        soldTv.visibility = if(attributes.getBoolean(R.styleable.propertyCardView_sold, false)) {
            View.VISIBLE
        } else {
            View.GONE
        }

        attributes.recycle()
    }

    fun setSold(sold: Boolean){
        soldTv.visibility = if(sold) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun setPropertyPicture(url: String){
        Glide.with(context)
            .load(url)
            .centerCrop()
            .timeout(2000)
            .error(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_building, null))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(propertyPicture)
    }
}