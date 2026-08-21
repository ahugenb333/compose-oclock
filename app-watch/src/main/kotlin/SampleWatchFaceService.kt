package com.louiscad.composeoclockplayground

import androidx.compose.runtime.Composable
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import kotlinx.coroutines.flow.StateFlow
import org.splitties.compose.oclock.ComposeWatchFaceService
import org.splitties.compose.oclock.InvalidationMode
import org.splitties.compose.oclock.sample.watchfaces.WatchFaceSwitcher

class SampleWatchFaceService : ComposeWatchFaceService(
    complicationSlotIds = emptySet(),
    invalidationMode = InvalidationMode.WaitForInvalidation,
) {
    override fun watchFaceContent(): @Composable (Map<Int, StateFlow<ComplicationData>>) -> Unit = {
        WatchFaceSwitcher()
    }

    override fun supportedComplicationTypes(slotId: Int) = listOf(
        ComplicationType.RANGED_VALUE,
        ComplicationType.SHORT_TEXT,
    )
}
