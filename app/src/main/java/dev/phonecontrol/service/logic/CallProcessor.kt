package dev.phonecontrol.service.logic

import android.Manifest.permission.READ_CALL_LOG
import android.Manifest.permission.READ_CONTACTS
import android.Manifest.permission.READ_PHONE_STATE
import android.annotation.SuppressLint
import android.content.Context
import android.telecom.Call
import android.telecom.CallScreeningService
import dev.phonecontrol.data.datastore.UserPreferencesRepository
import dev.phonecontrol.data.datastore.dataStore
import dev.phonecontrol.domain.model.CallBlockingRule
import dev.phonecontrol.domain.model.CallInfo
import dev.phonecontrol.misc.hasPermission
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class CallProcessor(
    private val applicationContext: Context,
) {
    private val userPreferencesRepository = UserPreferencesRepository(applicationContext.dataStore)

    // TODO
    @SuppressLint("MissingPermission")
    suspend fun processCall(callInfo: CallInfo): CallScreeningService.CallResponse.Builder {
        val response = CallScreeningService.CallResponse.Builder()

        if (callInfo.callDirection != Call.Details.DIRECTION_INCOMING) {
            return response
        }

        val ruleList = runBlocking {
            userPreferencesRepository.ruleListFlow.first()
        }

        val canDetectSim =
            applicationContext.hasPermission(READ_PHONE_STATE) && applicationContext.hasPermission(
                READ_CALL_LOG
            )
        val canGetContactStatus = applicationContext.hasPermission(READ_CONTACTS)

        val enabledRules = ruleList.filter { rule -> rule.enabled }

        val simDetectionRequired = enabledRules.any { rule -> rule.cardId != null }
        val contactStatusRequired = canGetContactStatus && enabledRules.any { rule ->
            when (rule.target) {
                CallBlockingRule.Target.EVERYONE -> false
                CallBlockingRule.Target.NON_CONTACTS -> true
            }
        }

        val simCardId: Int? = if (simDetectionRequired && canDetectSim) {
            val simChecker = SimChecker(applicationContext, callInfo.phoneNumber)
            val subscription = simChecker.getCachedSubscriptionInfo()
            subscription?.cardId
        } else {
            null
        }

        val isContact: Boolean? = if (contactStatusRequired) {
            val contactChecker = ContactChecker(applicationContext, callInfo.phoneNumber)
            contactChecker.isNumberInContacts
        } else {
            null
        }

        val matchedRules = enabledRules.filter { rule ->
                rule.cardId == null || rule.cardId == simCardId
            }.filter { rule ->
                when (rule.target) {
                    CallBlockingRule.Target.EVERYONE -> true
                    CallBlockingRule.Target.NON_CONTACTS -> isContact != true
                }
            }

        matchedRules.forEach { rule ->
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
}
