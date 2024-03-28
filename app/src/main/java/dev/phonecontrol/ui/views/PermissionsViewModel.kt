package dev.phonecontrol.ui.views

import android.Manifest.permission.READ_CALL_LOG
import android.Manifest.permission.READ_CONTACTS
import android.Manifest.permission.READ_PHONE_STATE
import android.app.Application
import android.app.role.RoleManager
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import dev.phonecontrol.misc.hasPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class PermissionsViewModel(private val application: Application) : ViewModel() {
    val stateFlow = MutableStateFlow(checkAllPermissions())

    private fun checkAllPermissions(): PermissionsState {
        val roleManager = application.getSystemService<RoleManager>()
        return PermissionsState(
            callScreeningSupported = roleManager?.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) == true,
            hasCallScreeningRole = roleManager?.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) == true,
            hasReadContactsPermission = application.hasPermission(READ_CONTACTS),
            hasReadPhoneStatePermission = application.hasPermission(READ_PHONE_STATE),
            hasReadCallLogPermission = application.hasPermission(READ_CALL_LOG),
        )
    }

    fun refreshPermissionsState() {
        val newState = checkAllPermissions()
        stateFlow.update { newState }
    }
}
