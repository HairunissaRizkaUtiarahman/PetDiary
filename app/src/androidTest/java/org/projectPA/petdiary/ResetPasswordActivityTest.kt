package org.projectPA.petdiary

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.projectPA.petdiary.view.activities.auth.ResetPasswordActivity
import org.projectPA.petdiary.view.activities.auth.SigninActivity

@RunWith(AndroidJUnit4::class)
class ResetPasswordActivityTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(ResetPasswordActivity::class.java)

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val snackbarIdlingResource = SnackbarIdlingResource()


    @Before
    fun setUp() {
        Intents.init()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        auth.signOut()
        IdlingRegistry.getInstance().register(FirebaseAuthIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(snackbarIdlingResource)
        IdlingRegistry.getInstance().unregister(FirebaseAuthIdlingResource.countingIdlingResource)
    }

    @Test
    fun testEmptyEmailShowsError() {
        onView(withId(R.id.reset_Btn)).perform(click())

        onView(withId(R.id.email_TIET))
            .check(matches(hasErrorText(containsString("Email cannot be empty"))))
    }

    @Test
    fun testInvalidEmailShowsError() {
        onView(withId(R.id.email_TIET)).perform(typeText("invalid_email"), closeSoftKeyboard())
        onView(withId(R.id.reset_Btn)).perform(click())

        onView(withId(R.id.email_TIET))
            .check(matches(hasErrorText(containsString("Email not Valid"))))
    }

    @Test
    fun testNonExistingEmailShowsSnackbar() {

        onView(withId(R.id.email_TIET)).perform(typeText("noneyxistingemail@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.reset_Btn)).perform(click())

        onView(withSnackbarText("Email not found")).check(matches(isDisplayed()))

    }

    @Test
    fun testValidEmailOpenSignin() {
        onView(withId(R.id.email_TIET)).perform(typeText("akupetdiary@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.reset_Btn)).perform(click())

        Thread.sleep(5000)
        intended(hasComponent(SigninActivity::class.java.name))
    }
}

