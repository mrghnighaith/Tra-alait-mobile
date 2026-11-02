package com.tracalait.app

enum class AppView(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val section: String,
    val adminOnly: Boolean = false
) {
    DASHBOARD("dashboard", "Tableau de bord", "📊", "Général"),
    FOURNISSEURS("fournisseurs", "Fournisseurs", "🏭", "Chaîne de production"),
    RECEPTIONS("receptions", "Réceptions lait", "🥛", "Chaîne de production"),
    LOTS("lots", "Lots de production", "🧀", "Chaîne de production"),
    DISTRIBUTIONS("distributions", "Distributions", "🚚", "Chaîne de production"),
    CLIENTS("clients", "Clients", "🏪", "Référentiels"),
    TRACABILITE("tracabilite", "Recherche tracé", "🔍", "Traçabilité"),
    PRODUITS("produits", "Produits", "🥛", "Produits"),
    STOCKAGES("stockages", "Stockages", "📦", "Produits"),
    UTILISATEURS("utilisateurs", "Utilisateurs", "👤", "Administration", adminOnly = true),
    JOURNAL_CONNEXIONS("journal_connexions", "Journal connexions", "🕒", "Administration", adminOnly = true)
}

data class Utilisateur(
    val id: Int,
    val nom: String,
    val email: String,
    val passwordPlain: String,
    val role: String, // "Administrateur", "Responsable Qualité", "Responsable Production"
    val actif: Int = 1,
    val createdAt: String
)

data class JournalConnexion(
    val id: Int,
    val utilisateurId: Int?,
    val nom: String,
    val email: String,
    val role: String,
    val action: String, // "Connexion" | "Déconnexion"
    val adresseIp: String,
    val navigateur: String,
    val createdAt: String
)

data class Fournisseur(
    val id: Int,
    val nom: String,
    val adresse: String = "",
    val telephone: String = "",
    val email: String = ""
)

data class Reception(
    val id: Int,
    val fournisseurId: Int,
    val dateReception: String,
    val heureReception: String,
    val quantiteLitres: Double,
    val temperatureC: Double?,
    val ph: Double?,
    val tauxMatieresGrasses: Double?,
    val resultatControle: String, // "En attente" | "Accepté" | "Refusé"
    val observations: String = ""
)

data class Produit(
    val id: Int,
    val nom: String,
    val typeProduit: String, // "Lait pasteurisé", "Yaourt", "Fromage", "Beurre", "Crème", "Lait cru", "Autre"
    val description: String = ""
)

data class LotProduction(
    val id: Int,
    val numeroLot: String,
    val produitId: Int,
    val receptionId: Int,
    val dateProduction: String,
    val quantiteKg: Double,
    val dateExpiration: String,
    val statut: String, // "En production", "Stocké", "Distribué", "Rappelé"
    val observations: String = ""
)

data class Stockage(
    val id: Int,
    val lotId: Int,
    val zoneStockage: String,
    val temperatureC: Double?,
    val dateEntree: String,
    val dateSortie: String? = null
)

data class Client(
    val id: Int,
    val nom: String,
    val typeClient: String, // "Supermarché", "Restaurant", "Particulier", "Épicerie", "Autre"
    val adresse: String = "",
    val telephone: String = "",
    val email: String = ""
)

data class Distribution(
    val id: Int,
    val lotId: Int,
    val clientId: Int,
    val dateLivraison: String,
    val quantiteKg: Double,
    val bonLivraison: String = "",
    val statut: String // "Planifiée", "Livrée", "Annulée"
)

data class ToastMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val type: String = "success" // "success", "error", "info"
)

object PermissionHelper {
    fun isAdmin(user: Utilisateur?): Boolean = user?.role == "Administrateur"

    fun canWrite(user: Utilisateur?, module: String): Boolean {
        if (user == null) return false
        if (isAdmin(user)) return true
        val permissions = mapOf(
            "Responsable Qualité" to setOf("fournisseurs", "receptions", "produits"),
            "Responsable Production" to setOf("lots", "stockages", "distributions", "clients")
        )
        return permissions[user.role]?.contains(module) == true
    }
}
