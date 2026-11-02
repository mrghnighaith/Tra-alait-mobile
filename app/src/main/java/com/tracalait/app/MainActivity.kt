package com.tracalait.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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

private fun iconForView(view: AppView): ImageVector = when (view) {
    AppView.DASHBOARD -> Icons.Default.Dashboard
    AppView.RECEPTIONS -> Icons.Default.LocalDrink
    AppView.LOTS -> Icons.Default.Factory
    AppView.STOCKAGES -> Icons.Default.Inventory2
    AppView.TRACABILITE -> Icons.Default.Search
    AppView.FOURNISSEURS -> Icons.Default.Agriculture
    AppView.DISTRIBUTIONS -> Icons.Default.LocalShipping
    AppView.CLIENTS -> Icons.Default.Storefront
    AppView.PRODUITS -> Icons.Default.Category
    AppView.UTILISATEURS -> Icons.Default.People
    AppView.JOURNAL_CONNEXIONS -> Icons.Default.History
}

private fun countBadgeForView(view: AppView, state: TracaLaitUiState): String? = when (view) {
    AppView.RECEPTIONS -> state.receptions.size.toString()
    AppView.LOTS -> state.lots.size.toString()
    AppView.STOCKAGES -> state.stockages.size.toString()
    AppView.FOURNISSEURS -> state.fournisseurs.size.toString()
    AppView.DISTRIBUTIONS -> state.distributions.size.toString()
    AppView.CLIENTS -> state.clients.size.toString()
    AppView.PRODUITS -> state.produits.size.toString()
    AppView.UTILISATEURS -> state.utilisateurs.size.toString()
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TracaLaitApp(viewModel: TracaLaitViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.toast?.id) {
        if (state.toast != null) {
            delay(3000)
            viewModel.clearToast()
        }
    }

    BackHandler(
        enabled = state.currentUser != null && (drawerState.isOpen || state.currentView != AppView.DASHBOARD)
    ) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            viewModel.navigateTo(AppView.DASHBOARD)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.currentUser == null) {
            LoginScreen(onLogin = { email, password -> viewModel.login(email, password) })
        } else {
            val isAdmin = PermissionHelper.isAdmin(state.currentUser)

            val primaryDrawerViews = listOf(
                AppView.DASHBOARD,
                AppView.RECEPTIONS,
                AppView.LOTS,
                AppView.STOCKAGES,
                AppView.TRACABILITE
            )

            val secondaryDrawerViews = listOf(
                AppView.FOURNISSEURS,
                AppView.DISTRIBUTIONS,
                AppView.CLIENTS,
                AppView.PRODUITS
            )

            val adminDrawerViews = if (isAdmin) {
                listOf(AppView.UTILISATEURS, AppView.JOURNAL_CONNEXIONS)
            } else {
                emptyList()
            }

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = NavyPrimary,
                        drawerContentColor = Color.White,
                        modifier = Modifier
                            .width(290.dp)
                            .testTag("navigation_drawer")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.15f))
                                .padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(BlueAccent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🥛", fontSize = 20.sp)
                                }
                                Column {
                                    Text(
                                        text = "TraçaLait",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = "Traçabilité Laitière v1.0",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Surface(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                    Text(
                                        text = state.currentUser?.nom ?: "",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = state.currentUser?.role ?: "",
                                        color = BlueLight,
                                        fontSize = 11.5.sp
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "OPÉRATIONS PRINCIPALES",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 12.dp, top = 6.dp, bottom = 4.dp)
                            )

                            primaryDrawerViews.forEach { view ->
                                DrawerMenuItem(
                                    view = view,
                                    selected = state.currentView == view,
                                    badgeText = countBadgeForView(view, state),
                                    onClick = {
                                        viewModel.navigateTo(view)
                                        scope.launch { drawerState.close() }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                            Text(
                                text = "CHAÎNE & RÉFÉRENTIELS",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 12.dp, top = 10.dp, bottom = 4.dp)
                            )

                            secondaryDrawerViews.forEach { view ->
                                DrawerMenuItem(
                                    view = view,
                                    selected = state.currentView == view,
                                    badgeText = countBadgeForView(view, state),
                                    onClick = {
                                        viewModel.navigateTo(view)
                                        scope.launch { drawerState.close() }
                                    }
                                )
                            }

                            if (adminDrawerViews.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                                Text(
                                    text = "ADMINISTRATION",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 12.dp, top = 10.dp, bottom = 4.dp)
                                )

                                adminDrawerViews.forEach { view ->
                                    DrawerMenuItem(
                                        view = view,
                                        selected = state.currentView == view,
                                        badgeText = countBadgeForView(view, state),
                                        onClick = {
                                            viewModel.navigateTo(view)
                                            scope.launch { drawerState.close() }
                                        }
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                        NavigationDrawerItem(
                            label = { Text("Changer de compte / Déconnexion", fontWeight = FontWeight.Medium) },
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Déconnexion"
                                )
                            },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                viewModel.logout()
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = Color.Transparent,
                                unselectedTextColor = RedLight,
                                unselectedIconColor = RedLight
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
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
                                        contentDescription = "Ouvrir le menu de navigation",
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
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = WhiteSurface,
                            tonalElevation = 4.dp
                        ) {
                            listOf(
                                AppView.DASHBOARD to "Accueil",
                                AppView.RECEPTIONS to "Réception",
                                AppView.LOTS to "Production",
                                AppView.STOCKAGES to "Stockage",
                                AppView.TRACABILITE to "Recherche"
                            ).forEach { (view, shortLabel) ->
                                NavigationBarItem(
                                    selected = state.currentView == view,
                                    onClick = { viewModel.navigateTo(view) },
                                    icon = {
                                        Icon(
                                            imageVector = iconForView(view),
                                            contentDescription = shortLabel
                                        )
                                    },
                                    label = { Text(shortLabel, fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = BlueAccent,
                                        selectedTextColor = BlueAccent,
                                        indicatorColor = BlueLight,
                                        unselectedIconColor = Gray600,
                                        unselectedTextColor = Gray600
                                    ),
                                    modifier = Modifier.testTag("bottom_nav_${view.id}")
                                )
                            }
                        }
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
                    .padding(bottom = 84.dp, end = 20.dp, start = 20.dp)
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

@Composable
private fun DrawerMenuItem(
    view: AppView,
    selected: Boolean,
    badgeText: String?,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Text(
                text = view.title,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        icon = {
            Icon(
                imageVector = iconForView(view),
                contentDescription = view.title
            )
        },
        badge = if (badgeText != null) {
            {
                Surface(
                    color = if (selected) Color.White.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        } else null,
        selected = selected,
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = BlueAccent,
            selectedIconColor = Color.White,
            selectedTextColor = Color.White,
            unselectedContainerColor = Color.Transparent,
            unselectedIconColor = Color.White.copy(alpha = 0.78f),
            unselectedTextColor = Color.White.copy(alpha = 0.85f)
        ),
        modifier = Modifier.testTag("nav_${view.id}")
    )
}
