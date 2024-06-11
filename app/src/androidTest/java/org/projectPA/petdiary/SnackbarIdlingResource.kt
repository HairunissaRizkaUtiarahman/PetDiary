package org.projectPA.petdiary

import androidx.test.espresso.idling.CountingIdlingResource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import android.util.Log

object FirebaseAuthIdlingResource {
    private const val RESOURCE = "GLOBAL"
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    private const val TAG = "FirebaseAuthIdlingResource"

    fun increment() {
        Log.d(TAG, "Incrementing idling resource")
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            Log.d(TAG, "Decrementing idling resource")
            countingIdlingResource.decrement()
        } else {
            Log.d(TAG, "Idling resource is already idle")
        }
    }

    val authListener = AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null && user.isEmailVerified) {
            decrement()
        }
    }

    init {
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
    }
}
