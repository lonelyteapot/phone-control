package dev.phonecontrol.ui.views

import android.annotation.SuppressLint
import android.app.Application
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.phonecontrol.domain.model.CallBlockingRule
import dev.phonecontrol.data.datastore.UserPreferencesRepository
import dev.phonecontrol.data.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


class MainViewModel(private val application: Application) : ViewModel() {
    private val userPreferencesRepository = UserPreferencesRepository(application.dataStore)

    val ruleListFlow: Flow<List<CallBlockingRule>>
        get() = userPreferencesRepository.ruleListFlow
    val subscriptionListFlow = MutableStateFlow(emptyList<SubscriptionInfo>())

    private val onSubscriptionsChangedListener: OnSubscriptionsChangedListener

    init {
        val subscriptionManager = application.getSystemService<SubscriptionManager>()!!
        onSubscriptionsChangedListener = object : OnSubscriptionsChangedListener() {
            @SuppressLint("MissingPermission")
            override fun onSubscriptionsChanged() {
                super.onSubscriptionsChanged()
                val subscriptions = subscriptionManager.activeSubscriptionInfoList
                if (subscriptions != null) {
                    subscriptionListFlow.update { subscriptions }
                }
            }
        }
        subscriptionManager.addOnSubscriptionsChangedListener(onSubscriptionsChangedListener)
        // TODO errors
    }

    override fun onCleared() {
        val subscriptionManager = application.getSystemService<SubscriptionManager>()!!
        subscriptionManager.removeOnSubscriptionsChangedListener(onSubscriptionsChangedListener)
        super.onCleared()
    }

    suspend fun createNewRule(pos: Int) {
        val newRule = CallBlockingRule(
            uuid = UUID.randomUUID(),
            enabled = true,
            action = CallBlockingRule.Action.SILENCE,
            target = CallBlockingRule.Target.NON_CONTACTS,
            cardId = null,
            cardName = null,
            position = pos
        )
        userPreferencesRepository.createRule(newRule)
    }

    fun updateRule(rule: CallBlockingRule) {
        viewModelScope.launch {
            userPreferencesRepository.updateRule(rule)
        }
    }

    fun deleteRule(rule: CallBlockingRule) {
        viewModelScope.launch {
            userPreferencesRepository.deleteRule(rule.uuid)
        }
    }
}

