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
        onView(withId(R.layout.fragment_home)).check(matches(isDisplayed()))
    }

    @Test
    fun testSwitchToProfileFragment() {
        onView(withId(R.id.profile)).perform(click())

        onView(withId(R.layout.fragment_profile)).check(matches(isDisplayed()))
    }

    @Test
    fun testAddButtonOpensAddButtonFragment() {
        onView(withId(R.id.add_button)).perform(click())

        onView(withId(R.layout.fragment_add_button)).check(matches(isDisplayed()))
    }
}
