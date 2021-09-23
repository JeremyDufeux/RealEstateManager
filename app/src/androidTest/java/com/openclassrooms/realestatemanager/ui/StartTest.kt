package com.openclassrooms.realestatemanager.ui


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.ui.list.ListActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class StartTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Rule(order = 1)
    @JvmField
    var mActivityTestRule = ActivityTestRule(ListActivity::class.java)

    @Rule(order = 2)
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION"
        )

    @Test
    fun startTest() {

        val textView = onView(
            allOf(
                withId(R.id.list_activity_filter_menu), withContentDescription("Filter"),
                withParent(withParent(withId(R.id.activity_list_toolbar))),
                isDisplayed()
            )
        )
        textView.check(matches(isDisplayed()))
    }
}
