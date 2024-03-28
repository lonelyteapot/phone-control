package dev.phonecontrol.service.logic

import dev.phonecontrol.misc.logd

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
        val delta = subtract(callHandleTime, bindTime)
        logd("Started processing the call in $delta ns")
    }

    fun recordCallHandled() {
        callHandledTime = System.nanoTime()
        val delta = subtract(callHandledTime, callHandleTime)
        logd("Finished processing the call in $delta ns")
    }

    fun recordUnbind() {
        unbindTime = System.nanoTime()
        val delta = subtract(unbindTime, bindTime)
        logd("CallScreeningService finished in $delta ns")
    }
}
