package dev.phonecontrol.misc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds


val DEFAULT_RETRY_THRESHOLD = 1.seconds

@ExperimentalPermissionsApi
@Composable
public fun rememberPermissionStateWithRetryTimer(
    permission: String,
    retryThreshold: Duration = DEFAULT_RETRY_THRESHOLD,
): PermissionState {
    val context = LocalContext.current
    val timerState = remember {
        MutableTimerState(threshold = retryThreshold, onFailedFasterThanThreshold = {
            context.findActivity().openAppSettings()
        })
    }
    val baseState = rememberPermissionState(
        permission = permission,
        onPermissionResult = { isGranted ->
            timerState.onPermissionResult(isGranted)
        },
    )
    return PermissionStateWithTimer(baseState, onLaunchRequest = {
        timerState.onLaunchRequest()
    })
}

@ExperimentalPermissionsApi
@Composable
public fun rememberMultiplePermissionsStateWithRetryTimer(
    permissions: List<String>,
    retryThreshold: Duration = DEFAULT_RETRY_THRESHOLD,
): MultiplePermissionsState {
    val context = LocalContext.current
    val timerState = remember {
        MutableTimerState(threshold = retryThreshold, onFailedFasterThanThreshold = {
            context.findActivity().openAppSettings()
        })
    }
    val baseState = rememberMultiplePermissionsState(
        permissions = permissions,
        onPermissionsResult = { isGrantedMap ->
            val allGranted = isGrantedMap.values.all { it }
            timerState.onPermissionResult(allGranted)
        },
    )
    return MultiplePermissionsStateWithTimer(baseState, onLaunchRequest = {
        timerState.onLaunchRequest()
    })
}

@ExperimentalPermissionsApi
@Stable
public class PermissionStateWithTimer(
    private val base: PermissionState,
    private val onLaunchRequest: () -> Unit,
) : PermissionState by base {
    public override fun launchPermissionRequest(): Unit {
        onLaunchRequest()
        return base.launchPermissionRequest()
    }
}

@ExperimentalPermissionsApi
@Stable
public class MultiplePermissionsStateWithTimer(
    private val base: MultiplePermissionsState,
    private val onLaunchRequest: () -> Unit,
) : MultiplePermissionsState by base {
    override fun launchMultiplePermissionRequest() {
        onLaunchRequest()
        return base.launchMultiplePermissionRequest()
    }
}

@Stable
private class MutableTimerState(
    val threshold: Duration,
    private val onFailedFasterThanThreshold: () -> Unit,
) {
    private var launchCallTime: Long? = null

    fun onLaunchRequest() {
        launchCallTime = System.nanoTime()
    }

    fun onPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            return
        }
        val launchCallTime = launchCallTime ?: return
        val delta = (System.nanoTime() - launchCallTime).nanoseconds
        if (delta <= threshold) {
            onFailedFasterThanThreshold()
        }
    }
}
