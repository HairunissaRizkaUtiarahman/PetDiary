package org.projectPA.petdiary

import android.view.View
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.projectPA.petdiary.view.activities.ChooseProductActivity
import org.projectPA.petdiary.view.activities.DetailReviewActivity
import org.projectPA.petdiary.view.activities.ProductDetailActivity
import org.projectPA.petdiary.view.activities.auth.SigninActivity

@RunWith(AndroidJUnit4::class)
class AddReviewTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

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
    fun testAddReviewFlowAndVerify() {

        ActivityScenario.launch(SigninActivity::class.java)

        onView(withId(R.id.email_TIET)).perform(typeText("akupetdiary@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.signIn_Btn)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.bottom_navigation_view)).check(matches(isDisplayed()))

        onView(withId(R.id.home)).perform(click())

        onView(withId(R.id.add_button)).perform(click())

        onView(withId(R.id.add_a_review_button)).perform(click())

        intended(hasComponent(ChooseProductActivity::class.java.name))
        onView(withId(R.id.list_product))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        onView(withId(R.id.ratingBar)).perform(setRating(4.0f))
        onView(withId(R.id.next_button_to_usage_product)).perform(click())

        onView(withId(R.id.usageDropdown)).perform(click())
        onView(withText("2 – 6 months")).perform(click())
        onView(withId(R.id.next_button_to_write_review)).perform(click())

        onView(withId(R.id.reviewEditText)).perform(typeText("This is a great product. I highly recommend it!, this for testing add product"), closeSoftKeyboard())
        onView(withId(R.id.next_button_to_recommend_product)).perform(click())

        onView(withId(R.id.ic_thumbs_up_inactive)).perform(click())
        onView(withId(R.id.submit_button)).perform(click())

        Thread.sleep(5000)

        intended(hasComponent(ProductDetailActivity::class.java.name))

        Thread.sleep(5000)

        onView(withId(R.id.main_content)).perform(swipeUp())

        Thread.sleep(5000)
        onView(withId(R.id.main_content)).perform(swipeUp(), swipeUp())

        Thread.sleep(5000)

        onView(withId(R.id.list_review)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        Thread.sleep(5000)

        intended(hasComponent(DetailReviewActivity::class.java.name))

        onView(withId(R.id.deskripsi_review)).check(matches(withText(containsString("This is a great product. I highly recommend it!, this for testing add product"))))
    }

    private fun setRating(rating: Float): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(RatingBar::class.java)
            }

            override fun getDescription(): String {
                return "Set rating on RatingBar"
            }

            override fun perform(uiController: UiController, view: View) {
                val ratingBar = view as RatingBar
                ratingBar.rating = rating
            }
        }
    }
}
