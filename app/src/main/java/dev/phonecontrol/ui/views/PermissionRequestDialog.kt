package dev.phonecontrol.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.phonecontrol.R


@Composable
fun PermissionRequestDialog(
    text: @Composable (() -> Unit),
    confirmButtonLabel: @Composable () -> String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isGranted: Boolean,
    modifier: Modifier = Modifier,
) {
    val showDismissButton = !isGranted

    val titleText = if (isGranted) {
        stringResource(R.string.permissions_are_granted)
    } else {
        stringResource(R.string.permissions_required)
    }

    val confirmButtonText = if (isGranted) {
        stringResource(R.string.ok)
    } else {
        confirmButtonLabel()
    }

    AlertDialog(
        title = {
            Text(titleText)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                text()
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(confirmButtonText)
            }
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            if (showDismissButton) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.not_now))
                }
            }
        },
        modifier = modifier,
    )
}
