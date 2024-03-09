package dev.phonecontrol.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.phonecontrol.R
import dev.phonecontrol.gesturesDisabled
import dev.phonecontrol.ui.theme.PhoneControlTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = MainViewModel(applicationContext)
        setContent {
            PhoneControlTheme {
                // A surface container using the 'background' color from the theme
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

    @Composable
    fun PhoneControlApp() {
        val coroutineScope = rememberCoroutineScope()
        val hasCallScreeningRoleState = viewModel.hasCallScreeningRoleFlow.collectAsState()
        val hasContactsPermissionState = viewModel.hasContactsPermissionFlow.collectAsState()
        val ruleListState = viewModel.ruleListFlow.collectAsState(initial = emptyList())
        val listState = rememberLazyListState()
        val snackbarHostState = remember { SnackbarHostState() }

        val requestPermissionLauncher =
            rememberLauncherForActivityResult(RequestPermission()) { _ ->
                viewModel.checkForContactsPermission()
            }

        Scaffold(
            contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.navigationBars),
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
                    .padding(horizontal = 16.dp)
                    .padding(top = 28.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
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
                            .weight(1f),
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
                        text = "",
                        checked = hasCallScreeningRoleState.value,
                        onClick = { /*TODO*/ },
                    )
                }
                Spacer(modifier = Modifier.height(28.dp))
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
                        text = "Rules",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = if (hasCallScreeningRoleState.value) {
                            Modifier
                        } else {
                            Modifier.alpha(0.38f)
                        },
                    )
                    HorizontalDivider()
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(
                            top = 16.dp,
                            bottom = 16.dp + 72.dp + WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding()
                        ),
                        state = listState,
                    ) {
                        items(
                            items = ruleListState.value,
                            key = { rule -> rule.uuid }
                        ) { rule ->
                            RuleCard(
                                rule = rule,
                                deleteRule = {
                                    viewModel.deleteRule(rule)
                                },
                                updateRule = { newRule ->
                                    viewModel.updateRule(newRule)
                                },
                                modifier = Modifier.animateItemPlacement(),
                                onEveryoneDisabledClick = {
                                    coroutineScope.launch {
                                        if (snackbarHostState.currentSnackbarData != null) {
                                            return@launch
                                        }
                                        val snackbarResult = snackbarHostState.showSnackbar(
                                            "To use this, allow us to access your contacts",
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
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
