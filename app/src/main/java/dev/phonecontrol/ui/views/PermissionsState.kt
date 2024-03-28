package dev.phonecontrol.ui.views

data class PermissionsState(
    val callScreeningSupported: Boolean,
    val hasCallScreeningRole: Boolean,
    val hasReadContactsPermission: Boolean,
    val hasReadPhoneStatePermission: Boolean,
    val hasReadCallLogPermission: Boolean,
) {
    companion object {
        fun empty() = PermissionsState(
            callScreeningSupported = false,
            hasCallScreeningRole = false,
            hasReadContactsPermission = false,
            hasReadPhoneStatePermission = false,
            hasReadCallLogPermission = false,
        )
    }
}