package dev.phonecontrol.ui.components.permissiondialog

import android.Manifest
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import dev.phonecontrol.R
import dev.phonecontrol.ui.views.PermissionRequestDialog

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactsAccessDialog(permissionState: PermissionState, shouldShowState: MutableState<Boolean>) {
    val context = LocalContext.current
    val pm = context.packageManager
    val permInfo = pm.getPermissionInfo(Manifest.permission.READ_CONTACTS, 0)
    val permLabel = permInfo?.loadLabel(pm)?.toString() ?: "read your contacts"
    val appLabel = context.applicationInfo.loadLabel(pm)

    PermissionRequestDialog(
        text = {
            Text(buildAnnotatedString {
                append(stringResource(R.string.contacts_access_dialog_1, appLabel))
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(permLabel)
                }
                append(".")
            })
            if (permissionState.status.isGranted) {
                Text(stringResource(R.string.contacts_access_dialog_2))
            }
        },
        confirmButtonLabel = {
            stringResource(R.string.dialog_perm_allow)
        },
        onDismiss = {
            shouldShowState.value = false
        },
        onConfirm = {
            shouldShowState.value = false
            permissionState.launchPermissionRequest()
        },
        isGranted = permissionState.status.isGranted,
    )

}

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun ContactsAccessDialogPreviewGranted() {
    ContactsAccessDialog(
        permissionState = object : PermissionState {
            override val permission = Manifest.permission.READ_CONTACTS
            override val status = PermissionStatus.Granted
            override fun launchPermissionRequest() {}
        },
        shouldShowState = remember { mutableStateOf(true) },
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun ContactsAccessDialogPreviewDenied() {
    ContactsAccessDialog(
        permissionState = object : PermissionState {
            override val permission = Manifest.permission.READ_CONTACTS
            override val status = PermissionStatus.Denied(false)
            override fun launchPermissionRequest() {}
        },
        shouldShowState = remember { mutableStateOf(true) },
    )
}
