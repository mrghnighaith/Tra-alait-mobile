package com.tracalait.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tracalait.app.model.AppView
import com.tracalait.app.model.PermissionHelper
import com.tracalait.app.ui.screens.*
import com.tracalait.app.ui.theme.*
import com.tracalait.app.viewmodel.TracaLaitViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TracaLaitTheme {
                TracaLaitApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TracaLaitApp(viewModel: TracaLaitViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Auto-dismiss toast after 3 seconds
    LaunchedEffect(state.toast?.id) {
        if (state.toast != null) {
            delay(3000)
            viewModel.clearToast()
        }
    }

    // Back handler when not on Dashboard
    BackHandler(enabled = state.currentUser != null && state.currentView != AppView.DASHBOARD) {
        viewModel.navigateTo(AppView.DASHBOARD)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.currentUser == null) {
            LoginScreen(onLogin = { email, password -> viewModel.login(email, password) })
        } else {
            val isAdmin = PermissionHelper.isAdmin(state.currentUser)
            val availableViews = remember(isAdmin) {
                AppView.entries.filter { !it.adminOnly || isAdmin }
            }

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = NavyPrimary,
                        drawerContentColor = Color.White,
                        modifier = Modifier.width(260.dp)
                    ) {
                        // Sidebar Logo Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BlueAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🥛", fontSize = 18.sp)
                            }
                            Column {
                                Text("TraçaLait", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("v1.0", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            }
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                        // Sidebar Navigation grouped by section
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 8.dp)
                        ) {
                            val grouped = availableViews.groupBy { it.section }
                            grouped.forEach { (section, views) ->
                                Text(
                                    text = section.uppercase(),
                                    color = Color.White.copy(alpha = 0.45f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 18.dp, top = 14.dp, bottom = 6.dp)
                                )
                                views.forEach { view ->
                                    val active = state.currentView == view
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(if (active) BlueAccent else Color.Transparent)
                                            .clickable {
                                                viewModel.navigateTo(view)
                                                scope.launch { drawerState.close() }
                                            }
                                            .padding(horizontal = 18.dp, vertical = 11.dp)
                                            .testTag("nav_${view.id}"),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(view.iconEmoji, fontSize = 16.sp)
                                        Text(
                                            text = view.title,
                                            color = if (active) Color.White else Color.White.copy(alpha = 0.78f),
                                            fontSize = 14.sp,
                                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            ) {
                Scaffold(
                    containerColor = MilkBg,
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = state.currentView.title,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                    Text(
                                        text = "${state.currentUser?.nom ?: ""} – ${state.currentUser?.role ?: ""}",
                                        fontSize = 11.5.sp,
                                        color = Gray600
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("open_drawer_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu de navigation",
                                        tint = NavyPrimary
                                    )
                                }
                            },
                            actions = {
                                OutlinedButton(
                                    onClick = { viewModel.logout() },
                                    shape = RoundedCornerShape(7.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .padding(end = 12.dp)
                                        .testTag("logout_button")
                                ) {
                                    Text("Déconnexion", fontSize = 12.sp, color = Gray600)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = WhiteSurface)
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        when (state.currentView) {
                            AppView.DASHBOARD -> DashboardScreen(
                                state = state,
                                onSelectLotForTrace = { lotId -> viewModel.voirTracabilite(lotId) }
                            )
                            AppView.FOURNISSEURS -> FournisseursScreen(state, viewModel)
                            AppView.RECEPTIONS -> ReceptionsScreen(state, viewModel)
                            AppView.LOTS -> LotsScreen(state, viewModel)
                            AppView.DISTRIBUTIONS -> DistributionsScreen(state, viewModel)
                            AppView.CLIENTS -> ClientsScreen(state, viewModel)
                            AppView.TRACABILITE -> TracabiliteScreen(
                                state = state,
                                onQueryChange = { viewModel.updateTraceSearchQuery(it) },
                                onSearch = { viewModel.rechercherLot() },
                                onSelectLot = { viewModel.voirTracabilite(it) }
                            )
                            AppView.PRODUITS -> ProduitsScreen(state, viewModel)
                            AppView.STOCKAGES -> StockagesScreen(state, viewModel)
                            AppView.UTILISATEURS -> UtilisateursScreen(state, viewModel)
                            AppView.JOURNAL_CONNEXIONS -> JournalConnexionsScreen(state)
                        }
                    }
                }
            }
        }

        // Toast Banner at bottom-right
        state.toast?.let { toast ->
            val bg = when (toast.type) {
                "error" -> RedDanger
                "info" -> BlueAccent
                else -> GreenSuccess
            }
            val icon = when (toast.type) {
                "error" -> "✕"
                "info" -> "ℹ"
                else -> "✓"
            }
            Surface(
                color = bg,
                shape = RoundedCornerShape(9.dp),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .systemBarsPadding()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(icon, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(toast.text, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 13.5.sp)
                }
            }
        }
    }
}
