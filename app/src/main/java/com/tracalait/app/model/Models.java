package com.tracalait.app.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Models {

    public enum AppView {
        DASHBOARD("dashboard", "Tableau de bord", "📊", "Général", false),
        FOURNISSEURS("fournisseurs", "Fournisseurs", "🏭", "Chaîne de production", false),
        RECEPTIONS("receptions", "Réceptions lait", "🥛", "Chaîne de production", false),
        LOTS("lots", "Lots de production", "🧀", "Chaîne de production", false),
        DISTRIBUTIONS("distributions", "Distributions", "🚚", "Chaîne de production", false),
        CLIENTS("clients", "Clients", "🏪", "Référentiels", false),
        TRACABILITE("tracabilite", "Recherche tracé", "🔍", "Traçabilité", false),
        PRODUITS("produits", "Produits", "🥛", "Produits", false),
        STOCKAGES("stockages", "Stockages", "📦", "Produits", false),
        UTILISATEURS("utilisateurs", "Utilisateurs", "👤", "Administration", true),
        JOURNAL_CONNEXIONS("journal_connexions", "Journal connexions", "🕒", "Administration", true);

        public final String id;
        public final String title;
        public final String iconEmoji;
        public final String section;
        public final boolean adminOnly;

        AppView(String id, String title, String iconEmoji, String section, boolean adminOnly) {
            this.id = id;
            this.title = title;
            this.iconEmoji = iconEmoji;
            this.section = section;
            this.adminOnly = adminOnly;
        }
    }

    public static class Utilisateur {
        public int id;
        public String nom;
        public String email;
        public String passwordPlain;
        public String role;
        public int actif;
        public String createdAt;

        public Utilisateur(int id, String nom, String email, String passwordPlain, String role, int actif, String createdAt) {
            this.id = id;
            this.nom = nom;
            this.email = email;
            this.passwordPlain = passwordPlain;
            this.role = role;
            this.actif = actif;
            this.createdAt = createdAt;
        }
    }

    public static class JournalConnexion {
        public int id;
        public Integer utilisateurId;
        public String nom;
        public String email;
        public String role;
        public String action;
        public String adresseIp;
        public String navigateur;
        public String createdAt;

        public JournalConnexion(int id, Integer utilisateurId, String nom, String email, String role, String action, String adresseIp, String navigateur, String createdAt) {
            this.id = id;
            this.utilisateurId = utilisateurId;
            this.nom = nom;
            this.email = email;
            this.role = role;
            this.action = action;
            this.adresseIp = adresseIp;
            this.navigateur = navigateur;
            this.createdAt = createdAt;
        }
    }

    public static class Fournisseur {
        public int id;
        public String nom;
        public String adresse;
        public String telephone;
        public String email;

        public Fournisseur(int id, String nom, String adresse, String telephone, String email) {
            this.id = id;
            this.nom = nom;
            this.adresse = adresse != null ? adresse : "";
            this.telephone = telephone != null ? telephone : "";
            this.email = email != null ? email : "";
        }
    }

    public static class Reception {
        public int id;
        public int fournisseurId;
        public String dateReception;
        public String heureReception;
        public double quantiteLitres;
        public Double temperatureC;
        public Double ph;
        public Double tauxMatieresGrasses;
        public String resultatControle;
        public String observations;

        public Reception(int id, int fournisseurId, String dateReception, String heureReception, double quantiteLitres, Double temperatureC, Double ph, Double tauxMatieresGrasses, String resultatControle, String observations) {
            this.id = id;
            this.fournisseurId = fournisseurId;
            this.dateReception = dateReception;
            this.heureReception = heureReception;
            this.quantiteLitres = quantiteLitres;
            this.temperatureC = temperatureC;
            this.ph = ph;
            this.tauxMatieresGrasses = tauxMatieresGrasses;
            this.resultatControle = resultatControle;
            this.observations = observations != null ? observations : "";
        }
    }

    public static class Produit {
        public int id;
        public String nom;
        public String typeProduit;
        public String description;

        public Produit(int id, String nom, String typeProduit, String description) {
            this.id = id;
            this.nom = nom;
            this.typeProduit = typeProduit;
            this.description = description != null ? description : "";
        }
    }

    public static class LotProduction {
        public int id;
        public String numeroLot;
        public int produitId;
        public int receptionId;
        public String dateProduction;
        public double quantiteKg;
        public String dateExpiration;
        public String statut;
        public String observations;

        public LotProduction(int id, String numeroLot, int produitId, int receptionId, String dateProduction, double quantiteKg, String dateExpiration, String statut, String observations) {
            this.id = id;
            this.numeroLot = numeroLot;
            this.produitId = produitId;
            this.receptionId = receptionId;
            this.dateProduction = dateProduction;
            this.quantiteKg = quantiteKg;
            this.dateExpiration = dateExpiration;
            this.statut = statut;
            this.observations = observations != null ? observations : "";
        }
    }

    public static class Stockage {
        public int id;
        public int lotId;
        public String zoneStockage;
        public Double temperatureC;
        public String dateEntree;
        public String dateSortie;

        public Stockage(int id, int lotId, String zoneStockage, Double temperatureC, String dateEntree, String dateSortie) {
            this.id = id;
            this.lotId = lotId;
            this.zoneStockage = zoneStockage;
            this.temperatureC = temperatureC;
            this.dateEntree = dateEntree;
            this.dateSortie = dateSortie;
        }
    }

    public static class Client {
        public int id;
        public String nom;
        public String typeClient;
        public String adresse;
        public String telephone;
        public String email;

        public Client(int id, String nom, String typeClient, String adresse, String telephone, String email) {
            this.id = id;
            this.nom = nom;
            this.typeClient = typeClient;
            this.adresse = adresse != null ? adresse : "";
            this.telephone = telephone != null ? telephone : "";
            this.email = email != null ? email : "";
        }
    }

    public static class Distribution {
        public int id;
        public int lotId;
        public int clientId;
        public String dateLivraison;
        public double quantiteKg;
        public String bonLivraison;
        public String statut;

        public Distribution(int id, int lotId, int clientId, String dateLivraison, double quantiteKg, String bonLivraison, String statut) {
            this.id = id;
            this.lotId = lotId;
            this.clientId = clientId;
            this.dateLivraison = dateLivraison;
            this.quantiteKg = quantiteKg;
            this.bonLivraison = bonLivraison != null ? bonLivraison : "";
            this.statut = statut;
        }
    }

    public static class PermissionHelper {
        private static final Map<String, Set<String>> ROLE_PERMISSIONS = new HashMap<>();

        static {
            ROLE_PERMISSIONS.put("Responsable Qualité", new HashSet<>(Arrays.asList("fournisseurs", "receptions", "produits")));
            ROLE_PERMISSIONS.put("Responsable Production", new HashSet<>(Arrays.asList("lots", "stockages", "distributions", "clients")));
        }

        public static boolean isAdmin(Utilisateur user) {
            return user != null && "Administrateur".equals(user.role);
        }

        public static boolean canWrite(Utilisateur user, String module) {
            if (user == null) return false;
            if (isAdmin(user)) return true;
            Set<String> allowed = ROLE_PERMISSIONS.getOrDefault(user.role, Collections.emptySet());
            return allowed.contains(module);
        }
    }
}
