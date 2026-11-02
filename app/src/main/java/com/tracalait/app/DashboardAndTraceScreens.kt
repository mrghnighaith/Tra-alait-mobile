package com.tracalait.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("admin@tracalait.local") }
    var password by remember { mutableStateOf("admin123") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(NavyPrimary, BlueAccent)
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🥛", fontSize = 24.sp)
                }

                Text(
                    text = "TraçaLait",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Text(
                    text = "Connexion à la traçabilité laitière",
                    fontSize = 13.5.sp,
                    color = Gray600
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Email", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Gray600)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        singleLine = true,
                        shape = RoundedCornerShape(7.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input")
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Mot de passe", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Gray600)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(7.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input")
                    )
                }

                Button(
                    onClick = { onLogin(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("login_submit_button"),
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
                ) {
                    Text("Se connecter", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }

                Text(
                    text = "Comptes de démo (cliquez pour remplir) :",
                    fontSize = 12.sp,
                    color = Gray600,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "Admin" to "admin@tracalait.local",
                        "Qualité" to "qualite@tracalait.local",
                        "Production" to "production@tracalait.local"
                    ).forEach { (label, demoEmail) ->
                        AssistChip(
                            onClick = {
                                email = demoEmail
                                password = "admin123"
                            },
                            label = { Text(label, fontSize = 11.5.sp) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(
    state: TracaLaitUiState,
    onSelectLotForTrace: (Int) -> Unit
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.FRANCE) }
    val totalReceptions = state.receptions.size
    val litresAcceptes = state.receptions
        .filter { it.resultatControle == "Accepté" }
        .sumOf { it.quantiteLitres }
    val totalLots = state.lots.size
    val lotsStockes = state.lots.count { it.statut == "Stocké" }
    val livraisonsEffectuees = state.distributions.count { it.statut == "Livrée" }
    val today = remember {
        java.text.SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(java.util.Date())
    }
    val lotsExpires = state.lots.count { it.dateExpiration < today && it.statut != "Distribué" }

    val stats = listOf(
        Triple("🥛", totalReceptions.toString(), "Réceptions totales"),
        Triple("📦", numberFormat.format(litresAcceptes), "Litres acceptés"),
        Triple("🧀", totalLots.toString(), "Lots créés"),
        Triple("🏬", lotsStockes.toString(), "Lots en stock"),
        Triple("🚚", livraisonsEffectuees.toString(), "Livraisons effectuées"),
        Triple("⚠️", lotsExpires.toString(), "Lots expirés")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            stats.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { (icon, value, label) ->
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = icon, fontSize = 22.sp)
                                Text(
                                    text = value,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    color = Gray400,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        SectionHeaderCard(title = "Répartition des lots par statut") {
            val statusCounts = state.lots.groupingBy { it.statut }.eachCount()
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (statusCounts.isEmpty()) {
                    Text("Aucun lot enregistré", color = Gray400, fontSize = 13.5.sp)
                } else {
                    statusCounts.forEach { (statut, count) ->
                        val barColor = when (statut) {
                            "En production" -> AmberWarning
                            "Stocké" -> BlueAccent
                            "Distribué" -> GreenSuccess
                            "Rappelé" -> RedDanger
                            else -> Gray400
                        }
                        val fraction = (count * 0.2f).coerceIn(0.08f, 1f)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(statut, fontSize = 13.5.sp, color = Gray800)
                                Text(count.toString(), fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Gray200)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(barColor)
                                )
                            }
                        }
                    }
                }
            }
        }

        SectionHeaderCard(title = "Accès rapide – Traçabilité des lots récents") {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                state.lots.take(4).forEach { lot ->
                    val prod = state.produits.find { it.id == lot.produitId }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectLotForTrace(lot.id) }
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(lot.numeroLot, fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 14.sp)
                            Text(prod?.nom ?: "Produit", color = Gray600, fontSize = 12.5.sp)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatusBadge(lot.statut)
                            Text("🔍", fontSize = 16.sp)
                        }
                    }
                    HorizontalDivider(color = Gray100)
                }
            }
        }
    }
}

@Composable
fun TracabiliteScreen(
    state: TracaLaitUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onSelectLot: (Int) -> Unit
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.FRANCE) }
    val selectedLot = state.lots.find { it.id == state.selectedTraceLotId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SectionHeaderCard(title = "🔍 Tracer un lot") {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = state.traceSearchQuery,
                        onValueChange = onQueryChange,
                        placeholder = { Text("Entrez un numéro de lot…", fontSize = 13.5.sp, color = Gray400) },
                        singleLine = true,
                        shape = RoundedCornerShape(7.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("trace_search_input")
                    )
                    Button(
                        onClick = onSearch,
                        colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                        shape = RoundedCornerShape(7.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("trace_search_button")
                    ) {
                        Text("Rechercher", fontWeight = FontWeight.SemiBold)
                    }
                }
                Text(
                    text = "Retrouvez l'historique complet d'un lot : réception → production → stockage → distribution.",
                    fontSize = 13.sp,
                    color = Gray400
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.lots.forEach { lot ->
                        FilterChip(
                            selected = lot.id == state.selectedTraceLotId,
                            onClick = { onSelectLot(lot.id) },
                            label = { Text(lot.numeroLot, fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        SectionHeaderCard(title = "Historique de traçabilité") {
            if (selectedLot == null) {
                EmptyStateView(
                    iconEmoji = "🔍",
                    message = "Recherchez ou sélectionnez un lot pour afficher son historique complet"
                )
            } else {
                val produit = state.produits.find { it.id == selectedLot.produitId }
                val reception = state.receptions.find { it.id == selectedLot.receptionId }
                val fournisseur = state.fournisseurs.find { it.id == reception?.fournisseurId }
                val lotStockages = state.stockages.filter { it.lotId == selectedLot.id }
                val lotDistributions = state.distributions.filter { it.lotId == selectedLot.id }

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (reception != null) {
                        TimelineStep(
                            title = "🐄 Réception du lait – ${reception.dateReception}"
                        ) {
                            Text("Fournisseur : ${fournisseur?.nom ?: "Inconnu"}", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = Gray800)
                            Text("Quantité : ${numberFormat.format(reception.quantiteLitres)} L à ${reception.heureReception}", fontSize = 13.sp, color = Gray600)
                            Text("Température : ${reception.temperatureC?.let { "$it °C" } ?: "—"} | pH : ${reception.ph ?: "—"}", fontSize = 13.sp, color = Gray600)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Contrôle :", fontSize = 13.sp, color = Gray600)
                                StatusBadge(reception.resultatControle)
                            }
                        }
                    }

                    TimelineStep(
                        title = "🏭 Production – Lot ${selectedLot.numeroLot}"
                    ) {
                        Text("Produit : ${produit?.nom ?: "Inconnu"} (${produit?.typeProduit ?: ""})", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = Gray800)
                        Text("Date de production : ${selectedLot.dateProduction}", fontSize = 13.sp, color = Gray600)
                        Text("Quantité : ${numberFormat.format(selectedLot.quantiteKg)} kg", fontSize = 13.sp, color = Gray600)
                        Text("Date d'expiration : ${selectedLot.dateExpiration}", fontSize = 13.sp, color = Gray600)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Statut :", fontSize = 13.sp, color = Gray600)
                            StatusBadge(selectedLot.statut)
                        }
                    }

                    if (lotStockages.isNotEmpty()) {
                        TimelineStep(
                            title = "🏬 Stockage (${lotStockages.size} enregistrement(s))"
                        ) {
                            lotStockages.forEach { s ->
                                Surface(
                                    color = Gray50,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text("Zone : ${s.zoneStockage} | Temp. : ${s.temperatureC?.let { "$it °C" } ?: "—"}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Gray800)
                                        Text("Entrée : ${s.dateEntree} → Sortie : ${s.dateSortie ?: "En stock"}", fontSize = 12.5.sp, color = Gray600)
                                    }
                                }
                            }
                        }
                    }

                    if (lotDistributions.isNotEmpty()) {
                        TimelineStep(
                            title = "🚚 Distribution (${lotDistributions.size} livraison(s))"
                        ) {
                            lotDistributions.forEach { d ->
                                val client = state.clients.find { it.id == d.clientId }
                                Surface(
                                    color = Gray50,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("Client : ${client?.nom ?: "Inconnu"} (${client?.typeClient ?: ""})", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Gray800)
                                        Text("Date : ${d.dateLivraison} | Quantité : ${numberFormat.format(d.quantiteKg)} kg | BL : ${d.bonLivraison.ifBlank { "—" }}", fontSize = 12.5.sp, color = Gray600)
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("Statut :", fontSize = 12.5.sp, color = Gray600)
                                            StatusBadge(d.statut)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineStep(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(BlueAccent)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .heightIn(min = 48.dp)
                    .background(Gray200)
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            content()
        }
    }
}
