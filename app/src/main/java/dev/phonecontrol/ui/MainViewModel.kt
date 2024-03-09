package dev.phonecontrol.ui

import android.app.role.RoleManager
import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.phonecontrol.data.UserPreferencesRepository
import dev.phonecontrol.data.dataStore
import dev.phonecontrol.data.BlockingRule
import dev.phonecontrol.data.PermissionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID


class MainViewModel(private val applicationContext: Context) : ViewModel() {
    private val userPreferencesRepository = UserPreferencesRepository(applicationContext.dataStore)
    private val permissionsRepository = PermissionsRepository()

    val ruleListFlow: Flow<List<BlockingRule>>
        get() = userPreferencesRepository.ruleListFlow
    val hasCallScreeningRoleFlow = MutableStateFlow(hasCallScreeningRole())
    val hasContactsPermissionFlow = MutableStateFlow(permissionsRepository.hasContactsPermission(applicationContext))

    suspend fun createNewRule(pos: Int) {
        val newRule = BlockingRule(
            uuid = UUID.randomUUID(),
            enabled = false,
            action = BlockingRule.Action.SILENCE,
            target = BlockingRule.Target.NON_CONTACTS,
            position = pos
        )
        userPreferencesRepository.createRule(newRule)
    }

    fun updateRule(rule: BlockingRule) {
        viewModelScope.launch {
            userPreferencesRepository.updateRule(rule)
        }
    }

    fun deleteRule(rule: BlockingRule) {
        viewModelScope.launch {
            userPreferencesRepository.deleteRule(rule.uuid)
        }
    }

    fun hasCallScreeningRole(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val roleManager = applicationContext.getSystemService(Context.ROLE_SERVICE) as RoleManager?
            return roleManager?.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) == true
        }
        return false
    }

    fun checkForCallScreeningRole(): Unit {
        hasCallScreeningRoleFlow.tryEmit(hasCallScreeningRole())
    }

    fun canRequestCallScreeningRole(): Boolean {
        val roleManager = applicationContext.getSystemService<RoleManager>()
        if (roleManager == null) {
            // TODO
            return false
        }
        if (!roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
            return false
        }
        return true
    }

    fun requestCallScreeningRole(context: ComponentActivity): Unit {
        if (!canRequestCallScreeningRole()) {
            return
        }
        val roleManager = applicationContext.getSystemService<RoleManager>()
        val intent = roleManager?.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        if (intent == null) {
            return
        }
        context.startActivityForResult(intent, 0)
    }

    fun checkForContactsPermission(): Unit {
        hasContactsPermissionFlow.tryEmit(permissionsRepository.hasContactsPermission(applicationContext))
    }
}
