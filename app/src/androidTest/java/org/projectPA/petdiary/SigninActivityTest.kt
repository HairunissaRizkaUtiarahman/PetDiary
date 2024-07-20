package org.projectPA.petdiary

import androidx.test.core.app.ActivityScenario
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
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.projectPA.petdiary.view.activities.DashboardActivity
import org.projectPA.petdiary.view.activities.auth.SigninActivity

@RunWith(AndroidJUnit4::class)
class SigninActivityTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(SigninActivity::class.java)

    private lateinit var auth: FirebaseAuth

    @Before
    fun setUp() {
        Intents.init()
        auth = FirebaseAuth.getInstance()
        auth.signOut()
        IdlingRegistry.getInstance().register(FirebaseAuthIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(FirebaseAuthIdlingResource.countingIdlingResource)
    }

    @Test
    fun testSigninWithValidCredentials() {
        val scenario = ActivityScenario.launch(SigninActivity::class.java)
        onView(withId(R.id.email_TIET)).perform(typeText("akupetdiary@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())

        onView(withId(R.id.signIn_Btn)).perform(click())

        Thread.sleep(5000)
        intended(hasComponent(DashboardActivity::class.java.name))

        scenario.close()
    }

    @Test
    fun testSigninWithUnverifiedEmailShowsError() {

        val scenario = ActivityScenario.launch(SigninActivity::class.java)

        onView(withId(R.id.email_TIET)).perform(typeText("lontong@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("abc123"), closeSoftKeyboard())

        onView(withId(R.id.signIn_Btn)).perform(click())

        onView(withSnackbarText("Please check your email address to verify before logging in.")).check(matches(isDisplayed()))
        scenario.close()

    }

    @Test
    fun testSigninWithInvalidCredentialsShowsError() {

        val scenario = ActivityScenario.launch(SigninActivity::class.java)

        onView(withId(R.id.email_TIET)).perform(typeText("invalidemaill@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("WrongPassword"), closeSoftKeyboard())

        onView(withId(R.id.signIn_Btn)).perform(click())

        onView(withSnackbarText("Authentication failed")).check(matches(isDisplayed()))
        scenario.close()
    }
}
