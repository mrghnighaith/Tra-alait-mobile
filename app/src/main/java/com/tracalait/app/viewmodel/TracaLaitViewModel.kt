package com.tracalait.app.viewmodel

import androidx.lifecycle.ViewModel
import com.tracalait.app.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class TracaLaitUiState(
    val currentUser: Utilisateur? = null,
    val currentView: AppView = AppView.DASHBOARD,
    val selectedTraceLotId: Int? = null,
    val traceSearchQuery: String = "",
    val toast: ToastMessage? = null,
    val utilisateurs: List<Utilisateur> = emptyList(),
    val journalConnexions: List<JournalConnexion> = emptyList(),
    val fournisseurs: List<Fournisseur> = emptyList(),
    val receptions: List<Reception> = emptyList(),
    val produits: List<Produit> = emptyList(),
    val lots: List<LotProduction> = emptyList(),
    val stockages: List<Stockage> = emptyList(),
    val clients: List<Client> = emptyList(),
    val distributions: List<Distribution> = emptyList()
)

class TracaLaitViewModel : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.FRANCE)

    private fun daysOffset(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return dateFormat.format(cal.time)
    }

    private fun dateTimeOffset(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return dateTimeFormat.format(cal.time)
    }

    private fun nowDateTime(): String = dateTimeFormat.format(Date())

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<TracaLaitUiState> = _uiState.asStateFlow()

    private fun createInitialState(): TracaLaitUiState {
        val now = nowDateTime()
        return TracaLaitUiState(
            utilisateurs = listOf(
                Utilisateur(1, "Administrateur", "admin@tracalait.local", "admin123", "Administrateur", 1, now),
                Utilisateur(2, "Responsable Qualité", "qualite@tracalait.local", "admin123", "Responsable Qualité", 1, now),
                Utilisateur(3, "Responsable Production", "production@tracalait.local", "admin123", "Responsable Production", 1, now)
            ),
            fournisseurs = listOf(
                Fournisseur(1, "Ferme Ben Salem", "Route de Tunis Km 12, Manouba", "+216 71 234 567", "bensalem@ferme.tn"),
                Fournisseur(2, "Élevage Trabelsi", "Douar Ain Draham, Jendouba", "+216 78 345 678", "trabelsi@elevage.tn"),
                Fournisseur(3, "Coopérative Sfax Lait", "Zone Industrielle Sfax", "+216 74 456 789", "sfaxlait@coop.tn")
            ),
            produits = listOf(
                Produit(1, "Lait Entier 1L", "Lait pasteurisé", "Lait entier pasteurisé conditionné en bouteille 1L"),
                Produit(2, "Yaourt Nature 500g", "Yaourt", "Yaourt nature ferme en pot plastique 500g"),
                Produit(3, "Fromage Frais 250g", "Fromage", "Fromage frais onctueux en barquette 250g"),
                Produit(4, "Beurre Doux 200g", "Beurre", "Beurre doux de qualité supérieure")
            ),
            clients = listOf(
                Client(1, "Carrefour Tunis", "Supermarché", "Berges du Lac, Tunis", "+216 71 111 111", "contact@carrefour.tn"),
                Client(2, "Monoprix Centre Ville", "Supermarché", "Avenue Habib Bourguiba, Tunis", "+216 71 222 222", "contact@monoprix.tn"),
                Client(3, "Restaurant Le Palais", "Restaurant", "La Marsa, Tunis", "+216 71 333 333", "lepalais@resto.tn")
            ),
            receptions = listOf(
                Reception(1, 1, daysOffset(-5), "06:30", 1500.0, 4.2, 6.7, 3.8, "Accepté"),
                Reception(2, 2, daysOffset(-4), "07:00", 2000.0, 4.5, 6.8, 3.9, "Accepté"),
                Reception(3, 3, daysOffset(-3), "06:45", 800.0, 5.1, 6.5, 3.5, "Refusé"),
                Reception(4, 1, daysOffset(-2), "07:15", 1800.0, 4.0, 6.8, 4.0, "Accepté")
            ),
            lots = listOf(
                LotProduction(1, "LOT-2024-001", 1, 1, daysOffset(-4), 1200.0, daysOffset(10), "Distribué"),
                LotProduction(2, "LOT-2024-002", 2, 2, daysOffset(-3), 500.0, daysOffset(20), "Stocké"),
                LotProduction(3, "LOT-2024-003", 3, 4, daysOffset(-1), 300.0, daysOffset(15), "Stocké"),
                LotProduction(4, "LOT-2024-004", 4, 2, daysOffset(0), 200.0, daysOffset(60), "En production")
            ),
            stockages = listOf(
                Stockage(1, 1, "Zone A - Chambre froide 1", 4.0, dateTimeOffset(-4), dateTimeOffset(-2)),
                Stockage(2, 2, "Zone B - Chambre froide 2", 4.5, dateTimeOffset(-3), null),
                Stockage(3, 3, "Zone A - Chambre froide 1", 4.2, dateTimeOffset(-1), null),
                Stockage(4, 4, "Zone C - Production", 18.0, dateTimeOffset(0), null)
            ),
            distributions = listOf(
                Distribution(1, 1, 1, daysOffset(-2), 600.0, "BL-2024-001", "Livrée"),
                Distribution(2, 1, 2, daysOffset(-2), 600.0, "BL-2024-002", "Livrée"),
                Distribution(3, 2, 3, daysOffset(2), 200.0, "BL-2024-003", "Planifiée")
            )
        )
    }

    fun showToast(message: String, type: String = "success") {
        _uiState.update { it.copy(toast = ToastMessage(text = message, type = type)) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toast = null) }
    }

    fun navigateTo(view: AppView) {
        _uiState.update { it.copy(currentView = view) }
    }

    fun login(email: String, passwordPlain: String) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty() || passwordPlain.isEmpty()) {
            showToast("Email et mot de passe requis", "error")
            return
        }
        val state = _uiState.value
        val user = state.utilisateurs.find {
            it.email.equals(trimmedEmail, ignoreCase = true) && it.actif == 1 && it.passwordPlain == passwordPlain
        }
        if (user == null) {
            showToast("Identifiants invalides", "error")
            return
        }
        val logEntry = JournalConnexion(
            id = (state.journalConnexions.maxOfOrNull { it.id } ?: 0) + 1,
            utilisateurId = user.id,
            nom = user.nom,
            email = user.email,
            role = user.role,
            action = "Connexion",
            adresseIp = "127.0.0.1",
            navigateur = "Application Android TraçaLait",
            createdAt = nowDateTime()
        )
        _uiState.update {
            it.copy(
                currentUser = user,
                currentView = AppView.DASHBOARD,
                journalConnexions = listOf(logEntry) + it.journalConnexions,
                toast = ToastMessage(text = "Connexion réussie", type = "success")
            )
        }
    }

    fun logout() {
        val state = _uiState.value
        val user = state.currentUser
        val updatedLogs = if (user != null) {
            val logEntry = JournalConnexion(
                id = (state.journalConnexions.maxOfOrNull { it.id } ?: 0) + 1,
                utilisateurId = user.id,
                nom = user.nom,
                email = user.email,
                role = user.role,
                action = "Déconnexion",
                adresseIp = "127.0.0.1",
                navigateur = "Application Android TraçaLait",
                createdAt = nowDateTime()
            )
            listOf(logEntry) + state.journalConnexions
        } else {
            state.journalConnexions
        }
        _uiState.update {
            it.copy(
                currentUser = null,
                currentView = AppView.DASHBOARD,
                journalConnexions = updatedLogs,
                toast = ToastMessage(text = "Déconnexion réussie", type = "info")
            )
        }
    }

    // ── Traçabilité ─────────────────────────────────────────────
    fun voirTracabilite(lotId: Int) {
        _uiState.update {
            val lot = it.lots.find { l -> l.id == lotId }
            it.copy(
                currentView = AppView.TRACABILITE,
                selectedTraceLotId = lotId,
                traceSearchQuery = lot?.numeroLot ?: it.traceSearchQuery
            )
        }
    }

    fun updateTraceSearchQuery(q: String) {
        _uiState.update { it.copy(traceSearchQuery = q) }
    }

    fun rechercherLot() {
        val q = _uiState.value.traceSearchQuery.trim()
        if (q.isEmpty()) {
            showToast("Entrez un numéro de lot", "info")
            return
        }
        val found = _uiState.value.lots.find { it.numeroLot.contains(q, ignoreCase = true) }
        if (found == null) {
            showToast("Lot introuvable", "error")
            return
        }
        _uiState.update { it.copy(selectedTraceLotId = found.id) }
    }

    // ── Fournisseurs CRUD ───────────────────────────────────────
    fun saveFournisseur(id: Int?, nom: String, adresse: String, telephone: String, email: String) {
        if (nom.isBlank()) {
            showToast("Le nom est requis", "error")
            return
        }
        _uiState.update { state ->
            val list = if (id == null) {
                val nextId = (state.fournisseurs.maxOfOrNull { it.id } ?: 0) + 1
                state.fournisseurs + Fournisseur(nextId, nom.trim(), adresse.trim(), telephone.trim(), email.trim())
            } else {
                state.fournisseurs.map {
                    if (it.id == id) it.copy(nom = nom.trim(), adresse = adresse.trim(), telephone = telephone.trim(), email = email.trim()) else it
                }
            }
            state.copy(
                fournisseurs = list.sortedBy { it.nom },
                toast = ToastMessage(text = if (id == null) "Fournisseur créé" else "Fournisseur mis à jour")
            )
        }
    }

    fun deleteFournisseur(id: Int) {
        _uiState.update { state ->
            state.copy(
                fournisseurs = state.fournisseurs.filterNot { it.id == id },
                toast = ToastMessage(text = "Fournisseur supprimé")
            )
        }
    }

    // ── Réceptions CRUD ─────────────────────────────────────────
    fun saveReception(
        id: Int?,
        fournisseurId: Int,
        dateReception: String,
        heureReception: String,
        quantiteLitres: Double?,
        temperatureC: Double?,
        ph: Double?,
        tauxMg: Double?,
        resultatControle: String,
        observations: String
    ) {
        if (quantiteLitres == null || quantiteLitres <= 0.0) {
            showToast("La quantité est requise", "error")
            return
        }
        _uiState.update { state ->
            val list = if (id == null) {
                val nextId = (state.receptions.maxOfOrNull { it.id } ?: 0) + 1
                listOf(
                    Reception(
                        id = nextId,
                        fournisseurId = fournisseurId,
                        dateReception = dateReception.ifBlank { daysOffset(0) },
                        heureReception = heureReception.ifBlank { "07:00" },
                        quantiteLitres = quantiteLitres,
                        temperatureC = temperatureC,
                        ph = ph,
                        tauxMatieresGrasses = tauxMg,
                        resultatControle = resultatControle,
                        observations = observations.trim()
                    )
                ) + state.receptions
            } else {
                state.receptions.map {
                    if (it.id == id) it.copy(
                        fournisseurId = fournisseurId,
                        dateReception = dateReception,
                        heureReception = heureReception,
                        quantiteLitres = quantiteLitres,
                        temperatureC = temperatureC,
                        ph = ph,
                        tauxMatieresGrasses = tauxMg,
                        resultatControle = resultatControle,
                        observations = observations.trim()
                    ) else it
                }
            }
            state.copy(
                receptions = list.sortedByDescending { it.dateReception + " " + it.heureReception },
                toast = ToastMessage(text = if (id == null) "Réception enregistrée" else "Réception mise à jour")
            )
        }
    }

    fun deleteReception(id: Int) {
        _uiState.update { state ->
            state.copy(
                receptions = state.receptions.filterNot { it.id == id },
                toast = ToastMessage(text = "Réception supprimée")
            )
        }
    }

    // ── Produits CRUD ───────────────────────────────────────────
    fun saveProduit(id: Int?, nom: String, typeProduit: String, description: String) {
        if (nom.isBlank()) {
            showToast("Le nom est requis", "error")
            return
        }
        _uiState.update { state ->
            val list = if (id == null) {
                val nextId = (state.produits.maxOfOrNull { it.id } ?: 0) + 1
                state.produits + Produit(nextId, nom.trim(), typeProduit, description.trim())
            } else {
                state.produits.map {
                    if (it.id == id) it.copy(nom = nom.trim(), typeProduit = typeProduit, description = description.trim()) else it
                }
            }
            state.copy(
                produits = list.sortedBy { it.nom },
                toast = ToastMessage(text = if (id == null) "Produit créé" else "Produit mis à jour")
            )
        }
    }

    fun deleteProduit(id: Int) {
        _uiState.update { state ->
            state.copy(
                produits = state.produits.filterNot { it.id == id },
                toast = ToastMessage(text = "Produit supprimé")
            )
        }
    }

    // ── Lots de production CRUD ─────────────────────────────────
    fun saveLot(
        id: Int?,
        numeroLot: String,
        produitId: Int,
        receptionId: Int,
        dateProduction: String,
        quantiteKg: Double?,
        dateExpiration: String,
        statut: String,
        observations: String
    ) {
        if (numeroLot.isBlank() || quantiteKg == null || quantiteKg <= 0.0) {
            showToast("Champs requis manquants", "error")
            return
        }
        _uiState.update { state ->
            val list = if (id == null) {
                val nextId = (state.lots.maxOfOrNull { it.id } ?: 0) + 1
                listOf(
                    LotProduction(
                        id = nextId,
                        numeroLot = numeroLot.trim(),
                        produitId = produitId,
                        receptionId = receptionId,
                        dateProduction = dateProduction.ifBlank { daysOffset(0) },
                        quantiteKg = quantiteKg,
                        dateExpiration = dateExpiration.ifBlank { daysOffset(15) },
                        statut = statut,
                        observations = observations.trim()
                    )
                ) + state.lots
            } else {
                state.lots.map {
                    if (it.id == id) it.copy(
                        numeroLot = numeroLot.trim(),
                        produitId = produitId,
                        receptionId = receptionId,
                        dateProduction = dateProduction,
                        quantiteKg = quantiteKg,
                        dateExpiration = dateExpiration,
                        statut = statut,
                        observations = observations.trim()
                    ) else it
                }
            }
            state.copy(
                lots = list.sortedByDescending { it.dateProduction },
                toast = ToastMessage(text = if (id == null) "Lot créé" else "Lot mis à jour")
            )
        }
    }

    fun deleteLot(id: Int) {
        _uiState.update { state ->
            state.copy(
                lots = state.lots.filterNot { it.id == id },
                toast = ToastMessage(text = "Lot supprimé")
            )
        }
    }

    // ── Stockages CRUD ──────────────────────────────────────────
    fun saveStockage(
        id: Int?,
        lotId: Int,
        zoneStockage: String,
        temperatureC: Double?,
        dateEntree: String,
        dateSortie: String?
    ) {
        if (zoneStockage.isBlank() || dateEntree.isBlank()) {
            showToast("La zone et la date d'entrée sont requises", "error")
            return
        }
        _uiState.update { state ->
            val list = if (id == null) {
                val nextId = (state.stockages.maxOfOrNull { it.id } ?: 0) + 1
                listOf(
                    Stockage(
                        id = nextId,
                        lotId = lotId,
                        zoneStockage = zoneStockage.trim(),
                        temperatureC = temperatureC,
                        dateEntree = dateEntree.trim(),
                        dateSortie = dateSortie?.takeIf { it.isNotBlank() }
                    )
                ) + state.stockages
            } else {
                state.stockages.map {
                    if (it.id == id) it.copy(
                        lotId = lotId,
                        zoneStockage = zoneStockage.trim(),
                        temperatureC = temperatureC,
                        dateEntree = dateEntree.trim(),
                        dateSortie = dateSortie?.takeIf { s -> s.isNotBlank() }
                    ) else it
                }
            }
            state.copy(
                stockages = list.sortedByDescending { it.dateEntree },
                toast = ToastMessage(text = if (id == null) "Stockage enregistré" else "Stockage mis à jour")
            )
        }
    }

    fun deleteStockage(id: Int) {
        _uiState.update { state ->
            state.copy(
                stockages = state.stockages.filterNot { it.id == id },
                toast = ToastMessage(text = "Stockage supprimé")
            )
        }
    }

    // ── Clients CRUD ────────────────────────────────────────────
    fun saveClient(id: Int?, nom: String, typeClient: String, adresse: String, telephone: String, email: String) {
        if (nom.isBlank()) {
            showToast("Le nom est requis", "error")
            return
        }
        _uiState.update { state ->
            val list = if (id == null) {
                val nextId = (state.clients.maxOfOrNull { it.id } ?: 0) + 1
                state.clients + Client(nextId, nom.trim(), typeClient, adresse.trim(), telephone.trim(), email.trim())
            } else {
                state.clients.map {
                    if (it.id == id) it.copy(nom = nom.trim(), typeClient = typeClient, adresse = adresse.trim(), telephone = telephone.trim(), email = email.trim()) else it
                }
            }
            state.copy(
                clients = list.sortedBy { it.nom },
                toast = ToastMessage(text = if (id == null) "Client créé" else "Client mis à jour")
            )
        }
    }

    fun deleteClient(id: Int) {
        _uiState.update { state ->
            state.copy(
                clients = state.clients.filterNot { it.id == id },
                toast = ToastMessage(text = "Client supprimé")
            )
        }
    }

    // ── Distributions CRUD ──────────────────────────────────────
    fun saveDistribution(
        id: Int?,
        lotId: Int,
        clientId: Int,
        dateLivraison: String,
        quantiteKg: Double?,
        bonLivraison: String,
        statut: String
    ) {
        if (quantiteKg == null || quantiteKg <= 0.0) {
            showToast("La quantité est requise", "error")
            return
        }
        _uiState.update { state ->
            val list = if (id == null) {
                val nextId = (state.distributions.maxOfOrNull { it.id } ?: 0) + 1
                listOf(
                    Distribution(
                        id = nextId,
                        lotId = lotId,
                        clientId = clientId,
                        dateLivraison = dateLivraison.ifBlank { daysOffset(0) },
                        quantiteKg = quantiteKg,
                        bonLivraison = bonLivraison.trim(),
                        statut = statut
                    )
                ) + state.distributions
            } else {
                state.distributions.map {
                    if (it.id == id) it.copy(
                        lotId = lotId,
                        clientId = clientId,
                        dateLivraison = dateLivraison,
                        quantiteKg = quantiteKg,
                        bonLivraison = bonLivraison.trim(),
                        statut = statut
                    ) else it
                }
            }
            state.copy(
                distributions = list.sortedByDescending { it.dateLivraison },
                toast = ToastMessage(text = if (id == null) "Distribution créée" else "Distribution mise à jour")
            )
        }
    }

    fun deleteDistribution(id: Int) {
        _uiState.update { state ->
            state.copy(
                distributions = state.distributions.filterNot { it.id == id },
                toast = ToastMessage(text = "Distribution supprimée")
            )
        }
    }

    // ── Utilisateurs CRUD ───────────────────────────────────────
    fun saveUtilisateur(
        id: Int?,
        nom: String,
        email: String,
        role: String,
        actif: Int,
        passwordPlain: String
    ) {
        if (nom.isBlank() || email.isBlank()) {
            showToast("Nom et email sont requis", "error")
            return
        }
        if (id == null && passwordPlain.length < 6) {
            showToast("Le mot de passe doit contenir au moins 6 caractères", "error")
            return
        }
        _uiState.update { state ->
            val list = if (id == null) {
                val nextId = (state.utilisateurs.maxOfOrNull { it.id } ?: 0) + 1
                state.utilisateurs + Utilisateur(
                    id = nextId,
                    nom = nom.trim(),
                    email = email.trim(),
                    passwordPlain = passwordPlain,
                    role = role,
                    actif = actif,
                    createdAt = nowDateTime()
                )
            } else {
                state.utilisateurs.map {
                    if (it.id == id) it.copy(
                        nom = nom.trim(),
                        email = email.trim(),
                        role = role,
                        actif = actif,
                        passwordPlain = if (passwordPlain.isNotBlank()) passwordPlain else it.passwordPlain
                    ) else it
                }
            }
            state.copy(
                utilisateurs = list.sortedBy { it.nom },
                toast = ToastMessage(text = if (id == null) "Utilisateur créé" else "Utilisateur mis à jour")
            )
        }
    }

    fun deleteUtilisateur(id: Int) {
        val currentId = _uiState.value.currentUser?.id
        if (id == currentId) {
            showToast("Vous ne pouvez pas supprimer votre propre compte", "error")
            return
        }
        _uiState.update { state ->
            state.copy(
                utilisateurs = state.utilisateurs.filterNot { it.id == id },
                toast = ToastMessage(text = "Utilisateur supprimé")
            )
        }
    }
}
