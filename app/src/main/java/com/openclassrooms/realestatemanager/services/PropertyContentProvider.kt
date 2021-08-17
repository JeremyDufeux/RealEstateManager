package com.openclassrooms.realestatemanager.services

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.openclassrooms.realestatemanager.database.PropertyProviderDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class PropertyContentProvider: ContentProvider() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PropertyContentProviderEntryPoint {
        fun getPropertyProviderDao(): PropertyProviderDao
    }

    private val mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    private val propertiesMatchCode = 100
    private val propertyIdMatchCode = 101
    private val propertyIdMediaMatchCode = 120
    private val propertyIdPointOfInterestMatchCode = 130
    private val mediasMatchCode = 200
    private val mediaIdMatchCode = 201
    private val pointOfInterestMatchCode = 300

    init {
        mUriMatcher.addURI(getContentAuthority(), getPropertiesPath(), propertiesMatchCode)
        mUriMatcher.addURI(getContentAuthority(), "${getPropertiesPath()}/*", propertyIdMatchCode)
        mUriMatcher.addURI(getContentAuthority(), "${getPropertiesPath()}/*/${getMediasPath()}", propertyIdMediaMatchCode)
        mUriMatcher.addURI(getContentAuthority(), "${getPropertiesPath()}/*/${getPointOfInterestPath()}", propertyIdPointOfInterestMatchCode)
        mUriMatcher.addURI(getContentAuthority(), getMediasPath(), mediasMatchCode)
        mUriMatcher.addURI(getContentAuthority(), "${getMediasPath()}/*", mediaIdMatchCode)
        mUriMatcher.addURI(getContentAuthority(), getPointOfInterestPath(), pointOfInterestMatchCode)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {

        val appContext = context?.applicationContext ?: throw IllegalStateException()
        val hiltEntryPoint = EntryPointAccessors.fromApplication(appContext, PropertyContentProviderEntryPoint::class.java)
        val propertyProviderDao = hiltEntryPoint.getPropertyProviderDao()

        return when(mUriMatcher.match(uri)){
            propertiesMatchCode -> {
                propertyProviderDao.getProperties()
            }
            propertyIdMatchCode -> {
                propertyProviderDao.getPropertyWithId(uri.lastPathSegment)
            }
            propertyIdMediaMatchCode -> {
                propertyProviderDao.getMediaForPropertyId(uri.pathSegments[1])
            }
            propertyIdPointOfInterestMatchCode -> {
                propertyProviderDao.getPointOfInterestForProperty(uri.pathSegments[1])
            }
            mediasMatchCode -> {
                propertyProviderDao.getMedias()
            }
            pointOfInterestMatchCode -> {
                propertyProviderDao.getPointsOfInterest()
            }
            else -> {
                throw java.lang.IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    override fun getType(uri: Uri): String {
        return when (mUriMatcher.match(uri)) {
            propertiesMatchCode -> "vnd.android.cursor.dir/${getContentAuthority()}.properties"
            propertyIdMatchCode -> "vnd.android.cursor.item/${getContentAuthority()}.properties"
            propertyIdMediaMatchCode -> "vnd.android.cursor.dir/${getContentAuthority()}.medias"
            propertyIdPointOfInterestMatchCode -> "vnd.android.cursor.dir/${getContentAuthority()}.pointofinterest"
            mediasMatchCode -> "vnd.android.cursor.dir/${getContentAuthority()}.medias"
            mediaIdMatchCode -> "vnd.android.cursor.item/${getContentAuthority()}.medias"
            pointOfInterestMatchCode -> "vnd.android.cursor.dir/${getContentAuthority()}.pointofinterest"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw java.lang.IllegalArgumentException("Unsupported operation")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw java.lang.IllegalArgumentException("Unsupported operation")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw java.lang.IllegalArgumentException("Unsupported operation")
    }
}

fun getContentAuthority(): String{
    return "com.openclassrooms.realestatemanager.propeties"
}

fun getPropertiesPath(): String{
    return "properties"
}

fun getMediasPath(): String{
    return "medias"
}

fun getPointOfInterestPath(): String{
    return "point_of_interest"
}