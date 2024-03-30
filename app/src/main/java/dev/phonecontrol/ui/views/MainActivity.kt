package dev.phonecontrol.ui.views

import android.Manifest
import android.app.role.RoleManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import dev.phonecontrol.R
import dev.phonecontrol.misc.blurredUnavailable
import dev.phonecontrol.misc.conditional
import dev.phonecontrol.misc.findActivity
import dev.phonecontrol.misc.openAppSettings
import dev.phonecontrol.misc.role.rememberRoleState
import dev.phonecontrol.ui.components.CustomButton1
import dev.phonecontrol.ui.components.NewRuleCard
import dev.phonecontrol.ui.components.RuleCard2
import dev.phonecontrol.ui.theme.PhoneControlTheme
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var subscriptionsViewModel: SubscriptionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        mainViewModel = MainViewModel(application)
        subscriptionsViewModel = SubscriptionsViewModel(application)

        setContent {
            PhoneControlTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    PhoneControlApp(mainViewModel, subscriptionsViewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        subscriptionsViewModel.refreshSubscriptionsState()
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
@Composable
fun PhoneControlApp(
    viewModel: MainViewModel,
    subscriptionsViewModel: SubscriptionsViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val ruleListState = viewModel.ruleListFlow.collectAsState(initial = emptyList())
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val subscriptionsState = subscriptionsViewModel.subscriptionListFlow.collectAsState()

    val callScreeningRoleState = rememberRoleState(RoleManager.ROLE_CALL_SCREENING)
    val simAccessState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG
        )
    )
    val contactsAccessState = rememberPermissionState(Manifest.permission.READ_CONTACTS)

    var shouldShowCallScreeningRoleDialog by remember { mutableStateOf(false) }
    var shouldShowSimAccessDialog by remember { mutableStateOf(false) }
    var shouldShowContactsAccessDialog by remember { mutableStateOf(false) }


    val supportedLanguageTags = arrayOf("en-US", "ru-RU")

    val appLocale = LocalConfiguration.current.locales[0]

    fun cycleAppLocale() {
        val currentIndex = supportedLanguageTags.indexOf(appLocale.toLanguageTag())
        val nextIndex = (currentIndex + 1) % supportedLanguageTags.size
        val nextLanguageTag = supportedLanguageTags[nextIndex]

        val newLocale = LocaleListCompat.forLanguageTags(nextLanguageTag)
        AppCompatDelegate.setApplicationLocales(newLocale)
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.navigationBars),
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(top = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    IconButton(
                        onClick = {
                            cycleAppLocale()
                        },
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_language),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.permissions_header),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CustomButton1(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    text = stringResource(R.string.label_call_screening_role),
                    checked = callScreeningRoleState.status.isHeld,
                    onClick = {
                        shouldShowCallScreeningRoleDialog = true
                    },
                )
                CustomButton1(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .conditional(!callScreeningRoleState.status.isHeld) {
                            blurredUnavailable()
                        },
                    text = stringResource(R.string.label_sim_card_access),
                    checked = simAccessState.allPermissionsGranted,
                    onClick = {
                        shouldShowSimAccessDialog = true
                    },
                )
                CustomButton1(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .conditional(!callScreeningRoleState.status.isHeld) {
                            blurredUnavailable()
                        },
                    text = stringResource(R.string.label_contacts_access),
                    checked = contactsAccessState.status.isGranted,
                    onClick = {
                        shouldShowContactsAccessDialog = true
                    },
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
            Column(modifier = Modifier.conditional(!callScreeningRoleState.status.isHeld) {
                blurredUnavailable()
            }) {
                Text(
                    text = stringResource(id = R.string.rules_header),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .conditional(!callScreeningRoleState.status.isHeld) {
                            alpha(0.38f)
                        },
                )
                val shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(
                        top = 24.dp,
                        bottom = 16.dp + WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding(),
                        start = 16.dp,
                        end = 16.dp,
                    ),
                    state = listState,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.background, shape = shape
                        )
                        .fillMaxSize()
                        .clip(shape = shape),
                ) {
                    items(items = ruleListState.value, key = { rule -> rule.uuid }) { rule ->
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
                                shouldShowContactsAccessDialog = true
                            },
                            onNoSimCardAccess = {
                                shouldShowSimAccessDialog = true
                            },
                            subscription = subscription,
                            subscriptionList = subscriptionsState.value,
                            canAccessSimCards = simAccessState.allPermissionsGranted,
                            modifier = Modifier.animateItemPlacement(),
                        )
                    }
                    item(key = "new_rule") {
                        NewRuleCard(
                            modifier = Modifier.animateItemPlacement(),
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.createNewRule(ruleListState.value.size)
                                }
                            },
                        )
                    }
                }
            }
        }

        if (shouldShowCallScreeningRoleDialog) {
            PermissionRequestDialog(
                title = {
                    Text(stringResource(R.string.label_call_screening_role))
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(stringResource(R.string.dialog_descr_call_screening_role_1))
                        Text(stringResource(R.string.dialog_descr_call_screening_role_2))
                        if (!callScreeningRoleState.status.isHeld) {
                            Text(buildAnnotatedString {
                                append(stringResource(R.string.dialog_call_screening_role_desc_action1))
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(stringResource(R.string.set_as_default_app))
                                }
                                append(stringResource(R.string.dialog_call_screening_role_desc_action2))
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(stringResource(context.applicationInfo.labelRes))
                                }
                                append(stringResource(R.string.dialog_call_screening_role_desc_action3))
                            })
                        }
                    }
                },
                confirmButtonText = if (!callScreeningRoleState.status.isHeld) {
                    stringResource(R.string.set_as_default_app)
                } else {
                    stringResource(R.string.ok)
                },
                showDismissButton = !callScreeningRoleState.status.isHeld,
                onDismiss = {
                    shouldShowCallScreeningRoleDialog = false
                },
                onConfirm = {
                    shouldShowCallScreeningRoleDialog = false
                    if (callScreeningRoleState.status.isHeld) return@PermissionRequestDialog
                    callScreeningRoleState.launchRoleRequest()
                },
            )
        } else if (shouldShowSimAccessDialog) {
            val canRequestNormally = simAccessState.shouldShowRationale
            PermissionRequestDialog(
                title = {
                    Text(stringResource(R.string.label_sim_card_access))
                },
                text = {
                    Text(stringResource(R.string.dialog_descr_sim_card_access))
                },
                confirmButtonText = if (simAccessState.allPermissionsGranted) {
                    stringResource(R.string.ok)
                } else if (canRequestNormally) {
                    stringResource(R.string.dialog_perm_allow)
                } else {
                    stringResource(R.string.dialog_perm_allow_in_settings)
                },
                showDismissButton = !simAccessState.allPermissionsGranted,
                onDismiss = {
                    shouldShowSimAccessDialog = false
                },
                onConfirm = {
                    shouldShowSimAccessDialog = false
                    if (simAccessState.allPermissionsGranted) return@PermissionRequestDialog
                    if (canRequestNormally) {
                        simAccessState.launchMultiplePermissionRequest()
                    } else {
                        context.findActivity().openAppSettings()
                    }
                },
            )
        } else if (shouldShowContactsAccessDialog) {
            val canRequestNormally = contactsAccessState.status.shouldShowRationale
            PermissionRequestDialog(
                title = {
                    Text(stringResource(R.string.label_contacts_access))
                },
                text = {
                    Text(stringResource(R.string.dialog_descr_contacts_access))
                },
                confirmButtonText = if (contactsAccessState.status.isGranted) {
                    stringResource(R.string.ok)
                } else if (canRequestNormally) {
                    stringResource(R.string.dialog_perm_allow)
                } else {
                    stringResource(R.string.dialog_perm_allow_in_settings)
                },
                showDismissButton = !contactsAccessState.status.isGranted,
                onDismiss = {
                    shouldShowContactsAccessDialog = false
                },
                onConfirm = {
                    shouldShowContactsAccessDialog = false
                    if (contactsAccessState.status.isGranted) return@PermissionRequestDialog
                    if (canRequestNormally) {
                        contactsAccessState.launchPermissionRequest()
                    } else {
                        context.findActivity().openAppSettings()
                    }
                },
            )
        }
    }
}