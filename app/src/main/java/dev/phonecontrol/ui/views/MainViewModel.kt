package dev.phonecontrol.ui.views

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.phonecontrol.data.datastore.UserPreferencesRepository
import dev.phonecontrol.data.datastore.dataStore
import dev.phonecontrol.domain.model.CallBlockingRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID


class MainViewModel(private val application: Application) : ViewModel() {
    private val userPreferencesRepository = UserPreferencesRepository(application.dataStore)

    val ruleListFlow: Flow<List<CallBlockingRule>>
        get() = userPreferencesRepository.ruleListFlow

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
