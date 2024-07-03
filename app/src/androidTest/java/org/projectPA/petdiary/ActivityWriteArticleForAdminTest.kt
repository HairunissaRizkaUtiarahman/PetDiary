package org.projectPA.petdiary.view.activities

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasType
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.projectPA.petdiary.R

@RunWith(AndroidJUnit4::class)
@LargeTest
class ActivityWriteArticleForAdminTest {

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testSelectImage() {
        // Ganti dengan URI gambar nyata dari galeri setelah mengambil gambar menggunakan kamera
        val imageUri = getImageUriFromFilePath(InstrumentationRegistry.getInstrumentation().targetContext, "/storage/emulated/0/Pictures/IMG_20240521_221734.jpg")
        val resultData = Intent().apply {
            data = imageUri
        }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)

        Intents.intending(allOf(
            hasAction(Intent.ACTION_GET_CONTENT),
            hasType("image/*")
        )).respondWith(result)

        ActivityScenario.launch(ActivityWriteArticleForAdmin::class.java)

        onView(withId(R.id.upload_photo_button)).perform(click())

        // Check if the image URI is set correctly
        onView(withId(R.id.upload_image_article)).check(matches(imageUri?.let { withImageUri(it) }))
    }

    // Matcher to check if an ImageView has the expected image URI
    private fun withImageUri(expectedUri: Uri): Matcher<View> {
        return object : BoundedMatcher<View, ImageView>(ImageView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("with image URI: $expectedUri")
            }

            override fun matchesSafely(imageView: ImageView): Boolean {
                // Anda mungkin perlu menetapkan tag di kode aktivitas saat memuat gambar
                return imageView.drawable != null && imageView.tag == expectedUri
            }
        }
    }

    @Test
    fun testPublishWithCompleteForm() {
        // Ganti dengan URI gambar nyata dari galeri setelah mengambil gambar menggunakan kamera
        val imageUri = getImageUriFromFilePath(InstrumentationRegistry.getInstrumentation().targetContext, "/storage/emulated/0/Pictures/IMG_20240521_221734.jpg")
        val resultData = Intent().apply {
            data = imageUri
        }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)

        Intents.intending(allOf(
            hasAction(Intent.ACTION_GET_CONTENT),
            hasType("image/*")
        )).respondWith(result)

        ActivityScenario.launch(ActivityWriteArticleForAdmin::class.java)

        onView(withId(R.id.input_tittle_field)).perform(typeText("Test Title"), closeSoftKeyboard())
        onView(withId(R.id.categoryDropdown)).perform(click())
        onData(allOf(instanceOf(String::class.java), `is`("Category 1"))).perform(click())
        onView(withId(R.id.editor)).perform(typeText("This is the body of the article"), closeSoftKeyboard())

        // Simulate selecting an image
        onView(withId(R.id.upload_photo_button)).perform(click())

        // Check if the image URI is set correctly
        onView(withId(R.id.upload_image_article)).check(matches(imageUri?.let { withImageUri(it) }))

        onView(withId(R.id.publish_button)).perform(click())
        // Check for success toast
        onView(withText("Article published successfully"))
            .inRoot(withDecorView(not(getActivityDecorView())))
            .check(matches(isDisplayed()))
    }

    private fun getActivityDecorView(): View {
        var decorView: View? = null
        ActivityScenario.launch(ActivityWriteArticleForAdmin::class.java).onActivity {
            decorView = it.window.decorView
        }
        return decorView!!
    }

    private fun getImageUriFromFilePath(context: Context, filePath: String): Uri? {
        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            "${MediaStore.Images.Media.DATA}=? ",
            arrayOf(filePath), null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            val id: Int = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
            cursor.close()
            Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
        } else {
            cursor?.close()
            null
        }
    }
}
