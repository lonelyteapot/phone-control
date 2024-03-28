package dev.phonecontrol.service.logic

import dev.phonecontrol.misc.logd
import kotlin.time.Duration.Companion.nanoseconds

class PerformanceRecorder {
    private var bindTime: Long? = null
    private var callHandleTime: Long? = null
    private var callHandledTime: Long? = null
    private var unbindTime: Long? = null

    companion object {
        fun subtract(a: Long?, b: Long?): Long? {
            if (a == null || b == null) {
                return null
            }
            return a - b
        }
    }

    fun recordBind() {
        bindTime = System.nanoTime()
    }

    fun recordCallReceived() {
        callHandleTime = System.nanoTime()
        val delta = subtract(callHandleTime, bindTime)?.nanoseconds
        logd("Started processing the call after $delta")
    }

    fun recordCallHandled() {
        callHandledTime = System.nanoTime()
        val delta = subtract(callHandledTime, callHandleTime)?.nanoseconds
        logd("Finished processing the call in $delta")
    }

    fun recordUnbind() {
        unbindTime = System.nanoTime()
        val delta = subtract(unbindTime, bindTime)?.nanoseconds
        logd("CallScreeningService finished after $delta")
    }
}
