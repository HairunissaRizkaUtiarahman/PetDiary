package org.projectPA.petdiary

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.projectPA.petdiary.view.activities.FillProductInformationActivity
import org.projectPA.petdiary.view.activities.SigninActivity

@RunWith(AndroidJUnit4::class)
class FillProductInformationActivityTest {

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testProductInput() {
        // Simulate login
        val loginIntent = Intent(Intent.ACTION_MAIN).apply {
            setClassName("org.projectPA.petdiary", "org.projectPA.petdiary.view.activities.LoginActivity")
        }
        ActivityScenario.launch<SigninActivity>(loginIntent)

        // Perform login actions (replace with actual login steps)
        onView(withId(R.id.email_TIET)).perform(typeText("testUser"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("testPassword"), closeSoftKeyboard())
        onView(withId(R.id.signIn_Btn)).perform(click())

        // Launch the FillProductInformationActivity
        val intent = Intent(Intent.ACTION_MAIN).apply {
            setClassName("org.projectPA.petdiary", "org.projectPA.petdiary.view.activities.FillProductInformationActivity")
            putExtra("pet_type", "Cat")
            putExtra("category", "Food")
        }
        ActivityScenario.launch<FillProductInformationActivity>(intent)

        // Check initial state
        onView(withId(R.id.submit_button)).check(matches(not(isEnabled())))

        // Fill in brand name
        onView(withId(R.id.formInputBrandName)).perform(typeText("Test Brand"), closeSoftKeyboard())

        // Fill in product name
        onView(withId(R.id.formInputProductName)).perform(typeText("Test Product"), closeSoftKeyboard())

        // Fill in description
        onView(withId(R.id.formInputDescription)).perform(typeText("This is a test product description that is long enough."), closeSoftKeyboard())

        // Mock the photo picking action
        val resultData = Intent().apply {
            data = Uri.parse("content://test/uri")
        }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_PICK)).respondWith(result)

        // Simulate picking a photo
        onView(withId(R.id.upload_photo_button)).perform(click())

        // Check if submit button is enabled
        onView(withId(R.id.submit_button)).check(matches(isEnabled()))

        // Click submit button
        onView(withId(R.id.submit_button)).perform(click())

        // Verify if the product is submitted and navigate to Product Detail
        // You might want to check for a Toast message or the navigation to the Product Detail screen
        onView(withText("Product added successfully")).inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }
}
