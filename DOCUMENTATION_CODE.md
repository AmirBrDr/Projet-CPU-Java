# Documentation du projet CPU Java

## 1. Présentation générale

Ce projet est un simulateur de processeur écrit en Java. Il permet d'écrire un programme en assembleur simplifié, de l'assembler, de le charger en mémoire, puis de l'exécuter dans un CPU simulé.

L'application contient trois grandes parties :

- `noyau` : le coeur du simulateur, avec le CPU, la mémoire, les registres et l'ALU.
- `assembleur` : le traducteur du code assembleur vers des instructions internes et du code machine.
- `interfaceutilisateur` : l'interface graphique Swing utilisée pour écrire, assembler, exécuter et visualiser l'état du CPU.

Le point d'entrée du programme est la classe `Main`, qui lance la fenêtre graphique `SimulateurGUI`.

## 2. Organisation des fichiers

```text
src/
├── Main.java
├── assembleur/
│   ├── Assembleur.java
│   ├── Instruction.java
│   ├── Programme.java
│   ├── TypeInstruction.java
│   ├── TypeOperande.java
│   └── Operande*.java
├── noyau/
│   ├── CPU.java
│   ├── Memoire.java
│   ├── BanqueRegistres.java
│   ├── Registre.java
│   ├── ALU.java
│   ├── ResultatDiv.java
│   └── ResultatMulti.java
├── interfaceutilisateur/
│   └── SimulateurGUI.java
└── test/
    ├── AluTest.java
    ├── AssembleurTest.java
    ├── BanqueRegistresTest.java
    ├── CpuTest.java
    └── MemoireTest.java
```

## 3. Fonctionnement global

Le fonctionnement complet suit cette chaîne :

1. L'utilisateur écrit du code assembleur dans l'éditeur de gauche.
2. Le bouton `Assembler` appelle `Assembleur.assembler(source)`.
3. L'assembleur transforme chaque ligne en objet `Instruction`.
4. Le programme obtenu est chargé dans le CPU avec `cpu.chargerProgramme(programmeActuel)`.
5. Le CPU écrit les opcodes et les opérandes dans la mémoire.
6. L'utilisateur lance l'exécution avec `Exécuter tout` ou `Pas à pas`.
7. Le CPU lit les octets en mémoire, décode l'instruction courante, puis modifie les registres, la mémoire ou le compteur de programme.
8. L'interface met à jour l'affichage du PC, des registres, de la mémoire et des logs.

## 4. Classe `Main`

La classe `Main` est le point d'entrée de l'application.

Elle utilise :

```java
SwingUtilities.invokeLater(...)
```

Cela permet de créer l'interface graphique dans le thread Swing prévu pour les composants graphiques. La classe crée simplement une instance de `SimulateurGUI` puis l'affiche.

## 5. Paquet `noyau`

Le paquet `noyau` contient la simulation matérielle du processeur.

### 5.1 Classe `Memoire`

La classe `Memoire` représente la mémoire principale du CPU.

Caractéristiques :

- taille : `65536` octets ;
- équivalent à `64 Ko` ;
- adresses disponibles : de `0` à `65535`, soit de `0x0000` à `0xFFFF` ;
- chaque case mémoire contient un `byte`.

Méthodes principales :

- `lireOctet(int address)` : lit un octet à une adresse.
- `ecrireOctet(int address, byte valeur)` : écrit un octet à une adresse.
- `lireBloc(int addressDebut, int taille)` : lit plusieurs octets à la suite.
- `ecrireBloc(int addressDebut, byte[] donnees)` : écrit plusieurs octets.
- `vider()` : remet toute la mémoire à zéro.
- `estAddressValide(int address)` : vérifie qu'une adresse est comprise entre `0` et `65535`.

### 5.2 Classe `Registre`

La classe `Registre` représente un registre individuel.

Un registre contient :

- un numéro ;
- une valeur sur 8 bits, stockée dans un `byte`.

Méthodes principales :

- `lireValeur()` : retourne la valeur du registre.
- `ecrireValeur(byte valeur)` : remplace la valeur du registre.
- `reinitialiser()` : remet le registre à `0`.

### 5.3 Classe `BanqueRegistres`

La classe `BanqueRegistres` contient les 16 registres du CPU.

Les registres disponibles sont :

```text
R0, R1, R2, ..., R15
```

Méthodes principales :

- `lireRegistre(int numero)` : lit la valeur d'un registre.
- `ecrireRegistre(int numero, byte valeur)` : écrit une valeur dans un registre.
- `getRegistre(int numero)` : retourne l'objet `Registre`.
- `reinitialiser()` : recrée les 16 registres à zéro.
- `estNumeroValide(int numero)` : vérifie que le numéro est entre `0` et `15`.

### 5.4 Classe `ALU`

La classe `ALU` représente l'unité arithmétique et logique.

Elle effectue les opérations suivantes :

| Méthode | Rôle |
|---|---|
| `additionner(a, b)` | addition sur 8 bits |
| `soustraire(a, b)` | soustraction sur 8 bits |
| `multiplier(a, b)` | multiplication avec résultat sur deux octets |
| `diviser(a, b)` | division avec quotient et reste |
| `estBinaire(a, b)` | opération logique AND |
| `ouBinaire(a, b)` | opération logique OR |
| `xorBinaire(a, b)` | opération logique XOR |

La multiplication retourne un objet `ResultatMulti`, contenant :

- `poidsFaible` ;
- `poidsFort`.

La division retourne un objet `ResultatDiv`, contenant :

- `quotient` ;
- `reste`.

Si le diviseur vaut `0`, `diviser` lance une exception `ArithmeticException` avec le message `Division par zéro`.

### 5.5 Classe `CPU`

La classe `CPU` est la classe centrale du simulateur.

Elle contient :

- une `Memoire` de 64 Ko ;
- une `BanqueRegistres` de 16 registres ;
- une `ALU` ;
- un compteur de programme `pc` ;
- un booléen `enExecution` ;
- un tableau `adressesExecutables` indiquant quelles adresses correspondent réellement au début d'une instruction exécutable.

Le `pc` est le compteur de programme. Il contient l'adresse mémoire de la prochaine instruction à exécuter.

#### Chargement d'un programme

La méthode `chargerProgramme(Programme programme)` :

1. vide la mémoire ;
2. remet à zéro les adresses exécutables ;
3. parcourt les instructions du programme ;
4. écrit en mémoire l'opcode et les opérandes de chaque instruction ;
5. marque comme exécutables uniquement les vraies instructions CPU ;
6. ne marque pas `DATA` et `STRING` comme exécutables ;
7. place le `pc` sur la première instruction exécutable.

Cette logique évite que le CPU essaie d'exécuter des données comme si c'étaient des instructions.

Exemple :

```asm
BREAK
DATA 10, 20, 30
STRING "fin"
```

Ici, `DATA` et `STRING` sont bien écrits en mémoire, mais ne sont pas considérés comme des instructions à décoder.

#### Exécution complète

La méthode `executerProgramme()` lance une boucle :

```java
while (enExecution) {
    executerInstruction();
}
```

Elle s'arrête quand :

- une instruction `BREAK` est exécutée ;
- une erreur survient ;
- la limite de sécurité de `100000` instructions est atteinte.

La limite évite qu'un programme en boucle infinie bloque l'interface.

#### Exécution pas à pas

La méthode `executerInstruction()` :

1. appelle `decoderInstruction()` ;
2. reçoit une instruction abstraite ;
3. appelle `appliquerInstruction(instruction)`.

Cela permet d'exécuter une seule instruction à la fois.

#### Décodage d'une instruction

La méthode `decoderInstruction()` :

1. avance le `pc` vers la prochaine adresse exécutable ;
2. lit l'opcode en mémoire ;
3. lit les opérandes nécessaires ;
4. construit un objet `Instruction`.

Les méthodes utilisées pour lire la mémoire sont :

- `lireOpocode()` : lit l'opcode courant et incrémente le `pc`.
- `lireOctetSuivant()` : lit un octet d'opérande et incrémente le `pc`.
- `lireAdresseSuivante()` : lit deux octets et reconstruit une adresse 16 bits.

#### Application d'une instruction

La méthode `appliquerInstruction(Instruction instruction)` modifie l'état du CPU selon le type d'instruction.

Exemples :

- `LOAD_CONSTANTE` écrit une valeur immédiate dans un registre.
- `LOAD_MEMOIRE` lit une adresse mémoire et place la valeur dans un registre.
- `STORE_MEMOIRE` écrit la valeur d'un registre dans la mémoire.
- `ADD`, `SUB`, `MUL`, `DIV`, `AND`, `OR`, `XOR` utilisent l'ALU.
- `JUMP` modifie directement le `pc`.
- `BEQ` saute si deux registres sont égaux.
- `BNE` saute si deux registres sont différents.
- `BREAK` arrête l'exécution.

#### Validation des sauts

Les instructions `JUMP`, `BEQ` et `BNE` utilisent `validerAdresseSaut(int adresse)`.

Cette méthode vérifie que l'adresse cible :

- est dans la mémoire ;
- correspond exactement au début d'une instruction exécutable ;
- ne pointe pas dans une zone `DATA` ;
- ne pointe pas dans une zone `STRING` ;
- ne pointe pas au milieu d'une instruction.

Si l'adresse n'est pas correcte, une erreur est générée :

```text
Adresse de saut non exécutable
```

## 6. Paquet `assembleur`

Le paquet `assembleur` transforme le code utilisateur en objets Java compréhensibles par le CPU.

### 6.1 Classe `Programme`

La classe `Programme` contient une liste d'objets `Instruction`.

Elle sert de représentation intermédiaire entre le code source assembleur et le CPU.

Méthodes :

- `ajouterInstruction(Instruction instruction)` ;
- `getInstructions()`.

La liste retournée par `getInstructions()` est non modifiable.

### 6.2 Classe `Instruction`

Une `Instruction` contient :

- un `TypeInstruction` ;
- une liste d'opérandes ;
- la ligne source originale.

La ligne source est conservée pour faciliter le débogage et les messages d'erreur.

### 6.3 Enum `TypeInstruction`

`TypeInstruction` liste toutes les instructions supportées :

```text
LOAD_CONSTANTE
LOAD_MEMOIRE
LOAD_INDEXE
STORE_MEMOIRE
STORE_INDEXE
BREAK
ADD
SUB
MUL
DIV
AND
OR
XOR
JUMP
BEQ
BNE
DATA
STRING
```

### 6.4 Classes `Operande`

Les opérandes représentent les paramètres des instructions.

| Classe | Rôle |
|---|---|
| `OperandeRegistre` | registre `R0` à `R15` |
| `OperandeConstante` | valeur immédiate |
| `OperandeAdresse` | adresse mémoire absolue |
| `OperandeAdresseIndexee` | adresse de base + registre d'index |
| `OperandeDonnees` | liste de valeurs pour `DATA` |
| `OperandeChaine` | chaîne pour `STRING` |

### 6.5 Classe `Assembleur`

La classe `Assembleur` lit le code source assembleur.

Sa méthode principale est :

```java
Programme assembler(String source)
```

Elle :

1. vérifie que le code source n'est pas nul ;
2. sépare le texte en lignes ;
3. ignore les lignes vides ;
4. traduit chaque ligne avec `traduireLigne` ;
5. ajoute chaque instruction dans un `Programme`.

Si une ligne est invalide, l'assembleur indique :

- le numéro de ligne ;
- le code fautif ;
- un exemple de syntaxe correcte quand c'est une erreur de syntaxe.

Exemple de message :

```text
Ligne 4 : Syntaxe load invalide
Code : LOAD R1
Exemple correct : load r0, 5  ou  load r0, @100  ou  load r0, @100, r1
```

### 6.6 Syntaxe assembleur supportée

Les instructions peuvent être écrites en majuscules ou minuscules.

Exemples :

```asm
LOAD R0, 5
load r0, 5
```

Les adresses commencent par `@`.

Exemples :

```asm
LOAD R0, @100
STORE R1, @0x200
JUMP @42
```

Les valeurs numériques peuvent être :

- décimales : `100` ;
- hexadécimales : `0x64`.

## 7. Jeu d'instructions

### 7.1 Tableau des opcodes

| Opcode | Instruction | Syntaxe assembleur | Taille en mémoire | Description |
|---:|---|---|---:|---|
| `0` | `BREAK` | `BREAK` | 1 octet | arrête le programme |
| `1` | `LOAD_CONSTANTE` | `LOAD R0, 5` | 3 octets | charge une constante dans un registre |
| `2` | `LOAD_MEMOIRE` | `LOAD R0, @100` | 4 octets | charge depuis une adresse mémoire |
| `3` | `STORE_MEMOIRE` | `STORE R0, @100` | 4 octets | écrit un registre en mémoire |
| `4` | `ADD` | `ADD R2, R0, R1` | 4 octets | additionne deux registres |
| `5` | `SUB` | `SUB R2, R0, R1` | 4 octets | soustrait deux registres |
| `6` | `MUL` | `MUL R2, R3, R0, R1` | 5 octets | multiplie deux registres, résultat sur deux registres |
| `7` | `DIV` | `DIV R2, R3, R0, R1` | 5 octets | divise deux registres, quotient et reste |
| `8` | `AND` | `AND R2, R0, R1` | 4 octets | ET logique bit à bit |
| `9` | `OR` | `OR R2, R0, R1` | 4 octets | OU logique bit à bit |
| `10` | `XOR` | `XOR R2, R0, R1` | 4 octets | OU exclusif bit à bit |
| `11` | `JUMP` | `JUMP @100` | 3 octets | saute à une adresse |
| `12` | `BEQ` | `BEQ R0, R1, @100` | 5 octets | saute si les registres sont égaux |
| `13` | `BNE` | `BNE R0, R1, @100` | 5 octets | saute si les registres sont différents |
| `14` | `LOAD_INDEXE` | `LOAD R0, @100, R1` | 5 octets | lit à l'adresse `base + index` |
| `15` | `STORE_INDEXE` | `STORE R0, @100, R1` | 5 octets | écrit à l'adresse `base + index` |
| brut | `DATA` | `DATA 1, 2, 3` | variable | écrit directement des octets |
| brut | `STRING` | `STRING "abc"` | variable | écrit les octets UTF-8 d'une chaîne |

### 7.2 Convention des opérations arithmétiques

Pour les opérations à trois registres, la syntaxe utilisateur place la destination en premier :

```asm
ADD destination, source1, source2
SUB destination, source1, source2
AND destination, source1, source2
OR  destination, source1, source2
XOR destination, source1, source2
```

Exemple :

```asm
ADD R3, R1, R2
```

Cela signifie :

```text
R3 = R1 + R2
```

En interne, l'instruction est stockée sous la forme :

```text
source1, source2, destination
```

### 7.3 Convention de `MUL`

Syntaxe :

```asm
MUL registrePoidsFaible, registrePoidsFort, source1, source2
```

Exemple :

```asm
MUL R2, R3, R0, R1
```

Cela signifie :

```text
R2 = poids faible de R0 * R1
R3 = poids fort de R0 * R1
```

### 7.4 Convention de `DIV`

Syntaxe :

```asm
DIV registreQuotient, registreReste, source1, source2
```

Exemple :

```asm
DIV R2, R3, R0, R1
```

Cela signifie :

```text
R2 = quotient de R0 / R1
R3 = reste de R0 / R1
```

Si `source2` vaut `0`, l'ALU déclenche une erreur `Division par zéro`.

### 7.5 Adressage indexé

L'adressage indexé permet de manipuler des tableaux.

Syntaxe :

```asm
LOAD R0, @100, R1
STORE R0, @100, R1
```

Le CPU calcule l'adresse réelle ainsi :

```text
adresse réelle = adresse de base + valeur du registre d'index
```

Exemple :

```asm
LOAD R0, @100, R1
```

Si `R1 = 5`, alors le CPU lit à l'adresse :

```text
100 + 5 = 105
```

## 8. Encodage en mémoire

Chaque instruction est encodée en mémoire sous forme d'octets.

Exemple :

```asm
LOAD R0, 5
```

Encodage :

```text
1 0 5
```

Signification :

- `1` : opcode de `LOAD_CONSTANTE` ;
- `0` : numéro du registre `R0` ;
- `5` : constante à charger.

Exemple avec une adresse :

```asm
STORE R1, @0x6500
```

Encodage :

```text
3 1 0x65 0x00
```

Signification :

- `3` : opcode de `STORE_MEMOIRE` ;
- `1` : registre `R1` ;
- `0x65` : octet de poids fort de l'adresse ;
- `0x00` : octet de poids faible de l'adresse.

Les adresses sont stockées sur deux octets, en poids fort puis poids faible.

## 9. Directives `DATA` et `STRING`

`DATA` et `STRING` ne sont pas des instructions exécutées par le CPU. Ce sont des directives qui écrivent directement des données en mémoire.

Exemple :

```asm
DATA 10, 20, 30
STRING "fin"
```

`DATA` écrit les valeurs données sous forme d'octets.

`STRING` écrit les octets UTF-8 de la chaîne.

Bonne pratique :

```asm
BREAK
DATA 10, 20, 30
STRING "fin"
```

Le `BREAK` évite que le programme continue dans la zone de données. Le CPU possède aussi une sécurité supplémentaire avec `adressesExecutables`, qui empêche d'exécuter accidentellement `DATA` ou `STRING`.

## 10. Interface graphique `SimulateurGUI`

La classe `SimulateurGUI` construit l'interface Swing.

Elle hérite de :

```java
JFrame
```

### 10.1 Zone gauche : éditeur assembleur

La zone gauche contient :

- un `JTextArea` pour écrire le code assembleur ;
- une colonne de numéros de lignes ;
- un `JScrollPane` avec titre `Code Source Assembleur`.

Les numéros de lignes sont mis à jour avec un `DocumentListener`. Dès que l'utilisateur ajoute ou supprime du texte, la méthode `mettreAJourNumerosLignes()` recalcule les numéros.

### 10.2 Zone centrale : console et boutons

La zone centrale contient :

- la console de logs ;
- les boutons d'action.

Boutons disponibles :

| Bouton | Rôle |
|---|---|
| `Assembler` | assemble le code et le charge dans le CPU |
| `Exécuter tout` | exécute le programme jusqu'à `BREAK` ou erreur |
| `Pas à pas` | exécute une seule instruction |
| `Réinitialiser` | vide la mémoire, remet les registres à zéro et efface les logs |

La console affiche des messages avec deux niveaux :

```text
[INFO] ...
[ERREUR] ...
```

### 10.3 Exécution en arrière-plan

Le bouton `Exécuter tout` utilise un `SwingWorker`.

Cela évite de bloquer complètement l'interface pendant l'exécution du programme.

Pendant l'exécution :

- les boutons principaux sont désactivés ;
- le CPU exécute le programme ;
- à la fin, l'interface met à jour les registres, la mémoire et le PC ;
- en cas d'erreur, un message est ajouté dans la console.

### 10.4 Zone droite : état du CPU

La zone droite affiche :

- le compteur de programme `PC` ;
- les 16 registres ;
- une page de la mémoire.

Les registres sont affichés avec :

- leur nom ;
- leur valeur hexadécimale ;
- leur valeur décimale.

La mémoire est affichée avec :

- l'adresse en hexadécimal ;
- l'adresse en décimal ;
- la valeur en hexadécimal ;
- la valeur en décimal.

### 10.5 Pagination mémoire

La mémoire réelle contient `65536` adresses. Pour éviter une table énorme, l'interface affiche seulement `256` adresses à la fois.

Les boutons :

```text
< Précédent    Suivant >
```

permettent de naviguer page par page.

La plage courante est affichée, par exemple :

```text
0x0000 - 0x00FF
```

Chaque page contient `256` adresses.

Nombre total de pages :

```text
65536 / 256 = 256 pages
```

La première page va de :

```text
0x0000 à 0x00FF
```

La dernière page va de :

```text
0xFF00 à 0xFFFF
```

## 11. Gestion des erreurs

### 11.1 Erreurs d'assemblage

Les erreurs d'assemblage sont détectées dans `Assembleur`.

Exemples :

- instruction inconnue ;
- mauvais nombre d'opérandes ;
- registre invalide ;
- adresse invalide ;
- valeur numérique invalide ;
- chaîne `STRING` sans guillemets.

L'erreur indique la ligne concernée.

Exemple :

```text
[ERREUR] Erreur d'assemblage : Ligne 2 : Syntaxe add invalide
Code : ADD R1, R2
Exemple correct : add r3, r1, r2
```

### 11.2 Erreurs d'exécution

Les erreurs d'exécution peuvent venir du CPU ou de l'ALU.

Exemples :

- division par zéro ;
- opcode inconnu ;
- adresse de saut non exécutable ;
- limite d'instructions atteinte.

Exemple :

```text
[ERREUR] Erreur à l'exécution : Division par zéro
```

### 11.3 Protection contre les boucles infinies

Le CPU arrête l'exécution si plus de `100000` instructions sont exécutées sans atteindre `BREAK`.

Message possible :

```text
Limite de 100000 instructions atteinte. Programme probablement en boucle infinie.
```

## 12. Tests

Le projet contient plusieurs classes de tests dans `src/test`.

| Fichier | Élément testé |
|---|---|
| `AluTest.java` | opérations arithmétiques et logiques |
| `AssembleurTest.java` | traduction du code assembleur |
| `BanqueRegistresTest.java` | lecture/écriture des registres |
| `CpuTest.java` | chargement et exécution des programmes |
| `MemoireTest.java` | lecture/écriture mémoire |

Le fichier `pom.xml` configure Maven avec :

- Java 17 ;
- JUnit Jupiter ;
- le dossier `src` comme dossier de sources ;
- le dossier `src/test` comme dossier de tests.

Commande prévue si Maven est installé :

```bash
mvn test
```

Compilation manuelle possible :

```bash
javac --release 17 -d out src/Main.java src/noyau/*.java src/assembleur/*.java src/interfaceutilisateur/*.java
```

Lancement possible après compilation :

```bash
java -cp out Main
```

## 13. Exemple de programme assembleur

Ce programme additionne les nombres de `1` à `5`, stocke le résultat en mémoire, puis s'arrête.

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

Résultat attendu :

- `R0 = 15` ;
- mémoire `@200 = 15`.

Attention : les adresses utilisées avec `@` sont des adresses mémoire en octets, pas des numéros de lignes.

## 14. Exemple avec tableau, boucle et adressage indexé

Ce programme lit un tableau en mémoire, additionne ses valeurs, puis stocke la somme.

```asm
LOAD R0, 0
LOAD R1, 4
LOAD R2, 1
LOAD R3, 0

LOAD R4, @35, R0
ADD R3, R3, R4
ADD R0, R0, R2
BNE R0, R1, @12

STORE R3, @100
BREAK

DATA 5, 10, 15, 20
```

Remarque importante : dans cet exemple, l'adresse `@35` correspond à l'adresse réelle où les données `DATA` sont chargées. Comme le simulateur ne gère pas encore les labels, il faut calculer les adresses à partir de la taille des instructions.

## 15. Points importants et limites actuelles

Le simulateur respecte les fonctionnalités principales :

- 16 registres de 8 bits ;
- mémoire de 64 Ko ;
- compteur de programme sur 16 bits ;
- instructions `LOAD`, `STORE`, `BREAK` ;
- ALU avec addition, soustraction, multiplication, division, AND, OR, XOR ;
- sauts `JUMP`, `BEQ`, `BNE` ;
- adressage indexé ;
- directives `DATA` et `STRING` ;
- interface graphique ;
- affichage des registres et de la mémoire ;
- exécution complète et pas à pas ;
- gestion d'erreurs avec messages détaillés.

Limites actuelles :

- il n'y a pas encore de labels assembleur ;
- les adresses de saut doivent être calculées manuellement ;
- les valeurs sont stockées dans des `byte`, donc l'affichage Java décimal peut être signé entre `-128` et `127` ;
- certaines écritures invalides en mémoire ou registre affichent un message console Java plutôt qu'une exception ;
- les commentaires dans le code assembleur utilisateur ne sont pas encore gérés.

## 16. Résumé du cycle d'exécution

```text
Code assembleur utilisateur
        |
        v
Assembleur.assembler(source)
        |
        v
Programme contenant des Instruction
        |
        v
CPU.chargerProgramme(programme)
        |
        v
Mémoire remplie avec opcodes + opérandes
        |
        v
CPU.decoderInstruction()
        |
        v
CPU.appliquerInstruction()
        |
        v
Modification des registres, mémoire ou PC
        |
        v
Mise à jour de l'interface graphique
```

Ce projet simule donc à la fois le rôle d'un assembleur et le fonctionnement minimal d'un processeur : encodage des instructions, stockage en mémoire, décodage par opcode, exécution, registres, ALU, mémoire et contrôle du flot d'exécution.
