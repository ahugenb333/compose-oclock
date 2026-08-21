package org.splitties.compose.oclock

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextMeasurer
import org.splitties.compose.oclock.internal.InternalComposeOClockApi

internal val LocalDensity = androidx.compose.runtime.compositionLocalOf<androidx.compose.ui.unit.Density> {
    error("No Density provided")
}

@InternalComposeOClockApi
object LocalTextMeasurerWithoutCache {
    val current: TextMeasurer
        @Composable
        get() = rememberDefaultTextMeasurer(cacheSize = 0)
}

internal val LocalTextMeasurer = androidx.compose.runtime.compositionLocalOf<TextMeasurer> {
    error("No TextMeasurer provided")
}

@Composable
internal fun rememberDefaultTextMeasurer(cacheSize: Int = 64): TextMeasurer =
    androidx.compose.ui.text.rememberTextMeasurer(cacheSize = cacheSize)
