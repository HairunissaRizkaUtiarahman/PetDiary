package org.projectPA.petdiary

import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.ViewActions.actionWithAssertions

fun swipeUpSlightly(): ViewAction {
    return actionWithAssertions(
        GeneralSwipeAction(
            Swipe.SLOW, // Menggunakan geser yang lebih lambat untuk kontrol lebih baik
            GeneralLocation.BOTTOM_CENTER,
            GeneralLocation.translate(GeneralLocation.BOTTOM_CENTER, 0f, -0.45f), // Menggeser sedikit lebih besar
            Press.FINGER
        )
    )
}

fun swipeDownSlightly(): ViewAction {
    return actionWithAssertions(
        GeneralSwipeAction(
            Swipe.SLOW, // Menggunakan geser yang lebih lambat untuk kontrol lebih baik
            GeneralLocation.TOP_CENTER,
            GeneralLocation.translate(GeneralLocation.TOP_CENTER, 0f, 0.15f), // Menggeser sedikit lebih besar
            Press.FINGER
        )
    )
}
