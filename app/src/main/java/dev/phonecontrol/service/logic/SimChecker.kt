package dev.phonecontrol.service.logic

import android.Manifest.permission.READ_CALL_LOG
import android.Manifest.permission.READ_PHONE_STATE
import android.content.Context
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import androidx.core.content.getSystemService
import dev.phonecontrol.misc.loge
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

class SimChecker(private val context: Context, private val phoneNumber: String) {
    private var executed: Boolean = false
    private var result: SubscriptionInfo? = null

    @RequiresPermission(allOf = [READ_PHONE_STATE, READ_CALL_LOG])
    suspend fun getCachedSubscriptionInfo(): SubscriptionInfo? {
        if (!executed) {
            executed = true
            // TODO: time management
            result = withTimeoutOrNull(4.seconds) {
                getSubscriptionForCallingNumber(phoneNumber)
            }
            if (result == null) {
                loge("Timed out looking for subscription for $phoneNumber")
            }
        }
        return result
    }

    @RequiresPermission(allOf = [READ_PHONE_STATE, READ_CALL_LOG])
    private suspend fun getSubscriptionForCallingNumber(phoneNumber: String?): SubscriptionInfo? {
        // TODO handle nulls
        val subscriptionManager = context.getSystemService<SubscriptionManager>()!!
        val defaultTelephonyManager = context.getSystemService<TelephonyManager>()!!

        val subscriptions = subscriptionManager.activeSubscriptionInfoList
        return subscriptions.firstOrNull { subscriptionInfo ->
            val telephonyManager =
                defaultTelephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
            val subscriptionPhoneNumber = getPhoneNumberForTelephonyManager(telephonyManager)
            // TODO parallelize
            subscriptionPhoneNumber == phoneNumber
        }
    }

    private suspend fun getPhoneNumberForTelephonyManager(telephonyManager: TelephonyManager): String? {
        val listener = MyPhoneStateListener()
        listener.registerOn(telephonyManager)
        listener.unregisterOn(telephonyManager)
        return listener.deferredPhoneNumber.await()
    }
}