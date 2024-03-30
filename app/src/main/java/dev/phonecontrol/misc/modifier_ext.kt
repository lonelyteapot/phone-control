package dev.phonecontrol.misc

import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

fun Modifier.gesturesDisabled(): Modifier {
    return this.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                awaitPointerEvent(pass = PointerEventPass.Initial).changes.forEach(
                    PointerInputChange::consume
                )
            }
        }
    }
}

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        this.then(modifier(Modifier))
    } else {
        this
    }
}

val blurSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

fun Modifier.blurredUnavailable(): Modifier {
    return gesturesDisabled().then(
        if (blurSupported) {
            blur(radius = 8.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
        } else {
            alpha(0.1f)
        }
    )
}
