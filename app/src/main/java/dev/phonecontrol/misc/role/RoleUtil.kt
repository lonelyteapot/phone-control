package dev.phonecontrol.misc.role

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Stable
data class RoleStatus(
    val isAvailable: Boolean,
    val isHeld: Boolean,
)

@Composable
internal fun RoleLifecycleCheckerEffect(
    roleState: MutableRoleState,
    lifecycleEvent: Lifecycle.Event = Lifecycle.Event.ON_RESUME
) {
    // Check if the role was granted when the lifecycle is resumed.
    // The user might've gone to the Settings screen and granted the role.
    val roleCheckedObserver = remember(roleState) {
        LifecycleEventObserver { _, event ->
            if (event == lifecycleEvent) {
                roleState.refreshRoleStatus()
            }
        }
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, roleCheckedObserver) {
        lifecycle.addObserver(roleCheckedObserver)
        onDispose { lifecycle.removeObserver(roleCheckedObserver) }
    }
}
