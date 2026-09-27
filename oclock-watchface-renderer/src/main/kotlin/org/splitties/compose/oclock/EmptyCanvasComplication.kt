package org.splitties.compose.oclock

import android.graphics.Canvas
import android.graphics.Rect
import androidx.annotation.ColorInt
import androidx.wear.watchface.BoundingArc
import androidx.wear.watchface.CanvasComplication
import androidx.wear.watchface.RenderParameters
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationExperimental
import androidx.wear.watchface.complications.data.EmptyComplicationData
import java.time.ZonedDateTime

/**
 * Forwards complication data to [ComplicationSlot.complicationData] without drawing on the canvas.
 * Compose O'Clock renders complications in [OClockCanvas] instead.
 */
@OptIn(ComplicationExperimental::class)
internal class EmptyCanvasComplication : CanvasComplication {
    private var data: ComplicationData = EmptyComplicationData()

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        renderParameters: RenderParameters,
        slotId: Int,
    ) = Unit

    override fun drawHighlight(
        canvas: Canvas,
        bounds: Rect,
        boundsType: Int,
        zonedDateTime: ZonedDateTime,
        @ColorInt color: Int,
    ) = Unit

    override fun drawHighlight(
        canvas: Canvas,
        bounds: Rect,
        boundsType: Int,
        zonedDateTime: ZonedDateTime,
        @ColorInt color: Int,
        boundingArc: BoundingArc?,
    ) = Unit

    override fun getData(): ComplicationData = data

    override fun loadData(complicationData: ComplicationData, loadDrawablesAsynchronous: Boolean) {
        data = complicationData
    }
}
