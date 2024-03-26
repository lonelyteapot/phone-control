package dev.phonecontrol.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.telecom.Call
import android.telecom.CallScreeningService
import androidx.core.content.ContextCompat
import dev.phonecontrol.data.BlockingRule
import dev.phonecontrol.data.UserPreferencesRepository
import dev.phonecontrol.data.dataStore
import dev.phonecontrol.misc.logi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class CallProcessor(
    private val applicationContext: Context,
) {
    private val userPreferencesRepository = UserPreferencesRepository(applicationContext.dataStore)

    suspend fun processCall(callInfo: MyCallInfo): CallScreeningService.CallResponse.Builder {
        val response = CallScreeningService.CallResponse.Builder()

        if (callInfo.callDirection != Call.Details.DIRECTION_INCOMING) {
            logi("Call direction is not incoming, ignoring")
            return response
        }

        val contactChecker = if (hasContactsPermission(applicationContext)) {
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
                    BlockingRule.Action.SILENCE -> {
                        response.setSilenceCall(true)
                    }

                    BlockingRule.Action.BLOCK -> {
                        response.setDisallowCall(true)
                    }

                    BlockingRule.Action.REJECT -> {
                        response.setDisallowCall(true)
                        response.setRejectCall(true)
                    }
                }
            }
        return response
    }

    private fun targetMatchesRule(rule: BlockingRule, callInfo: MyCallInfo, contactChecker: ContactChecker?): Boolean {
        return when (rule.target) {
            BlockingRule.Target.EVERYONE -> true
            BlockingRule.Target.NON_CONTACTS -> {
                // contactChecker is only null when our app doesn't have permission to read contacts.
                // In that case, all calls sent to onScreenCall are guaranteed by Android to NOT be from contacts.
                !(contactChecker?.isNumberInContacts ?: false)
            }
        }
    }

    // TODO permission check
    @SuppressLint("MissingPermission")
    private suspend fun simMatchesRule(rule: BlockingRule, callInfo: MyCallInfo, simChecker: SimChecker): Boolean {
        if (rule.cardId == null) {
            return true
        }
        // TODO: check for permissions to avoid timeout
        val subscriptionInfo = simChecker.getCachedSubscriptionInfo()
            ?: return false // if null, failed to detect the sim card
        return subscriptionInfo.cardId == rule.cardId
    }

}

private fun hasContactsPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED
}
