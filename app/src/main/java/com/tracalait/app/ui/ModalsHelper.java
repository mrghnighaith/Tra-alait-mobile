package com.tracalait.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import com.tracalait.app.data.TracaLaitRepository;
import com.tracalait.app.model.Models.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModalsHelper {

    public interface ModalCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    private static LinearLayout createFormContainer(Context context, ScrollView scrollOut) {
        LinearLayout form = new LinearLayout(context);
        form.setOrientation(LinearLayout.VERTICAL);
        int pad = UiHelper.dp(context, 20);
        form.setPadding(pad, UiHelper.dp(context, 8), pad, pad);
        scrollOut.addView(form);
        return form;
    }

    private static Double parseNullableDouble(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void showFournisseurModal(Context context, TracaLaitRepository repo, Fournisseur item, ModalCallback cb) {
        ScrollView scroll = new ScrollView(context);
        LinearLayout form = createFormContainer(context, scroll);

        EditText nomIn = UiHelper.addFormInput(context, form, "Nom *", item != null ? item.nom : "", "Nom du fournisseur", false);
        EditText adrIn = UiHelper.addFormInput(context, form, "Adresse", item != null ? item.adresse : "", "Adresse complète", false);
        EditText telIn = UiHelper.addFormInput(context, form, "Téléphone", item != null ? item.telephone : "", "+216 XX XXX XXX", false);
        EditText emailIn = UiHelper.addFormInput(context, form, "Email", item != null ? item.email : "", "contact@exemple.tn", false);

        new AlertDialog.Builder(context)
                .setTitle(item == null ? "Nouveau fournisseur" : "Modifier fournisseur")
                .setView(scroll)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Enregistrer", (d, w) -> {
                    String nom = nomIn.getText().toString().trim();
                    if (nom.isEmpty()) {
                        cb.onError("Le nom est requis");
                        return;
                    }
                    repo.saveFournisseur(
                            item != null ? item.id : null,
                            nom,
                            adrIn.getText().toString(),
                            telIn.getText().toString(),
                            emailIn.getText().toString()
                    );
                    cb.onSuccess(item == null ? "Fournisseur créé" : "Fournisseur mis à jour");
                })
                .show();
    }

    public static void showReceptionModal(Context context, TracaLaitRepository repo, Reception item, ModalCallback cb) {
        ScrollView scroll = new ScrollView(context);
        LinearLayout form = createFormContainer(context, scroll);

        List<String> fLabels = new ArrayList<>();
        int selFIndex = 0;
        for (int i = 0; i < repo.fournisseurs.size(); i++) {
            Fournisseur f = repo.fournisseurs.get(i);
            fLabels.add(f.nom);
            if (item != null && item.fournisseurId == f.id) selFIndex = i;
        }
        Spinner fSpin = UiHelper.addFormSpinner(context, form, "Fournisseur *", fLabels, selFIndex);
        EditText dateIn = UiHelper.addFormInput(context, form, "Date *", item != null ? item.dateReception : repo.daysOffset(0), "YYYY-MM-DD", false);
        EditText heureIn = UiHelper.addFormInput(context, form, "Heure *", item != null ? item.heureReception : "07:00", "07:00", false);
        EditText qteIn = UiHelper.addFormInput(context, form, "Quantité (litres) *", item != null ? String.valueOf(item.quantiteLitres) : "", "ex: 1500", true);
        EditText tempIn = UiHelper.addFormInput(context, form, "Température (°C)", item != null && item.temperatureC != null ? String.valueOf(item.temperatureC) : "", "ex: 4.2", true);
        EditText phIn = UiHelper.addFormInput(context, form, "pH", item != null && item.ph != null ? String.valueOf(item.ph) : "", "ex: 6.7", true);
        EditText mgIn = UiHelper.addFormInput(context, form, "Matières grasses (%)", item != null && item.tauxMatieresGrasses != null ? String.valueOf(item.tauxMatieresGrasses) : "", "ex: 3.8", true);

        List<String> controles = Arrays.asList("En attente", "Accepté", "Refusé");
        int selCtrl = item != null ? Math.max(0, controles.indexOf(item.resultatControle)) : 0;
        Spinner ctrlSpin = UiHelper.addFormSpinner(context, form, "Résultat contrôle", controles, selCtrl);
        EditText obsIn = UiHelper.addFormInput(context, form, "Observations", item != null ? item.observations : "", "Notes éventuelles…", false);

        new AlertDialog.Builder(context)
                .setTitle(item == null ? "Nouvelle réception" : "Modifier réception")
                .setView(scroll)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Enregistrer", (d, w) -> {
                    Double qte = parseNullableDouble(qteIn.getText().toString());
                    if (qte == null || qte <= 0) {
                        cb.onError("La quantité est requise");
                        return;
                    }
                    int fIdx = fSpin.getSelectedItemPosition();
                    int fId = (fIdx >= 0 && fIdx < repo.fournisseurs.size()) ? repo.fournisseurs.get(fIdx).id : 1;
                    repo.saveReception(
                            item != null ? item.id : null,
                            fId,
                            dateIn.getText().toString().trim(),
                            heureIn.getText().toString().trim(),
                            qte,
                            parseNullableDouble(tempIn.getText().toString()),
                            parseNullableDouble(phIn.getText().toString()),
                            parseNullableDouble(mgIn.getText().toString()),
                            controles.get(ctrlSpin.getSelectedItemPosition()),
                            obsIn.getText().toString()
                    );
                    cb.onSuccess(item == null ? "Réception enregistrée" : "Réception mise à jour");
                })
                .show();
    }

    public static void showLotModal(Context context, TracaLaitRepository repo, LotProduction item, ModalCallback cb) {
        ScrollView scroll = new ScrollView(context);
        LinearLayout form = createFormContainer(context, scroll);

        String defaultNum = "LOT-2026-" + String.format("%04d", System.currentTimeMillis() % 10000);
        EditText numIn = UiHelper.addFormInput(context, form, "Numéro de lot *", item != null ? item.numeroLot : defaultNum, "LOT-2026-XXX", false);

        List<String> pLabels = new ArrayList<>();
        int selP = 0;
        for (int i = 0; i < repo.produits.size(); i++) {
            Produit p = repo.produits.get(i);
            pLabels.add(p.nom);
            if (item != null && item.produitId == p.id) selP = i;
        }
        Spinner pSpin = UiHelper.addFormSpinner(context, form, "Produit *", pLabels, selP);

        List<String> rLabels = new ArrayList<>();
        int selR = 0;
        for (int i = 0; i < repo.receptions.size(); i++) {
            Reception r = repo.receptions.get(i);
            Fournisseur f = repo.findFournisseur(r.fournisseurId);
            rLabels.add(r.dateReception + " – " + (f != null ? f.nom : "") + " (" + r.quantiteLitres + "L)");
            if (item != null && item.receptionId == r.id) selR = i;
        }
        Spinner rSpin = UiHelper.addFormSpinner(context, form, "Réception lait *", rLabels, selR);

        EditText dateProdIn = UiHelper.addFormInput(context, form, "Date de production *", item != null ? item.dateProduction : repo.daysOffset(0), "YYYY-MM-DD", false);
        EditText qteIn = UiHelper.addFormInput(context, form, "Quantité (kg) *", item != null ? String.valueOf(item.quantiteKg) : "", "ex: 500", true);
        EditText dateExpIn = UiHelper.addFormInput(context, form, "Date d'expiration *", item != null ? item.dateExpiration : repo.daysOffset(15), "YYYY-MM-DD", false);

        List<String> statuts = Arrays.asList("En production", "Stocké", "Distribué", "Rappelé");
        int selSt = item != null ? Math.max(0, statuts.indexOf(item.statut)) : 0;
        Spinner stSpin = UiHelper.addFormSpinner(context, form, "Statut", statuts, selSt);
        EditText obsIn = UiHelper.addFormInput(context, form, "Observations", item != null ? item.observations : "", "Notes…", false);

        new AlertDialog.Builder(context)
                .setTitle(item == null ? "Nouveau lot de production" : "Modifier lot")
                .setView(scroll)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Enregistrer", (d, w) -> {
                    String num = numIn.getText().toString().trim();
                    Double qte = parseNullableDouble(qteIn.getText().toString());
                    if (num.isEmpty() || qte == null || qte <= 0) {
                        cb.onError("Champs requis manquants");
                        return;
                    }
                    int pIdx = pSpin.getSelectedItemPosition();
                    int rIdx = rSpin.getSelectedItemPosition();
                    int pId = (pIdx >= 0 && pIdx < repo.produits.size()) ? repo.produits.get(pIdx).id : 1;
                    int rId = (rIdx >= 0 && rIdx < repo.receptions.size()) ? repo.receptions.get(rIdx).id : 1;
                    repo.saveLot(
                            item != null ? item.id : null,
                            num,
                            pId,
                            rId,
                            dateProdIn.getText().toString().trim(),
                            qte,
                            dateExpIn.getText().toString().trim(),
                            statuts.get(stSpin.getSelectedItemPosition()),
                            obsIn.getText().toString()
                    );
                    cb.onSuccess(item == null ? "Lot créé" : "Lot mis à jour");
                })
                .show();
    }

    public static void showDistributionModal(Context context, TracaLaitRepository repo, Distribution item, ModalCallback cb) {
        ScrollView scroll = new ScrollView(context);
        LinearLayout form = createFormContainer(context, scroll);

        List<String> lLabels = new ArrayList<>();
        int selL = 0;
        for (int i = 0; i < repo.lots.size(); i++) {
            LotProduction l = repo.lots.get(i);
            Produit p = repo.findProduit(l.produitId);
            lLabels.add(l.numeroLot + " – " + (p != null ? p.nom : ""));
            if (item != null && item.lotId == l.id) selL = i;
        }
        Spinner lSpin = UiHelper.addFormSpinner(context, form, "Lot *", lLabels, selL);

        List<String> cLabels = new ArrayList<>();
        int selC = 0;
        for (int i = 0; i < repo.clients.size(); i++) {
            Client c = repo.clients.get(i);
            cLabels.add(c.nom);
            if (item != null && item.clientId == c.id) selC = i;
        }
        Spinner cSpin = UiHelper.addFormSpinner(context, form, "Client *", cLabels, selC);

        EditText dateIn = UiHelper.addFormInput(context, form, "Date de livraison *", item != null ? item.dateLivraison : repo.daysOffset(0), "YYYY-MM-DD", false);
        EditText qteIn = UiHelper.addFormInput(context, form, "Quantité (kg) *", item != null ? String.valueOf(item.quantiteKg) : "", "ex: 200", true);
        String defBl = "BL-2026-" + String.format("%03d", System.currentTimeMillis() % 1000);
        EditText blIn = UiHelper.addFormInput(context, form, "Bon de livraison", item != null ? item.bonLivraison : defBl, "BL-2026-XXX", false);

        List<String> statuts = Arrays.asList("Planifiée", "Livrée", "Annulée");
        int selSt = item != null ? Math.max(0, statuts.indexOf(item.statut)) : 0;
        Spinner stSpin = UiHelper.addFormSpinner(context, form, "Statut", statuts, selSt);

        new AlertDialog.Builder(context)
                .setTitle(item == null ? "Nouvelle distribution" : "Modifier distribution")
                .setView(scroll)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Enregistrer", (d, w) -> {
                    Double qte = parseNullableDouble(qteIn.getText().toString());
                    if (qte == null || qte <= 0) {
                        cb.onError("La quantité est requise");
                        return;
                    }
                    int lIdx = lSpin.getSelectedItemPosition();
                    int cIdx = cSpin.getSelectedItemPosition();
                    int lId = (lIdx >= 0 && lIdx < repo.lots.size()) ? repo.lots.get(lIdx).id : 1;
                    int cId = (cIdx >= 0 && cIdx < repo.clients.size()) ? repo.clients.get(cIdx).id : 1;
                    repo.saveDistribution(
                            item != null ? item.id : null,
                            lId,
                            cId,
                            dateIn.getText().toString().trim(),
                            qte,
                            blIn.getText().toString(),
                            statuts.get(stSpin.getSelectedItemPosition())
                    );
                    cb.onSuccess(item == null ? "Distribution créée" : "Distribution mise à jour");
                })
                .show();
    }

    public static void showClientModal(Context context, TracaLaitRepository repo, Client item, ModalCallback cb) {
        ScrollView scroll = new ScrollView(context);
        LinearLayout form = createFormContainer(context, scroll);

        EditText nomIn = UiHelper.addFormInput(context, form, "Nom *", item != null ? item.nom : "", "Nom du client", false);
        List<String> types = Arrays.asList("Supermarché", "Restaurant", "Particulier", "Épicerie", "Autre");
        int selT = item != null ? Math.max(0, types.indexOf(item.typeClient)) : 0;
        Spinner typeSpin = UiHelper.addFormSpinner(context, form, "Type *", types, selT);
        EditText adrIn = UiHelper.addFormInput(context, form, "Adresse", item != null ? item.adresse : "", "Adresse complète", false);
        EditText telIn = UiHelper.addFormInput(context, form, "Téléphone", item != null ? item.telephone : "", "+216 XX XXX XXX", false);
        EditText emailIn = UiHelper.addFormInput(context, form, "Email", item != null ? item.email : "", "contact@exemple.tn", false);

        new AlertDialog.Builder(context)
                .setTitle(item == null ? "Nouveau client" : "Modifier client")
                .setView(scroll)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Enregistrer", (d, w) -> {
                    String nom = nomIn.getText().toString().trim();
                    if (nom.isEmpty()) {
                        cb.onError("Le nom est requis");
                        return;
                    }
                    repo.saveClient(
                            item != null ? item.id : null,
                            nom,
                            types.get(typeSpin.getSelectedItemPosition()),
                            adrIn.getText().toString(),
                            telIn.getText().toString(),
                            emailIn.getText().toString()
                    );
                    cb.onSuccess(item == null ? "Client créé" : "Client mis à jour");
                })
                .show();
    }

    public static void showProduitModal(Context context, TracaLaitRepository repo, Produit item, ModalCallback cb) {
        ScrollView scroll = new ScrollView(context);
        LinearLayout form = createFormContainer(context, scroll);

        EditText nomIn = UiHelper.addFormInput(context, form, "Nom *", item != null ? item.nom : "", "Nom du produit", false);
        List<String> types = Arrays.asList("Lait cru", "Lait pasteurisé", "Fromage", "Yaourt", "Beurre", "Crème", "Autre");
        int selT = item != null ? Math.max(0, types.indexOf(item.typeProduit)) : 1;
        Spinner typeSpin = UiHelper.addFormSpinner(context, form, "Type *", types, selT);
        EditText descIn = UiHelper.addFormInput(context, form, "Description", item != null ? item.description : "", "Description du produit…", false);

        new AlertDialog.Builder(context)
                .setTitle(item == null ? "Nouveau produit" : "Modifier produit")
                .setView(scroll)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Enregistrer", (d, w) -> {
                    String nom = nomIn.getText().toString().trim();
                    if (nom.isEmpty()) {
                        cb.onError("Le nom est requis");
                        return;
                    }
                    repo.saveProduit(
                            item != null ? item.id : null,
                            nom,
                            types.get(typeSpin.getSelectedItemPosition()),
                            descIn.getText().toString()
                    );
                    cb.onSuccess(item == null ? "Produit créé" : "Produit mis à jour");
                })
                .show();
    }

    public static void showStockageModal(Context context, TracaLaitRepository repo, Stockage item, ModalCallback cb) {
        ScrollView scroll = new ScrollView(context);
        LinearLayout form = createFormContainer(context, scroll);

        List<String> lLabels = new ArrayList<>();
        int selL = 0;
        for (int i = 0; i < repo.lots.size(); i++) {
            LotProduction l = repo.lots.get(i);
            Produit p = repo.findProduit(l.produitId);
            lLabels.add(l.numeroLot + " – " + (p != null ? p.nom : ""));
            if (item != null && item.lotId == l.id) selL = i;
        }
        Spinner lSpin = UiHelper.addFormSpinner(context, form, "Lot de production *", lLabels, selL);
        EditText zoneIn = UiHelper.addFormInput(context, form, "Zone de stockage *", item != null ? item.zoneStockage : "", "ex : Zone A – Chambre froide 1", false);
        EditText tempIn = UiHelper.addFormInput(context, form, "Température (°C)", item != null && item.temperatureC != null ? String.valueOf(item.temperatureC) : "", "ex : 4.0", true);
        EditText entreeIn = UiHelper.addFormInput(context, form, "Date d'entrée *", item != null ? item.dateEntree : repo.nowDateTime(), "YYYY-MM-DD HH:mm", false);
        EditText sortieIn = UiHelper.addFormInput(context, form, "Date de sortie", item != null && item.dateSortie != null ? item.dateSortie : "", "Laisser vide si en stock", false);

        new AlertDialog.Builder(context)
                .setTitle(item == null ? "Nouveau stockage" : "Modifier stockage")
                .setView(scroll)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Enregistrer", (d, w) -> {
                    String zone = zoneIn.getText().toString().trim();
                    String entree = entreeIn.getText().toString().trim();
                    if (zone.isEmpty() || entree.isEmpty()) {
                        cb.onError("La zone et la date d'entrée sont requises");
                        return;
                    }
                    int lIdx = lSpin.getSelectedItemPosition();
                    int lId = (lIdx >= 0 && lIdx < repo.lots.size()) ? repo.lots.get(lIdx).id : 1;
                    repo.saveStockage(
                            item != null ? item.id : null,
                            lId,
                            zone,
                            parseNullableDouble(tempIn.getText().toString()),
                            entree,
                            sortieIn.getText().toString()
                    );
                    cb.onSuccess(item == null ? "Stockage enregistré" : "Stockage mis à jour");
                })
                .show();
    }

    public static void showUtilisateurModal(Context context, TracaLaitRepository repo, Utilisateur item, ModalCallback cb) {
        ScrollView scroll = new ScrollView(context);
        LinearLayout form = createFormContainer(context, scroll);

        EditText nomIn = UiHelper.addFormInput(context, form, "Nom *", item != null ? item.nom : "", "Nom complet", false);
        EditText emailIn = UiHelper.addFormInput(context, form, "Email *", item != null ? item.email : "", "utilisateur@exemple.tn", false);

        List<String> roles = Arrays.asList("Administrateur", "Responsable Qualité", "Responsable Production");
        int selR = item != null ? Math.max(0, roles.indexOf(item.role)) : 1;
        Spinner roleSpin = UiHelper.addFormSpinner(context, form, "Rôle *", roles, selR);

        List<String> statuts = Arrays.asList("Actif", "Inactif");
        int selActif = (item != null && item.actif == 0) ? 1 : 0;
        Spinner actifSpin = UiHelper.addFormSpinner(context, form, "Statut", statuts, selActif);

        EditText passIn = UiHelper.addFormInput(context, form, "Mot de passe", "", item != null ? "Laisser vide pour conserver" : "Minimum 6 caractères", false);

        new AlertDialog.Builder(context)
                .setTitle(item == null ? "Nouvel utilisateur" : "Modifier utilisateur")
                .setView(scroll)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Enregistrer", (d, w) -> {
                    String nom = nomIn.getText().toString().trim();
                    String email = emailIn.getText().toString().trim();
                    String pass = passIn.getText().toString();
                    if (nom.isEmpty() || email.isEmpty()) {
                        cb.onError("Nom et email sont requis");
                        return;
                    }
                    if (item == null && pass.length() < 6) {
                        cb.onError("Le mot de passe doit contenir au moins 6 caractères");
                        return;
                    }
                    int actif = actifSpin.getSelectedItemPosition() == 0 ? 1 : 0;
                    repo.saveUtilisateur(
                            item != null ? item.id : null,
                            nom,
                            email,
                            roles.get(roleSpin.getSelectedItemPosition()),
                            actif,
                            pass
                    );
                    cb.onSuccess(item == null ? "Utilisateur créé" : "Utilisateur mis à jour");
                })
                .show();
    }
}
