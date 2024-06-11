package org.projectPA.petdiary

import androidx.test.espresso.IdlingResource
import com.google.android.material.snackbar.Snackbar

class SnackbarIdlingResource : IdlingResource {

    private var callback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = this.javaClass.name

    override fun isIdleNow(): Boolean {
        val idle = SnackbarManager.instance.isIdleNow
        if (idle && callback != null) {
            callback!!.onTransitionToIdle()
        }
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        this.callback = callback
    }

    object SnackbarManager {
        private val snackbarList: MutableList<Snackbar> = ArrayList()
        val instance = this

        fun registerSnackbar(snackbar: Snackbar) {
            snackbarList.add(snackbar)
        }

        fun unregisterSnackbar(snackbar: Snackbar) {
            snackbarList.remove(snackbar)
        }

        val isIdleNow: Boolean
            get() = snackbarList.isEmpty()
    }
}
