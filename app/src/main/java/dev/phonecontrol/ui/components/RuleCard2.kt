package dev.phonecontrol.ui.components

import android.telephony.SubscriptionInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.phonecontrol.R
import dev.phonecontrol.data.BlockingRule
import dev.phonecontrol.misc.conditional
import dev.phonecontrol.ui.assets.simCardImageVector
import java.util.UUID

@Composable
fun RuleCard2(
    rule: BlockingRule,
    modifier: Modifier = Modifier,
    onCheckedChange: (checked: Boolean) -> Unit,
    onClick: () -> Unit,
    subscription: SubscriptionInfo?,
) {
    ElevatedCard(
        enabled = rule.enabled,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    FilterChip(
                        selected = true,
                        enabled = rule.enabled,
                        onClick = { },
                        leadingIcon = {
                            val drawableId = when (rule.action) {
                                BlockingRule.Action.SILENCE -> R.drawable.ic_silent
                                BlockingRule.Action.BLOCK -> R.drawable.ic_telephone_call
                                BlockingRule.Action.REJECT -> R.drawable.ic_hang_up
                            }
                            Icon(
                                painter = painterResource(id = drawableId),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(AssistChipDefaults.IconSize)
                                    .conditional(!rule.enabled) {
                                        alpha(0.38f)
                                    }
                            )
                        },
                        label = {
                            val stringId = when (rule.action) {
                                BlockingRule.Action.SILENCE -> R.string.rule_action_silence
                                BlockingRule.Action.BLOCK -> R.string.rule_action_ignore
                                BlockingRule.Action.REJECT -> R.string.rule_action_decline
                            }
                            Text(stringResource(stringId), maxLines = 1)
                        },
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        stringResource(id = R.string.incoming_calls_from),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 2.dp),
                    )
                }
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    FilterChip(
                        selected = true,
                        enabled = rule.enabled,
                        onClick = { },
                        label = {
                            val stringId = when (rule.target) {
                                BlockingRule.Target.EVERYONE -> R.string.everyone
                                BlockingRule.Target.NON_CONTACTS -> R.string.everyone_except_contacts
                            }
                            Text(stringResource(stringId), maxLines = 1)
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Text(
                        stringResource(id = R.string.rule_card_on),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    FilterChip(
                        selected = true,
                        enabled = rule.enabled,
                        onClick = { },
                        leadingIcon = {
                            val tint = if (rule.cardId == null) {
                                Color(0xFFe6e6e6)
                            } else if (subscription == null) {
                                Color.Black
                            } else (
                                    Color(subscription.iconTint)
                                    )
                            Icon(
                                imageVector = simCardImageVector(innerColor = tint),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(AssistChipDefaults.IconSize)
                                    .conditional(!rule.enabled) {
                                        alpha(0.38f)
                                    }
                            )
                        },
                        label = {
                            // TODO localization
                            Text(
                                text = if (rule.cardId == null) {
                                    "Any SIM card"
                                } else {
                                    rule.cardName ?: "Unknown SIM card"
                                },
                                maxLines = 1,
                            )
                        },
                    )
                }
            }
            VerticalDivider(modifier = Modifier
                .padding(vertical = 8.dp)
                .alpha(0.38f))
            Switch(
                checked = rule.enabled,
                onCheckedChange = onCheckedChange,
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .align(Alignment.CenterVertically),
                thumbContent = if (rule.enabled) {
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_checkmark),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else {
                    null
                }
            )
        }
    }
}

@Preview
@Composable
private fun RuleCard2Preview() {
    RuleCard2(
        BlockingRule(
            uuid = UUID.randomUUID(),
            enabled = true,
            action = BlockingRule.Action.BLOCK,
            target = BlockingRule.Target.NON_CONTACTS,
            cardId = null,
            cardName = null,
            position = 0,
        ),
        onCheckedChange = {},
        onClick = {},
        subscription = null,
    )
}

@Preview
@Composable
private fun RuleCard2Preview2() {
    RuleCard2(
        BlockingRule(
            uuid = UUID.randomUUID(),
            enabled = true,
            action = BlockingRule.Action.SILENCE,
            target = BlockingRule.Target.EVERYONE,
            cardId = 1,
            cardName = "SIM1",
            position = 0,
        ),
        onCheckedChange = {},
        onClick = {},
        subscription = null,
    )
}
