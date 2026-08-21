package org.splitties.compose.oclock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.time.ZonedDateTime

@ExperimentalComposeOClockApi
object PreviewTime {
    data class Config(
        val zonedDateTime: ZonedDateTime,
        val tickMillis: Long = 1_000L,
    )

    @Composable
    fun rememberConfig(
        zonedDateTime: ZonedDateTime = ZonedDateTime.now(),
        tickMillis: Long = 1_000L,
    ): Config = remember(zonedDateTime, tickMillis) {
        Config(zonedDateTime = zonedDateTime, tickMillis = tickMillis)
    }
}

@OptIn(ExperimentalComposeOClockApi::class)
@Composable
internal fun rememberPreviewClock(config: PreviewTime.Config): CurrentTime {
    var epochMillis by remember(config) {
        mutableLongStateOf(config.zonedDateTime.toInstant().toEpochMilli())
    }
    androidx.compose.runtime.LaunchedEffect(config) {
        while (true) {
            kotlinx.coroutines.delay(config.tickMillis)
            epochMillis = System.currentTimeMillis()
        }
    }
    return remember(epochMillis) { CurrentTime.fromMillis(epochMillis) }
}

@Composable
internal fun rememberWallClock(): CurrentTime {
    var epochMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(16L)
            epochMillis = System.currentTimeMillis()
        }
    }
    return remember(epochMillis) { CurrentTime.fromMillis(epochMillis) }
}
