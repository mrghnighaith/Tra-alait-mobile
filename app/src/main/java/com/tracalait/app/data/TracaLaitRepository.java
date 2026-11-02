package com.tracalait.app.data;

import com.tracalait.app.model.Models.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TracaLaitRepository {

    private static TracaLaitRepository instance;

    public static synchronized TracaLaitRepository getInstance() {
        if (instance == null) {
            instance = new TracaLaitRepository();
        }
        return instance;
    }

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.FRANCE);

    public Utilisateur currentUser = null;
    public AppView currentView = AppView.DASHBOARD;
    public Integer selectedTraceLotId = null;
    public String traceSearchQuery = "";

    public final List<Utilisateur> utilisateurs = new ArrayList<>();
    public final List<JournalConnexion> journalConnexions = new ArrayList<>();
    public final List<Fournisseur> fournisseurs = new ArrayList<>();
    public final List<Reception> receptions = new ArrayList<>();
    public final List<Produit> produits = new ArrayList<>();
    public final List<LotProduction> lots = new ArrayList<>();
    public final List<Stockage> stockages = new ArrayList<>();
    public final List<Client> clients = new ArrayList<>();
    public final List<Distribution> distributions = new ArrayList<>();

    private int nextUtilisateurId = 4;
    private int nextJournalId = 1;
    private int nextFournisseurId = 4;
    private int nextReceptionId = 5;
    private int nextProduitId = 5;
    private int nextLotId = 5;
    private int nextStockageId = 5;
    private int nextClientId = 4;
    private int nextDistributionId = 4;

    private TracaLaitRepository() {
        seedInitialData();
    }

    public String daysOffset(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, days);
        return dateFormat.format(cal.getTime());
    }

    public String dateTimeOffset(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, days);
        return dateTimeFormat.format(cal.getTime());
    }

    public String nowDateTime() {
        return dateTimeFormat.format(new Date());
    }

    private void seedInitialData() {
        String now = nowDateTime();

        utilisateurs.add(new Utilisateur(1, "Administrateur", "admin@tracalait.local", "admin123", "Administrateur", 1, now));
        utilisateurs.add(new Utilisateur(2, "Responsable Qualité", "qualite@tracalait.local", "admin123", "Responsable Qualité", 1, now));
        utilisateurs.add(new Utilisateur(3, "Responsable Production", "production@tracalait.local", "admin123", "Responsable Production", 1, now));

        fournisseurs.add(new Fournisseur(1, "Ferme Ben Salem", "Route de Tunis Km 12, Manouba", "+216 71 234 567", "bensalem@ferme.tn"));
        fournisseurs.add(new Fournisseur(2, "Élevage Trabelsi", "Douar Ain Draham, Jendouba", "+216 78 345 678", "trabelsi@elevage.tn"));
        fournisseurs.add(new Fournisseur(3, "Coopérative Sfax Lait", "Zone Industrielle Sfax", "+216 74 456 789", "sfaxlait@coop.tn"));

        produits.add(new Produit(1, "Lait Entier 1L", "Lait pasteurisé", "Lait entier pasteurisé conditionné en bouteille 1L"));
        produits.add(new Produit(2, "Yaourt Nature 500g", "Yaourt", "Yaourt nature ferme en pot plastique 500g"));
        produits.add(new Produit(3, "Fromage Frais 250g", "Fromage", "Fromage frais onctueux en barquette 250g"));
        produits.add(new Produit(4, "Beurre Doux 200g", "Beurre", "Beurre doux de qualité supérieure"));

        clients.add(new Client(1, "Carrefour Tunis", "Supermarché", "Berges du Lac, Tunis", "+216 71 111 111", "contact@carrefour.tn"));
        clients.add(new Client(2, "Monoprix Centre Ville", "Supermarché", "Avenue Habib Bourguiba, Tunis", "+216 71 222 222", "contact@monoprix.tn"));
        clients.add(new Client(3, "Restaurant Le Palais", "Restaurant", "La Marsa, Tunis", "+216 71 333 333", "lepalais@resto.tn"));

        receptions.add(new Reception(1, 1, daysOffset(-5), "06:30", 1500.0, 4.2, 6.7, 3.8, "Accepté", ""));
        receptions.add(new Reception(2, 2, daysOffset(-4), "07:00", 2000.0, 4.5, 6.8, 3.9, "Accepté", ""));
        receptions.add(new Reception(3, 3, daysOffset(-3), "06:45", 800.0, 5.1, 6.5, 3.5, "Refusé", ""));
        receptions.add(new Reception(4, 1, daysOffset(-2), "07:15", 1800.0, 4.0, 6.8, 4.0, "Accepté", ""));

        lots.add(new LotProduction(1, "LOT-2024-001", 1, 1, daysOffset(-4), 1200.0, daysOffset(10), "Distribué", ""));
        lots.add(new LotProduction(2, "LOT-2024-002", 2, 2, daysOffset(-3), 500.0, daysOffset(20), "Stocké", ""));
        lots.add(new LotProduction(3, "LOT-2024-003", 3, 4, daysOffset(-1), 300.0, daysOffset(15), "Stocké", ""));
        lots.add(new LotProduction(4, "LOT-2024-004", 4, 2, daysOffset(0), 200.0, daysOffset(60), "En production", ""));

        stockages.add(new Stockage(1, 1, "Zone A - Chambre froide 1", 4.0, dateTimeOffset(-4), dateTimeOffset(-2)));
        stockages.add(new Stockage(2, 2, "Zone B - Chambre froide 2", 4.5, dateTimeOffset(-3), null));
        stockages.add(new Stockage(3, 3, "Zone A - Chambre froide 1", 4.2, dateTimeOffset(-1), null));
        stockages.add(new Stockage(4, 4, "Zone C - Production", 18.0, dateTimeOffset(0), null));

        distributions.add(new Distribution(1, 1, 1, daysOffset(-2), 600.0, "BL-2024-001", "Livrée"));
        distributions.add(new Distribution(2, 1, 2, daysOffset(-2), 600.0, "BL-2024-002", "Livrée"));
        distributions.add(new Distribution(3, 2, 3, daysOffset(2), 200.0, "BL-2024-003", "Planifiée"));
    }

    public String login(String email, String password) {
        String e = email != null ? email.trim() : "";
        String p = password != null ? password : "";
        if (e.isEmpty() || p.isEmpty()) {
            return "Email et mot de passe requis";
        }
        for (Utilisateur u : utilisateurs) {
            if (u.email.equalsIgnoreCase(e) && u.actif == 1 && u.passwordPlain.equals(p)) {
                currentUser = u;
                currentView = AppView.DASHBOARD;
                journalConnexions.add(0, new JournalConnexion(
                        nextJournalId++, u.id, u.nom, u.email, u.role, "Connexion",
                        "127.0.0.1", "Application Android TraçaLait (Java)", nowDateTime()
                ));
                return null;
            }
        }
        return "Identifiants invalides";
    }

    public void logout() {
        if (currentUser != null) {
            journalConnexions.add(0, new JournalConnexion(
                    nextJournalId++, currentUser.id, currentUser.nom, currentUser.email,
                    currentUser.role, "Déconnexion", "127.0.0.1",
                    "Application Android TraçaLait (Java)", nowDateTime()
            ));
        }
        currentUser = null;
        currentView = AppView.DASHBOARD;
    }

    public Fournisseur findFournisseur(int id) {
        for (Fournisseur f : fournisseurs) if (f.id == id) return f;
        return null;
    }

    public Produit findProduit(int id) {
        for (Produit p : produits) if (p.id == id) return p;
        return null;
    }

    public Reception findReception(int id) {
        for (Reception r : receptions) if (r.id == id) return r;
        return null;
    }

    public LotProduction findLot(int id) {
        for (LotProduction l : lots) if (l.id == id) return l;
        return null;
    }

    public Client findClient(int id) {
        for (Client c : clients) if (c.id == id) return c;
        return null;
    }

    public void saveFournisseur(Integer id, String nom, String adresse, String tel, String email) {
        if (id == null) {
            fournisseurs.add(new Fournisseur(nextFournisseurId++, nom.trim(), adresse.trim(), tel.trim(), email.trim()));
        } else {
            for (Fournisseur f : fournisseurs) {
                if (f.id == id) {
                    f.nom = nom.trim();
                    f.adresse = adresse.trim();
                    f.telephone = tel.trim();
                    f.email = email.trim();
                    break;
                }
            }
        }
        fournisseurs.sort(Comparator.comparing(a -> a.nom.toLowerCase(Locale.ROOT)));
    }

    public void deleteFournisseur(int id) {
        fournisseurs.removeIf(f -> f.id == id);
    }

    public void saveReception(Integer id, int fournisseurId, String date, String heure, double quantite, Double temp, Double ph, Double mg, String controle, String obs) {
        if (id == null) {
            receptions.add(0, new Reception(nextReceptionId++, fournisseurId, date, heure, quantite, temp, ph, mg, controle, obs));
        } else {
            for (Reception r : receptions) {
                if (r.id == id) {
                    r.fournisseurId = fournisseurId;
                    r.dateReception = date;
                    r.heureReception = heure;
                    r.quantiteLitres = quantite;
                    r.temperatureC = temp;
                    r.ph = ph;
                    r.tauxMatieresGrasses = mg;
                    r.resultatControle = controle;
                    r.observations = obs;
                    break;
                }
            }
        }
    }

    public void deleteReception(int id) {
        receptions.removeIf(r -> r.id == id);
    }

    public void saveProduit(Integer id, String nom, String type, String desc) {
        if (id == null) {
            produits.add(new Produit(nextProduitId++, nom.trim(), type, desc.trim()));
        } else {
            for (Produit p : produits) {
                if (p.id == id) {
                    p.nom = nom.trim();
                    p.typeProduit = type;
                    p.description = desc.trim();
                    break;
                }
            }
        }
        produits.sort(Comparator.comparing(a -> a.nom.toLowerCase(Locale.ROOT)));
    }

    public void deleteProduit(int id) {
        produits.removeIf(p -> p.id == id);
    }

    public void saveLot(Integer id, String numero, int produitId, int receptionId, String dateProd, double quantite, String dateExp, String statut, String obs) {
        if (id == null) {
            lots.add(0, new LotProduction(nextLotId++, numero.trim(), produitId, receptionId, dateProd, quantite, dateExp, statut, obs));
        } else {
            for (LotProduction l : lots) {
                if (l.id == id) {
                    l.numeroLot = numero.trim();
                    l.produitId = produitId;
                    l.receptionId = receptionId;
                    l.dateProduction = dateProd;
                    l.quantiteKg = quantite;
                    l.dateExpiration = dateExp;
                    l.statut = statut;
                    l.observations = obs;
                    break;
                }
            }
        }
    }

    public void deleteLot(int id) {
        lots.removeIf(l -> l.id == id);
    }

    public void saveStockage(Integer id, int lotId, String zone, Double temp, String entree, String sortie) {
        String cleanSortie = (sortie != null && !sortie.trim().isEmpty()) ? sortie.trim() : null;
        if (id == null) {
            stockages.add(0, new Stockage(nextStockageId++, lotId, zone.trim(), temp, entree.trim(), cleanSortie));
        } else {
            for (Stockage s : stockages) {
                if (s.id == id) {
                    s.lotId = lotId;
                    s.zoneStockage = zone.trim();
                    s.temperatureC = temp;
                    s.dateEntree = entree.trim();
                    s.dateSortie = cleanSortie;
                    break;
                }
            }
        }
    }

    public void deleteStockage(int id) {
        stockages.removeIf(s -> s.id == id);
    }

    public void saveClient(Integer id, String nom, String type, String adresse, String tel, String email) {
        if (id == null) {
            clients.add(new Client(nextClientId++, nom.trim(), type, adresse.trim(), tel.trim(), email.trim()));
        } else {
            for (Client c : clients) {
                if (c.id == id) {
                    c.nom = nom.trim();
                    c.typeClient = type;
                    c.adresse = adresse.trim();
                    c.telephone = tel.trim();
                    c.email = email.trim();
                    break;
                }
            }
        }
        clients.sort(Comparator.comparing(a -> a.nom.toLowerCase(Locale.ROOT)));
    }

    public void deleteClient(int id) {
        clients.removeIf(c -> c.id == id);
    }

    public void saveDistribution(Integer id, int lotId, int clientId, String dateLiv, double quantite, String bl, String statut) {
        if (id == null) {
            distributions.add(0, new Distribution(nextDistributionId++, lotId, clientId, dateLiv, quantite, bl.trim(), statut));
        } else {
            for (Distribution d : distributions) {
                if (d.id == id) {
                    d.lotId = lotId;
                    d.clientId = clientId;
                    d.dateLivraison = dateLiv;
                    d.quantiteKg = quantite;
                    d.bonLivraison = bl.trim();
                    d.statut = statut;
                    break;
                }
            }
        }
    }

    public void deleteDistribution(int id) {
        distributions.removeIf(d -> d.id == id);
    }

    public void saveUtilisateur(Integer id, String nom, String email, String role, int actif, String password) {
        if (id == null) {
            utilisateurs.add(new Utilisateur(nextUtilisateurId++, nom.trim(), email.trim(), password, role, actif, nowDateTime()));
        } else {
            for (Utilisateur u : utilisateurs) {
                if (u.id == id) {
                    u.nom = nom.trim();
                    u.email = email.trim();
                    u.role = role;
                    u.actif = actif;
                    if (password != null && !password.trim().isEmpty()) {
                        u.passwordPlain = password;
                    }
                    break;
                }
            }
        }
        utilisateurs.sort(Comparator.comparing(a -> a.nom.toLowerCase(Locale.ROOT)));
    }

    public boolean deleteUtilisateur(int id) {
        if (currentUser != null && currentUser.id == id) {
            return false;
        }
        utilisateurs.removeIf(u -> u.id == id);
        return true;
    }
}
