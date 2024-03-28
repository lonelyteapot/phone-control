package dev.phonecontrol.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.phonecontrol.domain.model.CallBlockingRule
import kotlinx.coroutines.flow.map
import java.util.UUID


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

private object Keys {
    val RULE_SET = stringSetPreferencesKey("blocking_rule_set")
}

private class RuleKeys(val ruleId: String) {
    val enabled get() = booleanPreferencesKey(ruleId + "_enabled")
    val action get() = stringPreferencesKey(ruleId + "_action")
    val target get() = stringPreferencesKey(ruleId + "_target")
    val cardId get() = intPreferencesKey(ruleId + "_card_id")
    val cardName get() = stringPreferencesKey(ruleId + "_card_name")
    val position get() = intPreferencesKey(ruleId + "_position")
}

private fun Preferences.readRule(ruleId: String): CallBlockingRule {
    val keys = RuleKeys(ruleId)
    return CallBlockingRule(
        uuid = UUID.fromString(ruleId),
        enabled = get(keys.enabled)!!,
        action = CallBlockingRule.Action.valueOf(get(keys.action)!!),
        target = CallBlockingRule.Target.valueOf(get(keys.target)!!),
        cardId = get(keys.cardId),
        cardName = get(keys.cardName),
        position = get(keys.position)!!,
    )
}

private fun MutablePreferences.setRule(rule: CallBlockingRule) {
    val keys = RuleKeys(rule.uuid.toString())
    set(keys.enabled, rule.enabled)
    set(keys.action, rule.action.name)
    set(keys.target, rule.target.name)
    setNullable(keys.cardId, rule.cardId)
    setNullable(keys.cardName, rule.cardName)
    set(keys.position, rule.position)
}

private fun MutablePreferences.removeRule(ruleId: String) {
    val keys = RuleKeys(ruleId)
    remove(keys.enabled)
    remove(keys.action)
    remove(keys.target)
    remove(keys.cardId)
    remove(keys.cardName)
    remove(keys.position)
}

private fun <T> MutablePreferences.setNullable(key: Preferences.Key<T>, value: T?) {
    if (value == null) {
        remove(key)
    } else {
        set(key, value)
    }
}

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    val ruleListFlow = dataStore.data.map { preferences ->
        val idList = preferences[Keys.RULE_SET] ?: emptySet()
        idList.map { ruleId ->
            preferences.readRule(ruleId)
        }.sortedBy { rule -> rule.position }
    }

    suspend fun createRule(rule: CallBlockingRule) {
        val ruleId = rule.uuid.toString()
        dataStore.edit { preferences ->
            val idSet = preferences[Keys.RULE_SET] ?: emptySet()
            assert(!idSet.contains(ruleId))
            preferences[Keys.RULE_SET] = idSet.plus(ruleId)
            preferences.setRule(rule)
        }
    }

    suspend fun updateRule(rule: CallBlockingRule) {
        dataStore.edit { preferences ->
            preferences.setRule(rule)
        }
    }

    suspend fun deleteRule(ruleId: UUID) {
        val strId = ruleId.toString()
        dataStore.edit { preferences ->
            val ruleSet = preferences[Keys.RULE_SET] ?: emptySet()
            assert(ruleSet.contains(strId))
            preferences[Keys.RULE_SET] = ruleSet.minusElement(strId)
            preferences.removeRule(strId)
        }
    }
}
