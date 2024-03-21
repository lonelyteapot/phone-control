package dev.phonecontrol.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.phonecontrol.R

@Composable
fun CustomButton1(
    modifier: Modifier,
    text: String,
    checked: Boolean,
    onClick: () -> Unit,
    noIcon: Boolean = false
) {
    Column(
        modifier = modifier,
    ) {
        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f),
            shape = MaterialTheme.shapes.extraLarge,
            onClick = onClick,
        ) {
            if (!noIcon) Icon(
                painterResource(
                    id = if (checked) {
                        R.drawable.ic_checkmark
                    } else {
                        R.drawable.ic_close
                    }
                ),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedContent(
            targetState = text,
            label = "AnimatedText",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
        ) {targetText ->
            Text(
                text = targetText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}