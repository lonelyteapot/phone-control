package dev.phonecontrol.ui.views

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.phonecontrol.R
import dev.phonecontrol.misc.conditional
import dev.phonecontrol.misc.gesturesDisabled
import dev.phonecontrol.ui.components.CustomButton1
import dev.phonecontrol.ui.components.RuleCard2
import dev.phonecontrol.ui.theme.PhoneControlTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = MainViewModel(applicationContext)
        setContent {
            PhoneControlTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhoneControlApp()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.checkForCallScreeningRole()
        viewModel.checkForContactsPermission()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkForCallScreeningRole()
        viewModel.checkForContactsPermission()
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun PhoneControlApp() {
        val coroutineScope = rememberCoroutineScope()
        val hasCallScreeningRoleState = viewModel.hasCallScreeningRoleFlow.collectAsState()
        val hasContactsPermissionState = viewModel.hasContactsPermissionFlow.collectAsState()
        val ruleListState = viewModel.ruleListFlow.collectAsState(initial = emptyList())
        val listState = rememberLazyListState()
        val snackbarHostState = remember { SnackbarHostState() }
        val subscriptionsState = viewModel.subscriptionListFlow.collectAsState()

        val requestPermissionLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
                viewModel.checkForContactsPermission()
            }

        Scaffold(
            contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.navigationBars),
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
            floatingActionButton = {
                if (hasCallScreeningRoleState.value) ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.new_rule)) },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    onClick = {
                        coroutineScope.launch {
                            viewModel.createNewRule(ruleListState.value.size)
                            // TODO -1 lastIndex
                            listState.animateScrollToItem(ruleListState.value.lastIndex)
                        }
                    },
                    modifier = Modifier.navigationBarsPadding(),
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.permissions_header),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp)
                        .conditional(!hasCallScreeningRoleState.value) {
                            alpha(0.38f)
                        },
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CustomButton1(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .conditional(!hasCallScreeningRoleState.value) {
                                blur(8.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                                    .gesturesDisabled()
                            },
                        text = if (hasContactsPermissionState.value) {
                            stringResource(R.string.contacts_permission_button_enabled)
                        } else {
                            stringResource(R.string.contacts_permission_button_disabled)
                        },
                        checked = hasContactsPermissionState.value,
                        onClick = {
                            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        },
                    )
                    CustomButton1(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        text = if (hasCallScreeningRoleState.value) {
                            stringResource(R.string.call_screening_role_button_enabled)
                        } else {
                            stringResource(R.string.call_screening_role_button_disabled)
                        },
                        checked = hasCallScreeningRoleState.value,
                        onClick = {
                            viewModel.requestCallScreeningRole(this@MainActivity)
                        },
                    )
                    CustomButton1(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .conditional(!hasCallScreeningRoleState.value) {
                                blur(8.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                                    .gesturesDisabled()
                            },
                        text = "",
                        checked = hasCallScreeningRoleState.value,
                        onClick = { /*TODO*/ },
                        noIcon = true,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = if (hasCallScreeningRoleState.value) {
                        Modifier
                    } else {
                        Modifier
                            .blur(8.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                            .gesturesDisabled()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.rules_header),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp)
                            .conditional(!hasCallScreeningRoleState.value) {
                                alpha(0.38f)
                            },
                    )
                    val shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(
                            top = 24.dp,
                            bottom = 16.dp + 72.dp + WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding(),
                            start = 16.dp,
                            end = 16.dp,
                        ),
                        state = listState,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.background,
                                shape = shape
                            )
                            .fillMaxSize()
                            .clip(shape = shape),
                    ) {
                        items(
                            items = ruleListState.value,
                            key = { rule -> rule.uuid }
                        ) { rule ->
                            val subscription = if (rule.cardId == null) null else {
                                subscriptionsState.value.firstOrNull { subscription ->
                                    subscription.cardId == rule.cardId
                                }
                            }
                            RuleCard2(
                                rule = rule,
                                onUpdateRule = { newRule ->
                                    viewModel.updateRule(newRule)
                                },
                                onDeleteClick = {
                                    viewModel.deleteRule(rule)
                                },
                                onNoContactsPermission = {
                                    coroutineScope.launch {
                                        if (snackbarHostState.currentSnackbarData != null) {
                                            return@launch
                                        }
                                        val snackbarResult = snackbarHostState.showSnackbar(
                                            "Access to contacts is required to use this",
                                            actionLabel = "Allow",
                                            duration = SnackbarDuration.Short,
                                        )
                                        when (snackbarResult) {
                                            SnackbarResult.ActionPerformed -> {
                                                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                                            }
                                            else -> {}
                                        }
                                    }
                                },
                                subscription = subscription,
                                subscriptionList = subscriptionsState.value,
                                modifier = Modifier.animateItemPlacement(),
                            )
//                            RuleCard(
//                                rule = rule,
//                                deleteRule = {
//                                    viewModel.deleteRule(rule)
//                                },
//                                updateRule = { newRule ->
//                                    viewModel.updateRule(newRule)
//                                },
//                                modifier = Modifier.animateItemPlacement(),
//                                onEveryoneDisabledClick = {
//                                    coroutineScope.launch {
//                                        if (snackbarHostState.currentSnackbarData != null) {
//                                            return@launch
//                                        }
//                                        val snackbarResult = snackbarHostState.showSnackbar(
//                                            "Access to contacts is required to use this",
//                                            actionLabel = "Allow",
//                                            duration = SnackbarDuration.Short,
//                                        )
//                                        when (snackbarResult) {
//                                            SnackbarResult.ActionPerformed -> {
//                                                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
//                                            }
//
//                                            else -> {}
//                                        }
//                                    }
//                                },
//                                onRemovedSimCardClick = {
//                                    coroutineScope.launch {
//                                        if (snackbarHostState.currentSnackbarData != null) {
//                                            return@launch
//                                        }
//                                        snackbarHostState.showSnackbar(
//                                            "This SIM card has been removed",
//                                            duration = SnackbarDuration.Short,
//                                        )
//                                    }
//                                },
//                                subscriptions = subscriptionsState.value,
//                            )
                        }
                    }
                }
            }
        }
    }
}