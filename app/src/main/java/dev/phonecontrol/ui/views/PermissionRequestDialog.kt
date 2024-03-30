package dev.phonecontrol.ui.views

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.phonecontrol.R


@Composable
fun PermissionRequestDialog(
    title: @Composable (() -> Unit),
    text: @Composable (() -> Unit),
    confirmButtonText: String,
    showDismissButton: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(confirmButtonText)
            }
        },
        dismissButton = if (showDismissButton) {
            {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.not_now))
                }
            }
        } else null,
        title = title,
        text = text,
        modifier = modifier,
    )
}
