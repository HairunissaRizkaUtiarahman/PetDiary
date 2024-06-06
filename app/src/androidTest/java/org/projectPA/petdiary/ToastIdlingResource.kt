package org.projectPA.petdiary

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback

class ToastIdlingResource : IdlingResource {
    @Volatile private var resourceCallback: ResourceCallback? = null

    override fun getName(): String {
        return ToastIdlingResource::class.java.name
    }

    override fun isIdleNow(): Boolean {
        resourceCallback?.onTransitionToIdle()
        return true
    }

    override fun registerIdleTransitionCallback(callback: ResourceCallback?) {
        resourceCallback = callback
    }
}