package dev.phonecontrol.service

import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import dev.phonecontrol.data.BlockingRule
import dev.phonecontrol.data.UserPreferencesRepository
import dev.phonecontrol.data.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class CallScreeningServiceImpl : CallScreeningService() {
    private val TAG = "CallScreeningServiceImpl"

    override fun onScreenCall(callDetails: Call.Details) {
        val response = handleCall(callDetails)
        Log.i(TAG, "Responding: disallow=${response.disallowCall}, silence=${response.silenceCall}, reject=${response.rejectCall}")
        respondToCall(callDetails, response)
    }

    private fun handleCall(callDetails: Call.Details): CallResponse {
        val response = CallResponse.Builder()

        val phoneNumber: Uri = callDetails.handle

        Log.i(TAG, "Receiving a call from/to `$phoneNumber`...")

        val isIncoming = callDetails.callDirection == Call.Details.DIRECTION_INCOMING
        if (!isIncoming) {
            Log.i(TAG, "The call is outgoing or unknown, ignoring")
            return response.build();
        }

        val userPreferencesRepository = UserPreferencesRepository(applicationContext.dataStore)

        val ruleList = runBlocking {
            userPreferencesRepository.ruleListFlow.first()
        }
        ruleList
            .filter { rule -> rule.enabled }
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
        return response.build()
    }
}
