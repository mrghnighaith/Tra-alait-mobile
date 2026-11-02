# TraçaLait – Traçabilité des Produits Laitiers (Android)

Application Android native développée en **Kotlin** et **Jetpack Compose** (Material 3) pour la gestion et la traçabilité de la chaîne de production laitière.

## 📲 Télécharger l'application Android (APK)

- **[⬇ Télécharger TracaLait.apk (Dernière version – GitHub Releases)](../../releases/latest/download/TracaLait.apk)**
- **[📦 Voir toutes les versions (Releases)](../../releases)**
- **[⚙️ Voir les builds GitHub Actions](../../actions/workflows/android-apk.yml)**

> **Note :** À chaque `push` sur la branche principale (`main` / `master`), le workflow GitHub Actions (`.github/workflows/android-apk.yml`) compile automatiquement le projet et publie le fichier **`TracaLait.apk`** dans l'onglet **Releases** ainsi que dans les **Artifacts** de l'action.

---

## Fonctionnalités principales

- **Authentification & Contrôle d'accès par rôle** :
  - `admin@tracalait.local` / `admin123` (Administrateur)
  - `qualite@tracalait.local` / `admin123` (Responsable Qualité)
  - `production@tracalait.local` / `admin123` (Responsable Production)
- **Tableau de bord** : Indicateurs clés (réceptions, litres acceptés, lots créés, lots en stock, livraisons effectuées, lots expirés) et répartition des lots par statut.
- **Chaîne de production** : Gestion complète des fournisseurs, réceptions de lait cru, lots de production et distributions.
- **Traçabilité complète** : Recherche par numéro de lot et historique chronologique complet (Réception → Production → Stockage → Distribution).
- **Référentiels & Produits** : Gestion des clients/distributeurs, catalogue des produits laitiers et suivi des zones de stockage.
- **Administration** : Gestion des comptes utilisateurs et journal des connexions.
