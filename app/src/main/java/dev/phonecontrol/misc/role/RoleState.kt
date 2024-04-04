package dev.phonecontrol.misc.role

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Composable
fun rememberRoleState(
    roleName: String,
    onRoleResult: (Boolean) -> Unit = {},
): RoleState {
    return rememberMutableRoleState(roleName, onRoleResult)
}

@Stable
interface RoleState {
    val roleName: String

    val status: RoleStatus

    fun launchRoleRequest(): Unit
}