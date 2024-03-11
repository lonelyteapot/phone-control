package dev.phonecontrol.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import java.util.UUID


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

object PreferenceKeys {
    val RULE_SET = stringSetPreferencesKey("blocking_rule_set")
}

private class RuleKeyFactory(val ruleId: String) {
    val enabled get() = booleanPreferencesKey("${ruleId}_enabled")
    val action get() = stringPreferencesKey("${ruleId}_action")
    val target get() = stringPreferencesKey("${ruleId}_target")
    val cardId get() = intPreferencesKey("${ruleId}_card_id")
    val cardName get() = stringPreferencesKey("${ruleId}_card_name")
    val position get() = intPreferencesKey("${ruleId}_position")
}

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    val ruleListFlow = dataStore.data.map { preferences ->
        val idList = preferences[PreferenceKeys.RULE_SET] ?: emptySet()
        idList.map { ruleId ->
            val keys = RuleKeyFactory(ruleId)
            val enabled = preferences[keys.enabled]
            val action = preferences[keys.action]
            val target = preferences[keys.target]
            val cardId = preferences[keys.cardId]
            val cardName = preferences[keys.cardName]
            val position = preferences[keys.position]
            BlockingRule(
                uuid = UUID.fromString(ruleId),
                enabled = enabled!!,
                action = BlockingRule.Action.valueOf(action!!),
                target = BlockingRule.Target.valueOf(target!!),
                cardId = cardId,
                cardName = cardName,
                position = position!!,
            )
        }.sortedBy { rule -> rule.position }
    }

    suspend fun createRule(rule: BlockingRule) {
        val ruleId = rule.uuid.toString()
        val keys = RuleKeyFactory(ruleId)
        dataStore.edit { preferences ->
            val idSet = preferences[PreferenceKeys.RULE_SET] ?: emptySet()
            assert(!idSet.contains(ruleId))
            preferences[PreferenceKeys.RULE_SET] = idSet.plus(ruleId)
            preferences[keys.enabled] = rule.enabled
            preferences[keys.action] = rule.action.toString()
            preferences[keys.target] = rule.target.toString()
            if (rule.cardId != null) {
                preferences[keys.cardId] = rule.cardId
            } else {
                assert(preferences[keys.cardId] == null)
            }
            if (rule.cardName != null) {
                preferences[keys.cardName] = rule.cardName
            } else {
                assert(preferences[keys.cardName] == null)
            }
            preferences[keys.position] = rule.position
        }
    }

    suspend fun updateRule(rule: BlockingRule) {
        val ruleId = rule.uuid.toString()
        val keys = RuleKeyFactory(ruleId)
        dataStore.edit { preferences ->
            preferences[keys.enabled] = rule.enabled
            preferences[keys.action] = rule.action.toString()
            preferences[keys.target] = rule.target.toString()
            if (rule.cardId != null) {
                preferences[keys.cardId] = rule.cardId
            } else {
                preferences.remove(keys.cardId)
            }
            if (rule.cardName != null) {
                preferences[keys.cardName] = rule.cardName
            } else {
                preferences.remove(keys.cardName)
            }
            preferences[keys.position] = rule.position
        }
    }

    suspend fun deleteRule(ruleId: UUID) {
        val strId = ruleId.toString()
        val keys = RuleKeyFactory(strId)
        dataStore.edit { preferences ->
            val ruleSet = preferences[PreferenceKeys.RULE_SET] ?: emptySet()
            assert(ruleSet.contains(strId))
            preferences[PreferenceKeys.RULE_SET] = ruleSet.minusElement(strId)
            preferences.remove(keys.enabled)
            preferences.remove(keys.action)
            preferences.remove(keys.target)
            preferences.remove(keys.cardId)
            preferences.remove(keys.cardName)
            preferences.remove(keys.position)
        }
    }
}

