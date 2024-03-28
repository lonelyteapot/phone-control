package dev.phonecontrol.domain.model

import android.net.Uri
import android.os.Build
import android.telecom.Call
import androidx.annotation.RequiresApi

class CallInfo(private val callDetails: Call.Details) {
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

    val loggableDirection: String
        get() {
            return when (callDirection) {
                Call.Details.DIRECTION_INCOMING -> "incoming"
                Call.Details.DIRECTION_OUTGOING -> "outgoing"
                else -> "(unknown direction)"
            }
        }
}