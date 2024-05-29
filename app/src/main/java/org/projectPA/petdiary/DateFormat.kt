package org.projectPA.petdiary

import android.text.format.DateUtils
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun Timestamp.relativeTime(): CharSequence =
    DateUtils.getRelativeTimeSpanString(
        this.toDate().time,
        Calendar.getInstance().timeInMillis,
        DateUtils.MINUTE_IN_MILLIS
    )

fun Timestamp.formatDate(format: String = "dd-MM-yyyy", locale: Locale = Locale.getDefault()): String =
    SimpleDateFormat(format, locale).format(this.toDate())