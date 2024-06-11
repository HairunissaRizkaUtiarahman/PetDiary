package org.projectPA.petdiary

import android.view.View
import android.widget.RatingBar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.Matchers.hasToString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.projectPA.petdiary.view.activities.ProductDetailActivity
import org.projectPA.petdiary.view.activities.DetailReviewActivity
import org.projectPA.petdiary.view.activities.DashboardActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseAuth
import org.projectPA.petdiary.view.activities.ChooseProductActivity
import org.projectPA.petdiary.view.activities.auth.SigninActivity

@RunWith(AndroidJUnit4::class)
class AddReviewTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

    private lateinit var auth: FirebaseAuth

    @Before
    fun setUp() {
        Intents.init() // Initialize Espresso Intents
        auth = FirebaseAuth.getInstance()
        auth.signOut()
    }

    @After
    fun tearDown() {
        Intents.release() // Release Espresso Intents
    }

    @Test
    fun testAddReviewFlowAndVerify() {

        ActivityScenario.launch(SigninActivity::class.java)

        // Sign in with email and password
        onView(withId(R.id.email_TIET)).perform(typeText("akupetdiary@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("Aku123456"), closeSoftKeyboard())
        onView(withId(R.id.signIn_Btn)).perform(click())

        // Wait for the dashboard to load
        Thread.sleep(2000) // Adjust the sleep time as needed
        // Verify that DashboardActivity is displayed
        onView(withId(R.id.bottom_navigation_view)).check(matches(isDisplayed()))

        // Navigate to HomeFragment
        onView(withId(R.id.home)).perform(click())

        // Click on the add button to open AddButtonFragment
        onView(withId(R.id.add_button)).perform(click())

        onView(withId(R.id.add_a_review_button)).perform(click())

        // Choose a product in ChooseProductActivity
        intended(hasComponent(ChooseProductActivity::class.java.name))
        onView(withId(R.id.list_product))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

        // GiveRatingFragment
        onView(withId(R.id.ratingBar)).perform(setRating(4.0f))
        onView(withId(R.id.next_button_to_usage_product)).perform(click())

        // UsageProductFragment
        onView(withId(R.id.usageDropdown)).perform(click())
        onData(allOf(instanceOf(String::class.java), hasToString("2 – 6 months"))).perform(click())
        onView(withId(R.id.next_button_to_write_review)).perform(click())

        // WriteReviewFragment
        onView(withId(R.id.reviewEditText)).perform(typeText("This is a great product. I highly recommend it!, this for testing add product"), closeSoftKeyboard())
        onView(withId(R.id.next_button_to_recommend_product)).perform(click())

        // RecommendProductFragment
        onView(withId(R.id.ic_thumbs_up_inactive)).perform(click())
        onView(withId(R.id.submit_button)).perform(click())

        // Verify navigation to ProductDetailActivity
        intended(hasComponent(ProductDetailActivity::class.java.name))
        onView(withId(R.id.list_review)).check(matches(isDisplayed()))

        // Scroll to the newly added review and click on it
        // Scroll to the newly added review and click on it
        onView(withId(R.id.list_review))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, scrollTo()))
            .check(matches(isDisplayed()))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))


        // Navigate to DetailReviewActivity
        intended(hasComponent(DetailReviewActivity::class.java.name))

        // Verify the review details in DetailReviewActivity
        onView(withId(R.id.deskripsi_review)).check(matches(withText(containsString("This is a great product. I highly recommend it!, this for testing add product"))))
    }

    private fun setRating(rating: Float): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(android.widget.RatingBar::class.java)
            }


            override fun getDescription(): String {
                return "Set rating on RatingBar"
            }


            override fun perform(uiController: UiController, view: View) {
                val ratingBar = view as android.widget.RatingBar
                ratingBar.rating = rating
            }
        }
    }

}
