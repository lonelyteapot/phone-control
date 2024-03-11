package dev.phonecontrol.data

import java.util.UUID

data class BlockingRule(
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

