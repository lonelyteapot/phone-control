package dev.phonecontrol.misc.role

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Composable
public fun rememberRoleState(
    roleName: String,
    onRoleResult: (Boolean) -> Unit = {},
): RoleState {
    return rememberMutableRoleState(roleName, onRoleResult)
}

@Stable
public interface RoleState {
    public val roleName: String

    public val status: RoleStatus

    public fun launchRoleRequest(): Unit
}