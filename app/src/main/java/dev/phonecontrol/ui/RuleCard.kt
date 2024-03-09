package dev.phonecontrol.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import dev.phonecontrol.R
import dev.phonecontrol.data.BlockingRule
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuleCard(
    rule: BlockingRule,
    modifier: Modifier = Modifier,
    deleteRule: () -> Unit,
    updateRule: (newRule: BlockingRule) -> Unit,
    onEveryoneDisabledClick: () -> Unit,
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
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
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null, // TODO
                modifier = Modifier
                    .clickable(onClick = deleteRule)
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            )
            Switch(checked = rule.enabled, onCheckedChange = {
                updateRule(rule.copy(enabled = !rule.enabled))
            })
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
                colors = if (rule.enabled) {
                    SegmentedButtonDefaults.colors()
                } else {
                    SegmentedButtonDefaults.colors(
                        activeContainerColor = SegmentedButtonDefaults.colors().disabledActiveContainerColor,
                        activeContentColor = SegmentedButtonDefaults.colors().disabledActiveContentColor,
                        activeBorderColor = SegmentedButtonDefaults.colors().disabledActiveBorderColor,
                        inactiveContainerColor = SegmentedButtonDefaults.colors().disabledInactiveContainerColor,
                        inactiveContentColor = SegmentedButtonDefaults.colors().disabledActiveContentColor, // note
                        inactiveBorderColor = SegmentedButtonDefaults.colors().disabledActiveBorderColor, // note
                    )
                },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
            ) {
                Text(stringResource(R.string.rule_action_silence))
            }
            SegmentedButton(
                selected = rule.action == BlockingRule.Action.BLOCK,
                onClick = {
                    updateRule(rule.copy(action = BlockingRule.Action.BLOCK))
                },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                colors = if (rule.enabled) {
                    SegmentedButtonDefaults.colors()
                } else {
                    SegmentedButtonDefaults.colors(
                        activeContainerColor = SegmentedButtonDefaults.colors().disabledActiveContainerColor,
                        activeContentColor = SegmentedButtonDefaults.colors().disabledActiveContentColor,
                        activeBorderColor = SegmentedButtonDefaults.colors().disabledActiveBorderColor,
                        inactiveContainerColor = SegmentedButtonDefaults.colors().disabledInactiveContainerColor,
                        inactiveContentColor = SegmentedButtonDefaults.colors().disabledActiveContentColor, // note
                        inactiveBorderColor = SegmentedButtonDefaults.colors().disabledActiveBorderColor, // note
                    )
                },
            ) {
                Text(stringResource(R.string.rule_action_ignore))
            }
            SegmentedButton(
                selected = rule.action == BlockingRule.Action.REJECT,
                onClick = {
                    updateRule(rule.copy(action = BlockingRule.Action.REJECT))
                },
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                colors = if (rule.enabled) {
                    SegmentedButtonDefaults.colors()
                } else {
                    SegmentedButtonDefaults.colors(
                        activeContainerColor = SegmentedButtonDefaults.colors().disabledActiveContainerColor,
                        activeContentColor = SegmentedButtonDefaults.colors().disabledActiveContentColor,
                        activeBorderColor = SegmentedButtonDefaults.colors().disabledActiveBorderColor,
                        inactiveContainerColor = SegmentedButtonDefaults.colors().disabledInactiveContainerColor,
                        inactiveContentColor = SegmentedButtonDefaults.colors().disabledActiveContentColor, // note
                        inactiveBorderColor = SegmentedButtonDefaults.colors().disabledActiveBorderColor, // note
                    )
                },
            ) {
                Text(stringResource(R.string.rule_action_decline))
            }
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.incoming_calls_from),
            textAlign = TextAlign.Center
        )

        val hasContactsPermission = ContextCompat.checkSelfPermission(
            LocalContext.current, Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

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
        Spacer(Modifier.height(4.dp))
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
            position = 0,
        ),
        deleteRule = {},
        updateRule = {},
        onEveryoneDisabledClick = {},
    )
}
