# Idée de vidéo de démonstration du projet CPU Java

## Objectif de la vidéo

Faire une vidéo claire, courte et professionnelle qui montre que le projet respecte le sujet :

- simulation d'un CPU ;
- mémoire de 64 Ko ;
- 16 registres de 8 bits ;
- assembleur ;
- instructions de base ;
- ALU ;
- sauts et conditions ;
- tableaux avec adressage indexé ;
- interface graphique.

Durée maximale conseillée : **8 à 10 minutes**.

Format conseillé : slides Canva + captures d'écran de l'application + voix enregistrée.

## Structure globale

| Partie | Durée estimée |
|---|---:|
| Introduction | 45 secondes |
| Architecture du projet | 1 minute |
| Fonctionnement du CPU | 1 minute 30 |
| Fonctionnement de l'assembleur | 1 minute 30 |
| Démonstration dans l'interface | 3 minutes |
| Gestion des erreurs | 1 minute |
| Conclusion | 30 secondes |

Durée totale : environ **9 minutes**.

## Slide 1 - Titre du projet

### Contenu de la slide

Titre :

```text
Simulateur de CPU avec assembleur en Java
```

Sous-titre :

```text
Projet Carré Petit Utile
```

Ajouter :

- vos noms ;
- une capture globale de l'interface ;
- une petite mention : `Java / Swing / Assembleur / CPU`.


## Slide 2 - But du projet

### Contenu de la slide

Afficher une liste simple :

```text
Objectifs principaux :
- Simuler un CPU
- Gérer 16 registres de 8 bits
- Gérer une mémoire de 64 Ko
- Traduire du code assembleur
- Exécuter les instructions pas à pas ou entièrement
- Visualiser l'état du CPU dans une interface graphique
```

### Capture recommandée

Pas forcément de capture ici. Tu peux mettre une icône de CPU ou une capture floutée de l'interface en arrière-plan.


## Slide 3 - Architecture du projet

### Contenu de la slide

Afficher trois blocs :

```text
noyau
CPU, mémoire, registres, ALU

assembleur
Traduction du code source en instructions

interfaceutilisateur
Interface graphique Swing
```

### Capture recommandée

Capture de l'arborescence du projet dans l'IDE, avec les dossiers :

- `src/noyau`
- `src/assembleur`
- `src/interfaceutilisateur`
- `src/test`


## Slide 4 - Le coeur du CPU

### Contenu de la slide

Mettre un schéma simple :

```text
CPU
├── Mémoire : 65536 octets
├── Registres : R0 à R15
├── ALU : ADD, SUB, MUL, DIV, AND, OR, XOR
└── PC : compteur de programme
```

### Capture recommandée

Capture de la partie droite de l'interface :

- PC ;
- table des registres ;
- table mémoire.


## Slide 5 - Cycle d'exécution

### Contenu de la slide

Afficher ce cycle :

```text
1. Lire l'opcode en mémoire
2. Lire les opérandes
3. Décoder l'instruction
4. Exécuter l'opération
5. Modifier les registres, la mémoire ou le PC
6. Passer à l'instruction suivante
```


## Slide 6 - L'assembleur

### Contenu de la slide

Mettre un exemple très simple :

```asm
LOAD R0, 5
LOAD R1, 10
ADD R2, R0, R1
STORE R2, @200
BREAK
```

Puis montrer l'idée :

```text
Code assembleur → Instructions Java → Code machine en mémoire
```

### Capture recommandée

Capture du code dans l'éditeur de gauche.


## Slide 7 - Jeu d'instructions supporté

### Contenu de la slide

Faire un tableau court :

| Catégorie | Instructions |
|---|---|
| Base | `LOAD`, `STORE`, `BREAK` |
| ALU | `ADD`, `SUB`, `MUL`, `DIV`, `AND`, `OR`, `XOR` |
| Sauts | `JUMP`, `BEQ`, `BNE` |
| Tableaux | `LOAD indexé`, `STORE indexé` |
| Données | `DATA`, `STRING` |


## Slide 8 - Démonstration 1 : programme simple

### Contenu de la slide

Titre :

```text
Démonstration : addition et stockage en mémoire
```

Code à montrer :

```asm
LOAD R0, 5
LOAD R1, 10
ADD R2, R0, R1
STORE R2, @200
BREAK
```

### Capture ou vidéo recommandée

Faire une capture de l'interface après exécution :

- `R0 = 5`
- `R1 = 10`
- `R2 = 15`
- mémoire à l'adresse `200` contenant `15`


## Slide 9 - Démonstration 2 : boucle avec BNE

### Contenu de la slide

Titre :

```text
Démonstration : boucle avec BNE
```

Code conseillé :

```asm
LOAD R0, 0
LOAD R1, 1
LOAD R2, 6
LOAD R3, 1
LOAD R4, 0

BEQ R1, R2, @33
ADD R0, R0, R1
ADD R1, R1, R3
BNE R1, R4, @15

STORE R0, @200
BREAK
```

### Ce que fait le programme

Il calcule :

```text
1 + 2 + 3 + 4 + 5 = 15
```

Puis il stocke le résultat à l'adresse mémoire `200`.

### Capture recommandée

Capture après exécution :

- `R0 = 15` ;
- adresse mémoire `0x00C8`, donc `200`, avec la valeur `15` ;
- logs indiquant assemblage et exécution.


## Slide 10 - Démonstration 3 : DATA, STRING et mémoire

### Contenu de la slide

Code :

```asm
LOAD R0, 1
BREAK

DATA 10, 20, 30
STRING "fin"
```

### Capture recommandée

Montrer la table mémoire avec les valeurs de `DATA` et les caractères de `STRING`.

Expliquer que :

- `DATA` écrit directement des valeurs ;
- `STRING` écrit les caractères en UTF-8 ;
- le CPU n'exécute pas ces données comme des instructions.


## Slide 11 - Interface graphique

### Contenu de la slide

Mettre une grande capture annotée de l'interface.

Annoter :

```text
1. Éditeur assembleur avec numéros de lignes
2. Console de logs
3. Boutons d'exécution
4. Registres
5. Mémoire paginée
```


## Slide 12 - Gestion des erreurs

### Contenu de la slide

Montrer un exemple volontairement faux :

```asm
LOAD R0
```

Résultat attendu dans les logs :

```text
Ligne 1 : Syntaxe load invalide
Exemple correct : load r0, 5 ou load r0, @100
```

### Capture recommandée

Capture de la console avec une erreur d'assemblage.


## Slide 13 - Sécurité d'exécution

### Contenu de la slide

Afficher :

```text
Sécurités ajoutées :
- arrêt avec BREAK
- limite de 100000 instructions
- vérification des adresses de saut
- DATA et STRING non exécutés
- erreurs affichées dans la console
```


## Slide 14 - Tests et validation

### Contenu de la slide

Afficher :

```text
Tests réalisés :
- Mémoire
- Registres
- ALU
- Assembleur
- CPU
```

Ajouter :

```text
Compilation Java 17
Tests JUnit
Documentation Javadoc
```

### Capture recommandée

Capture de l'arborescence `src/test` ou d'une compilation réussie.


## Slide 15 - Conclusion

### Contenu de la slide

Afficher :

```text
Conclusion

Le projet permet :
- d'écrire du code assembleur ;
- de le traduire en instructions machine ;
- de l'exécuter dans un CPU simulé ;
- d'observer les registres, la mémoire et le PC ;
- de tester des boucles, conditions et tableaux.
```


## Voix off complète slide par slide


Conseil : parle naturellement, pas trop vite. Si tu vois que tu dépasses, raccourcis surtout les slides 8, 9, 10 et 11.

### Slide 1 - Titre du projet

Durée conseillée : **30 secondes**

> Bonjour, dans cette vidéo, je vais présenter notre projet intitulé Simulateur de CPU avec assembleur en Java. Le but de ce projet est de reproduire le fonctionnement simplifié d'un processeur. L'utilisateur peut écrire un programme en assembleur, l'assembler, le charger en mémoire, puis l'exécuter dans une interface graphique. Pendant l'exécution, on peut observer les registres, la mémoire, le compteur de programme et les logs.

### Slide 2 - But du projet

Durée conseillée : **35 secondes**

> Le projet répond aux différentes étapes du sujet. On devait d'abord gérer une mémoire de 64 Ko et 16 registres de 8 bits. Ensuite, il fallait ajouter des instructions simples comme LOAD, STORE et BREAK. Puis on devait ajouter un assembleur, une unité arithmétique et logique, les sauts avec JUMP, BEQ et BNE, et enfin la gestion des tableaux avec l'adressage indexé, DATA et STRING. Notre application regroupe tous ces éléments dans une interface graphique.

### Slide 3 - Architecture du projet

Durée conseillée : **40 secondes**

> Le projet est organisé en plusieurs paquets pour séparer les responsabilités. Le paquet noyau contient la partie processeur, donc le CPU, la mémoire, les registres et l'ALU. Le paquet assembleur contient toutes les classes qui traduisent le code source assembleur en instructions compréhensibles par le CPU. Enfin, le paquet interfaceutilisateur contient la fenêtre graphique Swing. Cette séparation rend le projet plus clair, parce que l'interface ne fait pas directement les calculs du CPU, elle utilise les classes du noyau.

### Slide 4 - Le coeur du CPU

Durée conseillée : **45 secondes**

> Le coeur du simulateur est la classe CPU. Elle contient une mémoire de 65536 octets, ce qui correspond à 64 Ko. Les adresses vont donc de 0 à 65535, ou en hexadécimal de 0x0000 à 0xFFFF. Le CPU possède aussi une banque de 16 registres, de R0 à R15, chacun sur 8 bits. L'ALU effectue les opérations arithmétiques et logiques. Enfin, le PC, ou compteur de programme, indique l'adresse mémoire de la prochaine instruction à exécuter.

### Slide 5 - Cycle d'exécution

Durée conseillée : **45 secondes**

> Le fonctionnement du CPU suit un cycle simple. D'abord, il lit l'opcode à l'adresse indiquée par le PC. Ensuite, il lit les opérandes nécessaires, par exemple un numéro de registre ou une adresse mémoire. Après cela, il décode l'instruction pour savoir quelle opération effectuer. Puis il applique l'instruction : il peut modifier un registre, écrire dans la mémoire, utiliser l'ALU ou changer le PC pour faire un saut. Ce cycle continue jusqu'à l'instruction BREAK ou jusqu'à une erreur.

### Slide 6 - L'assembleur

Durée conseillée : **45 secondes**

> L'assembleur sert à éviter d'écrire directement les opcodes numériques en mémoire. À la place, l'utilisateur écrit un code plus lisible, par exemple LOAD R0, 5 ou ADD R2, R0, R1. La classe Assembleur lit chaque ligne, reconnaît le mot-clé, vérifie la syntaxe et construit un objet Instruction. Ensuite, le programme peut être chargé en mémoire par le CPU. Cette étape fait le lien entre le code écrit par l'utilisateur et le code machine exécuté par le processeur simulé.

### Slide 7 - Jeu d'instructions supporté

Durée conseillée : **45 secondes**

> Le simulateur prend en charge toutes les grandes familles d'instructions demandées dans le sujet. On retrouve les instructions de base LOAD, STORE et BREAK. L'ALU permet d'utiliser ADD, SUB, MUL, DIV, AND, OR et XOR. Les instructions JUMP, BEQ et BNE permettent de gérer les sauts, les conditions et les boucles. Pour les tableaux, on a aussi LOAD indexé et STORE indexé. Enfin, DATA et STRING permettent d'écrire directement des données dans la mémoire.

### Slide 8 - Démonstration 1 : programme simple

Durée conseillée : **50 secondes**

> Pour la première démonstration, on utilise un programme très simple. On charge la valeur 5 dans le registre R0, puis la valeur 10 dans le registre R1. Ensuite, l'instruction ADD R2, R0, R1 additionne R0 et R1, puis place le résultat dans R2. Après cela, STORE R2, @200 écrit la valeur de R2 dans la mémoire à l'adresse 200. Quand on exécute le programme, on voit que R2 contient bien 15, et que la mémoire à l'adresse 200 contient aussi 15. Cela montre le fonctionnement de LOAD, ADD, STORE et BREAK.

### Slide 9 - Démonstration 2 : boucle avec BNE

Durée conseillée : **1 minute 05**

> Dans cette deuxième démonstration, on utilise une boucle. Le programme calcule la somme des nombres de 1 à 5. Le registre R0 sert d'accumulateur pour stocker la somme. Le registre R1 sert de compteur, R2 contient la limite, R3 contient la valeur 1 pour incrémenter le compteur, et R4 contient zéro. À chaque tour de boucle, on ajoute R1 dans R0, puis on augmente R1 de 1. L'instruction BNE permet de revenir au début de la boucle tant que la condition est vraie. Quand la boucle est terminée, le résultat est stocké à l'adresse mémoire 200. On obtient donc 15, ce qui montre que les sauts conditionnels fonctionnent.

### Slide 10 - Démonstration 3 : DATA, STRING et mémoire

Durée conseillée : **45 secondes**

> Cette démonstration montre la gestion des données en mémoire. L'instruction DATA écrit directement une suite de valeurs dans la mémoire, par exemple 10, 20 et 30. L'instruction STRING écrit les caractères d'une chaîne sous forme d'octets UTF-8. Dans notre simulateur, DATA et STRING sont bien chargés en mémoire, mais ne sont pas considérés comme des instructions exécutables. Cela évite que le CPU essaie d'exécuter une donnée comme un opcode, ce qui pouvait provoquer des erreurs comme opcode inconnu.

### Slide 11 - Interface graphique

Durée conseillée : **55 secondes**

> L'interface graphique permet de manipuler le simulateur plus facilement. À gauche, on trouve l'éditeur de code assembleur, avec les numéros de lignes. Au centre, la console affiche les informations et les erreurs. En bas, les boutons permettent d'assembler le programme, de l'exécuter entièrement, de l'exécuter pas à pas ou de réinitialiser le CPU. À droite, on voit l'état du processeur avec le PC, les registres et la mémoire. La mémoire est affichée par pages de 256 adresses, avec les boutons Précédent et Suivant pour parcourir les 64 Ko.

### Slide 12 - Gestion des erreurs

Durée conseillée : **40 secondes**

> Le simulateur donne aussi des messages d'erreur pour aider l'utilisateur. Par exemple, si on écrit LOAD R0 sans deuxième opérande, l'assembleur détecte une erreur de syntaxe. Il indique la ligne concernée, affiche le code qui pose problème, et donne un exemple correct. Cette fonctionnalité est utile parce qu'elle permet de corriger plus rapidement les programmes assembleur, surtout quand il y a plusieurs lignes de code.

### Slide 13 - Sécurité d'exécution

Durée conseillée : **45 secondes**

> Plusieurs sécurités ont été ajoutées pendant l'exécution. D'abord, le programme s'arrête normalement quand il rencontre BREAK. Ensuite, le CPU possède une limite de 100000 instructions pour éviter qu'une boucle infinie bloque l'application. Les adresses de saut sont aussi vérifiées : un JUMP, un BEQ ou un BNE doit pointer exactement vers une vraie instruction exécutable. Il n'est donc pas possible de sauter dans une zone DATA, STRING, ou au milieu d'une instruction.

### Slide 14 - Tests et validation

Durée conseillée : **40 secondes**

> Pour valider le projet, plusieurs tests unitaires ont été écrits. Les tests vérifient la mémoire, les registres, l'ALU, l'assembleur et le CPU. Par exemple, on teste les opérations arithmétiques, le chargement de valeurs, les lectures et écritures mémoire, les sauts, les boucles et les erreurs comme la division par zéro. Le projet contient aussi une documentation Javadoc générée à partir des commentaires du code, ce qui aide à comprendre les classes et leurs méthodes.

### Slide 15 - Conclusion

Durée conseillée : **35 secondes**

> Pour conclure, ce projet permet de montrer le fonctionnement complet d'un petit processeur simulé. On part d'un code assembleur écrit par l'utilisateur, on le transforme en instructions, on le charge en mémoire, puis le CPU le décode et l'exécute. L'interface permet de visualiser les registres, la mémoire, le PC et les logs. Le projet couvre donc les éléments essentiels demandés : mémoire, registres, assembleur, ALU, sauts, boucles, tableaux et interface graphique.

### Durée totale estimée

| Slide | Durée |
|---|---:|
| 1 | 30 s |
| 2 | 35 s |
| 3 | 40 s |
| 4 | 45 s |
| 5 | 45 s |
| 6 | 45 s |
| 7 | 45 s |
| 8 | 50 s |
| 9 | 1 min 05 |
| 10 | 45 s |
| 11 | 55 s |
| 12 | 40 s |
| 13 | 45 s |
| 14 | 40 s |
| 15 | 35 s |

Total estimé : **environ 10 minutes** avec des pauses naturelles, ou **environ 8 minutes 30 à 9 minutes** si tu parles de manière fluide.

## Plan recommandé pour Canva

### Style visuel

Utiliser un style simple et technique :

- fond clair ou gris foncé ;
- police lisible ;
- couleurs principales : bleu, gris, vert ;
- captures d'écran grandes et nettes ;
- peu de texte par slide ;
- une idée principale par slide.

### Types de visuels à mettre

- capture de l'interface complète ;
- zoom sur les registres ;
- zoom sur la mémoire ;
- zoom sur la console ;
- capture du code dans l'éditeur ;
- petit schéma CPU → mémoire → registres ;
- tableau des instructions.

## Ordre final conseillé des slides

1. Titre
2. But du projet
3. Architecture
4. Coeur du CPU
5. Cycle d'exécution
6. Assembleur
7. Jeu d'instructions
8. Démo programme simple
9. Démo boucle avec `BNE`
10. Démo `DATA` et `STRING`
11. Interface graphique
12. Gestion des erreurs
13. Sécurités d'exécution
14. Tests et validation
15. Conclusion

## Conseils pour rester sous 10 minutes

- Faire environ 30 à 45 secondes par slide.
- Ne pas lire tout le texte affiché.
- Montrer seulement 2 ou 3 vraies démonstrations.
- Préparer les captures avant d'enregistrer la voix.
- Parler surtout du fonctionnement, pas de chaque ligne de code Java.
- Pour les programmes assembleur, expliquer l'idée générale plutôt que chaque octet.

## Captures d'écran à préparer

Avant de créer les slides, préparer ces captures :

1. Interface complète vide.
2. Interface avec un programme assembleur simple.
3. Résultat après exécution du programme simple.
4. Résultat après exécution du programme avec boucle.
5. Table mémoire avec une adresse intéressante.
6. Console affichant une erreur de syntaxe.
7. Arborescence du projet dans l'IDE.
8. Dossier `doc/index.html` ou Javadoc ouverte.
9. Dossier `src/test` avec les tests.

## Script court de présentation complète

Tu peux utiliser ce texte comme base pour ta voix off :

> Bonjour, dans cette vidéo je présente notre simulateur de CPU avec assembleur, développé en Java. Le projet permet d'écrire un programme assembleur, de l'assembler, de le charger en mémoire, puis de l'exécuter dans un processeur simulé.
>
> Le projet est organisé en trois parties. Le paquet noyau contient le CPU, la mémoire, les registres et l'ALU. Le paquet assembleur traduit le code écrit par l'utilisateur. Enfin, le paquet interfaceutilisateur contient l'interface graphique en Swing.
>
> Le CPU possède 16 registres de 8 bits et une mémoire de 64 Ko, donc 65536 adresses. Le compteur de programme, appelé PC, indique l'adresse de la prochaine instruction. À chaque étape, le CPU lit un opcode, lit les opérandes, décode l'instruction et l'exécute.
>
> L'assembleur permet d'utiliser une syntaxe plus lisible, comme LOAD, STORE, ADD, JUMP, BEQ ou BNE. Il transforme chaque ligne en instruction interne, puis le CPU charge ces instructions sous forme d'octets dans la mémoire.
>
> Dans l'interface, on peut écrire le code assembleur à gauche, assembler le programme, l'exécuter entièrement ou pas à pas. À droite, on voit le PC, les registres et la mémoire. La mémoire est paginée pour afficher proprement les 65536 adresses.
>
> Voici un premier exemple simple : on charge deux valeurs dans les registres, on les additionne, puis on stocke le résultat en mémoire. Après exécution, on peut vérifier le résultat dans les registres et dans la table mémoire.
>
> Un deuxième exemple montre une boucle avec BNE. Le programme additionne les nombres de 1 à 5, puis stocke le résultat. Cela montre que les sauts conditionnels et le compteur de programme fonctionnent correctement.
>
> Le simulateur gère aussi DATA et STRING, qui permettent d'écrire directement des données en mémoire. Une sécurité empêche le CPU d'exécuter ces données comme des instructions.
>
> Enfin, l'application gère les erreurs. Si une syntaxe est incorrecte, elle indique la ligne concernée et donne un exemple correct. Le CPU vérifie aussi les sauts invalides et protège contre les boucles infinies avec une limite d'instructions.
>
> Pour conclure, ce projet montre le fonctionnement complet d'un petit processeur : écriture assembleur, assemblage, chargement mémoire, décodage, exécution, registres, ALU, mémoire et interface graphique.
