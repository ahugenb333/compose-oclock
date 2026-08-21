package org.splitties.compose.oclock

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

sealed interface TapEvent {
    val position: Offset

    data class Down(override val position: Offset) : TapEvent
    data class Up(override val position: Offset) : TapEvent
    data class Cancel(override val position: Offset) : TapEvent
}

interface OnTapScope {
    val size: androidx.compose.ui.geometry.Size
    val center: Offset
    val density: Density

    fun px(dp: Int): Float = with(density) { dp.dp.toPx() }
}
