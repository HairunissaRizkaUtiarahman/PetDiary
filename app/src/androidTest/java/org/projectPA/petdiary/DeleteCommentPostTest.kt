package org.projectPA.petdiary

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.projectPA.petdiary.view.activities.auth.SigninActivity

@RunWith(AndroidJUnit4::class)
class DeleteCommentPostTest {

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
    fun DeleteCommentPostProcces() {

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

        onView(withId(R.id.main_content)).perform(swipeUpSlightly())

        onView(withId(R.id.comment_Btn)).perform(click())

        Thread.sleep(3000)

        onView(withId(R.id.main_content)).perform(swipeUpSlightly())

        onView(withId(R.id.deleteButton)).perform(click())

        onView(withText("Yes")).perform(click())

        onView(withSnackbarText("Comment deleted")).check(matches(isDisplayed()))

        Thread.sleep(2000)

        onView(withId(R.id.noComment_TV)).check(matches(isDisplayed()))

    }

    private fun swipeUpSlightly(): ViewAction {
        return actionWithAssertions(
            GeneralSwipeAction(
                Swipe.SLOW,
                GeneralLocation.BOTTOM_CENTER,
                GeneralLocation.translate(GeneralLocation.BOTTOM_CENTER, 0f, -0.55f),
                Press.FINGER
            )
        )
    }
}
