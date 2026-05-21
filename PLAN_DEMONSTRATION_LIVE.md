# Plan de demonstration live - 10 minutes

Objectif de la demo : montrer en direct que le simulateur permet d'ecrire du code assembleur, de l'assembler, de le charger en memoire, puis de l'executer dans un CPU simule avec registres, memoire, ALU, sauts et adressage indexe.

## Preparation avant la presentation

Ouvrir dans l'IDE :

- `src/Main.java` : point d'entree qui lance l'interface Swing.
- `src/interfaceutilisateur/SimulateurGUI.java` : editeur, boutons, logs, registres et memoire.
- `src/assembleur/Assembleur.java` : traduction du code assembleur.
- `src/noyau/CPU.java` : chargement, decodage et execution des instructions.
- `src/noyau/ALU.java` : operations arithmetiques et logiques.

Commande de lancement si on passe par le terminal, sans Maven :

```bash
mkdir -p out/demo
javac -d out/demo $(find src -name "*.java" ! -path "src/test/*")
java -cp out/demo Main
```

Commande de tests si Maven est installe :

```bash
mvn test
```

Note importante : les adresses utilisees par `JUMP`, `BEQ` et `BNE` sont des adresses en octets dans la memoire, pas des numeros de lignes. Les adresses donnees dans ce document sont deja calculees.

## Timing conseille

| Temps | Partie | Ce qu'on montre |
|---:|---|---|
| 0:00 - 0:45 | Introduction | But du projet et lancement de l'application |
| 0:45 - 1:30 | Architecture | Paquets `noyau`, `assembleur`, `interfaceutilisateur`, `test` |
| 1:30 - 2:15 | Cycle CPU | `chargerProgramme`, `decoderInstruction`, `appliquerInstruction` |
| 2:15 - 3:45 | Demo 1 | `LOAD`, `ADD`, `STORE`, `BREAK` en pas a pas |
| 3:45 - 5:20 | Demo 2 | Boucle avec `BEQ` et `BNE` |
| 5:20 - 6:35 | Demo 3 | `DATA`, `STRING` et memoire |
| 6:35 - 7:50 | Demo 4 | Adressage indexe, comme un tableau |
| 7:50 - 8:55 | Demo 5 | `MUL` et `JUMP` |
| 8:55 - 9:35 | Erreur | Erreur de syntaxe et message utile |
| 9:35 - 10:00 | Conclusion | Recapitulatif rapide |

## Script oral court

Phrase d'ouverture :

> Notre projet est un simulateur de CPU en Java. L'utilisateur ecrit du code assembleur, l'assembleur le transforme en instructions, le CPU les charge en memoire, puis il les execute en mettant a jour les registres, la memoire et le compteur de programme.

Phrase architecture :

> Le projet est separe en trois parties : le noyau simule le materiel, l'assembleur traduit le code utilisateur, et l'interface Swing permet de manipuler le tout visuellement.

Phrase cycle CPU :

> Le CPU lit l'opcode a l'adresse du PC, lit les operandes, decode l'instruction, puis applique l'effet : modifier un registre, ecrire en memoire, utiliser l'ALU ou changer le PC pour un saut.

## Demo 1 - Addition et stockage en memoire

But : montrer le chemin complet le plus simple : assembleur, registres, ALU, memoire.

Action :

1. Cliquer sur `Reinitialiser`.
2. Coller le programme.
3. Cliquer sur `Assembler`.
4. Cliquer plusieurs fois sur `Pas a pas`.
5. Montrer les registres et l'adresse memoire `200`, soit `0x00C8`.

Code :

```asm
LOAD R0, 5
LOAD R1, 10
ADD R2, R0, R1
STORE R2, @200
BREAK
```

Resultat attendu :

- `R0 = 5`
- `R1 = 10`
- `R2 = 15`
- memoire `@200 = 15`

Ce qu'on dit :

> On charge 5 et 10 dans deux registres, l'ALU additionne les deux valeurs, puis on stocke le resultat dans la memoire. En pas a pas, on voit le PC avancer instruction par instruction.

## Demo 2 - Boucle avec branchements conditionnels

But : montrer que le CPU gere les conditions et les boucles.

Action :

1. Cliquer sur `Reinitialiser`.
2. Coller le programme.
3. Cliquer sur `Assembler`.
4. Cliquer sur `Executer tout`.
5. Montrer `R0` et la memoire `@200`.

Code :

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

Resultat attendu :

- le programme calcule `1 + 2 + 3 + 4 + 5`
- `R0 = 15`
- `R1 = 6` a la fin de la boucle
- memoire `@200 = 15`

Ce qu'on dit :

> Ici `R0` est l'accumulateur, `R1` est le compteur, `R2` contient la limite et `R3` sert a incrementer. `BEQ` permet de sortir quand le compteur arrive a 6, et `BNE` renvoie au debut de la boucle.

## Demo 3 - DATA, STRING et memoire brute

But : montrer que le programme peut aussi charger des donnees en memoire.

Action :

1. Cliquer sur `Reinitialiser`.
2. Coller le programme.
3. Cliquer sur `Assembler`.
4. Cliquer sur `Executer tout`.
5. Montrer les adresses memoire `4` a `9`.

Code :

```asm
LOAD R0, 1
BREAK
DATA 10, 20, 30
STRING "CPU"
```

Resultat attendu en memoire :

- adresse `4` : `10`
- adresse `5` : `20`
- adresse `6` : `30`
- adresse `7` : `67`, caractere `C`
- adresse `8` : `80`, caractere `P`
- adresse `9` : `85`, caractere `U`

Ce qu'on dit :

> `DATA` ecrit des octets directement en memoire et `STRING` ecrit les caracteres en UTF-8. Le CPU s'arrete sur `BREAK`, donc ces donnees restent en memoire sans etre executees comme des instructions.

## Demo 4 - Adressage indexe comme un tableau

But : montrer `STORE` et `LOAD` indexes, donc une adresse de base plus un registre d'index.

Action :

1. Cliquer sur `Reinitialiser`.
2. Coller le programme.
3. Cliquer sur `Assembler`.
4. Cliquer sur `Executer tout`.
5. Montrer `R2` et la memoire `@42`.

Code :

```asm
LOAD R0, 42
LOAD R1, 2
STORE R0, @40, R1
LOAD R2, @40, R1
BREAK
```

Resultat attendu :

- `R0 = 42`
- `R1 = 2`
- adresse calculee : `40 + 2 = 42`
- memoire `@42 = 42`
- `R2 = 42`

Ce qu'on dit :

> Cette syntaxe permet de manipuler une zone memoire comme un tableau : on prend une adresse de base et on ajoute un index contenu dans un registre.

## Demo 5 - Multiplication et saut inconditionnel

But : montrer une operation ALU avec deux registres resultat et un saut `JUMP`.

Action :

1. Cliquer sur `Reinitialiser`.
2. Coller le programme.
3. Cliquer sur `Assembler`.
4. Cliquer sur `Executer tout`.
5. Montrer `R2`, `R3` et la memoire `@200`.

Code :

```asm
LOAD R0, 6
LOAD R1, 7
MUL R2, R3, R0, R1
JUMP @17
LOAD R2, 0
STORE R2, @200
BREAK
```

Resultat attendu :

- `R2 = 42`, poids faible du resultat
- `R3 = 0`, poids fort du resultat
- `LOAD R2, 0` est saute par `JUMP @17`
- memoire `@200 = 42`

Ce qu'on dit :

> `MUL` produit un resultat sur deux octets : le poids faible dans `R2` et le poids fort dans `R3`. Ensuite `JUMP @17` saute l'instruction qui remettrait `R2` a zero, donc on prouve que le saut fonctionne.

## Demo erreur - Syntaxe incorrecte

But : montrer que l'assembleur aide l'utilisateur quand le code est invalide.

Action :

1. Cliquer sur `Reinitialiser`.
2. Coller le programme faux.
3. Cliquer sur `Assembler`.
4. Lire le message dans la console.

Code :

```asm
LOAD R0
```

Resultat attendu :

- la console affiche une erreur d'assemblage ;
- le message indique la ligne en erreur ;
- un exemple correct est propose, par exemple `load r0, 5`.

Ce qu'on dit :

> L'assembleur ne plante pas silencieusement : il explique la ligne incorrecte et donne un exemple de syntaxe valide.

## Points de code a montrer rapidement

Dans `SimulateurGUI.java` :

- `assemblerProgramme()` lit le texte, appelle l'assembleur, charge le programme dans le CPU.
- `executerProgramme()` lance l'execution complete.
- `executerPasAPas()` execute une seule instruction et met a jour l'affichage.
- `mettreAJourEtatCPU()` rafraichit le PC, les registres et la memoire.

Dans `Assembleur.java` :

- `assembler(source)` parcourt les lignes.
- `traduireLigne(...)` reconnait `LOAD`, `STORE`, `ADD`, `JUMP`, `DATA`, etc.
- `genererCodeMachine(...)` transforme les instructions en octets.

Dans `CPU.java` :

- `chargerProgramme(...)` ecrit les opcodes et les operandes en memoire.
- `decoderInstruction()` lit l'opcode et reconstruit l'instruction.
- `appliquerInstruction(...)` modifie les registres, la memoire ou le PC.
- `adressesExecutables` evite d'executer les zones `DATA` et `STRING`.

Dans `ALU.java` :

- `additionner`, `soustraire`, `multiplier`, `diviser`
- `estBinaire`, `ouBinaire`, `xorBinaire`

## Plan de secours si on manque de temps

Version courte en 6 minutes :

1. Introduction et architecture : 1 minute.
2. Demo 1 addition + memoire : 2 minutes.
3. Demo 2 boucle : 1 minute 30.
4. Demo erreur : 45 secondes.
5. Conclusion : 45 secondes.

Version ultra courte :

- ne faire que la demo 1 ;
- montrer ensuite le code de `CPU.java` et `Assembleur.java` ;
- conclure en listant les autres instructions supportees.

## Conclusion finale

Phrase de fin :

> En resume, notre application couvre tout le cycle d'un petit processeur : un code assembleur lisible est traduit en instructions, charge en memoire, decode par le CPU, execute avec l'ALU, puis observe dans l'interface avec les registres, la memoire, le PC et les logs.
