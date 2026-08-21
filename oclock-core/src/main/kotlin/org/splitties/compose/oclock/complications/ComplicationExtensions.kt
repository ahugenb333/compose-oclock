package org.splitties.compose.oclock.complications

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.wear.watchface.complications.data.ComplicationText
import androidx.wear.watchface.complications.data.GoalProgressComplicationData
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.MonochromaticImageComplicationData
import androidx.wear.watchface.complications.data.NoPermissionComplicationData
import androidx.wear.watchface.complications.data.PhotoImageComplicationData
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.data.SmallImageComplicationData
import androidx.wear.watchface.complications.data.WeightedElementsComplicationData
import org.splitties.compose.oclock.LocalTextMeasurer

@Composable
fun ComplicationText?.rememberMeasuredAsState(
    default: String,
    callMeasure: TextMeasurer.(string: String) -> TextLayoutResult,
): State<TextLayoutResult> {
    val textMeasurer = LocalTextMeasurer.current
    val text = default
    return produceState(initialValue = callMeasure(textMeasurer, text), text) {
        value = callMeasure(textMeasurer, text)
    }
}

@Composable
fun ComplicationText.rememberMeasuredAsState(
    callMeasure: TextMeasurer.(string: String) -> TextLayoutResult,
): State<TextLayoutResult> {
    val textMeasurer = LocalTextMeasurer.current
    val text = ""
    return produceState(initialValue = callMeasure(textMeasurer, text), text) {
        value = callMeasure(textMeasurer, text)
    }
}

@Composable fun NoPermissionComplicationData.rememberDrawableAsState(
    preferSmallImage: Boolean = false,
    tint: Color = Color.Unspecified,
    tintBlendMode: BlendMode = BlendMode.SrcIn,
    ambientTint: Color = tint,
    ambientTintBlendMode: BlendMode = tintBlendMode,
): State<Drawable?> = produceState<Drawable?>(null, this) { value = null }

@Composable fun LongTextComplicationData.rememberDrawableAsState(
    preferSmallImage: Boolean = false,
    tint: Color = Color.Unspecified,
    tintBlendMode: BlendMode = BlendMode.SrcIn,
    ambientTint: Color = tint,
    ambientTintBlendMode: BlendMode = tintBlendMode,
): State<Drawable?> = produceState<Drawable?>(null, this) { value = null }

@Composable fun MonochromaticImageComplicationData.rememberDrawableAsState(
    tint: Color = Color.Unspecified,
    tintBlendMode: BlendMode = BlendMode.SrcIn,
    ambientTint: Color = tint,
    ambientTintBlendMode: BlendMode = tintBlendMode,
): State<Drawable?> = produceState<Drawable?>(null, this) { value = null }

@Composable fun PhotoImageComplicationData.rememberDrawableAsState(
    tint: Color = Color.Unspecified,
    tintBlendMode: BlendMode = BlendMode.SrcIn,
): State<Drawable?> = produceState<Drawable?>(null, this) { value = null }

@Composable fun RangedValueComplicationData.rememberDrawableAsState(
    preferSmallImage: Boolean = false,
    tint: Color = Color.Unspecified,
    tintBlendMode: BlendMode = BlendMode.SrcIn,
    ambientTint: Color = tint,
    ambientTintBlendMode: BlendMode = tintBlendMode,
): State<Drawable?> = produceState<Drawable?>(null, this) { value = null }

@Composable fun ShortTextComplicationData.rememberDrawableAsState(
    preferSmallImage: Boolean = false,
    tint: Color = Color.Unspecified,
    tintBlendMode: BlendMode = BlendMode.SrcIn,
    ambientTint: Color = tint,
    ambientTintBlendMode: BlendMode = tintBlendMode,
): State<Drawable?> = produceState<Drawable?>(null, this) { value = null }

@Composable fun SmallImageComplicationData.rememberDrawableAsState(
    tint: Color = Color.Unspecified,
    tintBlendMode: BlendMode = BlendMode.SrcIn,
    ambientTint: Color = tint,
    ambientTintBlendMode: BlendMode = tintBlendMode,
): State<Drawable?> = produceState<Drawable?>(null, this) { value = null }

@Composable fun GoalProgressComplicationData.rememberDrawableAsState(
    preferSmallImage: Boolean = false,
    tint: Color = Color.Unspecified,
    tintBlendMode: BlendMode = BlendMode.SrcIn,
    ambientTint: Color = tint,
    ambientTintBlendMode: BlendMode = tintBlendMode,
): State<Drawable?> = produceState<Drawable?>(null, this) { value = null }

@Composable fun WeightedElementsComplicationData.rememberDrawableAsState(
    preferSmallImage: Boolean = false,
    tint: Color = Color.Unspecified,
    tintBlendMode: BlendMode = BlendMode.SrcIn,
    ambientTint: Color = tint,
    ambientTintBlendMode: BlendMode = tintBlendMode,
): State<Drawable?> = produceState<Drawable?>(null, this) { value = null }

fun Drawable.setTint(color: Color) = Unit
fun Drawable.setTintBlendMode(blendMode: BlendMode) = Unit
