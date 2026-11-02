# TraçaLait – Traçabilité des Produits Laitiers (Android)

Application Android native développée en **Kotlin** et **Jetpack Compose** (Material 3) pour la gestion et la traçabilité de la chaîne de production laitière.

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
