package com.tracalait.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.tracalait.app.data.TracaLaitRepository;
import com.tracalait.app.model.Models.*;
import com.tracalait.app.ui.ModalsHelper;
import com.tracalait.app.ui.UiHelper;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {

    private TracaLaitRepository repo;
    private FrameLayout rootContainer;
    private boolean isDrawerOpen = false;
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.FRANCE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repo = TracaLaitRepository.getInstance();
        rootContainer = new FrameLayout(this);
        rootContainer.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        setContentView(rootContainer);
        render();
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpen) {
            isDrawerOpen = false;
            render();
        } else if (repo.currentUser != null && repo.currentView != AppView.DASHBOARD) {
            repo.currentView = AppView.DASHBOARD;
            render();
        } else {
            super.onBackPressed();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private ModalsHelper.ModalCallback createModalCallback() {
        return new ModalsHelper.ModalCallback() {
            @Override
            public void onSuccess(String message) {
                showToast(message);
                render();
            }

            @Override
            public void onError(String error) {
                showToast(error);
            }
        };
    }

    private void render() {
        rootContainer.removeAllViews();
        if (repo.currentUser == null) {
            rootContainer.addView(buildLoginView());
        } else {
            rootContainer.addView(buildAuthenticatedShell());
        }
    }

    // ── LOGIN SCREEN ─────────────────────────────────────────────
    private View buildLoginView() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{UiHelper.COLOR_NAVY, UiHelper.COLOR_BLUE}
        );
        scroll.setBackground(bg);

        LinearLayout outer = new LinearLayout(this);
        outer.setOrientation(LinearLayout.VERTICAL);
        outer.setGravity(Gravity.CENTER);
        int pad = UiHelper.dp(this, 24);
        outer.setPadding(pad, pad, pad, pad);

        LinearLayout card = UiHelper.createCard(this);
        int cardPad = UiHelper.dp(this, 26);
        card.setPadding(cardPad, cardPad, cardPad, cardPad);
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        card.setLayoutParams(cardLp);

        TextView logoTv = new TextView(this);
        logoTv.setText("🥛");
        logoTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
        logoTv.setGravity(Gravity.CENTER);
        logoTv.setBackground(UiHelper.roundedBg(this, UiHelper.COLOR_BLUE_LT, 10f, 0, 0));
        LinearLayout.LayoutParams logoLp = new LinearLayout.LayoutParams(UiHelper.dp(this, 48), UiHelper.dp(this, 48));
        card.addView(logoTv, logoLp);

        TextView titleTv = new TextView(this);
        titleTv.setText("TraçaLait");
        titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
        titleTv.setTypeface(Typeface.DEFAULT_BOLD);
        titleTv.setTextColor(UiHelper.COLOR_NAVY);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLp.topMargin = UiHelper.dp(this, 12);
        card.addView(titleTv, titleLp);

        TextView subTv = new TextView(this);
        subTv.setText("Connexion à la traçabilité laitière");
        subTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
        subTv.setTextColor(UiHelper.COLOR_GRAY_600);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.topMargin = UiHelper.dp(this, 4);
        subLp.bottomMargin = UiHelper.dp(this, 8);
        card.addView(subTv, subLp);

        EditText emailIn = UiHelper.addFormInput(this, card, "Email", "admin@tracalait.local", "admin@tracalait.local", false);
        EditText passIn = UiHelper.addFormInput(this, card, "Mot de passe", "admin123", "Mot de passe", false);
        passIn.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        Button loginBtn = UiHelper.createPrimaryButton(this, "Se connecter", v -> {
            String err = repo.login(emailIn.getText().toString(), passIn.getText().toString());
            if (err != null) {
                showToast(err);
            } else {
                showToast("Connexion réussie");
                render();
            }
        });
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLp.topMargin = UiHelper.dp(this, 18);
        card.addView(loginBtn, btnLp);

        TextView demoLabel = new TextView(this);
        demoLabel.setText("Comptes de démo (cliquez pour remplir) :");
        demoLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        demoLabel.setTextColor(UiHelper.COLOR_GRAY_600);
        LinearLayout.LayoutParams dlLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dlLp.topMargin = UiHelper.dp(this, 14);
        card.addView(demoLabel, dlLp);

        LinearLayout demoRow = new LinearLayout(this);
        demoRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams drLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        drLp.topMargin = UiHelper.dp(this, 6);

        String[][] demos = {
                {"Admin", "admin@tracalait.local"},
                {"Qualité", "qualite@tracalait.local"},
                {"Production", "production@tracalait.local"}
        };
        for (String[] d : demos) {
            Button chip = UiHelper.createOutlineButton(this, d[0], v -> {
                emailIn.setText(d[1]);
                passIn.setText("admin123");
            });
            LinearLayout.LayoutParams cLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            cLp.rightMargin = UiHelper.dp(this, 6);
            demoRow.addView(chip, cLp);
        }
        card.addView(demoRow, drLp);

        outer.addView(card);
        scroll.addView(outer);
        return scroll;
    }

    // ── AUTHENTICATED SHELL (HEADER + CONTENT + DRAWER) ─────────
    private View buildAuthenticatedShell() {
        FrameLayout shell = new FrameLayout(this);
        shell.setBackgroundColor(UiHelper.COLOR_MILK);

        LinearLayout mainColumn = new LinearLayout(this);
        mainColumn.setOrientation(LinearLayout.VERTICAL);
        mainColumn.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Top Header Bar
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setBackgroundColor(UiHelper.COLOR_WHITE);
        header.setElevation(UiHelper.dp(this, 3));
        header.setPadding(UiHelper.dp(this, 12), UiHelper.dp(this, 12), UiHelper.dp(this, 14), UiHelper.dp(this, 12));

        Button menuBtn = UiHelper.createOutlineButton(this, "☰", v -> {
            isDrawerOpen = !isDrawerOpen;
            render();
        });
        header.addView(menuBtn);

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams tbLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tbLp.leftMargin = UiHelper.dp(this, 12);

        TextView pageTitle = new TextView(this);
        pageTitle.setText(repo.currentView.title);
        pageTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f);
        pageTitle.setTypeface(Typeface.DEFAULT_BOLD);
        pageTitle.setTextColor(UiHelper.COLOR_NAVY);
        titleBox.addView(pageTitle);

        TextView userSub = new TextView(this);
        userSub.setText(repo.currentUser.nom + " – " + repo.currentUser.role);
        userSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        userSub.setTextColor(UiHelper.COLOR_GRAY_600);
        titleBox.addView(userSub);

        header.addView(titleBox, tbLp);

        Button logoutBtn = UiHelper.createOutlineButton(this, "Déconnexion", v -> {
            repo.logout();
            isDrawerOpen = false;
            showToast("Déconnexion réussie");
            render();
        });
        header.addView(logoutBtn);

        mainColumn.addView(header);
        mainColumn.addView(UiHelper.createDivider(this));

        // Main Scrollable Content
        ScrollView contentScroll = new ScrollView(this);
        contentScroll.setFillViewport(true);
        LinearLayout.LayoutParams csLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        );
        LinearLayout contentWrapper = new LinearLayout(this);
        contentWrapper.setOrientation(LinearLayout.VERTICAL);
        int cPad = UiHelper.dp(this, 16);
        contentWrapper.setPadding(cPad, cPad, cPad, cPad);
        contentWrapper.addView(buildCurrentViewContent());
        contentScroll.addView(contentWrapper);
        mainColumn.addView(contentScroll, csLp);

        shell.addView(mainColumn);

        // Navigation Drawer Overlay
        if (isDrawerOpen) {
            View scrim = new View(this);
            scrim.setBackgroundColor(Color.argb(120, 0, 0, 0));
            scrim.setOnClickListener(v -> {
                isDrawerOpen = false;
                render();
            });
            shell.addView(scrim, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));

            shell.addView(buildSidebarDrawer());
        }

        return shell;
    }

    private View buildSidebarDrawer() {
        ScrollView drawerScroll = new ScrollView(this);
        drawerScroll.setBackgroundColor(UiHelper.COLOR_NAVY);
        FrameLayout.LayoutParams drawerLp = new FrameLayout.LayoutParams(
                UiHelper.dp(this, 260),
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        drawerScroll.setLayoutParams(drawerLp);

        LinearLayout navCol = new LinearLayout(this);
        navCol.setOrientation(LinearLayout.VERTICAL);
        navCol.setPadding(0, UiHelper.dp(this, 16), 0, UiHelper.dp(this, 24));

        // Brand Logo Row
        LinearLayout brandRow = new LinearLayout(this);
        brandRow.setOrientation(LinearLayout.HORIZONTAL);
        brandRow.setGravity(Gravity.CENTER_VERTICAL);
        brandRow.setPadding(UiHelper.dp(this, 18), UiHelper.dp(this, 8), UiHelper.dp(this, 18), UiHelper.dp(this, 16));

        TextView iconTv = new TextView(this);
        iconTv.setText("🥛");
        iconTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        iconTv.setGravity(Gravity.CENTER);
        iconTv.setBackground(UiHelper.roundedBg(this, UiHelper.COLOR_BLUE, 8f, 0, 0));
        brandRow.addView(iconTv, new LinearLayout.LayoutParams(UiHelper.dp(this, 36), UiHelper.dp(this, 36)));

        TextView brandText = new TextView(this);
        brandText.setText("TraçaLait  v1.0");
        brandText.setTextColor(Color.WHITE);
        brandText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        brandText.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams btLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btLp.leftMargin = UiHelper.dp(this, 10);
        brandRow.addView(brandText, btLp);
        navCol.addView(brandRow);

        boolean isAdmin = PermissionHelper.isAdmin(repo.currentUser);
        String lastSection = "";

        for (AppView view : AppView.values()) {
            if (view.adminOnly && !isAdmin) continue;
            if (!view.section.equals(lastSection)) {
                lastSection = view.section;
                TextView secTv = new TextView(this);
                secTv.setText(lastSection.toUpperCase(Locale.FRANCE));
                secTv.setTextColor(Color.argb(120, 255, 255, 255));
                secTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5f);
                secTv.setTypeface(Typeface.DEFAULT_BOLD);
                secTv.setPadding(UiHelper.dp(this, 18), UiHelper.dp(this, 14), UiHelper.dp(this, 18), UiHelper.dp(this, 6));
                navCol.addView(secTv);
            }

            boolean active = repo.currentView == view;
            LinearLayout itemRow = new LinearLayout(this);
            itemRow.setOrientation(LinearLayout.HORIZONTAL);
            itemRow.setGravity(Gravity.CENTER_VERTICAL);
            itemRow.setBackgroundColor(active ? UiHelper.COLOR_BLUE : Color.TRANSPARENT);
            itemRow.setPadding(UiHelper.dp(this, 18), UiHelper.dp(this, 12), UiHelper.dp(this, 18), UiHelper.dp(this, 12));
            itemRow.setOnClickListener(v -> {
                repo.currentView = view;
                isDrawerOpen = false;
                render();
            });

            TextView itemTv = new TextView(this);
            itemTv.setText(view.iconEmoji + "   " + view.title);
            itemTv.setTextColor(active ? Color.WHITE : Color.argb(200, 255, 255, 255));
            itemTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            if (active) itemTv.setTypeface(Typeface.DEFAULT_BOLD);
            itemRow.addView(itemTv);
            navCol.addView(itemRow);
        }

        drawerScroll.addView(navCol);
        return drawerScroll;
    }

    private View buildCurrentViewContent() {
        switch (repo.currentView) {
            case DASHBOARD:
                return buildDashboardView();
            case FOURNISSEURS:
                return buildFournisseursView();
            case RECEPTIONS:
                return buildReceptionsView();
            case LOTS:
                return buildLotsView();
            case DISTRIBUTIONS:
                return buildDistributionsView();
            case CLIENTS:
                return buildClientsView();
            case TRACABILITE:
                return buildTracabiliteView();
            case PRODUITS:
                return buildProduitsView();
            case STOCKAGES:
                return buildStockagesView();
            case UTILISATEURS:
                return buildUtilisateursView();
            case JOURNAL_CONNEXIONS:
                return buildJournalConnexionsView();
            default:
                return buildDashboardView();
        }
    }

    // ── DASHBOARD ────────────────────────────────────────────────
    private View buildDashboardView() {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);

        int totalReceptions = repo.receptions.size();
        double litresAcceptes = 0;
        for (Reception r : repo.receptions) {
            if ("Accepté".equals(r.resultatControle)) litresAcceptes += r.quantiteLitres;
        }
        int totalLots = repo.lots.size();
        int lotsStockes = 0;
        int lotsExpires = 0;
        String today = repo.daysOffset(0);
        Map<String, Integer> statusCounts = new LinkedHashMap<>();
        for (LotProduction l : repo.lots) {
            if ("Stocké".equals(l.statut)) lotsStockes++;
            if (l.dateExpiration.compareTo(today) < 0 && !"Distribué".equals(l.statut)) lotsExpires++;
            statusCounts.put(l.statut, statusCounts.getOrDefault(l.statut, 0) + 1);
        }
        int livraisons = 0;
        for (Distribution d : repo.distributions) {
            if ("Livrée".equals(d.statut)) livraisons++;
        }

        String[][] kpis = {
                {"🥛", String.valueOf(totalReceptions), "Réceptions totales"},
                {"📦", numberFormat.format(litresAcceptes), "Litres acceptés"},
                {"🧀", String.valueOf(totalLots), "Lots créés"},
                {"🏬", String.valueOf(lotsStockes), "Lots en stock"},
                {"🚚", String.valueOf(livraisons), "Livraisons effectuées"},
                {"⚠️", String.valueOf(lotsExpires), "Lots expirés"}
        };

        for (int i = 0; i < kpis.length; i += 2) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowLp.bottomMargin = UiHelper.dp(this, 12);

            for (int j = i; j < i + 2 && j < kpis.length; j++) {
                LinearLayout statCard = UiHelper.createCard(this);
                int pad = UiHelper.dp(this, 16);
                statCard.setPadding(pad, pad, pad, pad);
                LinearLayout.LayoutParams scLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                if (j == i) scLp.rightMargin = UiHelper.dp(this, 6);
                else scLp.leftMargin = UiHelper.dp(this, 6);

                TextView iconTv = new TextView(this);
                iconTv.setText(kpis[j][0]);
                iconTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f);
                statCard.addView(iconTv);

                TextView valTv = new TextView(this);
                valTv.setText(kpis[j][1]);
                valTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);
                valTv.setTypeface(Typeface.DEFAULT_BOLD);
                valTv.setTextColor(UiHelper.COLOR_NAVY);
                statCard.addView(valTv);

                TextView lblTv = new TextView(this);
                lblTv.setText(kpis[j][2]);
                lblTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
                lblTv.setTextColor(UiHelper.COLOR_GRAY_400);
                statCard.addView(lblTv);

                row.addView(statCard, scLp);
            }
            col.addView(row, rowLp);
        }

        // Répartition des lots par statut
        LinearLayout chartCard = UiHelper.createSectionCard(this, "Répartition des lots par statut", null, null);
        LinearLayout chartBody = new LinearLayout(this);
        chartBody.setOrientation(LinearLayout.VERTICAL);
        int cPad = UiHelper.dp(this, 18);
        chartBody.setPadding(cPad, cPad, cPad, cPad);

        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            LinearLayout itemHeader = new LinearLayout(this);
            itemHeader.setOrientation(LinearLayout.HORIZONTAL);
            TextView stName = new TextView(this);
            stName.setText(entry.getKey());
            stName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
            stName.setTextColor(UiHelper.COLOR_GRAY_800);
            itemHeader.addView(stName, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            TextView stVal = new TextView(this);
            stVal.setText(String.valueOf(entry.getValue()));
            stVal.setTypeface(Typeface.DEFAULT_BOLD);
            stVal.setTextColor(UiHelper.COLOR_NAVY);
            itemHeader.addView(stVal);
            chartBody.addView(itemHeader);

            LinearLayout barTrack = new LinearLayout(this);
            barTrack.setOrientation(LinearLayout.HORIZONTAL);
            barTrack.setBackground(UiHelper.roundedBg(this, UiHelper.COLOR_GRAY_200, 4f, 0, 0));
            LinearLayout.LayoutParams trackLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiHelper.dp(this, 8));
            trackLp.topMargin = UiHelper.dp(this, 4);
            trackLp.bottomMargin = UiHelper.dp(this, 12);

            int fillColor = UiHelper.COLOR_GRAY_400;
            if ("En production".equals(entry.getKey())) fillColor = UiHelper.COLOR_AMBER;
            else if ("Stocké".equals(entry.getKey())) fillColor = UiHelper.COLOR_BLUE;
            else if ("Distribué".equals(entry.getKey())) fillColor = UiHelper.COLOR_GREEN;
            else if ("Rappelé".equals(entry.getKey())) fillColor = UiHelper.COLOR_RED;

            float ratio = Math.min(1f, entry.getValue() * 0.25f);
            View fill = new View(this);
            fill.setBackground(UiHelper.roundedBg(this, fillColor, 4f, 0, 0));
            barTrack.addView(fill, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, ratio));
            View rest = new View(this);
            barTrack.addView(rest, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f - ratio));

            chartBody.addView(barTrack, trackLp);
        }
        chartCard.addView(chartBody);
        col.addView(chartCard);
        return col;
    }

    // ── FOURNISSEURS ─────────────────────────────────────────────
    private View buildFournisseursView() {
        boolean canWrite = PermissionHelper.canWrite(repo.currentUser, "fournisseurs");
        LinearLayout card = UiHelper.createSectionCard(
                this,
                "Fournisseurs de lait",
                canWrite ? "+ Nouveau" : null,
                v -> ModalsHelper.showFournisseurModal(this, repo, null, createModalCallback())
        );

        if (repo.fournisseurs.isEmpty()) {
            card.addView(UiHelper.createEmptyState(this, "🏭", "Aucun fournisseur enregistré"));
            return card;
        }

        for (Fournisseur f : repo.fournisseurs) {
            LinearLayout row = createRowContainer();
            LinearLayout info = createInfoColumn();
            addTitleText(info, f.nom);
            addSubText(info, "📍 " + (f.adresse.isEmpty() ? "—" : f.adresse));
            addMutedText(info, "📞 " + (f.telephone.isEmpty() ? "—" : f.telephone) + " | ✉️ " + (f.email.isEmpty() ? "—" : f.email));
            row.addView(info, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            if (canWrite) {
                row.addView(createEditDeleteButtons(
                        v -> ModalsHelper.showFournisseurModal(this, repo, f, createModalCallback()),
                        v -> confirmDelete("Supprimer le fournisseur \"" + f.nom + "\" ?", () -> {
                            repo.deleteFournisseur(f.id);
                            showToast("Fournisseur supprimé");
                            render();
                        })
                ));
            }
            card.addView(row);
            card.addView(UiHelper.createDivider(this));
        }
        return card;
    }

    // ── RÉCEPTIONS ───────────────────────────────────────────────
    private View buildReceptionsView() {
        boolean canWrite = PermissionHelper.canWrite(repo.currentUser, "receptions");
        LinearLayout card = UiHelper.createSectionCard(
                this,
                "Réceptions du lait cru",
                canWrite ? "+ Nouvelle" : null,
                v -> ModalsHelper.showReceptionModal(this, repo, null, createModalCallback())
        );

        if (repo.receptions.isEmpty()) {
            card.addView(UiHelper.createEmptyState(this, "🥛", "Aucune réception enregistrée"));
            return card;
        }

        for (Reception r : repo.receptions) {
            Fournisseur f = repo.findFournisseur(r.fournisseurId);
            LinearLayout row = createRowContainer();
            LinearLayout info = createInfoColumn();
            addTitleWithBadge(info, f != null ? f.nom : "Fournisseur", r.resultatControle);
            addSubText(info, r.dateReception + " à " + r.heureReception + " • " + numberFormat.format(r.quantiteLitres) + " L");
            addMutedText(info, "Temp: " + (r.temperatureC != null ? r.temperatureC + " °C" : "—")
                    + " | pH: " + (r.ph != null ? r.ph : "—")
                    + " | MG: " + (r.tauxMatieresGrasses != null ? r.tauxMatieresGrasses + " %" : "—"));
            row.addView(info, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            if (canWrite) {
                row.addView(createEditDeleteButtons(
                        v -> ModalsHelper.showReceptionModal(this, repo, r, createModalCallback()),
                        v -> confirmDelete("Supprimer cette réception ?", () -> {
                            repo.deleteReception(r.id);
                            showToast("Réception supprimée");
                            render();
                        })
                ));
            }
            card.addView(row);
            card.addView(UiHelper.createDivider(this));
        }
        return card;
    }

    // ── LOTS DE PRODUCTION ───────────────────────────────────────
    private View buildLotsView() {
        boolean canWrite = PermissionHelper.canWrite(repo.currentUser, "lots");
        LinearLayout card = UiHelper.createSectionCard(
                this,
                "Lots de production",
                canWrite ? "+ Nouveau lot" : null,
                v -> ModalsHelper.showLotModal(this, repo, null, createModalCallback())
        );

        if (repo.lots.isEmpty()) {
            card.addView(UiHelper.createEmptyState(this, "🧀", "Aucun lot de production"));
            return card;
        }

        String today = repo.daysOffset(0);
        for (LotProduction l : repo.lots) {
            Produit p = repo.findProduit(l.produitId);
            Reception r = repo.findReception(l.receptionId);
            Fournisseur f = r != null ? repo.findFournisseur(r.fournisseurId) : null;
            boolean expired = l.dateExpiration.compareTo(today) < 0;

            LinearLayout row = createRowContainer();
            LinearLayout info = createInfoColumn();
            addTitleWithBadge(info, l.numeroLot, l.statut);
            addSubText(info, (p != null ? p.nom + " (" + p.typeProduit + ")" : "Produit") + " • " + numberFormat.format(l.quantiteKg) + " kg");
            addMutedText(info, "Fournisseur : " + (f != null ? f.nom : "—") + " | Prod : " + l.dateProduction
                    + " | Exp : " + l.dateExpiration + (expired ? " ⚠️" : ""));
            row.addView(info, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            LinearLayout actions = new LinearLayout(this);
            actions.setOrientation(LinearLayout.HORIZONTAL);
            actions.setGravity(Gravity.CENTER_VERTICAL);

            Button traceBtn = UiHelper.createOutlineButton(this, "🔍", v -> {
                repo.selectedTraceLotId = l.id;
                repo.traceSearchQuery = l.numeroLot;
                repo.currentView = AppView.TRACABILITE;
                render();
            });
            actions.addView(traceBtn);

            if (canWrite) {
                LinearLayout ed = createEditDeleteButtons(
                        v -> ModalsHelper.showLotModal(this, repo, l, createModalCallback()),
                        v -> confirmDelete("Supprimer ce lot ?", () -> {
                            repo.deleteLot(l.id);
                            showToast("Lot supprimé");
                            render();
                        })
                );
                LinearLayout.LayoutParams edLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                edLp.leftMargin = UiHelper.dp(this, 6);
                actions.addView(ed, edLp);
            }
            row.addView(actions);
            card.addView(row);
            card.addView(UiHelper.createDivider(this));
        }
        return card;
    }

    // ── DISTRIBUTIONS ────────────────────────────────────────────
    private View buildDistributionsView() {
        boolean canWrite = PermissionHelper.canWrite(repo.currentUser, "distributions");
        LinearLayout card = UiHelper.createSectionCard(
                this,
                "Distributions",
                canWrite ? "+ Nouvelle" : null,
                v -> ModalsHelper.showDistributionModal(this, repo, null, createModalCallback())
        );

        if (repo.distributions.isEmpty()) {
            card.addView(UiHelper.createEmptyState(this, "🚚", "Aucune distribution enregistrée"));
            return card;
        }

        for (Distribution d : repo.distributions) {
            LotProduction l = repo.findLot(d.lotId);
            Produit p = l != null ? repo.findProduit(l.produitId) : null;
            Client c = repo.findClient(d.clientId);

            LinearLayout row = createRowContainer();
            LinearLayout info = createInfoColumn();
            addTitleWithBadge(info, d.bonLivraison.isEmpty() ? "BL—" : d.bonLivraison, d.statut);
            addSubText(info, "Lot : " + (l != null ? l.numeroLot : "—") + (p != null ? " (" + p.nom + ")" : ""));
            addMutedText(info, "Client : " + (c != null ? c.nom : "—") + " • " + d.dateLivraison + " • " + numberFormat.format(d.quantiteKg) + " kg");
            row.addView(info, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            if (canWrite) {
                row.addView(createEditDeleteButtons(
                        v -> ModalsHelper.showDistributionModal(this, repo, d, createModalCallback()),
                        v -> confirmDelete("Supprimer cette distribution ?", () -> {
                            repo.deleteDistribution(d.id);
                            showToast("Distribution supprimée");
                            render();
                        })
                ));
            }
            card.addView(row);
            card.addView(UiHelper.createDivider(this));
        }
        return card;
    }

    // ── CLIENTS ──────────────────────────────────────────────────
    private View buildClientsView() {
        boolean canWrite = PermissionHelper.canWrite(repo.currentUser, "clients");
        LinearLayout card = UiHelper.createSectionCard(
                this,
                "Clients / Distributeurs",
                canWrite ? "+ Nouveau" : null,
                v -> ModalsHelper.showClientModal(this, repo, null, createModalCallback())
        );

        if (repo.clients.isEmpty()) {
            card.addView(UiHelper.createEmptyState(this, "🏪", "Aucun client enregistré"));
            return card;
        }

        for (Client c : repo.clients) {
            LinearLayout row = createRowContainer();
            LinearLayout info = createInfoColumn();
            addTitleWithBadge(info, c.nom, c.typeClient);
            addSubText(info, "📍 " + (c.adresse.isEmpty() ? "—" : c.adresse));
            addMutedText(info, "📞 " + (c.telephone.isEmpty() ? "—" : c.telephone) + " | ✉️ " + (c.email.isEmpty() ? "—" : c.email));
            row.addView(info, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            if (canWrite) {
                row.addView(createEditDeleteButtons(
                        v -> ModalsHelper.showClientModal(this, repo, c, createModalCallback()),
                        v -> confirmDelete("Supprimer \"" + c.nom + "\" ?", () -> {
                            repo.deleteClient(c.id);
                            showToast("Client supprimé");
                            render();
                        })
                ));
            }
            card.addView(row);
            card.addView(UiHelper.createDivider(this));
        }
        return card;
    }

    // ── PRODUITS ─────────────────────────────────────────────────
    private View buildProduitsView() {
        boolean canWrite = PermissionHelper.canWrite(repo.currentUser, "produits");
        LinearLayout card = UiHelper.createSectionCard(
                this,
                "Produits laitiers",
                canWrite ? "+ Nouveau produit" : null,
                v -> ModalsHelper.showProduitModal(this, repo, null, createModalCallback())
        );

        if (repo.produits.isEmpty()) {
            card.addView(UiHelper.createEmptyState(this, "🥛", "Aucun produit enregistré"));
            return card;
        }

        for (Produit p : repo.produits) {
            LinearLayout row = createRowContainer();
            LinearLayout info = createInfoColumn();
            addTitleWithBadge(info, p.nom, p.typeProduit);
            addSubText(info, p.description.isEmpty() ? "—" : p.description);
            row.addView(info, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            if (canWrite) {
                row.addView(createEditDeleteButtons(
                        v -> ModalsHelper.showProduitModal(this, repo, p, createModalCallback()),
                        v -> confirmDelete("Supprimer \"" + p.nom + "\" ?", () -> {
                            repo.deleteProduit(p.id);
                            showToast("Produit supprimé");
                            render();
                        })
                ));
            }
            card.addView(row);
            card.addView(UiHelper.createDivider(this));
        }
        return card;
    }

    // ── STOCKAGES ────────────────────────────────────────────────
    private View buildStockagesView() {
        boolean canWrite = PermissionHelper.canWrite(repo.currentUser, "stockages");
        LinearLayout card = UiHelper.createSectionCard(
                this,
                "Stockages",
                canWrite ? "+ Nouveau stockage" : null,
                v -> ModalsHelper.showStockageModal(this, repo, null, createModalCallback())
        );

        if (repo.stockages.isEmpty()) {
            card.addView(UiHelper.createEmptyState(this, "📦", "Aucun stockage enregistré"));
            return card;
        }

        for (Stockage s : repo.stockages) {
            LotProduction l = repo.findLot(s.lotId);
            Produit p = l != null ? repo.findProduit(l.produitId) : null;

            LinearLayout row = createRowContainer();
            LinearLayout info = createInfoColumn();
            addTitleWithBadge(info, l != null ? l.numeroLot : "Lot", s.dateSortie == null ? "En stock" : "Sorti");
            addSubText(info, (p != null ? p.nom : "Produit") + " • " + s.zoneStockage + " (" + (s.temperatureC != null ? s.temperatureC + " °C" : "—") + ")");
            addMutedText(info, "Entrée : " + s.dateEntree + " → Sortie : " + (s.dateSortie != null ? s.dateSortie : "En stock"));
            row.addView(info, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            if (canWrite) {
                row.addView(createEditDeleteButtons(
                        v -> ModalsHelper.showStockageModal(this, repo, s, createModalCallback()),
                        v -> confirmDelete("Supprimer ce stockage ?", () -> {
                            repo.deleteStockage(s.id);
                            showToast("Stockage supprimé");
                            render();
                        })
                ));
            }
            card.addView(row);
            card.addView(UiHelper.createDivider(this));
        }
        return card;
    }

    // ── UTILISATEURS ─────────────────────────────────────────────
    private View buildUtilisateursView() {
        LinearLayout card = UiHelper.createSectionCard(
                this,
                "Gestion des utilisateurs",
                "+ Nouvel utilisateur",
                v -> ModalsHelper.showUtilisateurModal(this, repo, null, createModalCallback())
        );

        for (Utilisateur u : repo.utilisateurs) {
            LinearLayout row = createRowContainer();
            LinearLayout info = createInfoColumn();
            addTitleWithBadge(info, u.nom, u.role);
            addSubText(info, u.email + " • " + (u.actif == 1 ? "Actif" : "Inactif"));
            row.addView(info, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            row.addView(createEditDeleteButtons(
                    v -> ModalsHelper.showUtilisateurModal(this, repo, u, createModalCallback()),
                    v -> confirmDelete("Supprimer l'utilisateur \"" + u.nom + "\" ?", () -> {
                        if (!repo.deleteUtilisateur(u.id)) {
                            showToast("Vous ne pouvez pas supprimer votre propre compte");
                        } else {
                            showToast("Utilisateur supprimé");
                            render();
                        }
                    })
            ));
            card.addView(row);
            card.addView(UiHelper.createDivider(this));
        }
        return card;
    }

    // ── JOURNAL CONNEXIONS ───────────────────────────────────────
    private View buildJournalConnexionsView() {
        LinearLayout card = UiHelper.createSectionCard(
                this,
                "Journal des connexions",
                "Actualiser",
                v -> render()
        );

        if (repo.journalConnexions.isEmpty()) {
            card.addView(UiHelper.createEmptyState(this, "🕒", "Aucune connexion enregistrée"));
            return card;
        }

        for (JournalConnexion log : repo.journalConnexions) {
            LinearLayout row = createRowContainer();
            LinearLayout info = createInfoColumn();
            addTitleWithBadge(info, log.nom, log.action);
            addSubText(info, log.email + " • " + log.role);
            addMutedText(info, log.createdAt + " • IP : " + log.adresseIp);
            row.addView(info, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            card.addView(row);
            card.addView(UiHelper.createDivider(this));
        }
        return card;
    }

    // ── TRAÇABILITÉ COMPLÈTE ─────────────────────────────────────
    private View buildTracabiliteView() {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);

        LinearLayout searchCard = UiHelper.createSectionCard(this, "🔍 Tracer un lot", null, null);
        LinearLayout searchBody = new LinearLayout(this);
        searchBody.setOrientation(LinearLayout.VERTICAL);
        int pad = UiHelper.dp(this, 18);
        searchBody.setPadding(pad, pad, pad, pad);

        LinearLayout searchRow = new LinearLayout(this);
        searchRow.setOrientation(LinearLayout.HORIZONTAL);
        searchRow.setGravity(Gravity.CENTER_VERTICAL);

        EditText searchIn = new EditText(this);
        searchIn.setText(repo.traceSearchQuery);
        searchIn.setHint("Entrez un numéro de lot…");
        searchIn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        searchIn.setBackground(UiHelper.roundedBg(this, UiHelper.COLOR_WHITE, 7f, 1, UiHelper.COLOR_GRAY_200));
        searchIn.setPadding(UiHelper.dp(this, 12), UiHelper.dp(this, 10), UiHelper.dp(this, 12), UiHelper.dp(this, 10));
        LinearLayout.LayoutParams sInLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        sInLp.rightMargin = UiHelper.dp(this, 8);
        searchRow.addView(searchIn, sInLp);

        Button searchBtn = UiHelper.createPrimaryButton(this, "Rechercher", v -> {
            String q = searchIn.getText().toString().trim();
            repo.traceSearchQuery = q;
            if (q.isEmpty()) {
                showToast("Entrez un numéro de lot");
                return;
            }
            LotProduction found = null;
            for (LotProduction l : repo.lots) {
                if (l.numeroLot.toLowerCase(Locale.ROOT).contains(q.toLowerCase(Locale.ROOT))) {
                    found = l;
                    break;
                }
            }
            if (found == null) {
                showToast("Lot introuvable");
            } else {
                repo.selectedTraceLotId = found.id;
                render();
            }
        });
        searchRow.addView(searchBtn);
        searchBody.addView(searchRow);

        // Quick selection chips for lots
        HorizontalScrollView chipScroll = new HorizontalScrollView(this);
        LinearLayout chipRow = new LinearLayout(this);
        chipRow.setOrientation(LinearLayout.HORIZONTAL);
        chipRow.setPadding(0, UiHelper.dp(this, 10), 0, 0);
        for (LotProduction l : repo.lots) {
            Button chip = UiHelper.createOutlineButton(this, l.numeroLot, v -> {
                repo.selectedTraceLotId = l.id;
                repo.traceSearchQuery = l.numeroLot;
                render();
            });
            LinearLayout.LayoutParams cLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cLp.rightMargin = UiHelper.dp(this, 6);
            chipRow.addView(chip, cLp);
        }
        chipScroll.addView(chipRow);
        searchBody.addView(chipScroll);
        searchCard.addView(searchBody);
        col.addView(searchCard);

        // Result card
        LinearLayout resCard = UiHelper.createSectionCard(this, "Historique de traçabilité", null, null);
        LinearLayout.LayoutParams rcLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rcLp.topMargin = UiHelper.dp(this, 16);

        LotProduction selectedLot = repo.selectedTraceLotId != null ? repo.findLot(repo.selectedTraceLotId) : null;
        if (selectedLot == null) {
            resCard.addView(UiHelper.createEmptyState(this, "🔍", "Recherchez ou sélectionnez un lot pour afficher son historique complet"));
        } else {
            LinearLayout timeline = new LinearLayout(this);
            timeline.setOrientation(LinearLayout.VERTICAL);
            timeline.setPadding(pad, pad, pad, pad);

            Reception r = repo.findReception(selectedLot.receptionId);
            Fournisseur f = r != null ? repo.findFournisseur(r.fournisseurId) : null;
            Produit p = repo.findProduit(selectedLot.produitId);

            if (r != null) {
                addTimelineStep(timeline, "🐄 Réception du lait – " + r.dateReception,
                        "Fournisseur : " + (f != null ? f.nom : "Inconnu")
                                + "\nQuantité : " + numberFormat.format(r.quantiteLitres) + " L à " + r.heureReception
                                + "\nTempérature : " + (r.temperatureC != null ? r.temperatureC + " °C" : "—") + " | pH : " + (r.ph != null ? r.ph : "—"),
                        r.resultatControle);
            }

            addTimelineStep(timeline, "🏭 Production – Lot " + selectedLot.numeroLot,
                    "Produit : " + (p != null ? p.nom + " (" + p.typeProduit + ")" : "Inconnu")
                            + "\nDate de production : " + selectedLot.dateProduction
                            + "\nQuantité : " + numberFormat.format(selectedLot.quantiteKg) + " kg"
                            + "\nDate d'expiration : " + selectedLot.dateExpiration,
                    selectedLot.statut);

            StringBuilder stSb = new StringBuilder();
            int stCount = 0;
            for (Stockage s : repo.stockages) {
                if (s.lotId == selectedLot.id) {
                    stCount++;
                    if (stSb.length() > 0) stSb.append("\n\n");
                    stSb.append("Zone : ").append(s.zoneStockage)
                            .append(" | Temp. : ").append(s.temperatureC != null ? s.temperatureC + " °C" : "—")
                            .append("\nEntrée : ").append(s.dateEntree)
                            .append(" → Sortie : ").append(s.dateSortie != null ? s.dateSortie : "En stock");
                }
            }
            if (stCount > 0) {
                addTimelineStep(timeline, "🏬 Stockage (" + stCount + " enregistrement(s))", stSb.toString(), null);
            }

            StringBuilder distSb = new StringBuilder();
            int distCount = 0;
            for (Distribution d : repo.distributions) {
                if (d.lotId == selectedLot.id) {
                    distCount++;
                    Client c = repo.findClient(d.clientId);
                    if (distSb.length() > 0) distSb.append("\n\n");
                    distSb.append("Client : ").append(c != null ? c.nom + " (" + c.typeClient + ")" : "Inconnu")
                            .append("\nDate : ").append(d.dateLivraison)
                            .append(" | Quantité : ").append(numberFormat.format(d.quantiteKg)).append(" kg")
                            .append(" | BL : ").append(d.bonLivraison.isEmpty() ? "—" : d.bonLivraison)
                            .append(" [").append(d.statut).append("]");
                }
            }
            if (distCount > 0) {
                addTimelineStep(timeline, "🚚 Distribution (" + distCount + " livraison(s))", distSb.toString(), null);
            }

            resCard.addView(timeline);
        }

        col.addView(resCard, rcLp);
        return col;
    }

    private void addTimelineStep(LinearLayout parent, String title, String detailText, String badgeText) {
        LinearLayout stepBox = new LinearLayout(this);
        stepBox.setOrientation(LinearLayout.VERTICAL);
        stepBox.setBackground(UiHelper.roundedBg(this, UiHelper.COLOR_GRAY_50, 8f, 1, UiHelper.COLOR_GRAY_200));
        int pad = UiHelper.dp(this, 14);
        stepBox.setPadding(pad, pad, pad, pad);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = UiHelper.dp(this, 12);

        addTitleWithBadge(stepBox, title, badgeText);

        TextView detailTv = new TextView(this);
        detailTv.setText(detailText);
        detailTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        detailTv.setTextColor(UiHelper.COLOR_GRAY_600);
        LinearLayout.LayoutParams dLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dLp.topMargin = UiHelper.dp(this, 6);
        stepBox.addView(detailTv, dLp);

        parent.addView(stepBox, lp);
    }

    // ── ROW BUILDER HELPERS ──────────────────────────────────────
    private LinearLayout createRowContainer() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(UiHelper.dp(this, 16), UiHelper.dp(this, 12), UiHelper.dp(this, 16), UiHelper.dp(this, 12));
        return row;
    }

    private LinearLayout createInfoColumn() {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        return col;
    }

    private void addTitleText(LinearLayout parent, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5f);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextColor(UiHelper.COLOR_NAVY);
        parent.addView(tv);
    }

    private void addTitleWithBadge(LinearLayout parent, String title, String badge) {
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.CENTER_VERTICAL);

        TextView tv = new TextView(this);
        tv.setText(title);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5f);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextColor(UiHelper.COLOR_NAVY);
        r.addView(tv);

        if (badge != null && !badge.isEmpty()) {
            TextView b = UiHelper.createBadge(this, badge);
            LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            bLp.leftMargin = UiHelper.dp(this, 8);
            r.addView(b, bLp);
        }
        parent.addView(r);
    }

    private void addSubText(LinearLayout parent, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        tv.setTextColor(UiHelper.COLOR_GRAY_800);
        parent.addView(tv);
    }

    private void addMutedText(LinearLayout parent, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        tv.setTextColor(UiHelper.COLOR_GRAY_600);
        parent.addView(tv);
    }

    private LinearLayout createEditDeleteButtons(View.OnClickListener onEdit, View.OnClickListener onDelete) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.HORIZONTAL);
        Button editBtn = UiHelper.createOutlineButton(this, "✏️", onEdit);
        Button delBtn = UiHelper.createDangerButton(this, "🗑", onDelete);
        LinearLayout.LayoutParams delLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        delLp.leftMargin = UiHelper.dp(this, 6);
        box.addView(editBtn);
        box.addView(delBtn, delLp);
        return box;
    }

    private void confirmDelete(String message, Runnable onConfirm) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage(message)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Supprimer", (d, w) -> onConfirm.run())
                .show();
    }
}
