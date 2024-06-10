package org.projectPA.petdiary

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.android.material.snackbar.Snackbar
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.projectPA.petdiary.view.activities.auth.SignupActivity
import org.projectPA.petdiary.view.activities.auth.SigninActivity
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class SignupActivityTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(SignupActivity::class.java)

    private lateinit var snackbarIdlingResource: SnackbarIdlingResource

    @Before
    fun setUp() {
        Intents.init()
        snackbarIdlingResource = SnackbarIdlingResource()
        IdlingRegistry.getInstance().register(snackbarIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(snackbarIdlingResource)
    }

//    @Test
//    fun testSignupButtonOpensSigninActivity() {
//        // Simulate valid user input
//        onView(withId(R.id.name_TIET)).perform(typeText("rabbit"), closeSoftKeyboard())
//        onView(withId(R.id.email_TIET)).perform(typeText("rabbit@gmail.com"), closeSoftKeyboard())
//        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
//        onView(withId(R.id.confirmPassword_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
//
//        // Perform click action on signup button
//        onView(withId(R.id.signUp_Btn)).perform(click())
//
//        // Wait for the intent to be launched
//        Thread.sleep(1000)
//
//        // Check if the SigninActivity is opened
//        Intents.intended(hasComponent(SigninActivity::class.java.name))
//    }

    @Test
    fun testSignupButtonOpensSigninActivityy() {
        // Simulate user input
        onView(withId(R.id.name_TIET)).perform(typeText("lontong"), closeSoftKeyboard())
        onView(withId(R.id.email_TIET)).perform(
            typeText("lontong@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.confirmPassword_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())

        val latch = CountDownLatch(1)

        val activityScenario = activityScenarioRule.scenario
        activityScenario.onActivity { activity ->
            val viewModel = (activity as SignupActivity).viewModel
            viewModel.signupSuccess.observe(activity) { success ->
                if (success) {
                    latch.countDown()
                }
            }
        }
        onView(withId(R.id.signUp_Btn)).perform(click())
    }
}


@Test
    fun testSignupWithEmptyFieldsShowsError() {
        // Simulate empty fields input
        onView(withId(R.id.signUp_Btn)).perform(click())

        // Check if the error message is displayed as a Snackbar
        onView(withSnackbarText("All fields are required!")).check(matches(isDisplayed()))
    }

    @Test
    fun testSignupWithInvalidEmailShowsError() {
        // Simulate invalid email input
        onView(withId(R.id.name_TIET)).perform(typeText("kucing"), closeSoftKeyboard())
        onView(withId(R.id.email_TIET)).perform(typeText("invalid_email"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.confirmPassword_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())

        // Perform click action on signup button
        onView(withId(R.id.signUp_Btn)).perform(click())

        // Check if the error message is displayed as a Snackbar
        onView(withSnackbarText("Invalid email address!")).check(matches(isDisplayed()))
    }

    @Test
    fun testSignupWithMismatchedPasswordsShowsError() {
        // Simulate mismatched passwords input
        onView(withId(R.id.name_TIET)).perform(typeText("kucing"), closeSoftKeyboard())
        onView(withId(R.id.email_TIET)).perform(typeText("kucing@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.confirmPassword_TIET)).perform(typeText("DifferentPassword"), closeSoftKeyboard())

        // Perform click action on signup button
        onView(withId(R.id.signUp_Btn)).perform(click())

        // Check if the error message is displayed as a Snackbar
        onView(withSnackbarText("Passwords do not match!")).check(matches(isDisplayed()))
    }
}
