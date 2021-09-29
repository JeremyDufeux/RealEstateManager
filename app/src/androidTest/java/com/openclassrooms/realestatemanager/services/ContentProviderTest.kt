package com.openclassrooms.realestatemanager.services

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.models.enums.FileType
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.utils.generateOfflineProperties
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class xContentProviderTest {
    private val mPropertiesUri: Uri =
        Uri.parse("content://" + getContentAuthority() + "/" + getPropertiesPath())
    private val mMediasUri: Uri =
        Uri.parse("content://" + getContentAuthority() + "/" + getMediasPath())
    private val mPointsOfInterestUri: Uri =
        Uri.parse("content://" + getContentAuthority() + "/" + getPointOfInterestPath())

    private lateinit var mContentResolver: ContentResolver

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup(){
        mContentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    @Test
    fun queryPropertiesTestCountRows() {
        val cursor: Cursor? = mContentResolver.query(mPropertiesUri, null, null,null, null)
        assertThat(cursor).isNotNull()

        if (cursor != null) {
            assertThat(cursor.moveToFirst()).isTrue()

            var rows = 0
            do{
                rows++
            }while (cursor.moveToNext())

            assertThat(generateOfflineProperties()).hasSize(rows)

            cursor.close()
        }
    }

    @Test
    fun queryPropertiesTestAssertFirstRow() {
        val cursor: Cursor? = mContentResolver.query(mPropertiesUri, null, null,null, null)
        assertThat(cursor).isNotNull()

        if (cursor != null) {
            assertThat(cursor.moveToFirst()).isTrue()

            val id = cursor.getString(cursor.getColumnIndex("propertyId"))
            val type = cursor.getString(cursor.getColumnIndex("type"))
            val price = cursor.getDouble(cursor.getColumnIndex("price"))
            val surface = cursor.getDouble(cursor.getColumnIndex("surface"))
            val roomsAmount = cursor.getInt(cursor.getColumnIndex("roomsAmount"))
            val bathroomsAmount = cursor.getInt(cursor.getColumnIndex("bathroomsAmount"))
            val bedroomsAmount = cursor.getInt(cursor.getColumnIndex("bedroomsAmount"))
            val description = cursor.getString(cursor.getColumnIndex("description"))
            val addressLine1 = cursor.getString(cursor.getColumnIndex("addressLine1"))
            val addressLine2 = cursor.getString(cursor.getColumnIndex("addressLine2"))
            val city = cursor.getString(cursor.getColumnIndex("city"))
            val postalCode = cursor.getString(cursor.getColumnIndex("postalCode"))
            val country = cursor.getString(cursor.getColumnIndex("country"))
            val latitude = cursor.getDouble(cursor.getColumnIndex("latitude"))
            val longitude = cursor.getDouble(cursor.getColumnIndex("longitude"))
            val postDate = cursor.getLong(cursor.getColumnIndex("postDate"))
            val agentName = cursor.getString(cursor.getColumnIndex("agentName"))
            val mapPictureUrl = cursor.getString(cursor.getColumnIndex("mapPictureUrl"))

            var soldDate: Long? = null
            if(!cursor.isNull(cursor.getColumnIndex("soldDate"))){
                soldDate = cursor.getLong(cursor.getColumnIndex("soldDate"))
            }

            val property = generateOfflineProperties()[0]

            assertThat(id).isEqualTo(property.id)
            assertThat(type).isEqualTo(property.type.toString())
            assertThat(price).isEqualTo(property.price)
            assertThat(surface).isEqualTo(property.surface)
            assertThat(roomsAmount).isEqualTo(property.roomsAmount)
            assertThat(bathroomsAmount).isEqualTo(property.bathroomsAmount)
            assertThat(bedroomsAmount).isEqualTo(property.bedroomsAmount)
            assertThat(description).isEqualTo(property.description)
            assertThat(addressLine1).isEqualTo(property.addressLine1)
            assertThat(addressLine2).isEqualTo(property.addressLine2)
            assertThat(city).isEqualTo(property.city)
            assertThat(postalCode).isEqualTo(property.postalCode)
            assertThat(country).isEqualTo(property.country)
            assertThat(latitude).isEqualTo(property.latitude)
            assertThat(longitude).isEqualTo(property.longitude)
            assertThat(mapPictureUrl).isEqualTo(property.mapPictureUrl)
            assertThat(postDate).isEqualTo(property.postDate)
            assertThat(soldDate).isEqualTo(property.soldDate)
            assertThat(agentName).isEqualTo(property.agentName)

            cursor.close()
        }
    }

    @Test
    fun queryPropertyWithId2TestAssertData() {
        val propertyId = "3"
        val cursor: Cursor? = mContentResolver.query(Uri.withAppendedPath(mPropertiesUri, "/$propertyId"), null, null,null, null)
        assertThat(cursor).isNotNull()

        if (cursor != null) {
            assertThat(cursor.moveToFirst()).isTrue()

            val id = cursor.getString(cursor.getColumnIndex("propertyId"))
            val type = cursor.getString(cursor.getColumnIndex("type"))
            val price = cursor.getDouble(cursor.getColumnIndex("price"))
            val surface = cursor.getDouble(cursor.getColumnIndex("surface"))
            val roomsAmount = cursor.getInt(cursor.getColumnIndex("roomsAmount"))
            val bathroomsAmount = cursor.getInt(cursor.getColumnIndex("bathroomsAmount"))
            val bedroomsAmount = cursor.getInt(cursor.getColumnIndex("bedroomsAmount"))
            val description = cursor.getString(cursor.getColumnIndex("description"))
            val addressLine1 = cursor.getString(cursor.getColumnIndex("addressLine1"))
            val addressLine2 = cursor.getString(cursor.getColumnIndex("addressLine2"))
            val city = cursor.getString(cursor.getColumnIndex("city"))
            val postalCode = cursor.getString(cursor.getColumnIndex("postalCode"))
            val country = cursor.getString(cursor.getColumnIndex("country"))
            val latitude = cursor.getDouble(cursor.getColumnIndex("latitude"))
            val longitude = cursor.getDouble(cursor.getColumnIndex("longitude"))
            val postDate = cursor.getLong(cursor.getColumnIndex("postDate"))
            val agentName = cursor.getString(cursor.getColumnIndex("agentName"))
            val mapPictureUrl = cursor.getString(cursor.getColumnIndex("mapPictureUrl"))

            var soldDate: Long? = null
            if(!cursor.isNull(cursor.getColumnIndex("soldDate"))){
                soldDate = cursor.getLong(cursor.getColumnIndex("soldDate"))
            }

            val property = generateOfflineProperties().find { it.id == propertyId }

            assertThat(id).isEqualTo(property?.id)
            assertThat(type).isEqualTo(property?.type.toString())
            assertThat(price).isEqualTo(property?.price)
            assertThat(surface).isEqualTo(property?.surface)
            assertThat(roomsAmount).isEqualTo(property?.roomsAmount)
            assertThat(bathroomsAmount).isEqualTo(property?.bathroomsAmount)
            assertThat(bedroomsAmount).isEqualTo(property?.bedroomsAmount)
            assertThat(description).isEqualTo(property?.description)
            assertThat(addressLine1).isEqualTo(property?.addressLine1)
            assertThat(addressLine2).isEqualTo(property?.addressLine2)
            assertThat(city).isEqualTo(property?.city)
            assertThat(postalCode).isEqualTo(property?.postalCode)
            assertThat(country).isEqualTo(property?.country)
            assertThat(latitude).isEqualTo(property?.latitude)
            assertThat(longitude).isEqualTo(property?.longitude)
            assertThat(mapPictureUrl).isEqualTo(property?.mapPictureUrl)
            assertThat(postDate).isEqualTo(property?.postDate)
            assertThat(soldDate).isEqualTo(property?.soldDate)
            assertThat(agentName).isEqualTo(property?.agentName)

            cursor.close()
        }
    }

    @Test
    fun queryPropertyWithFakeIdTestAssertCursorEmpty() {
        val propertyId = "7b37a173-48ab-4869-b355-6eda251a09a7"
        val cursor: Cursor? = mContentResolver.query(Uri.withAppendedPath(mPropertiesUri, "/$propertyId"), null, null,null, null)
        assertThat(cursor).isNotNull()
        
        if (cursor != null) {
            assertThat(cursor.moveToFirst()).isFalse()
        }
    }

    @Test
    fun queryMediasTestCountRows() {
        val cursor: Cursor? = mContentResolver.query(mMediasUri, null, null,null, null)
        assertThat(cursor).isNotNull()

        if (cursor != null) {
            assertThat(cursor.moveToFirst()).isTrue()

            var rows = 0
            do{
                rows++
            }while (cursor.moveToNext())

            var expectedRows = 0
            for (property in generateOfflineProperties()){
                expectedRows += property.mediaList.size
            }

            assertThat(rows).isEqualTo(expectedRows)

            cursor.close()
        }
    }

    @Test
    fun queryMediasForPropertyIdTestCountRows() {
        val propertyId = "4"
        val cursor: Cursor? = mContentResolver.query(Uri.withAppendedPath(mPropertiesUri, "/$propertyId/${getMediasPath()}"), null, null,null, null)
        assertThat(cursor).isNotNull()

        if (cursor != null) {
            assertThat(cursor.moveToFirst()).isTrue()

            var rows = 0
            do{
                rows++
            }while (cursor.moveToNext())

            val expectedRows = generateOfflineProperties().find { it.id == propertyId }?.mediaList?.size

            assertThat(expectedRows).isEqualTo(rows)

            cursor.close()
        }
    }

    @Test
    fun queryMediasForPropertyIdTestCheckDataOfFirstOne() {
        val propertyId = "4"
        val cursor: Cursor? = mContentResolver.query(Uri.withAppendedPath(mPropertiesUri, "/$propertyId/${getMediasPath()}"), null, null,null, null)
        assertThat(cursor).isNotNull()

        if (cursor != null) {
            assertThat(cursor.moveToFirst()).isTrue()

            val mediaId = cursor.getString(cursor.getColumnIndex("mediaId"))
            val mediaProperty = cursor.getString(cursor.getColumnIndex("propertyId"))
            val url = cursor.getString(cursor.getColumnIndex("url"))
            val description = cursor.getString(cursor.getColumnIndex("description"))
            val fileType = cursor.getString(cursor.getColumnIndex("fileType"))

            val mediaList = generateOfflineProperties().find { it.id == propertyId }?.mediaList
            val media = mediaList?.find { it.id == mediaId }

            assertThat(mediaProperty).isEqualTo(propertyId)
            assertThat(mediaId).isEqualTo(media?.id)
            assertThat(url).isEqualTo(media?.url)
            assertThat(description).isEqualTo(media?.description)
            assertThat(FileType.valueOf(fileType)).isEqualTo(media?.fileType)

            cursor.close()
        }
    }

    @Test
    fun queryPointOfInterestIdTestCountRows() {
        val cursor: Cursor? = mContentResolver.query(mPointsOfInterestUri, null, null,null, null)
        assertThat(cursor).isNotNull()

        if (cursor != null) {
            assertThat(cursor.moveToFirst()).isTrue()

            var rows = 0
            do{
                rows++
            }while (cursor.moveToNext())

            val expectedRows = PointOfInterest.values().size

            assertThat(rows).isEqualTo(expectedRows)

            cursor.close()
        }
    }

    @Test
    fun queryPioForPropertyIdTestCheckData() {
        val propertyId = "6"
        val cursor: Cursor? = mContentResolver.query(Uri.withAppendedPath(mPropertiesUri, "/$propertyId/${getPointOfInterestPath()}"), null, null,null, null)
        assertThat(cursor).isNotNull()

        if (cursor != null) {
            assertThat(cursor.moveToFirst()).isTrue()

            val poiList = mutableListOf<PointOfInterest>()

            do {
                val poi = cursor.getString(cursor.getColumnIndex("description"))
                poiList.add(PointOfInterest.valueOf(poi))
            }while (cursor.moveToNext())

            assertThat(poiList).contains(PointOfInterest.SCHOOL)
            assertThat(poiList).contains(PointOfInterest.PUBLIC_TRANSPORT)
            assertThat(poiList).contains(PointOfInterest.FITNESS_CLUB)
            assertThat(poiList).contains(PointOfInterest.PARKING)
            assertThat(poiList).doesNotContain(PointOfInterest.GROCERY)
            assertThat(poiList).doesNotContain(PointOfInterest.PARK)
            assertThat(poiList).doesNotContain(PointOfInterest.SWIMMING_POOL)

            cursor.close()
        }
    }
}