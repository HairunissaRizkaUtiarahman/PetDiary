package org.projectPA.petdiary

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.projectPA.petdiary.view.activities.DashboardActivity

@RunWith(AndroidJUnit4::class)
class DashboardActivityTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(DashboardActivity::class.java)

    @Test
    fun testDefaultFragmentIsHomeFragment() {
        // Check if the HomeFragment is displayed by default
        onView(withId(R.layout.fragment_home)).check(matches(isDisplayed()))
    }

    @Test
    fun testSwitchToProfileFragment() {
        // Perform click action on the profile item in the bottom navigation view
        onView(withId(R.id.profile)).perform(click())

        // Check if the ProfileFragment is displayed
        onView(withId(R.layout.fragment_profile)).check(matches(isDisplayed()))
    }

    @Test
    fun testAddButtonOpensAddButtonFragment() {
        // Perform click action on the add button
        onView(withId(R.id.add_button)).perform(click())

        // Check if the AddButtonFragment is displayed
        onView(withId(R.layout.fragment_add_button)).check(matches(isDisplayed()))
    }
}
