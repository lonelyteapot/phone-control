package dev.phonecontrol.ui.components.permissiondialog

import android.app.role.RoleManager
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import dev.phonecontrol.R
import dev.phonecontrol.misc.role.RoleState
import dev.phonecontrol.ui.views.PermissionRequestDialog

@Composable
fun CallScreeningRoleDialog(
    roleState: RoleState,
    shouldShowState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val pm = context.packageManager
    val appLabel = context.applicationInfo.loadLabel(pm)

    PermissionRequestDialog(
        text = {
            Text(buildAnnotatedString {
                append(stringResource(R.string.call_screening_role_dialog_1, appLabel))
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(stringResource(R.string.call_screening_role_dialog_3))
                }
                append(".")
            })
            Text(stringResource(R.string.call_screening_role_dialog_2))
//            if (!roleState.status.isHeld) {
//                Text(buildAnnotatedString {
//                    append(stringResource(R.string.dialog_call_screening_role_desc_action1))
//                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
//                        append(stringResource(R.string.set_as_default_app))
////                        append(stringResource(RoleManager.ROLE_CALL_SCREENING))
//                    }
//                    append(stringResource(R.string.dialog_call_screening_role_desc_action2))
//                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
//                        append(stringResource(context.applicationInfo.labelRes))
//                    }
//                    append(stringResource(R.string.dialog_call_screening_role_desc_action3))
//                })
//            }
        },
        confirmButtonLabel = {
            stringResource(R.string.set_as_default_app)
        },
        onDismiss = {
            shouldShowState.value = false
        },
        onConfirm = {
            shouldShowState.value = false
            roleState.launchRoleRequest()
        },
        isGranted = roleState.status.isHeld,
        modifier = modifier,
    )
}