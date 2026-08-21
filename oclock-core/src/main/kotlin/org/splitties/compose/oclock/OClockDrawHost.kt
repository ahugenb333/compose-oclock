package org.splitties.compose.oclock

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density

internal class OClockLayer(
    val onTap: (OnTapScope.(TapEvent) -> Boolean)?,
    val onDraw: DrawScope.() -> Unit,
)

internal class OClockDrawHost {
    private val layers = linkedMapOf<Int, OClockLayer>()

    fun set(key: Int, onTap: (OnTapScope.(TapEvent) -> Boolean)?, draw: DrawScope.() -> Unit) {
        layers[key] = OClockLayer(onTap, draw)
    }

    fun remove(key: Int) {
        layers.remove(key)
    }

    fun drawAll(scope: DrawScope) {
        layers.values.forEach { layer ->
            layer.onDraw.invoke(scope)
        }
    }

    fun handleTap(scope: OnTapScope, event: TapEvent): Boolean {
        for (layer in layers.values.reversed()) {
            val onTap = layer.onTap ?: continue
            if (scope.onTap(event)) return true
        }
        return false
    }
}

internal val LocalOClockDrawHost = androidx.compose.runtime.compositionLocalOf<OClockDrawHost?> { null }

internal class SimpleOnTapScope(
    override val size: Size,
    override val density: Density,
) : OnTapScope {
    override val center: Offset get() = Offset(size.width / 2f, size.height / 2f)
}
