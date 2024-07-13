package org.projectPA.petdiary

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
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
import org.projectPA.petdiary.view.activities.ActivityWriteArticleForAdmin
import org.projectPA.petdiary.view.activities.auth.SigninActivity
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class WriteArticleForAdminTest {

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
    fun testArticleEntryProcess() {

        ActivityScenario.launch(SigninActivity::class.java)

        onView(withId(R.id.email_TIET)).perform(typeText("akupetdiary@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_TIET)).perform(typeText("Test1234"), closeSoftKeyboard())
        onView(withId(R.id.signIn_Btn)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.article_button)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.add_article_only_for_admin_button)).check(matches(isDisplayed()))

        onView(withId(R.id.add_article_only_for_admin_button)).perform(click())

        Thread.sleep(2000)

        ActivityScenario.launch(ActivityWriteArticleForAdmin::class.java).use { scenario ->
            scenario.onActivity { activity ->

                activity.setRichEditorText("test write article")
                val latch = CountDownLatch(1)
                var editorText: String? = null
                activity.getRichEditorText { text ->
                    editorText = text
                    latch.countDown()
                }
                latch.await(5, TimeUnit.SECONDS)

                assertThat(editorText, containsString("test write article"))
            }

            onView(withId(R.id.upload_photo_button)).perform(click())

            val imageUri = Uri.parse("android.resource:////org.projectPA.petdiary/drawable/test_image")
            val intent = Intent().setData(imageUri)
            intending(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT)).respondWith(Instrumentation.ActivityResult(RESULT_OK, intent))

            onView(withId(R.id.input_tittle_field)).perform(typeText("Testing tittle article"), closeSoftKeyboard())

            onView(withId(R.id.categoryDropdown)).perform(click())
            onView(withText("Event")).perform(click())

            onView(withId(R.id.publish_button)).perform(click())

            onView(withId(R.id.list_article)).perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

            onView(withId(R.id.tittle_article)).check(matches(withText(containsString("Testing tittle article"))))
            onView(withId(R.id.article_category)).check(matches(withText(containsString("Event"))))
        }
    }
}
