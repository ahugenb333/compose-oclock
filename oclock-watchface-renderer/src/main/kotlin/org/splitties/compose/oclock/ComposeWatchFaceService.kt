package org.splitties.compose.oclock

import android.graphics.Canvas
import android.graphics.Rect
import android.view.SurfaceHolder
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchFace
import androidx.wear.watchface.WatchFaceService
import androidx.wear.watchface.WatchFaceType
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.style.CurrentUserStyleRepository
import java.time.ZonedDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

abstract class ComposeWatchFaceService(
    @Suppress("unused") private val complicationSlotIds: Set<Int> = emptySet(),
    @Suppress("unused") private val invalidationMode: InvalidationMode = InvalidationMode.WaitForInvalidation,
) : WatchFaceService() {

    /** Implement as a composable lambda, e.g. `{ WatchFaceSwitcher() }`. */
    abstract fun watchFaceContent(): @Composable (Map<Int, StateFlow<ComplicationData>>) -> Unit

    open fun supportedComplicationTypes(slotId: Int): List<ComplicationType> = emptyList()

    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository,
    ): WatchFace {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        val isAmbientFlow = MutableStateFlow(currentAmbient(watchState))
        scope.launch {
            while (isActive) {
                isAmbientFlow.value = currentAmbient(watchState)
                delay(250L)
            }
        }

        val complicationData = emptyMap<Int, StateFlow<ComplicationData>>()
        val faceContent = watchFaceContent()
        val lifecycleOwner = WatchFaceLifecycleOwner()
        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setContent {
                OClockRootCanvasInternal(
                    isAmbientFlow = isAmbientFlow,
                    complicationData = complicationData,
                    wallClock = true,
                    previewTime = null,
                ) { data ->
                    faceContent(data)
                }
            }
        }
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val renderer = ComposeCanvasRenderer(
            surfaceHolder = surfaceHolder,
            watchState = watchState,
            currentUserStyleRepository = currentUserStyleRepository,
            composeView = composeView,
            scope = scope,
        )

        return WatchFace(WatchFaceType.ANALOG, renderer).also {
            renderer.onDestroyCallback = {
                lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                scope.cancel()
            }
        }
    }

    private fun currentAmbient(watchState: WatchState): Boolean {
        val ambient = watchState.isAmbient
        return when (ambient) {
            is Boolean -> ambient
            else -> false
        }
    }
}

private class ComposeCanvasRenderer(
    surfaceHolder: SurfaceHolder,
    watchState: WatchState,
    currentUserStyleRepository: CurrentUserStyleRepository,
    private val composeView: ComposeView,
    private val scope: CoroutineScope,
) : Renderer.CanvasRenderer2<Renderer.SharedAssets>(
    surfaceHolder = surfaceHolder,
    watchState = watchState,
    currentUserStyleRepository = currentUserStyleRepository,
    canvasType = CanvasType.HARDWARE,
    interactiveDrawModeUpdateDelayMillis = 16L,
    clearWithBackgroundTintBeforeRenderingHighlightLayer = true,
) {
    var onDestroyCallback: (() -> Unit)? = null

    override suspend fun createSharedAssets(): Renderer.SharedAssets =
        object : Renderer.SharedAssets {
            override fun onDestroy() = Unit
        }

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: Renderer.SharedAssets,
    ) {
        drawComposeView(canvas, bounds)
    }

    override fun renderHighlightLayer(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: Renderer.SharedAssets,
    ) = Unit

    override fun onDestroy() {
        super.onDestroy()
        onDestroyCallback?.invoke()
        scope.cancel()
    }

    private fun drawComposeView(canvas: Canvas, bounds: Rect) {
        if (bounds.width() <= 0 || bounds.height() <= 0) return
        val widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(bounds.width(), android.view.View.MeasureSpec.EXACTLY)
        val heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(bounds.height(), android.view.View.MeasureSpec.EXACTLY)
        composeView.measure(widthSpec, heightSpec)
        composeView.layout(0, 0, bounds.width(), bounds.height())
        canvas.save()
        canvas.clipRect(bounds)
        composeView.draw(canvas)
        canvas.restore()
    }
}

private class WatchFaceLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    init {
        savedStateRegistryController.performRestore(null)
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }
}
