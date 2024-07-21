package org.projectPA.petdiary


import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.projectPA.petdiary.view.activities.DetailReviewActivity
import org.projectPA.petdiary.view.activities.MoreReviewsActivity
import org.projectPA.petdiary.view.activities.ProductCategoriesPageActivity
import org.projectPA.petdiary.view.activities.ProductDetailActivity
import org.projectPA.petdiary.view.activities.ProductPageActivity
import org.projectPA.petdiary.view.activities.ReviewHomePageActivity
import org.projectPA.petdiary.view.activities.auth.SigninActivity

@RunWith(AndroidJUnit4::class)
class ReviewFlowTest {

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

        Thread.sleep(5000)

        onView(withId(R.id.list_most_review_product)).check(matches(isDisplayed()))

        onView(withId(R.id.review_button)).check(matches(isDisplayed()))

        onView(withId(R.id.review_button)).perform(click())

        Thread.sleep(5000)

        intended(hasComponent(ReviewHomePageActivity::class.java.name))

        Thread.sleep(5000)

        onView(withId(R.id.cat_button)).check(matches(isDisplayed()))

        onView(withId(R.id.cat_button)).perform(click())

        Thread.sleep(5000)

        intended(hasComponent(ProductCategoriesPageActivity::class.java.name))

        onView(withId(R.id.button_food_category)).perform(click())

        Thread.sleep(9000)

        intended(hasComponent(ProductPageActivity::class.java.name))

        Thread.sleep(10000)

        onView(withId(R.id.search_product)).perform(click())

        Thread.sleep(2000)

        onView(withId(androidx.appcompat.R.id.search_src_text))
            .perform(typeText("name product testing"), closeSoftKeyboard())

        Thread.sleep(5000)

        onView(withId(R.id.list_product))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        Thread.sleep(10000)

        intended(hasComponent(ProductDetailActivity::class.java.name))


        onView(withId(R.id.main_content)).check(matches(isDisplayed()))

        Thread.sleep(8000)

        onView(withId(R.id.main_content)).perform(swipeUpSlightly())

        Thread.sleep(7000)
        onView(withId(R.id.see_more_review_link)).perform(click())

        intended(hasComponent(MoreReviewsActivity::class.java.name))

        Thread.sleep(5000)

        onView(withId(R.id.button_sort_reviews)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.Newest_Review_option_sort)).check(matches(isDisplayed()))

        Thread.sleep(5000)

        onView(withId(R.id.Newest_Review_option_sort)).perform(click())

        onView(withId(R.id.list_review)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        Thread.sleep(5000)

        intended(hasComponent(DetailReviewActivity::class.java.name))

        Thread.sleep(5000)
    }
}