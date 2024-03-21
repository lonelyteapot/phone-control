package dev.phonecontrol.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.telecom.Call
import android.telecom.CallScreeningService
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import dev.phonecontrol.data.BlockingRule
import dev.phonecontrol.data.UserPreferencesRepository
import dev.phonecontrol.data.dataStore
import dev.phonecontrol.misc.logi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class CallScreeningServiceImpl : CallScreeningService() {
    private lateinit var coroutineScope: CoroutineScope;

    override fun onBind(intent: Intent?): IBinder? {
        coroutineScope = MainScope()
        return super.onBind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        coroutineScope.cancel("CallScreeningService has been unbound by the system")
        return super.onUnbind(intent)
    }

    override fun onScreenCall(callDetails: Call.Details) {
        val callInfo = MyCallInfo(callDetails)
        logi("Receiving a call from/to '${callInfo.phoneNumber}'")

        coroutineScope.launch {
            val response = processCall(callInfo).build()
            logi("Responding: disallow=${response.disallowCall}, silence=${response.silenceCall}, reject=${response.rejectCall}")
            respondToCall(callDetails, response)
        }
    }

    private suspend fun processCall(callInfo: MyCallInfo): CallResponse.Builder {
        val response = CallResponse.Builder()

        if (callInfo.callDirection != Call.Details.DIRECTION_INCOMING) {
            logi("Call direction is not incoming, ignoring")
            return response;
        }

        val userPreferencesRepository = UserPreferencesRepository(applicationContext.dataStore)

        val contactChecker = if (hasContactsPermission()) {
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

    private fun hasContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }
}

class MyCallInfo(private val callDetails: Call.Details) {
    val phoneNumber: String = callDetails.handle.schemeSpecificPart

    val callDirection: Int
        get() = callDetails.callDirection

    val callerNumberVerificationStatus: Int
        @RequiresApi(Build.VERSION_CODES.R)
        get() = callDetails.callerNumberVerificationStatus

    val connectTimeMillis: Long
        get() = callDetails.connectTimeMillis

    val creationTimeMillis: Long
        get() = callDetails.creationTimeMillis

    // handle.scheme is always PhoneAccount#SCHEME_TEL
    val handle: Uri
        get() = callDetails.handle
}

