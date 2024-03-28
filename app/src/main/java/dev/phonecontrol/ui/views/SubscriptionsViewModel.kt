package dev.phonecontrol.ui.views

import android.annotation.SuppressLint
import android.app.Application
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SubscriptionsViewModel(private val application: Application) : ViewModel() {
    private var subscriptionManager = application.getSystemService<SubscriptionManager>()
    private val onSubscriptionsChangedListener =
        object : SubscriptionManager.OnSubscriptionsChangedListener() {
            override fun onSubscriptionsChanged() {
                super.onSubscriptionsChanged()
                checkSubscriptions()
            }
        }
    val subscriptionListFlow = MutableStateFlow(emptyList<SubscriptionInfo>())

    init {
        registerListener()
    }

    override fun onCleared() {
        unregisterListener()
        super.onCleared()
    }

    fun refreshSubscriptionsState() {
        unregisterListener()
        subscriptionManager = application.getSystemService<SubscriptionManager>()
        registerListener()
    }

    private fun registerListener() {
        // TODO: investigate executors/loopers and use them on newer APIs
        subscriptionManager?.addOnSubscriptionsChangedListener(onSubscriptionsChangedListener)
    }

    private fun unregisterListener() {
        subscriptionManager?.removeOnSubscriptionsChangedListener(onSubscriptionsChangedListener)
    }

    // TODO
    @SuppressLint("MissingPermission")
    private fun checkSubscriptions() {
        val subscriptions = subscriptionManager?.activeSubscriptionInfoList
        if (subscriptions != null) {
            subscriptionListFlow.update { subscriptions }
        }
    }
}
