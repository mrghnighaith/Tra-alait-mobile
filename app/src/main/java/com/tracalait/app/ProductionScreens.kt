package com.tracalait.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FournisseursScreen(state: TracaLaitUiState, viewModel: TracaLaitViewModel) {
    val canWrite = PermissionHelper.canWrite(state.currentUser, "fournisseurs")
    var editingItem by remember { mutableStateOf<Fournisseur?>(null) }
    var showModal by remember { mutableStateOf(false) }

    SectionHeaderCard(
        title = "Fournisseurs de lait",
        actionButtonText = if (canWrite) "+ Nouveau" else null,
        onActionClick = {
            editingItem = null
            showModal = true
        },
        actionTag = "add_fournisseur_button"
    ) {
        if (state.fournisseurs.isEmpty()) {
            EmptyStateView("🏭", "Aucun fournisseur enregistré")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.fournisseurs, key = { it.id }) { f ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(f.nom, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = NavyPrimary)
                            Text("📍 ${f.adresse.ifBlank { "—" }}", fontSize = 12.5.sp, color = Gray600)
                            Text("📞 ${f.telephone.ifBlank { "—" }} | ✉️ ${f.email.ifBlank { "—" }}", fontSize = 12.sp, color = Gray400)
                        }
                        ItemActionButtons(
                            canWrite = canWrite,
                            onEdit = {
                                editingItem = f
                                showModal = true
                            },
                            onDelete = { viewModel.deleteFournisseur(f.id) }
                        )
                    }
                    HorizontalDivider(color = Gray100)
                }
            }
        }
    }

    if (showModal) {
        var nom by remember(editingItem) { mutableStateOf(editingItem?.nom ?: "") }
        var adresse by remember(editingItem) { mutableStateOf(editingItem?.adresse ?: "") }
        var tel by remember(editingItem) { mutableStateOf(editingItem?.telephone ?: "") }
        var email by remember(editingItem) { mutableStateOf(editingItem?.email ?: "") }

        AlertDialog(
            onDismissRequest = { showModal = false },
            title = { Text(if (editingItem == null) "Nouveau fournisseur" else "Modifier fournisseur", fontWeight = FontWeight.Bold, color = NavyPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    LabeledTextField("Nom *", nom, { nom = it }, "Nom du fournisseur")
                    LabeledTextField("Adresse", adresse, { adresse = it }, "Adresse complète")
                    LabeledTextField("Téléphone", tel, { tel = it }, "+216 XX XXX XXX")
                    LabeledTextField("Email", email, { email = it }, "contact@exemple.tn")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveFournisseur(editingItem?.id, nom, adresse, tel, email)
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
fun ReceptionsScreen(state: TracaLaitUiState, viewModel: TracaLaitViewModel) {
    val canWrite = PermissionHelper.canWrite(state.currentUser, "receptions")
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.FRANCE) }
    var editingItem by remember { mutableStateOf<Reception?>(null) }
    var showModal by remember { mutableStateOf(false) }

    SectionHeaderCard(
        title = "Réceptions du lait cru",
        actionButtonText = if (canWrite) "+ Nouvelle" else null,
        onActionClick = {
            editingItem = null
            showModal = true
        },
        actionTag = "add_reception_button"
    ) {
        if (state.receptions.isEmpty()) {
            EmptyStateView("🥛", "Aucune réception enregistrée")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.receptions, key = { it.id }) { r ->
                    val fournisseur = state.fournisseurs.find { it.id == r.fournisseurId }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(fournisseur?.nom ?: "Fournisseur", fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = NavyPrimary)
                                StatusBadge(r.resultatControle)
                            }
                            Text("${r.dateReception} à ${r.heureReception} • ${numberFormat.format(r.quantiteLitres)} L", fontSize = 13.sp, color = Gray800)
                            Text("Temp: ${r.temperatureC?.let { "$it °C" } ?: "—"} | pH: ${r.ph ?: "—"} | MG: ${r.tauxMatieresGrasses?.let { "$it %" } ?: "—"}", fontSize = 12.sp, color = Gray400)
                        }
                        ItemActionButtons(
                            canWrite = canWrite,
                            onEdit = {
                                editingItem = r
                                showModal = true
                            },
                            onDelete = { viewModel.deleteReception(r.id) }
                        )
                    }
                    HorizontalDivider(color = Gray100)
                }
            }
        }
    }

    if (showModal) {
        val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(Date()) }
        var fournisseurId by remember(editingItem) {
            mutableStateOf((editingItem?.fournisseurId ?: state.fournisseurs.firstOrNull()?.id ?: 1).toString())
        }
        var date by remember(editingItem) { mutableStateOf(editingItem?.dateReception ?: today) }
        var heure by remember(editingItem) { mutableStateOf(editingItem?.heureReception ?: "07:00") }
        var quantite by remember(editingItem) { mutableStateOf(editingItem?.quantiteLitres?.toString() ?: "") }
        var temp by remember(editingItem) { mutableStateOf(editingItem?.temperatureC?.toString() ?: "") }
        var ph by remember(editingItem) { mutableStateOf(editingItem?.ph?.toString() ?: "") }
        var mg by remember(editingItem) { mutableStateOf(editingItem?.tauxMatieresGrasses?.toString() ?: "") }
        var controle by remember(editingItem) { mutableStateOf(editingItem?.resultatControle ?: "En attente") }
        var obs by remember(editingItem) { mutableStateOf(editingItem?.observations ?: "") }

        AlertDialog(
            onDismissRequest = { showModal = false },
            title = { Text(if (editingItem == null) "Nouvelle réception" else "Modifier réception", fontWeight = FontWeight.Bold, color = NavyPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DropdownSelector(
                        label = "Fournisseur *",
                        options = state.fournisseurs.map { it.id.toString() to it.nom },
                        selectedValue = fournisseurId,
                        onValueChange = { fournisseurId = it }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            LabeledTextField("Date *", date, { date = it }, "YYYY-MM-DD")
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            LabeledTextField("Heure *", heure, { heure = it }, "07:00")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            LabeledTextField("Quantité (L) *", quantite, { quantite = it }, "ex: 1500")
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            LabeledTextField("Temp. (°C)", temp, { temp = it }, "ex: 4.2")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            LabeledTextField("pH", ph, { ph = it }, "ex: 6.7")
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            LabeledTextField("MG (%)", mg, { mg = it }, "ex: 3.8")
                        }
                    }
                    DropdownSelector(
                        label = "Résultat contrôle",
                        options = listOf("En attente" to "En attente", "Accepté" to "Accepté", "Refusé" to "Refusé"),
                        selectedValue = controle,
                        onValueChange = { controle = it }
                    )
                    LabeledTextField("Observations", obs, { obs = it }, "Notes éventuelles…")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveReception(
                            id = editingItem?.id,
                            fournisseurId = fournisseurId.toIntOrNull() ?: 1,
                            dateReception = date,
                            heureReception = heure,
                            quantiteLitres = quantite.toDoubleOrNull(),
                            temperatureC = temp.toDoubleOrNull(),
                            ph = ph.toDoubleOrNull(),
                            tauxMg = mg.toDoubleOrNull(),
                            resultatControle = controle,
                            observations = obs
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
fun LotsScreen(state: TracaLaitUiState, viewModel: TracaLaitViewModel) {
    val canWrite = PermissionHelper.canWrite(state.currentUser, "lots")
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(Date()) }
    var editingItem by remember { mutableStateOf<LotProduction?>(null) }
    var showModal by remember { mutableStateOf(false) }

    SectionHeaderCard(
        title = "Lots de production",
        actionButtonText = if (canWrite) "+ Nouveau lot" else null,
        onActionClick = {
            editingItem = null
            showModal = true
        },
        actionTag = "add_lot_button"
    ) {
        if (state.lots.isEmpty()) {
            EmptyStateView("🧀", "Aucun lot de production")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.lots, key = { it.id }) { l ->
                    val produit = state.produits.find { it.id == l.produitId }
                    val reception = state.receptions.find { it.id == l.receptionId }
                    val fournisseur = state.fournisseurs.find { it.id == reception?.fournisseurId }
                    val expired = l.dateExpiration < today

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(l.numeroLot, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = NavyPrimary)
                                StatusBadge(l.statut)
                            }
                            Text("${produit?.nom ?: "Produit"} (${produit?.typeProduit ?: ""}) • ${l.quantiteKg} kg", fontSize = 13.sp, color = Gray800)
                            Text("Fournisseur : ${fournisseur?.nom ?: "—"} | Prod : ${l.dateProduction}", fontSize = 12.sp, color = Gray600)
                            Text(
                                text = "Expiration : ${l.dateExpiration}${if (expired) " ⚠️" else ""}",
                                fontSize = 12.sp,
                                color = if (expired) RedDanger else Gray400,
                                fontWeight = if (expired) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                        ItemActionButtons(
                            canWrite = canWrite,
                            onEdit = {
                                editingItem = l
                                showModal = true
                            },
                            onDelete = { viewModel.deleteLot(l.id) },
                            extraLeadingButton = {
                                OutlinedButton(
                                    onClick = { viewModel.voirTracabilite(l.id) },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(7.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("🔍", fontSize = 13.sp)
                                }
                            }
                        )
                    }
                    HorizontalDivider(color = Gray100)
                }
            }
        }
    }

    if (showModal) {
        var numero by remember(editingItem) {
            mutableStateOf(editingItem?.numeroLot ?: "LOT-2026-${(System.currentTimeMillis() % 10000).toString().padStart(4, '0')}")
        }
        var produitId by remember(editingItem) {
            mutableStateOf((editingItem?.produitId ?: state.produits.firstOrNull()?.id ?: 1).toString())
        }
        var receptionId by remember(editingItem) {
            mutableStateOf((editingItem?.receptionId ?: state.receptions.firstOrNull()?.id ?: 1).toString())
        }
        var dateProd by remember(editingItem) { mutableStateOf(editingItem?.dateProduction ?: today) }
        var quantite by remember(editingItem) { mutableStateOf(editingItem?.quantiteKg?.toString() ?: "") }
        var dateExp by remember(editingItem) { mutableStateOf(editingItem?.dateExpiration ?: today) }
        var statut by remember(editingItem) { mutableStateOf(editingItem?.statut ?: "En production") }
        var obs by remember(editingItem) { mutableStateOf(editingItem?.observations ?: "") }

        AlertDialog(
            onDismissRequest = { showModal = false },
            title = { Text(if (editingItem == null) "Nouveau lot de production" else "Modifier lot", fontWeight = FontWeight.Bold, color = NavyPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LabeledTextField("Numéro de lot *", numero, { numero = it }, "LOT-2026-XXX")
                    DropdownSelector(
                        label = "Produit *",
                        options = state.produits.map { it.id.toString() to it.nom },
                        selectedValue = produitId,
                        onValueChange = { produitId = it }
                    )
                    DropdownSelector(
                        label = "Réception lait *",
                        options = state.receptions.map { r ->
                            val f = state.fournisseurs.find { it.id == r.fournisseurId }
                            r.id.toString() to "${r.dateReception} – ${f?.nom ?: ""} (${r.quantiteLitres}L)"
                        },
                        selectedValue = receptionId,
                        onValueChange = { receptionId = it }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            LabeledTextField("Date prod. *", dateProd, { dateProd = it }, "YYYY-MM-DD")
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            LabeledTextField("Quantité (kg) *", quantite, { quantite = it }, "ex: 500")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            LabeledTextField("Expiration *", dateExp, { dateExp = it }, "YYYY-MM-DD")
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DropdownSelector(
                                label = "Statut",
                                options = listOf("En production", "Stocké", "Distribué", "Rappelé").map { it to it },
                                selectedValue = statut,
                                onValueChange = { statut = it }
                            )
                        }
                    }
                    LabeledTextField("Observations", obs, { obs = it }, "Notes…")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveLot(
                            id = editingItem?.id,
                            numeroLot = numero,
                            produitId = produitId.toIntOrNull() ?: 1,
                            receptionId = receptionId.toIntOrNull() ?: 1,
                            dateProduction = dateProd,
                            quantiteKg = quantite.toDoubleOrNull(),
                            dateExpiration = dateExp,
                            statut = statut,
                            observations = obs
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
fun DistributionsScreen(state: TracaLaitUiState, viewModel: TracaLaitViewModel) {
    val canWrite = PermissionHelper.canWrite(state.currentUser, "distributions")
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.FRANCE) }
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(Date()) }
    var editingItem by remember { mutableStateOf<Distribution?>(null) }
    var showModal by remember { mutableStateOf(false) }

    SectionHeaderCard(
        title = "Distributions",
        actionButtonText = if (canWrite) "+ Nouvelle" else null,
        onActionClick = {
            editingItem = null
            showModal = true
        },
        actionTag = "add_distribution_button"
    ) {
        if (state.distributions.isEmpty()) {
            EmptyStateView("🚚", "Aucune distribution enregistrée")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.distributions, key = { it.id }) { d ->
                    val lot = state.lots.find { it.id == d.lotId }
                    val prod = state.produits.find { it.id == lot?.produitId }
                    val client = state.clients.find { it.id == d.clientId }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(d.bonLivraison.ifBlank { "BL—" }, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = NavyPrimary)
                                StatusBadge(d.statut)
                            }
                            Text("Lot : ${lot?.numeroLot ?: "—"} (${prod?.nom ?: ""})", fontSize = 13.sp, color = Gray800)
                            Text("Client : ${client?.nom ?: "—"} • ${d.dateLivraison} • ${numberFormat.format(d.quantiteKg)} kg", fontSize = 12.sp, color = Gray600)
                        }
                        ItemActionButtons(
                            canWrite = canWrite,
                            onEdit = {
                                editingItem = d
                                showModal = true
                            },
                            onDelete = { viewModel.deleteDistribution(d.id) }
                        )
                    }
                    HorizontalDivider(color = Gray100)
                }
            }
        }
    }

    if (showModal) {
        var lotId by remember(editingItem) {
            mutableStateOf((editingItem?.lotId ?: state.lots.firstOrNull()?.id ?: 1).toString())
        }
        var clientId by remember(editingItem) {
            mutableStateOf((editingItem?.clientId ?: state.clients.firstOrNull()?.id ?: 1).toString())
        }
        var dateLiv by remember(editingItem) { mutableStateOf(editingItem?.dateLivraison ?: today) }
        var quantite by remember(editingItem) { mutableStateOf(editingItem?.quantiteKg?.toString() ?: "") }
        var bl by remember(editingItem) {
            mutableStateOf(editingItem?.bonLivraison ?: "BL-2026-${(System.currentTimeMillis() % 1000).toString().padStart(3, '0')}")
        }
        var statut by remember(editingItem) { mutableStateOf(editingItem?.statut ?: "Planifiée") }

        AlertDialog(
            onDismissRequest = { showModal = false },
            title = { Text(if (editingItem == null) "Nouvelle distribution" else "Modifier distribution", fontWeight = FontWeight.Bold, color = NavyPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DropdownSelector(
                        label = "Lot *",
                        options = state.lots.map { l ->
                            val p = state.produits.find { it.id == l.produitId }
                            l.id.toString() to "${l.numeroLot} – ${p?.nom ?: ""}"
                        },
                        selectedValue = lotId,
                        onValueChange = { lotId = it }
                    )
                    DropdownSelector(
                        label = "Client *",
                        options = state.clients.map { it.id.toString() to it.nom },
                        selectedValue = clientId,
                        onValueChange = { clientId = it }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            LabeledTextField("Date livraison *", dateLiv, { dateLiv = it }, "YYYY-MM-DD")
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            LabeledTextField("Quantité (kg) *", quantite, { quantite = it }, "ex: 200")
                        }
                    }
                    LabeledTextField("Bon de livraison", bl, { bl = it }, "BL-2026-XXX")
                    DropdownSelector(
                        label = "Statut",
                        options = listOf("Planifiée", "Livrée", "Annulée").map { it to it },
                        selectedValue = statut,
                        onValueChange = { statut = it }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveDistribution(
                            id = editingItem?.id,
                            lotId = lotId.toIntOrNull() ?: 1,
                            clientId = clientId.toIntOrNull() ?: 1,
                            dateLivraison = dateLiv,
                            quantiteKg = quantite.toDoubleOrNull(),
                            bonLivraison = bl,
                            statut = statut
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
