package dev.phonecontrol.service.logic

import android.Manifest.permission.READ_CONTACTS
import android.annotation.SuppressLint
import android.content.Context
import android.telecom.Call
import android.telecom.CallScreeningService
import dev.phonecontrol.domain.model.CallBlockingRule
import dev.phonecontrol.data.datastore.UserPreferencesRepository
import dev.phonecontrol.data.datastore.dataStore
import dev.phonecontrol.domain.model.CallInfo
import dev.phonecontrol.misc.hasPermission
import dev.phonecontrol.misc.logi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class CallProcessor(
    private val applicationContext: Context,
) {
    private val userPreferencesRepository = UserPreferencesRepository(applicationContext.dataStore)

    suspend fun processCall(callInfo: CallInfo): CallScreeningService.CallResponse.Builder {
        val response = CallScreeningService.CallResponse.Builder()

        if (callInfo.callDirection != Call.Details.DIRECTION_INCOMING) {
            logi("Call direction is not incoming, ignoring")
            return response
        }

        val contactChecker = if (applicationContext.hasPermission(READ_CONTACTS)) {
            ContactChecker(applicationContext, callInfo.phoneNumber)
        } else {
            null
        }
        val simChecker = SimChecker(applicationContext, callInfo.phoneNumber)

        val ruleList = runBlocking {
            userPreferencesRepository.ruleListFlow.first()
        }

        ruleList
            .filter { rule -> rule.enabled }
            .filter { rule -> simMatchesRule(rule, callInfo, simChecker) }
            .filter { rule -> targetMatchesRule(rule, callInfo, contactChecker) }
            .forEach { rule ->
                when (rule.action) {
                    CallBlockingRule.Action.SILENCE -> {
                        response.setSilenceCall(true)
                    }

                    CallBlockingRule.Action.BLOCK -> {
                        response.setDisallowCall(true)
                    }

                    CallBlockingRule.Action.REJECT -> {
                        response.setDisallowCall(true)
                        response.setRejectCall(true)
                    }
                }
            }
        return response
    }

    private fun targetMatchesRule(rule: CallBlockingRule, callInfo: CallInfo, contactChecker: ContactChecker?): Boolean {
        return when (rule.target) {
            CallBlockingRule.Target.EVERYONE -> true
            CallBlockingRule.Target.NON_CONTACTS -> {
                // contactChecker is only null when our app doesn't have permission to read contacts.
                // In that case, all calls sent to onScreenCall are guaranteed by Android to NOT be from contacts.
                !(contactChecker?.isNumberInContacts ?: false)
            }
        }
    }

    // TODO permission check
    @SuppressLint("MissingPermission")
    private suspend fun simMatchesRule(rule: CallBlockingRule, callInfo: CallInfo, simChecker: SimChecker): Boolean {
        if (rule.cardId == null) {
            return true
        }
        // TODO: check for permissions to avoid timeout
        val subscriptionInfo = simChecker.getCachedSubscriptionInfo()
            ?: return false // if null, failed to detect the sim card
        return subscriptionInfo.cardId == rule.cardId
    }

}
