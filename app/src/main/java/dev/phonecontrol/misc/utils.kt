package dev.phonecontrol.misc

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.gesturesDisabled(disabled: Boolean = true) =
    if (disabled) {
        pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    awaitPointerEvent(pass = PointerEventPass.Initial)
                        .changes
                        .forEach(PointerInputChange::consume)
                }
            }
        }
    } else {
        this
    }

fun Modifier.conditional(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun segmentedButtonColors(enabled: Boolean): SegmentedButtonColors {
    return if (enabled) {
        SegmentedButtonDefaults.colors()
    } else {
        SegmentedButtonDefaults.colors(
            activeContainerColor = SegmentedButtonDefaults.colors().disabledActiveContainerColor,
            activeContentColor = SegmentedButtonDefaults.colors().disabledActiveContentColor,
            activeBorderColor = SegmentedButtonDefaults.colors().disabledActiveBorderColor,
            inactiveContainerColor = SegmentedButtonDefaults.colors().disabledInactiveContainerColor,
            inactiveContentColor = SegmentedButtonDefaults.colors().disabledActiveContentColor, // note
            inactiveBorderColor = SegmentedButtonDefaults.colors().disabledActiveBorderColor, // note
            disabledInactiveContentColor = SegmentedButtonDefaults.colors().disabledActiveContentColor, // note
            disabledInactiveBorderColor = SegmentedButtonDefaults.colors().disabledActiveBorderColor, // note
        )
    }
}