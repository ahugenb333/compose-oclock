package org.splitties.compose.oclock

import java.time.ZonedDateTime
import java.time.temporal.ChronoField

/**
 * Current watch time exposed to watch face composables via [LocalTime].
 */
data class CurrentTime(
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val millis: Int,
) {
    val hourWithMinutes: Float get() = hours + minutes / 60f
    val minutesWithSeconds: Float get() = minutes + seconds / 60f
    val secondsWithMillis: Float get() = seconds + millis / 1000f

    companion object {
        fun from(zonedDateTime: ZonedDateTime): CurrentTime = CurrentTime(
            hours = zonedDateTime.get(ChronoField.HOUR_OF_DAY),
            minutes = zonedDateTime.get(ChronoField.MINUTE_OF_HOUR),
            seconds = zonedDateTime.get(ChronoField.SECOND_OF_MINUTE),
            millis = zonedDateTime.get(ChronoField.MILLI_OF_SECOND),
        )

        fun fromMillis(epochMillis: Long): CurrentTime =
            from(ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(epochMillis), java.time.ZoneId.systemDefault()))
    }
}

internal val LocalCurrentTime = androidx.compose.runtime.compositionLocalOf {
    CurrentTime.fromMillis(System.currentTimeMillis())
}

/** Read the current time inside watch face composables. */
object LocalTime {
    val current: CurrentTime
        @androidx.compose.runtime.Composable
        @androidx.compose.runtime.ReadOnlyComposable
        get() = LocalCurrentTime.current
}

internal val LocalIsAmbientState = androidx.compose.runtime.compositionLocalOf {
    androidx.compose.runtime.mutableStateOf(false)
}

/** Whether the watch face is in ambient (burn-in protection) mode. */
object LocalIsAmbient {
    val current: androidx.compose.runtime.State<Boolean>
        @androidx.compose.runtime.Composable
        @androidx.compose.runtime.ReadOnlyComposable
        get() = LocalIsAmbientState.current
}

@RequiresOptIn("Compose O'Clock preview APIs are experimental.")
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalComposeOClockApi
