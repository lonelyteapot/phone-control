package dev.phonecontrol.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.telephony.SubscriptionInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import dev.phonecontrol.R
import dev.phonecontrol.data.BlockingRule
import dev.phonecontrol.misc.segmentedButtonColors
import dev.phonecontrol.ui.assets.simCardImageVector
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuleCard(
    rule: BlockingRule,
    modifier: Modifier = Modifier,
    deleteRule: () -> Unit,
    updateRule: (newRule: BlockingRule) -> Unit,
    onEveryoneDisabledClick: () -> Unit,
    onRemovedSimCardClick: () -> Unit,
    subscriptions: List<SubscriptionInfo>,
) {
    Card(
        enabled = rule.enabled,
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
    ) {
        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(
                onClick = deleteRule,
                modifier = Modifier.align(Alignment.CenterVertically),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trash),
                    contentDescription = null, // TODO
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            Switch(
                checked = rule.enabled,
                onCheckedChange = { isChecked ->
                    updateRule(rule.copy(enabled = isChecked))
                },
                thumbContent = if (rule.enabled) {{
                    Icon(
                        painter = painterResource(id = R.drawable.ic_checkmark),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }} else {
                    null
                }
            )
        }
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 0.dp, bottom = 8.dp)
                .fillMaxWidth(),
        ) {
            SegmentedButton(
                selected = rule.action == BlockingRule.Action.SILENCE,
                onClick = {
                    updateRule(rule.copy(action = BlockingRule.Action.SILENCE))
                },
                colors = segmentedButtonColors(enabled = rule.enabled),
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_silent),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = if (rule.enabled) Modifier else Modifier.alpha(0.38f),
                    )
                }
            ) {
                Text(stringResource(R.string.rule_action_silence))
            }
            SegmentedButton(
                selected = rule.action == BlockingRule.Action.BLOCK,
                onClick = {
                    updateRule(rule.copy(action = BlockingRule.Action.BLOCK))
                },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                colors = segmentedButtonColors(enabled = rule.enabled),
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_telephone_call),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = if (rule.enabled) Modifier else Modifier.alpha(0.38f),
                    )
                }
            ) {
                Text(stringResource(R.string.rule_action_ignore))
            }
            SegmentedButton(
                selected = rule.action == BlockingRule.Action.REJECT,
                onClick = {
                    updateRule(rule.copy(action = BlockingRule.Action.REJECT))
                },
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                colors = segmentedButtonColors(enabled = rule.enabled),
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_hang_up),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = if (rule.enabled) Modifier else Modifier.alpha(0.38f),
                    )
                }
            ) {
                Text(stringResource(R.string.rule_action_decline))
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.wrapContentWidth(),
                text = stringResource(R.string.incoming_calls_from),
                textAlign = TextAlign.Center
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        val hasContactsPermission = ContextCompat.checkSelfPermission(
            LocalContext.current, Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .selectable(
                    selected = rule.target == BlockingRule.Target.EVERYONE,
                    onClick = {
                        if (hasContactsPermission) {
                            updateRule(rule.copy(target = BlockingRule.Target.EVERYONE))
                        } else {
                            onEveryoneDisabledClick()
                        }
                    },
                    role = Role.RadioButton
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = rule.target == BlockingRule.Target.EVERYONE,
                enabled = hasContactsPermission,
                colors = if (!rule.enabled) {
                    RadioButtonDefaults.colors(
                        selectedColor = RadioButtonDefaults.colors().disabledSelectedColor,
                        unselectedColor = RadioButtonDefaults.colors().disabledUnselectedColor,
                    )
                } else {
                    RadioButtonDefaults.colors()
                },
                onClick = null // null recommended for accessibility with screenreaders
            )
            Text(
                text = stringResource(R.string.everyone),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .alpha(if (hasContactsPermission) 1f else 0.38f)
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(40.dp)
                .selectable(
                    selected = rule.target == BlockingRule.Target.NON_CONTACTS || (rule.target == BlockingRule.Target.EVERYONE && !hasContactsPermission),
                    onClick = {
                        updateRule(rule.copy(target = BlockingRule.Target.NON_CONTACTS))
                    },
                    role = Role.RadioButton
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = rule.target == BlockingRule.Target.NON_CONTACTS || (rule.target == BlockingRule.Target.EVERYONE && !hasContactsPermission),
                colors = if (!rule.enabled) {
                    RadioButtonDefaults.colors(
                        selectedColor = RadioButtonDefaults.colors().disabledSelectedColor,
                        unselectedColor = RadioButtonDefaults.colors().disabledUnselectedColor,
                    )
                } else {
                    RadioButtonDefaults.colors()
                },
                onClick = null // null recommended for accessibility with screenreaders
            )
            Text(
                text = stringResource(R.string.everyone_except_contacts),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.wrapContentWidth(),
                text = stringResource(R.string.rule_card_on),
                textAlign = TextAlign.Center
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))

        val isSelectedCardPresent = (rule.cardId == null) || (subscriptions.firstOrNull { subscription ->
            subscription.cardId == rule.cardId
        } != null)
        val buttonCount = subscriptions.size + 1 + if (isSelectedCardPresent) 0 else 1

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            SegmentedButton(
                selected = rule.cardId == null,
                onClick = {
                    updateRule(rule.copy(cardId = null, cardName = null))
                },
                colors = segmentedButtonColors(enabled = rule.enabled),
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = buttonCount),
                icon = {
                    Icon(
                        imageVector = simCardImageVector(),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = if (rule.enabled) Modifier else Modifier.alpha(0.38f),
                    )
                }
            ) {
                Text("All cards")
            }
            subscriptions.mapIndexed { index, subscription ->
                SegmentedButton(
                    selected = rule.cardId == subscription.cardId,
                    onClick = {
                        updateRule(rule.copy(cardId = subscription.cardId, cardName = subscription.displayName.toString()))
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index + 1,
                        count = buttonCount
                    ),
                    colors = segmentedButtonColors(enabled = rule.enabled),
                    icon = {
                        Icon(
                            imageVector = simCardImageVector(Color(subscription.iconTint)),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = if (rule.enabled) Modifier else Modifier.alpha(0.38f),
                        )
                    }
                ) {
                    Text("${subscription.displayName}")
                }
            }
            if (!isSelectedCardPresent) {
                SegmentedButton(
                    selected = true,
                    onClick = onRemovedSimCardClick,
                    colors = segmentedButtonColors(enabled = false).copy(
                        activeBorderColor = if (rule.enabled) {
                            SegmentedButtonDefaults.colors().activeBorderColor
                        } else {
                            SegmentedButtonDefaults.colors().disabledActiveBorderColor
                        },
                    ),
                    shape = SegmentedButtonDefaults.itemShape(index = buttonCount-1, count = buttonCount),
                    icon = {
                        Icon(
                            imageVector = simCardImageVector(Color.Black),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.alpha(0.38f)
                        )
                    }
                ) {
                    Text(rule.cardName ?: "removed")
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Preview
@Composable
private fun RuleCardPreview() {
    RuleCard(
        BlockingRule(
            uuid = UUID.randomUUID(),
            enabled = true,
            action = BlockingRule.Action.BLOCK,
            target = BlockingRule.Target.NON_CONTACTS,
            cardId = null,
            cardName = null,
            position = 0,
        ),
        deleteRule = {},
        updateRule = {},
        onEveryoneDisabledClick = {},
        onRemovedSimCardClick = {},
        subscriptions = emptyList(),
    )
}
