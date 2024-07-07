package org.projectPA.petdiary

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.projectPA.petdiary.view.activities.auth.SigninActivity

@RunWith(AndroidJUnit4::class)
class DeleteReviewTest {

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

    private lateinit var auth: FirebaseAuth

    @Before
    fun setUp() {
        Intents.init()
        auth = FirebaseAuth.getInstance()
        auth.signOut()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun DeleteReviewProcces() {

        ActivityScenario.launch(SigninActivity::class.java)

        onView(withId(R.id.email_TIET)).perform(typeText("akupetdiary@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.signIn_Btn)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.bottom_navigation_view)).check(matches(isDisplayed()))

        onView(withId(R.id.profile)).check(matches(isDisplayed()))

        onView(withId(R.id.profile)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.myProfile_Btn)).perform(click())

        Thread.sleep(5000)
        onView(withId(R.id.myProfileTL)).perform(selectTabAtPosition(1))

        Thread.sleep(5000)

        onView(withId(R.id.deleteButton)).perform(click())

        onView(withText("Yes")).perform(click())

        onView(withSnackbarText("Review deleted successfully")).check(matches(isDisplayed()))

        Thread.sleep(2000)

        onView(withId(R.id.noReview_TV)).check(matches(isDisplayed()))

    }

    private fun selectTabAtPosition(position: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(TabLayout::class.java)
            }

            override fun getDescription(): String {
                return "with tab at index $position"
            }

            override fun perform(uiController: UiController?, view: View?) {
                val tabLayout = view as TabLayout
                val tab = tabLayout.getTabAt(position)
                tab?.select()
            }
        }
    }
}
