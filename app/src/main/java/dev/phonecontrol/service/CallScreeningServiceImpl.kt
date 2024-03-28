package dev.phonecontrol.service

import android.content.Intent
import android.os.IBinder
import android.telecom.Call
import android.telecom.CallScreeningService
import dev.phonecontrol.domain.model.CallInfo
import dev.phonecontrol.misc.logi
import dev.phonecontrol.service.logic.CallProcessor
import dev.phonecontrol.service.logic.PerformanceRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class CallScreeningServiceImpl : CallScreeningService() {
    private lateinit var coroutineScope: CoroutineScope
    private var performanceRecorder: PerformanceRecorder? = null
    private var callProcessor: CallProcessor? = null

    override fun onBind(intent: Intent?): IBinder? {
        performanceRecorder = PerformanceRecorder()
        performanceRecorder!!.recordBind()
        callProcessor = CallProcessor(applicationContext)
        coroutineScope = MainScope()
        return super.onBind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        performanceRecorder?.recordUnbind()
        coroutineScope.cancel("CallScreeningService has been unbound by the system")
        return super.onUnbind(intent)
    }

    override fun onScreenCall(callDetails: Call.Details) {
        performanceRecorder?.recordCallReceived()
        val callInfo = CallInfo(callDetails)
        logi("Receiving a call from/to '${callInfo.phoneNumber}'")

        coroutineScope.launch {
            val response = callProcessor!!.processCall(callInfo).build()
            logi("Responding: disallow=${response.disallowCall}, silence=${response.silenceCall}, reject=${response.rejectCall}")
            respondToCall(callDetails, response)
            performanceRecorder?.recordCallHandled()
        }
    }
}
