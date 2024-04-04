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
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import dev.phonecontrol.R
import dev.phonecontrol.ui.views.PermissionRequestDialog

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SimCardAccessDialog(
    permissionState: MultiplePermissionsState,
    shouldShowState: MutableState<Boolean>,
) {
    val pm = LocalContext.current.packageManager
    val permInfo1 = pm.getPermissionInfo(Manifest.permission.READ_PHONE_STATE, 0)
    val permInfo2 = pm.getPermissionInfo(Manifest.permission.READ_CALL_LOG, 0)
    val permLabel1 = permInfo1?.loadLabel(pm)?.toString() ?: "read you contacts"
    val permLabel2 = permInfo2?.loadLabel(pm)?.toString() ?: "read you contacts"
    val appLabel = LocalContext.current.applicationInfo.loadLabel(pm)

    PermissionRequestDialog(
        text = {
            Text(buildAnnotatedString {
                append(stringResource(R.string.sim_card_access_dialog_1, appLabel))
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(permLabel1)
                }
                append(stringResource(R.string.sim_card_access_dialog_2, appLabel))
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(permLabel2)
                }
                append(".")
            })
            if (permissionState.allPermissionsGranted) {
                Text(stringResource(R.string.sim_card_access_dialog_3))
            }
        },
        confirmButtonLabel = {
            stringResource(R.string.dialog_perm_allow)
        },
        isGranted = permissionState.allPermissionsGranted,
        onDismiss = {
            shouldShowState.value = false
        },
        onConfirm = {
            shouldShowState.value = false
            permissionState.launchMultiplePermissionRequest()
        },
    )

}

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun SimCardAccessDialogPreviewGranted() {
    SimCardAccessDialog(
        permissionState = object : MultiplePermissionsState {
            override val allPermissionsGranted: Boolean = true
            override val permissions: List<PermissionState> = emptyList()
            override val revokedPermissions: List<PermissionState> = emptyList()
            override val shouldShowRationale: Boolean = false
            override fun launchMultiplePermissionRequest() {}
        },
        shouldShowState = remember { mutableStateOf(true) },
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun SimCardAccessDialogPreviewDenied() {
    SimCardAccessDialog(
        permissionState = object : MultiplePermissionsState {
            override val allPermissionsGranted: Boolean = false
            override val permissions: List<PermissionState> = emptyList()
            override val revokedPermissions: List<PermissionState> = emptyList()
            override val shouldShowRationale: Boolean = false
            override fun launchMultiplePermissionRequest() {}
        },
        shouldShowState = remember { mutableStateOf(true) },
    )
}
