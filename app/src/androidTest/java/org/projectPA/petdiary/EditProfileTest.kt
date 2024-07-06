package org.projectPA.petdiary

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import android.widget.DatePicker
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.RootMatchers
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
import org.projectPA.petdiary.databinding.FragmentProfileBinding
import org.projectPA.petdiary.view.activities.auth.SigninActivity
import org.projectPA.petdiary.view.activities.myprofile.MyProfileActivity

@RunWith(AndroidJUnit4::class)
class EditProfileTest {

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
    fun editProfileProcces() {

        ActivityScenario.launch(SigninActivity::class.java)


        onView(withId(R.id.email_TIET)).perform(typeText("akupetdiary@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.signIn_Btn)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.bottom_navigation_view)).check(matches(isDisplayed()))

        onView(withId(R.id.profile)).check(matches(isDisplayed()))

        onView(withId(R.id.profile)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.editProfile_Btn)).perform(click())

        onView(withId(R.id.pick_Btn)).perform(click())
        onView(withText("Choose from Gallery")).perform(click())


        val imageUri = Uri.parse("android.resource:////org.projectPA.petdiary/drawable/test_image")
        val intent = Intent().setData(imageUri)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(Instrumentation.ActivityResult(RESULT_OK, intent))

        onView(withId(R.id.name_TIET)).perform(typeText(" Official"), closeSoftKeyboard())
        onView(withId(R.id.address_TIET)).perform(typeText(" Sukapura"), closeSoftKeyboard())

        onView(withId(R.id.main_content)).perform(swipeUpSlightly())
        onView(withId(R.id.bio_TIET)).perform(typeText(" admin"), closeSoftKeyboard())

        onView(withId(R.id.save_Btn)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.myProfile_Btn)).perform(click())

        Thread.sleep(5000)

        Intents.intended(IntentMatchers.hasComponent(MyProfileActivity::class.java.name))

        Thread.sleep(6000)

        onView(withId(R.id.name_Tv)).check(matches(withText("Pet Diary Admin Official")))
        onView(withId(R.id.bio_Tv)).check(matches(withText("Official akun PetDiary admin")))
    }
}
