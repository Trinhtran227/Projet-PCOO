# Projet de Moteur de Jeu 2D - LibGDX
Ce projet est un moteur de jeu de tir spatial (Space Shooter) développé dans le cadre du cours **SLUF500 - Programmation et conception orientée objet**. Il démontre l'utilisation de l'architecture MVC, l'intégration de l'éditeur de carte **Tiled** et la gestion optimisée des ressources.
Ce dépôt contient le code source de mon projet réalisé dans le cadre du cours de Programmation Orientée Objet (PCOO). L'objectif est de développer un moteur de jeu 2D extensible en utilisant Java.

## 🛠 Technologies utilisées
* **Langage :** Java
* **Framework :** [LibGDX](https://libgdx.com/) (Gestion du rendu, des entrées et de la boucle de jeu).
* **Level Design :** [Tiled](https://www.mapeditor.org/) (Création et édition des cartes, gestion des calques et des objets).

## Exécution

Un script d'exécution (`gradlew`) est fourni dans ce dossier pour lancer le jeu immédiatement sans installation manuelle de Gradle.

### 1. Ouvrir le terminal
Ouvrez votre terminal (ou invite de commande) directement **dans ce dossier** (là où se trouve ce fichier README et le fichier gradlew).

### 2. Lancer la commande

* **Sur macOS / Linux :**
  ./gradlew lwjgl3:run

* **Sur Windows :**
  ```cmd
  gradlew.bat lwjgl3:run

* **OU Lancement : Ouvrir le projet dans IntelliJ IDEA, laisser Gradle synchroniser les dépendances, puis exécuter la classe Lwjgl3Launcher.**

** Contrôles (Gameplay)
Déplacement & Rotation : Flèches directionnelles (Haut, Bas, Gauche, Droite).

Tir : Barre d'espace (SPACE).
Redémarrer : Touche R (disponible uniquement lors de l'écran Game Over ou Victoire).

Objectif : Détruisez les vagues d'ennemis définies par la carte Tiled (maps/level1.tmx). Une fois tous les ennemis vaincus, un Boss apparaîtra. Battez-le pour gagner !

##  Fonctionnalités principales
* Chargement et rendu des cartes via **Tiled Map loader**.
* Gestion des collisions et des entités.
* Architecture extensible permettant l'ajout de nouveaux contenus sans modification majeure du code source.
---
**Auteur :** Tran Cong Trinh

**Université Côte d'Azur **
