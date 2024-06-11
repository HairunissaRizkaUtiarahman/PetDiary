package org.projectPA.petdiary

import android.view.View
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
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.Matchers.closeTo
import org.projectPA.petdiary.view.activities.auth.SigninActivity

@RunWith(AndroidJUnit4::class)
class AddReviewCommentTest {

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

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
        onView(withId(R.id.email_TIET)).perform(
            typeText("akupetdiary@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.password_TIET)).perform(typeText("Aku123456"), closeSoftKeyboard())
        onView(withId(R.id.signIn_Btn)).perform(click())

        // Wait for the dashboard to load
        Thread.sleep(8000)

        onView(withId(R.id.list_most_review_product))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

        Thread.sleep(8000)

        // Verify navigation to ProductDetailActivity
        intended(hasComponent(ProductDetailActivity::class.java.name))

        // Add a sleep to ensure the content is fully loaded
        Thread.sleep(5000)

        // Scroll to the bottom to make the review list visible
        onView(withId(R.id.main_content)).perform(swipeUp())

        Thread.sleep(5000)
        // Scroll the main content multiple times to ensure it's visible
        onView(withId(R.id.main_content)).perform(swipeUp(), swipeUp())

        Thread.sleep(5000)

        // Scroll to the newly added review and click on it
        onView(withId(R.id.list_review)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        Thread.sleep(5000)

        // Navigate to DetailReviewActivity
        intended(hasComponent(DetailReviewActivity::class.java.name))

        Thread.sleep(5000)
        onView(withId(R.id.main_content)).perform(swipeUp())
        Thread.sleep(5000)
        onView(withId(R.id.main_content)).perform(swipeUp(), swipeUp())
        Thread.sleep(5000)

        onView(withId(R.id.view_all_comments_button)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.main_content)).perform(swipeUp(), swipeUp())

        Thread.sleep(5000)

        onView(withId(R.id.card_review)).perform(swipeUp(), swipeUp())

        Thread.sleep(5000)

        onView(withId(R.id.comment_TIET)).perform(typeText("testing fungsionalitas add comment purpose"), closeSoftKeyboard())
        onView(withId(R.id.send_Btn)).perform(click())
        onView(withId(R.id.comment_TIET)).perform(closeSoftKeyboard())

        // Ensure the keyboard is closed
        onView(isRoot()).perform(closeSoftKeyboard())

        onView(withId(R.id.main_content)).perform(swipeUp(), swipeUp())

        Thread.sleep(5000)

        onView(withId(R.id.card_review)).perform(swipeUp(), swipeUp())

        Thread.sleep(5000)

        // Verify the newly added comment is displayed in the list
        onView(withId(R.id.list_comment))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, scrollTo()))
            .check(matches(hasDescendant(withText("testing fungsionalitas add comment purpose"))))
//            .check(matches(withText(containsString("This is a great product. I highly recommend it!, this for testing add product"))))
    }
}
