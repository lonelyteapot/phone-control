package dev.phonecontrol.ui.components

import android.Manifest.permission.READ_CONTACTS
import android.annotation.SuppressLint
import android.telephony.SubscriptionInfo
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import dev.phonecontrol.R
import dev.phonecontrol.domain.model.CallBlockingRule
import dev.phonecontrol.misc.conditional
import dev.phonecontrol.misc.hasPermission
import dev.phonecontrol.ui.assets.simCardImageVector
import java.util.UUID

private const val ANIMATION_DURATION_MS = 300

val ruleCardShape = RoundedCornerShape(16.dp)

private fun <T> slideLeftTransitionSpec(): AnimatedContentTransitionScope<T>.() -> ContentTransform {
    return {
        slideInHorizontally(tween(ANIMATION_DURATION_MS)) { width -> width } + fadeIn(
            tween(
                ANIMATION_DURATION_MS
            )
        ) togetherWith slideOutHorizontally(tween(ANIMATION_DURATION_MS)) { width -> -width } + fadeOut(
            tween(ANIMATION_DURATION_MS)
        ) using SizeTransform(clip = false)
    }
}

@Composable
fun RuleCard2(
    rule: CallBlockingRule,
    modifier: Modifier = Modifier,
    onUpdateRule: (newRule: CallBlockingRule) -> Unit,
    onDeleteClick: () -> Unit,
    onNoContactsPermission: () -> Unit,
    onNoSimCardAccess: () -> Unit,
    subscription: SubscriptionInfo?,
    subscriptionList: List<SubscriptionInfo>,
    canAccessSimCards: Boolean,
) {
    val context = LocalContext.current
    val isRuleWorking = rule.enabled && (rule.cardId == null || canAccessSimCards)

    fun cycleRuleAction() {
        val action = when (rule.action) {
            CallBlockingRule.Action.SILENCE -> CallBlockingRule.Action.BLOCK
            CallBlockingRule.Action.BLOCK -> CallBlockingRule.Action.REJECT
            CallBlockingRule.Action.REJECT -> CallBlockingRule.Action.SILENCE
        }
        onUpdateRule(rule.copy(action = action))
    }

    fun cycleRuleTarget() {
        val target = when (rule.target) {
            CallBlockingRule.Target.EVERYONE -> CallBlockingRule.Target.NON_CONTACTS
            CallBlockingRule.Target.NON_CONTACTS -> {
                if (!context.hasPermission(READ_CONTACTS)) {
                    onNoContactsPermission()
                    return
                }
                CallBlockingRule.Target.EVERYONE
            }
        }
        onUpdateRule(rule.copy(target = target))
    }

    fun cycleSimCard() {
        val newSubscription = if (rule.cardId == null) {
            subscriptionList.firstOrNull()
        } else {
            val prevIdx = subscriptionList.indexOfFirst {
                it.cardId == rule.cardId
            }
            if (prevIdx < 0 || prevIdx + 1 >= subscriptionList.size) {
                null
            } else {
                subscriptionList[prevIdx + 1]
            }
        }
        onUpdateRule(
            rule.copy(
                cardId = newSubscription?.cardId,
                cardName = newSubscription?.displayName?.toString(),
            )
        )
    }

    ElevatedCard(
        colors = elevatedCardColors(enabled = rule.enabled),
        shape = ruleCardShape,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    RuleActionChip(
                        action = rule.action,
                        enabled = rule.enabled,
                        onClick = { cycleRuleAction() },
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        stringResource(id = R.string.incoming_calls_from),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 8.dp, end = 2.dp)
                    )
                }
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    RuleTargetChip(
                        target = rule.target,
                        enabled = rule.enabled,
                        onClick = { cycleRuleTarget() },
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        stringResource(id = R.string.rule_card_on),
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .padding(start = 2.dp, end = 8.dp),
                    )
                    RuleSimCardChip(
                        cardId = rule.cardId,
                        cardName = rule.cardName,
                        iconTint = subscription?.iconTint,
                        enabled = rule.enabled,
                        onClick = {
                            if (canAccessSimCards) {
                                cycleSimCard()
                            } else {
                                onNoSimCardAccess()
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            VerticalDivider(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .alpha(0.38f)
            )
            ConstraintLayout(modifier = Modifier.fillMaxHeight()) {
                val (switch, deleteButton) = createRefs()
                Switch(
                    checked = rule.enabled,
                    onCheckedChange = { checked ->
                        onUpdateRule(rule.copy(enabled = checked))
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .constrainAs(switch) {
                            centerTo(parent)
                        },
                    thumbContent = if (rule.enabled) {
                        {
                            Icon(
                                painter = painterResource(
                                    if (isRuleWorking) {
                                        R.drawable.ic_checkmark
                                    } else {
                                        R.drawable.ic_close
                                    }
                                ),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else {
                        null
                    },
                )
                IconButton(
                    modifier = Modifier.constrainAs(deleteButton) {
                        centerHorizontallyTo(parent)
                        top.linkTo(switch.bottom)
                        bottom.linkTo(parent.bottom)
                    },
                    onClick = onDeleteClick,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_trash),
                        contentDescription = null,
                        tint = LocalContentColor.current,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                }
            }
        }
    }
}

@Composable
private fun elevatedCardColors(enabled: Boolean): CardColors {
    val colors = CardDefaults.elevatedCardColors()
    return if (enabled) {
        colors
    } else {
        colors.copy(
            containerColor = colors.disabledContainerColor,
            contentColor = colors.disabledContentColor,
        )
    }
}

@Composable
private fun RuleActionChip(
    action: CallBlockingRule.Action,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val labelId = when (action) {
        CallBlockingRule.Action.SILENCE -> R.string.rule_action_silence
        CallBlockingRule.Action.BLOCK -> R.string.rule_action_ignore
        CallBlockingRule.Action.REJECT -> R.string.rule_action_decline
    }
    val iconId = when (action) {
        CallBlockingRule.Action.SILENCE -> R.drawable.ic_silent
        CallBlockingRule.Action.BLOCK -> R.drawable.ic_telephone_call
        CallBlockingRule.Action.REJECT -> R.drawable.ic_hang_up
    }

    FilterChip(
        modifier = modifier,
        selected = true,
        enabled = enabled,
        onClick = onClick,
        leadingIcon = {
            AnimatedContent(
                targetState = iconId,
                label = "AnimatedIcon",
                transitionSpec = slideLeftTransitionSpec(),
            ) { targetId ->
                Icon(
                    painter = painterResource(targetId),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(AssistChipDefaults.IconSize)
                        .conditional(!enabled) {
                            alpha(0.38f)
                        },
                )
            }
        },
        label = {
            AnimatedContent(
                targetState = labelId,
                label = "AnimatedText",
                transitionSpec = slideLeftTransitionSpec()
            ) { targetId ->
                Text(text = stringResource(targetId), maxLines = 1)
            }
        },
    )
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
private fun RuleTargetChip(
    target: CallBlockingRule.Target,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val labelId = when (target) {
        CallBlockingRule.Target.EVERYONE -> R.string.everyone
        CallBlockingRule.Target.NON_CONTACTS -> R.string.everyone_except_contacts
    }
    val strikethrough =
        target == CallBlockingRule.Target.EVERYONE && !LocalContext.current.hasPermission(
            READ_CONTACTS
        )

    FilterChip(
        modifier = modifier,
        selected = true,
        enabled = enabled,
        onClick = onClick,
        leadingIcon = {
            AnimatedContent(
                targetState = labelId,
                label = "AnimatedIcon",
                transitionSpec = slideLeftTransitionSpec(),
            ) { _ ->
                Icon(
                    painter = painterResource(R.drawable.ic_person),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(AssistChipDefaults.IconSize)
                        .conditional(!enabled) {
                            alpha(0.38f)
                        },
                )
            }
        },
        label = {
            AnimatedContent(
                targetState = labelId,
                label = "AnimatedText",
                transitionSpec = slideLeftTransitionSpec(),
            ) { targetId ->
                Text(
                    stringResource(targetId),
                    maxLines = 1,
                    textDecoration = if (strikethrough) TextDecoration.LineThrough else null,
                )
            }
        },
    )
}

@Composable
private fun RuleSimCardChip(
    cardId: Int?,
    cardName: String?,
    iconTint: Int?,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val labelText = if (cardId == null) {
        stringResource(R.string.all_sim_cards)
    } else {
        cardName ?: stringResource(R.string.unknown_sim_card)
    }
    val finalIconTint = if (cardId == null) {
        Color(0xFFe6e6e6)
    } else if (iconTint == null) {
        Color.Black
    } else {
        Color(iconTint)
    }
    FilterChip(
        modifier = Modifier.then(modifier),
        selected = true,
        enabled = enabled,
        onClick = onClick,
        leadingIcon = {
            AnimatedContent(
                // TODO This isn't right
                targetState = finalIconTint,
                label = "AnimatedIcon",
                transitionSpec = slideLeftTransitionSpec(),
            ) { targetTint ->
                Icon(
                    imageVector = simCardImageVector(innerColor = targetTint),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(AssistChipDefaults.IconSize)
                        .conditional(!enabled) {
                            alpha(0.38f)
                        },
                )
            }
        },
        label = {
            AnimatedContent(
                targetState = labelText,
                label = "AnimatedText",
                transitionSpec = slideLeftTransitionSpec(),
            ) { targetText ->
                Text(text = targetText, maxLines = 1)
            }
        },
    )
}

@Preview
@Composable
private fun RuleCard2Preview() {
    RuleCard2(
        CallBlockingRule(
            uuid = UUID.randomUUID(),
            enabled = true,
            action = CallBlockingRule.Action.BLOCK,
            target = CallBlockingRule.Target.NON_CONTACTS,
            cardId = null,
            cardName = null,
            position = 0,
        ),
        onUpdateRule = {},
        onDeleteClick = {},
        onNoContactsPermission = {},
        onNoSimCardAccess = {},
        subscription = null,
        subscriptionList = emptyList(),
        canAccessSimCards = true,
    )
}

@Preview
@Composable
private fun RuleCard2Preview2() {
    RuleCard2(
        CallBlockingRule(
            uuid = UUID.randomUUID(),
            enabled = true,
            action = CallBlockingRule.Action.SILENCE,
            target = CallBlockingRule.Target.EVERYONE,
            cardId = 1,
            cardName = "SIM1",
            position = 0,
        ),
        onUpdateRule = {},
        onDeleteClick = {},
        onNoContactsPermission = {},
        onNoSimCardAccess = {},
        subscription = null,
        subscriptionList = emptyList(),
        canAccessSimCards = true,
    )
}
