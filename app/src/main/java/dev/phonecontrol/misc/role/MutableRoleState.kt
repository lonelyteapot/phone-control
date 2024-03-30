package dev.phonecontrol.misc.role


import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService

@Composable
internal fun rememberMutableRoleState(
    roleName: String,
    onRoleResult: (Boolean) -> Unit = {},
): MutableRoleState {
    val context = LocalContext.current
    val roleState = remember(roleName) {
        MutableRoleState(roleName, context)
    }

    // Refresh the role status when the lifecycle is resumed
    RoleLifecycleCheckerEffect(roleState)

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            roleState.refreshRoleStatus()
            onRoleResult(it.resultCode == Activity.RESULT_OK)
        }
    DisposableEffect(roleState, launcher) {
        roleState.launcher = launcher
        onDispose {
            roleState.launcher = null
        }
    }

    return roleState
}

@Stable
internal class MutableRoleState(
    override val roleName: String,
    private val context: Context,
) : RoleState {

    override var status: RoleStatus by mutableStateOf(getRoleStatus())

    override fun launchRoleRequest() {
        val roleManager = context.getSystemService<RoleManager>()
        val intent = roleManager?.createRequestRoleIntent(roleName)
        launcher?.launch(
            intent
        ) ?: throw IllegalStateException("ActivityResultLauncher cannot be null")
    }

    internal var launcher: ActivityResultLauncher<Intent>? = null

    internal fun refreshRoleStatus() {
        status = getRoleStatus()
    }

    private fun getRoleStatus(): RoleStatus {
        val roleManager = context.getSystemService<RoleManager>()
        return RoleStatus(
            isAvailable = roleManager?.isRoleAvailable(roleName) == true,
            isHeld = roleManager?.isRoleHeld(roleName) == true,
        )
    }
}
