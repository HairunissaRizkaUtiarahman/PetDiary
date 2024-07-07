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
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.projectPA.petdiary.view.activities.auth.SigninActivity
import org.projectPA.petdiary.view.activities.managepet.PetActivity

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ManagePetTest {

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
    fun test01AddPetFlowAndVerify() {
        ActivityScenario.launch(SigninActivity::class.java)

        onView(withId(R.id.email_TIET)).perform(typeText("akupetdiary@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.signIn_Btn)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.manage_pet_button)).perform(click())

        Thread.sleep(5000)

        intended(hasComponent(PetActivity::class.java.name))

        onView(withId(R.id.addPet_Btn)).perform(click())

        onView(withId(R.id.pick_Btn)).perform(click())
        onView(withText("Choose from Gallery")).perform(click())

        val imageUri = Uri.parse("android.resource:////org.projectPA.petdiary/drawable/test_image")
        val intent = Intent().setData(imageUri)
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, intent))

        onView(withId(R.id.petNameTIET)).perform(typeText("Lontong"), closeSoftKeyboard())
        onView(withId(R.id.petType_TIET)).perform(typeText("Ragdoll"), closeSoftKeyboard())
        onView(withId(R.id.male_RB)).perform(click())
        onView(withId(R.id.petAge_TIET)).perform(typeText("1"), closeSoftKeyboard())
        onView(withId(R.id.petDesc_TIET)).perform(typeText("Awas Suka Gigit"), closeSoftKeyboard())

        onView(withId(R.id.add_Btn)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.petDetail_Btn)).perform(click())

        onView(withId(R.id.petName_TV)).check(matches(withText(containsString("Lontong"))))
        onView(withId(R.id.petType_TV)).check(matches(withText(containsString("Ragdoll"))))
        onView(withId(R.id.petGender_TV)).check(matches(withText(containsString("Male"))))
        onView(withId(R.id.petAge_TV)).check(matches(withText(containsString("1 Month"))))
        onView(withId(R.id.petDesc_TV)).check(matches(withText(containsString("Awas Suka Gigit"))))
    }

    @Test
    fun test02EditPetAndDeletePet() {
        ActivityScenario.launch(SigninActivity::class.java)

        onView(withId(R.id.email_TIET)).perform(typeText("akupetdiary@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.signIn_Btn)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.manage_pet_button)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.petDetail_Btn)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.petEditProfileBtn)).perform(click())

        onView(withId(R.id.petNameTIET)).perform(typeText(" Rambo"), closeSoftKeyboard())
        onView(withId(R.id.petType_TIET)).perform(typeText(" Anggora"), closeSoftKeyboard())
        onView(withId(R.id.female_RB)).perform(click())
        onView(withId(R.id.petAge_TIET)).perform(typeText("1"), closeSoftKeyboard())
        onView(withId(R.id.petDesc_TIET)).perform(typeText(" dan Agak nakal"), closeSoftKeyboard())

        onView(withId(R.id.save_Btn)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.petDetail_Btn)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.petName_TV)).check(matches(withText(containsString("Lontong Rambo"))))
        onView(withId(R.id.petType_TV)).check(matches(withText(containsString("Ragdoll Anggora"))))
        onView(withId(R.id.petGender_TV)).check(matches(withText(containsString("Female"))))
        onView(withId(R.id.petAge_TV)).check(matches(withText(containsString("11 Month"))))
        onView(withId(R.id.petDesc_TV)).check(matches(withText(containsString("Awas Suka Gigit dan Agak nakal"))))

        onView(withId(R.id.delete_Btn)).perform(click())

        Thread.sleep(2000)

        onView(withText("Yes")).perform(click())

        onView(withSnackbarText("Pet deleted successfully")).check(matches(isDisplayed()))
    }
}
