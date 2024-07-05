package org.projectPA.petdiary

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.projectPA.petdiary.view.activities.auth.SignupActivity
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class SignupActivityTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(SignupActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testSignupButtonOpensSigninActivity() {

        onView(withId(R.id.name_TIET)).perform(typeText("Pet Diary Admin"), closeSoftKeyboard())
        onView(withId(R.id.email_TIET)).perform(typeText("akupetdiary@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234Test1234"), closeSoftKeyboard())
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

    @Test
    fun testSignupWithInvalidEmailShowsError() {

        onView(withId(R.id.name_TIET)).perform(typeText("kucing"), closeSoftKeyboard())
        onView(withId(R.id.email_TIET)).perform(typeText("invalid_email"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.confirmPassword_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())

        onView(withId(R.id.signUp_Btn)).perform(click())

        onView(withSnackbarText("Invalid email address!")).check(matches(isDisplayed()))
    }

    @Test
    fun testSignupWithMismatchedPasswordsShowsError() {

        onView(withId(R.id.name_TIET)).perform(typeText("kucing"), closeSoftKeyboard())
        onView(withId(R.id.email_TIET)).perform(typeText("kucing@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.confirmPassword_TIET)).perform(typeText("DifferentPassword"), closeSoftKeyboard())


        onView(withId(R.id.signUp_Btn)).perform(click())


        onView(withSnackbarText("Passwords do not match!")).check(matches(isDisplayed()))
    }

    @Test
    fun testSignupWithInvalidPasswordsShowsError() {

        onView(withId(R.id.name_TIET)).perform(typeText("mangga"), closeSoftKeyboard())
        onView(withId(R.id.email_TIET)).perform(typeText("mangga@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("1"), closeSoftKeyboard())
        onView(withId(R.id.confirmPassword_TIET)).perform(typeText("1"), closeSoftKeyboard())

        onView(withId(R.id.signUp_Btn)).perform(click())

        onView(withSnackbarText("Password should be between 6 and 12 characters and contain letters, numbers, and optionally dots!")).check(matches(isDisplayed()))
    }

    @Test
    fun testSignupEmptyNameShowsError() {
        onView(withId(R.id.name_TIET)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.email_TIET)).perform(typeText("mangga@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("1"), closeSoftKeyboard())
        onView(withId(R.id.confirmPassword_TIET)).perform(typeText("1"), closeSoftKeyboard())

        onView(withId(R.id.signUp_Btn)).perform(click())

        onView(withSnackbarText("Name is required and must be less than 100 characters")).check(matches(isDisplayed()))
    }

    @Test
    fun testSignupEmptyEmailShowsError() {
        onView(withId(R.id.name_TIET)).perform(typeText("mangga"), closeSoftKeyboard())
        onView(withId(R.id.email_TIET)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("abc123456"), closeSoftKeyboard())
        onView(withId(R.id.confirmPassword_TIET)).perform(typeText("abc123456"), closeSoftKeyboard())

        onView(withId(R.id.signUp_Btn)).perform(click())

        onView(withSnackbarText("Email is required and must be less than 100 characters")).check(matches(isDisplayed()))
    }

    @Test
    fun testSignupNameExistShowsError() {
        onView(withId(R.id.name_TIET)).perform(typeText("kalica"), closeSoftKeyboard())
        onView(withId(R.id.email_TIET)).perform(typeText("sayur@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("abc123456"), closeSoftKeyboard())
        onView(withId(R.id.confirmPassword_TIET)).perform(typeText("abc123456"), closeSoftKeyboard())

        onView(withId(R.id.signUp_Btn)).perform(click())

        Thread.sleep(1000)

        onView(withSnackbarText("Name already taken")).check(matches(isDisplayed()))
    }
}


