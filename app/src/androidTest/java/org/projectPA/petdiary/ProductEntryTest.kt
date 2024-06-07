package org.projectPA.petdiary

import android.app.Activity.RESULT_OK
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
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.projectPA.petdiary.view.activities.auth.SigninActivity

@RunWith(AndroidJUnit4::class)
class ProductEntryTest {

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
    fun testPetProductEntryProcess() {
        // Launch the SigninActivity
        ActivityScenario.launch(SigninActivity::class.java)

        // Sign in with email and password
        onView(withId(R.id.email_TIET)).perform(typeText("akupetdiary@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("12345678"), closeSoftKeyboard())
        onView(withId(R.id.signIn_Btn)).perform(click())

        // Wait for the dashboard to load
        Thread.sleep(2000) // Adjust the sleep time as needed

        // Verify that DashboardActivity is displayed
        onView(withId(R.id.bottom_navigation_view)).check(matches(isDisplayed()))

        // Navigate to HomeFragment
        onView(withId(R.id.home)).perform(click())

        // Click on the add button to open AddButtonFragment
        onView(withId(R.id.add_button)).perform(click())

        // Click on "Add a Product"
        onView(withId(R.id.add_a_product_button)).perform(click())

        // Choose a pet category
        onView(withId(R.id.choose_cat_button)).perform(click())

        // Choose a product category
        onView(withId(R.id.button_food_category)).perform(click())

        // Fill in the product information
        onView(withId(R.id.formInputBrandName)).perform(typeText("Testing add product"), closeSoftKeyboard())
        onView(withId(R.id.formInputProductName)).perform(typeText("Name product testing 17"), closeSoftKeyboard())
        onView(withId(R.id.formInputDescription)).perform(typeText("Delicious cat food for your lovely pet. Good for my cat fur, and she loves it so much, recommended, good quality"), closeSoftKeyboard())

        // Upload a product image
        onView(withId(R.id.upload_photo_button)).perform(click())
        // Choose to pick a photo from the gallery
        onView(withText("Choose from Gallery")).perform(click())

        // Simulate picking a photo from the gallery
        val imageUri = Uri.parse("android.resource://org.projectPA.petdiary/drawable/test_image")
        val intent = Intent().setData(imageUri)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(Instrumentation.ActivityResult(RESULT_OK, intent))

        // Verify that the submit button is enabled and submit the form
        onView(withId(R.id.submit_button)).check(matches(isEnabled())).perform(click())

        // Wait for ProductDetailActivity to be displayed
        Thread.sleep(2000)

        // Verify that ProductDetailActivity is opened and the data is displayed correctly
        onView(withId(R.id.product_brand_text)).check(matches(withText("Testing add product")))
        onView(withId(R.id.product_name_text)).check(matches(withText("Name Product Testing 17")))
        onView(withId(R.id.product_description_text)).check(matches(withText(containsString("Delicious cat food for your lovely pet. Good for my cat fur, and she loves it so much, recommended, good quality"))))
        onView(withId(R.id.product_category)).check(matches(withText(containsString("Food"))))
        onView(withId(R.id.for_what_pet_type)).check(matches(withText(containsString("Cat"))))
    }
}
