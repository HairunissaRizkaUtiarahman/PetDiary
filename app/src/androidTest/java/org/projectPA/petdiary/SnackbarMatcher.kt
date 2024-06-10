package org.projectPA.petdiary

import android.view.View
import android.widget.TextView
import androidx.test.espresso.matcher.BoundedMatcher
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import org.hamcrest.Description
import org.hamcrest.Matcher

fun withSnackbarText(text: String): Matcher<View> {
    return object : BoundedMatcher<View, SnackbarLayout>(SnackbarLayout::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("with snackbar text: ").appendText(text)
        }

        override fun matchesSafely(view: SnackbarLayout): Boolean {
            val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            return textView != null && textView.text == text
        }
    }
}
