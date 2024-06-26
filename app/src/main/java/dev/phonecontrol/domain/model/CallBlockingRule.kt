package dev.phonecontrol.domain.model

import java.util.UUID

data class CallBlockingRule(
    val uuid: UUID,
    val enabled: Boolean,
    val action: Action,
    val target: Target,
    val cardId: Int?,
    val cardName: String?,
    val position: Int,
) {
    enum class Action {
        SILENCE, BLOCK, REJECT
    }

    enum class Target {
        EVERYONE, NON_CONTACTS
    }
}