package org.projectPA.petdiary

import androidx.test.espresso.idling.CountingIdlingResource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

object FirebaseAuthIdlingResource {
    private const val RESOURCE = "GLOBAL"
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
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
