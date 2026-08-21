package org.splitties.compose.oclock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.wear.watchface.complications.data.ComplicationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun OClockCanvas(
    modifier: Modifier = Modifier,
    onTap: (OnTapScope.(TapEvent) -> Boolean)? = null,
    onDraw: DrawScope.() -> Unit,
) {
    val host = LocalOClockDrawHost.current
        ?: error("OClockCanvas must be used inside OClockRootCanvas or a ComposeWatchFaceService.")
    val key = androidx.compose.runtime.currentCompositeKeyHash
    val currentOnDraw by rememberUpdatedState(onDraw)
    val currentOnTap by rememberUpdatedState(onTap)
    SideEffect {
        host.set(key, currentOnTap, currentOnDraw)
    }
    DisposableEffect(key) {
        onDispose { host.remove(key) }
    }
    Canvas(modifier = modifier.fillMaxSize()) {}
}

@ExperimentalComposeOClockApi
@Composable
fun OClockRootCanvas(
    modifier: Modifier = Modifier,
    previewTime: PreviewTime.Config = PreviewTime.rememberConfig(),
    isAmbientFlow: StateFlow<Boolean> = remember { MutableStateFlow(false) },
    content: @Composable (complicationData: Map<Int, StateFlow<ComplicationData>>) -> Unit,
) {
    OClockRootCanvasInternal(
        modifier = modifier,
        previewTime = previewTime,
        isAmbientFlow = isAmbientFlow,
        complicationData = emptyMap(),
        wallClock = false,
        content = content,
    )
}

@OptIn(ExperimentalComposeOClockApi::class)
@Composable
fun OClockRootCanvasInternal(
    modifier: Modifier = Modifier,
    previewTime: PreviewTime.Config? = null,
    isAmbientFlow: StateFlow<Boolean>,
    complicationData: Map<Int, StateFlow<ComplicationData>>,
    wallClock: Boolean = true,
    content: @Composable (complicationData: Map<Int, StateFlow<ComplicationData>>) -> Unit,
) {
    val density = LocalDensity.current
    val drawHost = remember { OClockDrawHost() }
    val isAmbient by isAmbientFlow.collectAsState()
    val ambientState = remember { mutableStateOf(isAmbient) }
    SideEffect { ambientState.value = isAmbient }

    val currentTime = when {
        wallClock -> rememberWallClock()
        previewTime != null -> rememberPreviewClock(previewTime)
        else -> rememberWallClock()
    }
    val textMeasurer = rememberDefaultTextMeasurer()
    val tapScopeHolder = remember { arrayOfNulls<SimpleOnTapScope>(1) }

    CompositionLocalProvider(
        LocalOClockDrawHost provides drawHost,
        LocalCurrentTime provides currentTime,
        LocalIsAmbientState provides ambientState,
        LocalDensity provides density,
        LocalTextMeasurer provides textMeasurer,
    ) {
        content(complicationData)
        Canvas(
            modifier = modifier
                .fillMaxSize()
                .pointerInput(drawHost) {
                    detectTapGestures(
                        onPress = { offset ->
                            val scope = tapScopeHolder[0] ?: return@detectTapGestures
                            val handled = drawHost.handleTap(scope, TapEvent.Down(offset))
                            val released = tryAwaitRelease()
                            if (handled) {
                                drawHost.handleTap(
                                    scope,
                                    if (released) TapEvent.Up(offset) else TapEvent.Cancel(offset),
                                )
                            }
                        },
                    )
                },
        ) {
            tapScopeHolder[0] = SimpleOnTapScope(size, density)
            drawHost.drawAll(this)
        }
    }
}
