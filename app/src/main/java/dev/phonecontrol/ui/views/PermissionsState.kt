package dev.phonecontrol.ui.views

data class PermissionsState(
    val hasCallScreeningRole: Boolean,
    val hasReadContactsPermission: Boolean,
    val hasReadPhoneStatePermission: Boolean,
) {
    companion object {
        fun empty() = PermissionsState(
            hasCallScreeningRole = false,
            hasReadContactsPermission = false,
            hasReadPhoneStatePermission = false,
        )
    }
}