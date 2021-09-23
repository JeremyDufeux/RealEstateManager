package com.openclassrooms.realestatemanager.ui.list


import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.repositories.FILE_NAME
import com.openclassrooms.realestatemanager.ui.atPosition
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith




@LargeTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ChangeCurrencyTest{

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Rule(order = 1)
    @JvmField
    var activityTestRule: ActivityTestRule<ListActivity> = object : ActivityTestRule<ListActivity>(ListActivity::class.java) {
        override fun beforeActivityLaunched() {
            clearSharedPrefs()
            super.beforeActivityLaunched()
        }
    }

    @Rule(order = 2)
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION"
        )

    @Before
    @After
    fun resetSharedPrefs(){
        clearSharedPrefs()
    }

    private fun clearSharedPrefs() {
        val sharedPreferences: SharedPreferences = getInstrumentation().targetContext
            .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.commit()
    }

    @Test
    fun changeCurrencyTest() {
        val rv = onView(withId(R.id.fragment_list_rv))

        rv.check(matches(atPosition(0, hasDescendant(withText("$169,000")))))

        val actionMenuItemView = onView(
            allOf(
                withId(R.id.list_activity_settings_menu), withContentDescription("Settings"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.activity_list_toolbar),
                        1
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        actionMenuItemView.perform(click())

        val radioButton = onView(
            allOf(withText("Euro"),
                childAtPosition(
                    allOf(
                        withId(R.id.settings_bottom_currency_rg),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            5
                        )
                    ),
                    1
                ),
                isDisplayed()
            ))
        radioButton.perform(click())

        val appCompatImageButton = onView(
            allOf(
                withId(R.id.settings_bottom_save_ib), withContentDescription("Apply settings"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())

        rv.check(matches(atPosition(0, hasDescendant(withText("144,069€")))))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}