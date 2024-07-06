package org.projectPA.petdiary

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.projectPA.petdiary.view.activities.DashboardActivity
import org.projectPA.petdiary.view.activities.auth.SigninActivity
import org.projectPA.petdiary.view.activities.profile.ChangeLanguageProfileActivity

@RunWith(AndroidJUnit4::class)
class ChangeLanguageTest {

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
    fun editLanguageProcess() {
        ActivityScenario.launch(SigninActivity::class.java)

        onView(withId(R.id.email_TIET)).perform(typeText("akupetdiary@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.signIn_Btn)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.bottom_navigation_view)).check(matches(isDisplayed()))
        onView(withId(R.id.profile)).check(matches(isDisplayed()))
        onView(withId(R.id.profile)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.changeLanguage_Btn)).perform(click())

        Thread.sleep(5000)

        Intents.intended(IntentMatchers.hasComponent(ChangeLanguageProfileActivity::class.java.name))

        Thread.sleep(5000)

        onView(withId(R.id.indonesian_btn)).perform(click())

        Thread.sleep(5000)

        // Verifikasi teks-teks dalam bahasa Indonesia
        onView(withId(R.id.review_text)).check(matches(withText("Ulasan\n")))
        onView(withId(R.id.petshop_clinic_button_text)).check(matches(withText("Cari Toko & Klinik")))
        onView(withId(R.id.community_text)).check(matches(withText("Komunitas\n")))
        onView(withId(R.id.Article_text)).check(matches(withText("Artikel\n")))
        onView(withId(R.id.most_reviews_text)).check(matches(withText("5 Produk Terpopuler")))
    }
}
