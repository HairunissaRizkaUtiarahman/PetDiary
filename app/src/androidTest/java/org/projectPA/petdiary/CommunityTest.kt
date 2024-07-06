package org.projectPA.petdiary

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.projectPA.petdiary.view.activities.auth.SigninActivity
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class CommunityTest {

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
    fun test01PostEntryProcess() {

        ActivityScenario.launch(SigninActivity::class.java)


        onView(withId(R.id.email_TIET)).perform(
            typeText("akupetdiary@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.signIn_Btn)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.bottom_navigation_view)).check(matches(isDisplayed()))

        onView(withId(R.id.home)).perform(click())

        onView(withId(R.id.add_button)).perform(click())

        onView(withId(R.id.add_a_post_button)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.pick_Btn)).perform(click())
        onView(withText("Choose from Gallery")).perform(click())

        val imageUri = Uri.parse("android.resource:////org.projectPA.petdiary/drawable/test_image")
        val intent = Intent().setData(imageUri)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(
            Instrumentation.ActivityResult(
                RESULT_OK,
                intent
            )
        )

        onView(withId(R.id.desc_TIET)).perform(
            typeText("Testing isi postingan"), closeSoftKeyboard())

        onView(withId(R.id.post_Btn)).check(matches(isEnabled())).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.desc_TV)).check(matches(withText("Testing isi postingan")))
    }

    @Test
    fun test02CommentAndLikePost() {

        ActivityScenario.launch(SigninActivity::class.java)


        onView(withId(R.id.email_TIET)).perform(
            typeText("akupetdiary@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.signIn_Btn)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.community_button)).check(matches(isDisplayed()))

        onView(withId(R.id.community_button)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.comment_Btn)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.unlike_Btn)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.likeCount_TV)).check(matches(withText("1 Like")))


        onView(withId(R.id.comment_TIET)).perform(typeText("Test comment post"), closeSoftKeyboard())
        onView(withId(R.id.send_Btn)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.commentCount_TV)).check(matches(withText("1 Comment")))

        onView(withId(R.id.main_content)).perform(swipeUpSlightly())

        Thread.sleep(2000)

        onView(withId(R.id.comment_TV)).check(matches(withText(CoreMatchers.containsString("Test comment post"))))

    }

    @Test
    fun test03SearchPost() {

        ActivityScenario.launch(SigninActivity::class.java)


        onView(withId(R.id.email_TIET)).perform(
            typeText("akupetdiary@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.signIn_Btn)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.community_button)).check(matches(isDisplayed()))

        onView(withId(R.id.community_button)).perform(click())

        onView(withId(R.id.search)).perform(click())

        Thread.sleep(2000)

        onView(withId(androidx.appcompat.R.id.search_src_text))
            .perform(typeText("Testing isi postingan"), closeSoftKeyboard())

        Thread.sleep(5000)

        onView(withId(R.id.post_RV))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, scrollTo()))
            .check(matches(hasDescendant(withText("Testing isi postingan"))))
    }

    @Test
    fun test04SearchUser() {

        ActivityScenario.launch(SigninActivity::class.java)


        onView(withId(R.id.email_TIET)).perform(
            typeText("akupetdiary@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.signIn_Btn)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.community_button)).check(matches(isDisplayed()))

        onView(withId(R.id.community_button)).perform(click())

        onView(withId(R.id.search)).perform(click())

        Thread.sleep(2000)

        onView(withId(androidx.appcompat.R.id.search_src_text))
            .perform(typeText("kalica"), closeSoftKeyboard())

        Thread.sleep(5000)

        onView(withId(R.id.user_RV))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, scrollTo()))
            .check(matches(hasDescendant(withText("kalica"))))

        onView(withId(R.id.user_RV)).perform(click())

        onView(withId(R.id.name_Tv)).check(matches(withText(CoreMatchers.containsString("kalica"))))
    }
}
