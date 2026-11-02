package com.tracalait.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracalait.app.model.*
import com.tracalait.app.ui.components.*
import com.tracalait.app.ui.theme.*
import com.tracalait.app.viewmodel.TracaLaitUiState
import com.tracalait.app.viewmodel.TracaLaitViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClientsScreen(state: TracaLaitUiState, viewModel: TracaLaitViewModel) {
    val canWrite = PermissionHelper.canWrite(state.currentUser, "clients")
    var editingItem by remember { mutableStateOf<Client?>(null) }
    var showModal by remember { mutableStateOf(false) }

    SectionHeaderCard(
        title = "Clients / Distributeurs",
        actionButtonText = if (canWrite) "+ Nouveau" else null,
        onActionClick = {
            editingItem = null
            showModal = true
        },
        actionTag = "add_client_button"
    ) {
        if (state.clients.isEmpty()) {
            EmptyStateView("🏪", "Aucun client enregistré")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.clients, key = { it.id }) { c ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(c.nom, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = NavyPrimary)
                                StatusBadge(c.typeClient)
                            }
                            Text("📍 ${c.adresse.ifBlank { "—" }}", fontSize = 12.5.sp, color = Gray600)
                            Text("📞 ${c.telephone.ifBlank { "—" }} | ✉️ ${c.email.ifBlank { "—" }}", fontSize = 12.sp, color = Gray400)
                        }
                        ItemActionButtons(
                            canWrite = canWrite,
                            onEdit = {
                                editingItem = c
                                showModal = true
                            },
                            onDelete = { viewModel.deleteClient(c.id) }
                        )
                    }
                    HorizontalDivider(color = Gray100)
                }
            }
        }
    }

    if (showModal) {
        var nom by remember(editingItem) { mutableStateOf(editingItem?.nom ?: "") }
        var type by remember(editingItem) { mutableStateOf(editingItem?.typeClient ?: "Supermarché") }
        var adresse by remember(editingItem) { mutableStateOf(editingItem?.adresse ?: "") }
        var tel by remember(editingItem) { mutableStateOf(editingItem?.telephone ?: "") }
        var email by remember(editingItem) { mutableStateOf(editingItem?.email ?: "") }

        AlertDialog(
            onDismissRequest = { showModal = false },
            title = { Text(if (editingItem == null) "Nouveau client" else "Modifier client", fontWeight = FontWeight.Bold, color = NavyPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    LabeledTextField("Nom *", nom, { nom = it }, "Nom du client")
                    DropdownSelector(
                        label = "Type *",
                        options = listOf("Supermarché", "Restaurant", "Particulier", "Épicerie", "Autre").map { it to it },
                        selectedValue = type,
                        onValueChange = { type = it }
                    )
                    LabeledTextField("Adresse", adresse, { adresse = it }, "Adresse complète")
                    LabeledTextField("Téléphone", tel, { tel = it }, "+216 XX XXX XXX")
                    LabeledTextField("Email", email, { email = it }, "contact@exemple.tn")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveClient(editingItem?.id, nom, type, adresse, tel, email)
                        showModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
                ) { Text("Enregistrer") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showModal = false }) { Text("Annuler") }
            }
        )
    }
}

@Composable
fun ProduitsScreen(state: TracaLaitUiState, viewModel: TracaLaitViewModel) {
    val canWrite = PermissionHelper.canWrite(state.currentUser, "produits")
    var editingItem by remember { mutableStateOf<Produit?>(null) }
    var showModal by remember { mutableStateOf(false) }

    SectionHeaderCard(
        title = "Produits laitiers",
        actionButtonText = if (canWrite) "+ Nouveau produit" else null,
        onActionClick = {
            editingItem = null
            showModal = true
        },
        actionTag = "add_produit_button"
    ) {
        if (state.produits.isEmpty()) {
            EmptyStateView("🥛", "Aucun produit enregistré")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.produits, key = { it.id }) { p ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(p.nom, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = NavyPrimary)
                                StatusBadge(p.typeProduit)
                            }
                            Text(p.description.ifBlank { "—" }, fontSize = 12.5.sp, color = Gray600)
                        }
                        ItemActionButtons(
                            canWrite = canWrite,
                            onEdit = {
                                editingItem = p
                                showModal = true
                            },
                            onDelete = { viewModel.deleteProduit(p.id) }
                        )
                    }
                    HorizontalDivider(color = Gray100)
                }
            }
        }
    }

    if (showModal) {
        var nom by remember(editingItem) { mutableStateOf(editingItem?.nom ?: "") }
        var type by remember(editingItem) { mutableStateOf(editingItem?.typeProduit ?: "Lait pasteurisé") }
        var desc by remember(editingItem) { mutableStateOf(editingItem?.description ?: "") }

        AlertDialog(
            onDismissRequest = { showModal = false },
            title = { Text(if (editingItem == null) "Nouveau produit" else "Modifier produit", fontWeight = FontWeight.Bold, color = NavyPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    LabeledTextField("Nom *", nom, { nom = it }, "Nom du produit")
                    DropdownSelector(
                        label = "Type *",
                        options = listOf("Lait cru", "Lait pasteurisé", "Fromage", "Yaourt", "Beurre", "Crème", "Autre").map { it to it },
                        selectedValue = type,
                        onValueChange = { type = it }
                    )
                    LabeledTextField("Description", desc, { desc = it }, "Description du produit…", singleLine = false)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveProduit(editingItem?.id, nom, type, desc)
                        showModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
                ) { Text("Enregistrer") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showModal = false }) { Text("Annuler") }
            }
        )
    }
}

@Composable
fun StockagesScreen(state: TracaLaitUiState, viewModel: TracaLaitViewModel) {
    val canWrite = PermissionHelper.canWrite(state.currentUser, "stockages")
    var editingItem by remember { mutableStateOf<Stockage?>(null) }
    var showModal by remember { mutableStateOf(false) }

    SectionHeaderCard(
        title = "Stockages",
        actionButtonText = if (canWrite) "+ Nouveau stockage" else null,
        onActionClick = {
            editingItem = null
            showModal = true
        },
        actionTag = "add_stockage_button"
    ) {
        if (state.stockages.isEmpty()) {
            EmptyStateView("📦", "Aucun stockage enregistré")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.stockages, key = { it.id }) { s ->
                    val lot = state.lots.find { it.id == s.lotId }
                    val prod = state.produits.find { it.id == lot?.produitId }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(lot?.numeroLot ?: "Lot", fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = NavyPrimary)
                                if (s.dateSortie == null) {
                                    StatusBadge("En stock")
                                }
                            }
                            Text("${prod?.nom ?: "Produit"} • ${s.zoneStockage} (${s.temperatureC?.let { "$it °C" } ?: "—"})", fontSize = 13.sp, color = Gray800)
                            Text("Entrée : ${s.dateEntree} → Sortie : ${s.dateSortie ?: "En stock"}", fontSize = 12.sp, color = Gray400)
                        }
                        ItemActionButtons(
                            canWrite = canWrite,
                            onEdit = {
                                editingItem = s
                                showModal = true
                            },
                            onDelete = { viewModel.deleteStockage(s.id) }
                        )
                    }
                    HorizontalDivider(color = Gray100)
                }
            }
        }
    }

    if (showModal) {
        val nowStr = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.FRANCE).format(Date()) }
        var lotId by remember(editingItem) {
            mutableStateOf((editingItem?.lotId ?: state.lots.firstOrNull()?.id ?: 1).toString())
        }
        var zone by remember(editingItem) { mutableStateOf(editingItem?.zoneStockage ?: "") }
        var temp by remember(editingItem) { mutableStateOf(editingItem?.temperatureC?.toString() ?: "") }
        var entree by remember(editingItem) { mutableStateOf(editingItem?.dateEntree ?: nowStr) }
        var sortie by remember(editingItem) { mutableStateOf(editingItem?.dateSortie ?: "") }

        AlertDialog(
            onDismissRequest = { showModal = false },
            title = { Text(if (editingItem == null) "Nouveau stockage" else "Modifier stockage", fontWeight = FontWeight.Bold, color = NavyPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DropdownSelector(
                        label = "Lot de production *",
                        options = state.lots.map { l ->
                            val p = state.produits.find { it.id == l.produitId }
                            l.id.toString() to "${l.numeroLot} – ${p?.nom ?: ""}"
                        },
                        selectedValue = lotId,
                        onValueChange = { lotId = it }
                    )
                    LabeledTextField("Zone de stockage *", zone, { zone = it }, "ex : Zone A – Chambre froide 1")
                    LabeledTextField("Température (°C)", temp, { temp = it }, "ex : 4.0")
                    LabeledTextField("Date d'entrée *", entree, { entree = it }, "YYYY-MM-DD HH:mm")
                    LabeledTextField("Date de sortie", sortie, { sortie = it }, "Laisser vide si en stock")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveStockage(
                            id = editingItem?.id,
                            lotId = lotId.toIntOrNull() ?: 1,
                            zoneStockage = zone,
                            temperatureC = temp.toDoubleOrNull(),
                            dateEntree = entree,
                            dateSortie = sortie.takeIf { it.isNotBlank() }
                        )
                        showModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
                ) { Text("Enregistrer") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showModal = false }) { Text("Annuler") }
            }
        )
    }
}

@Composable
fun UtilisateursScreen(state: TracaLaitUiState, viewModel: TracaLaitViewModel) {
    var editingItem by remember { mutableStateOf<Utilisateur?>(null) }
    var showModal by remember { mutableStateOf(false) }

    SectionHeaderCard(
        title = "Gestion des utilisateurs",
        actionButtonText = "+ Nouvel utilisateur",
        onActionClick = {
            editingItem = null
            showModal = true
        },
        actionTag = "add_utilisateur_button"
    ) {
        if (state.utilisateurs.isEmpty()) {
            EmptyStateView("👤", "Aucun utilisateur enregistré")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.utilisateurs, key = { it.id }) { u ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(u.nom, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = NavyPrimary)
                            Text(u.email, fontSize = 12.5.sp, color = Gray600)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                StatusBadge(u.role)
                                StatusBadge(if (u.actif == 1) "Actif" else "Inactif")
                            }
                        }
                        ItemActionButtons(
                            canWrite = true,
                            onEdit = {
                                editingItem = u
                                showModal = true
                            },
                            onDelete = { viewModel.deleteUtilisateur(u.id) }
                        )
                    }
                    HorizontalDivider(color = Gray100)
                }
            }
        }
    }

    if (showModal) {
        var nom by remember(editingItem) { mutableStateOf(editingItem?.nom ?: "") }
        var email by remember(editingItem) { mutableStateOf(editingItem?.email ?: "") }
        var role by remember(editingItem) { mutableStateOf(editingItem?.role ?: "Responsable Qualité") }
        var actif by remember(editingItem) { mutableStateOf((editingItem?.actif ?: 1).toString()) }
        var password by remember(editingItem) { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showModal = false },
            title = { Text(if (editingItem == null) "Nouvel utilisateur" else "Modifier utilisateur", fontWeight = FontWeight.Bold, color = NavyPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LabeledTextField("Nom *", nom, { nom = it }, "Nom complet")
                    LabeledTextField("Email *", email, { email = it }, "utilisateur@exemple.tn")
                    DropdownSelector(
                        label = "Rôle *",
                        options = listOf("Administrateur", "Responsable Qualité", "Responsable Production").map { it to it },
                        selectedValue = role,
                        onValueChange = { role = it }
                    )
                    DropdownSelector(
                        label = "Statut",
                        options = listOf("1" to "Actif", "0" to "Inactif"),
                        selectedValue = actif,
                        onValueChange = { actif = it }
                    )
                    LabeledTextField(
                        label = "Mot de passe",
                        value = password,
                        onValueChange = { password = it },
                        placeholder = if (editingItem != null) "Laisser vide pour conserver" else "Minimum 6 caractères"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveUtilisateur(
                            id = editingItem?.id,
                            nom = nom,
                            email = email,
                            role = role,
                            actif = actif.toIntOrNull() ?: 1,
                            passwordPlain = password
                        )
                        showModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
                ) { Text("Enregistrer") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showModal = false }) { Text("Annuler") }
            }
        )
    }
}

@Composable
fun JournalConnexionsScreen(state: TracaLaitUiState) {
    SectionHeaderCard(title = "Journal des connexions") {
        if (state.journalConnexions.isEmpty()) {
            EmptyStateView("🕒", "Aucune connexion enregistrée")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.journalConnexions, key = { it.id }) { log ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(log.nom, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
                                StatusBadge(log.action)
                            }
                            Text("${log.email} • ${log.role}", fontSize = 12.5.sp, color = Gray600)
                            Text("${log.createdAt} • IP: ${log.adresseIp}", fontSize = 11.5.sp, color = Gray400)
                        }
                    }
                    HorizontalDivider(color = Gray100)
                }
            }
        }
    }
}
