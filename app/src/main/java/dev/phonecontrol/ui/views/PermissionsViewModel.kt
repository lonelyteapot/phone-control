package dev.phonecontrol.ui.views

import android.Manifest
import android.app.Application
import android.app.role.RoleManager
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class PermissionsViewModel(private val application: Application) : ViewModel() {
    val stateFlow = MutableStateFlow(checkAllPermissions())

    init {
        updatePermissionState()
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            application,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkAllPermissions(): PermissionsState {
        // TODO: handle cases where a device doesn't support the role
        val roleManager = application.getSystemService<RoleManager>()
        val callScreening = roleManager?.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) == true
        val readContacts = checkPermission(Manifest.permission.READ_CONTACTS)
        val readPhoneState = checkPermission(Manifest.permission.READ_PHONE_STATE)
        val readCallLog = checkPermission(Manifest.permission.READ_CALL_LOG)

        return PermissionsState(
            hasCallScreeningRole = callScreening,
            hasReadContactsPermission = readContacts,
            hasReadPhoneStatePermission = readPhoneState,
            hasReadCallLogPermission = readCallLog,
        )
    }

    fun updatePermissionState() {
        val newState = checkAllPermissions()
        stateFlow.update { newState }
    }
}
