package dev.phonecontrol.service.logic

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import dev.phonecontrol.misc.logd
import kotlinx.coroutines.CompletableDeferred

// TODO: use non-deprecated functionality for newer SDKs
class MyPhoneStateListener : PhoneStateListener() {
    val deferredPhoneNumber = CompletableDeferred<String?>()

    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        logd("Received call state '$state', phoneNumber = '$phoneNumber'")
        super.onCallStateChanged(state, phoneNumber)
        deferredPhoneNumber.complete(phoneNumber)
    }

    fun registerOn(telephonyManager: TelephonyManager) {
        // TODO care for binder transactions
        // https://developer.android.com/reference/android/telephony/TelephonyManager#listen(android.telephony.PhoneStateListener,%20int)
        telephonyManager.listen(this, LISTEN_CALL_STATE)
    }

    fun unregisterOn(telephonyManager: TelephonyManager) {
        telephonyManager.listen(this, LISTEN_NONE)
    }
}