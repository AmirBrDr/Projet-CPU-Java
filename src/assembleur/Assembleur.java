package assembleur;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Transforme un code source assembleur en programme puis en code machine.
 * <p>
 * Cette classe reconnaît les instructions du processeur simulé, construit les
 * objets {@link Instruction} correspondants et encode ensuite ces instructions
 * dans le format binaire attendu par le CPU.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class Assembleur {
    private static final int OPCODE_BREAK = 0;
    private static final int OPCODE_LOAD_CONSTANTE = 1;
    private static final int OPCODE_LOAD_MEMOIRE = 2;
    private static final int OPCODE_STORE_MEMOIRE = 3;
    private static final int OPCODE_ADD = 4;
    private static final int OPCODE_SUB = 5;
    private static final int OPCODE_MUL = 6;
    private static final int OPCODE_DIV = 7;
    private static final int OPCODE_AND = 8;
    private static final int OPCODE_OR = 9;
    private static final int OPCODE_XOR = 10;
    private static final int OPCODE_JUMP = 11;
    private static final int OPCODE_BEQ = 12;
    private static final int OPCODE_BNE = 13;
    private static final int OPCODE_LOAD_INDEXE = 14;
    private static final int OPCODE_STORE_INDEXE = 15;

    /**
     * Assemble un code source complet en une suite d'instructions abstraites.
     *
     * @param source le texte assembleur à traduire
     * @return un {@link Programme} contenant les instructions reconnues
     */
    public Programme assembler(String source) {
        // Verification de l'entree avant de commencer l'assemblage.
        if (source == null) {
            throw new IllegalArgumentException("Le code source ne doit pas etre nul");
        }

        Programme programme = new Programme();
        String[] lignes = source.split("\\R");

        // Chaque ligne non vide est traduite puis ajoutee au programme final.
        for (int indexLigne = 0; indexLigne < lignes.length; indexLigne++) {
            String ligne = lignes[indexLigne];
            String ligneNettoyee = ligne.trim();
            if (ligneNettoyee.isEmpty()) {
                continue;
            }

            try {
                Instruction instruction = traduireLigne(ligne);
                programme.ajouterInstruction(instruction);
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException(
                        construireMessageErreur(indexLigne + 1, ligne, exception.getMessage()),
                        exception
                );
            }
        }

        return programme;
    }

    /**
     * Construit un message d'erreur détaillé pour une ligne source invalide.
     *
     * @param numeroLigne  le numéro de la ligne dans le code source
     * @param ligneSource  la ligne source originale
     * @param messageErreur le message d'erreur initial
     * @return un message détaillé affichable dans l'interface
     */
    private String construireMessageErreur(int numeroLigne, String ligneSource, String messageErreur) {
        StringBuilder message = new StringBuilder();
        message.append("Ligne ").append(numeroLigne).append(" : ").append(messageErreur);
        message.append("\nCode : ").append(ligneSource.trim());

        if (estErreurDeSyntaxe(messageErreur)) {
            message.append("\nExemple correct : ").append(trouverExempleSyntaxe(ligneSource));
        }

        return message.toString();
    }

    /**
     * Indique si une erreur correspond à une erreur de syntaxe utilisateur.
     *
     * @param messageErreur le message d'erreur à analyser
     * @return {@code true} si l'erreur concerne la syntaxe, {@code false} sinon
     */
    private boolean estErreurDeSyntaxe(String messageErreur) {
        return messageErreur.startsWith("Syntaxe ")
                || messageErreur.startsWith("Instruction non reconnue")
                || messageErreur.startsWith("Adresse memoire invalide")
                || messageErreur.startsWith("Registre invalide")
                || messageErreur.startsWith("Numero de registre invalide")
                || messageErreur.startsWith("Valeur numerique invalide");
    }

    /**
     * Retourne un exemple correct adapté au mot-clé de la ligne en erreur.
     *
     * @param ligneSource la ligne source originale
     * @return une ligne assembleur valide servant d'exemple
     */
    private String trouverExempleSyntaxe(String ligneSource) {
        String ligneNettoyee = ligneSource.trim().toLowerCase();
        String[] mots = ligneNettoyee.split("\\s+");
        String motCle = mots.length == 0 ? "" : mots[0];

        return switch (motCle) {
            case "break" -> "break";
            case "load" -> "load r0, 5  ou  load r0, @100  ou  load r0, @100, r1";
            case "store" -> "store r0, @100  ou  store r0, @100, r1";
            case "add" -> "add r3, r1, r2";
            case "sub" -> "sub r3, r1, r2";
            case "mul" -> "mul r3, r4, r1, r2";
            case "div" -> "div r3, r4, r1, r2";
            case "and" -> "and r3, r1, r2";
            case "or" -> "or r3, r1, r2";
            case "xor" -> "xor r3, r1, r2";
            case "jump" -> "jump @100";
            case "beq" -> "beq r1, r2, @100";
            case "bne" -> "bne r1, r2, @100";
            case "data" -> "data 10, 20, 30";
            case "string" -> "string \"Bonjour\"";
            default -> "load r0, 5";
        };
    }

    /**
     * Traduit une ligne de code source en instruction abstraite.
     *
     * @param ligneSource la ligne assembleur à analyser
     * @return l'{@link Instruction} correspondant à la ligne
     */
    public Instruction traduireLigne(String ligneSource) {
        // Verification des cas invalides les plus simples.
        if (ligneSource == null) {
            throw new IllegalArgumentException("La ligne source ne doit pas etre nulle");
        }

        String ligneNettoyee = ligneSource.trim();
        if (ligneNettoyee.isEmpty()) {
            throw new IllegalArgumentException("La ligne source ne doit pas etre vide");
        }

        // Pour commencer simplement, on reconnait uniquement l'instruction BREAK.
        if (ligneNettoyee.equalsIgnoreCase("break")) {
            return new Instruction(TypeInstruction.BREAK, List.of(), ligneSource);
        }

        // Delegation vers le traducteur specialise de l'instruction LOAD.
        if (ligneNettoyee.toLowerCase().startsWith("load ")) {
            return traduireInstructionLoad(ligneNettoyee, ligneSource);
        }

        // Delegation vers le traducteur specialise de l'instruction STORE.
        if (ligneNettoyee.toLowerCase().startsWith("store ")) {
            return traduireInstructionStore(ligneNettoyee, ligneSource);
        }

        // Delegation vers le traducteur specialise de l'instruction ADD.
        if (ligneNettoyee.toLowerCase().startsWith("add ")) {
            return traduireInstructionAdd(ligneNettoyee, ligneSource);
        }

        // Delegation vers le traducteur specialise de l'instruction SUB.
        if (ligneNettoyee.toLowerCase().startsWith("sub ")) {
            return traduireInstructionSub(ligneNettoyee, ligneSource);
        }

        // Delegation vers le traducteur specialise de l'instruction MUL.
        if (ligneNettoyee.toLowerCase().startsWith("mul ")) {
            return traduireInstructionMul(ligneNettoyee, ligneSource);
        }

        // Delegation vers le traducteur specialise de l'instruction DIV.
        if (ligneNettoyee.toLowerCase().startsWith("div ")) {
            return traduireInstructionDiv(ligneNettoyee, ligneSource);
        }

        // Delegation vers le traducteur specialise de l'instruction AND.
        if (ligneNettoyee.toLowerCase().startsWith("and ")) {
            return traduireInstructionAnd(ligneNettoyee, ligneSource);
        }

        // Delegation vers le traducteur specialise de l'instruction OR.
        if (ligneNettoyee.toLowerCase().startsWith("or ")) {
            return traduireInstructionOr(ligneNettoyee, ligneSource);
        }

        // Delegation vers le traducteur specialise de l'instruction XOR.
        if (ligneNettoyee.toLowerCase().startsWith("xor ")) {
            return traduireInstructionXor(ligneNettoyee, ligneSource);
        }

        // Delegation vers le traducteur specialise de l'instruction JUMP.
        if (ligneNettoyee.toLowerCase().startsWith("jump ")) {
            return traduireInstructionJump(ligneNettoyee, ligneSource);
        }

        // Delegation vers le traducteur specialise de l'instruction BEQ.
        if (ligneNettoyee.toLowerCase().startsWith("beq ")) {
            return traduireInstructionBeq(ligneNettoyee, ligneSource);
        }

        // Delegation vers le traducteur specialise de l'instruction BNE.
        if (ligneNettoyee.toLowerCase().startsWith("bne ")) {
            return traduireInstructionBne(ligneNettoyee, ligneSource);
        }

        // Delegation vers le traducteur specialise de la directive DATA.
        if (ligneNettoyee.toLowerCase().startsWith("data ")) {
            return traduireInstructionData(ligneNettoyee, ligneSource);
        }

        // Delegation vers le traducteur specialise de la directive STRING.
        if (ligneNettoyee.toLowerCase().startsWith("string ")) {
            return traduireInstructionString(ligneNettoyee, ligneSource);
        }

        throw new IllegalArgumentException("Instruction non reconnue : " + ligneSource);
    }

    /**
     * Génère le code machine correspondant à un programme assemblé.
     *
     * @param programme le {@link Programme} à convertir en octets
     * @return un tableau d'octets contenant le code machine
     */
    public byte[] genererCodeMachine(Programme programme) {
        // Le programme doit exister avant de pouvoir etre converti en octets.
        if (programme == null) {
            throw new IllegalArgumentException("Le programme ne doit pas etre nul");
        }

        ByteArrayOutputStream codeMachine = new ByteArrayOutputStream();

        // Chaque instruction est convertie selon le format d'octets attendu par le CPU.
        for (Instruction instruction : programme.getInstructions()) {
            ecrireInstruction(codeMachine, instruction);
        }

        return codeMachine.toByteArray();
    }

    /**
     * Traduit une instruction LOAD, immédiate ou mémoire.
     *
     * @param ligneNettoyee la ligne sans espaces inutiles en début et fin
     * @param ligneSource   la ligne source originale
     * @return l'{@link Instruction} LOAD construite
     */
    private Instruction traduireInstructionLoad(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "load" pour ne garder que les operandes.
        String reste = ligneNettoyee.substring(4).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length == 3) {
            return traduireInstructionLoadIndexe(morceaux, ligneSource);
        }

        if (morceaux.length != 2) {
            throw new IllegalArgumentException("Syntaxe load invalide : " + ligneSource);
        }

        // Le premier operande est toujours le registre destination.
        OperandeRegistre registreDestination = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        String secondOperande = morceaux[1].trim();

        // Si le second operande commence par '@', on lit depuis la memoire.
        if (secondOperande.startsWith("@")) {
            int adresse = lireValeurNumerique(secondOperande.substring(1).trim(), ligneSource);
            OperandeAdresse adresseMemoire = new OperandeAdresse(adresse);
            return new Instruction(
                    TypeInstruction.LOAD_MEMOIRE,
                    List.of(registreDestination, adresseMemoire),
                    ligneSource
            );
        }

        // Sinon, on considere qu'il s'agit d'une constante immediate.
        int valeur = lireValeurNumerique(secondOperande, ligneSource);
        OperandeConstante constante = new OperandeConstante(valeur);
        return new Instruction(
                TypeInstruction.LOAD_CONSTANTE,
                List.of(registreDestination, constante),
                ligneSource
        );
    }

    /**
     * Traduit une instruction LOAD utilisant une adresse indexée.
     *
     * @param morceaux    les opérandes séparés par des virgules
     * @param ligneSource la ligne source originale
     * @return l'{@link Instruction} LOAD indexée construite
     */
    private Instruction traduireInstructionLoadIndexe(String[] morceaux, String ligneSource) {
        // L'instruction LOAD indexe attend un registre destination, une adresse de base et un registre d'index.
        OperandeRegistre registreDestination = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        String deuxiemeOperande = morceaux[1].trim();
        OperandeRegistre registreIndex = lireOperandeRegistre(morceaux[2].trim(), ligneSource);

        if (!deuxiemeOperande.startsWith("@")) {
            throw new IllegalArgumentException("Adresse memoire invalide dans la ligne : " + ligneSource);
        }

        int adresseBase = lireValeurNumerique(deuxiemeOperande.substring(1).trim(), ligneSource);
        OperandeAdresseIndexee adresseIndexee = new OperandeAdresseIndexee(
                adresseBase,
                registreIndex.getNumeroRegistre()
        );

        return new Instruction(
                TypeInstruction.LOAD_INDEXE,
                List.of(registreDestination, adresseIndexee),
                ligneSource
        );
    }

    /**
     * Traduit une instruction STORE, absolue ou indexée.
     *
     * @param ligneNettoyee la ligne sans espaces inutiles en début et fin
     * @param ligneSource   la ligne source originale
     * @return l'{@link Instruction} STORE construite
     */
    private Instruction traduireInstructionStore(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "store" pour ne garder que les operandes.
        String reste = ligneNettoyee.substring(5).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length == 3) {
            return traduireInstructionStoreIndexe(morceaux, ligneSource);
        }

        if (morceaux.length != 2) {
            throw new IllegalArgumentException("Syntaxe store invalide : " + ligneSource);
        }

        // Le premier operande est le registre source a ecrire en memoire.
        OperandeRegistre registreSource = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        String secondOperande = morceaux[1].trim();

        // Pour STORE, le second operande doit obligatoirement etre une adresse memoire.
        if (!secondOperande.startsWith("@")) {
            throw new IllegalArgumentException("Adresse memoire invalide dans la ligne : " + ligneSource);
        }

        int adresse = lireValeurNumerique(secondOperande.substring(1).trim(), ligneSource);
        OperandeAdresse adresseMemoire = new OperandeAdresse(adresse);
        return new Instruction(
                TypeInstruction.STORE_MEMOIRE,
                List.of(registreSource, adresseMemoire),
                ligneSource
        );
    }

    /**
     * Traduit une instruction STORE utilisant une adresse indexée.
     *
     * @param morceaux    les opérandes séparés par des virgules
     * @param ligneSource la ligne source originale
     * @return l'{@link Instruction} STORE indexée construite
     */
    private Instruction traduireInstructionStoreIndexe(String[] morceaux, String ligneSource) {
        // L'instruction STORE indexe attend un registre source, une adresse de base et un registre d'index.
        OperandeRegistre registreSource = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        String deuxiemeOperande = morceaux[1].trim();
        OperandeRegistre registreIndex = lireOperandeRegistre(morceaux[2].trim(), ligneSource);

        if (!deuxiemeOperande.startsWith("@")) {
            throw new IllegalArgumentException("Adresse memoire invalide dans la ligne : " + ligneSource);
        }

        int adresseBase = lireValeurNumerique(deuxiemeOperande.substring(1).trim(), ligneSource);
        OperandeAdresseIndexee adresseIndexee = new OperandeAdresseIndexee(
                adresseBase,
                registreIndex.getNumeroRegistre()
        );

        return new Instruction(
                TypeInstruction.STORE_INDEXE,
                List.of(registreSource, adresseIndexee),
                ligneSource
        );
    }

    /**
     * Traduit une instruction ADD.
     *
     * @param ligneNettoyee la ligne sans espaces inutiles en début et fin
     * @param ligneSource   la ligne source originale
     * @return l'{@link Instruction} ADD construite
     */
    private Instruction traduireInstructionAdd(String ligneNettoyee, String ligneSource) {
        return traduireOperationTroisRegistres(ligneNettoyee, ligneSource, "add", TypeInstruction.ADD);
    }

    /**
     * Traduit une instruction SUB.
     *
     * @param ligneNettoyee la ligne sans espaces inutiles en début et fin
     * @param ligneSource   la ligne source originale
     * @return l'{@link Instruction} SUB construite
     */
    private Instruction traduireInstructionSub(String ligneNettoyee, String ligneSource) {
        return traduireOperationTroisRegistres(ligneNettoyee, ligneSource, "sub", TypeInstruction.SUB);
    }

    /**
     * Traduit une instruction MUL.
     *
     * @param ligneNettoyee la ligne sans espaces inutiles en début et fin
     * @param ligneSource   la ligne source originale
     * @return l'{@link Instruction} MUL construite
     */
    private Instruction traduireInstructionMul(String ligneNettoyee, String ligneSource) {
        return traduireOperationQuatreRegistres(ligneNettoyee, ligneSource, "mul", TypeInstruction.MUL);
    }

    /**
     * Traduit une instruction DIV.
     *
     * @param ligneNettoyee la ligne sans espaces inutiles en début et fin
     * @param ligneSource   la ligne source originale
     * @return l'{@link Instruction} DIV construite
     */
    private Instruction traduireInstructionDiv(String ligneNettoyee, String ligneSource) {
        return traduireOperationQuatreRegistres(ligneNettoyee, ligneSource, "div", TypeInstruction.DIV);
    }

    /**
     * Traduit une instruction AND.
     *
     * @param ligneNettoyee la ligne sans espaces inutiles en début et fin
     * @param ligneSource   la ligne source originale
     * @return l'{@link Instruction} AND construite
     */
    private Instruction traduireInstructionAnd(String ligneNettoyee, String ligneSource) {
        return traduireOperationTroisRegistres(ligneNettoyee, ligneSource, "and", TypeInstruction.AND);
    }

    /**
     * Traduit une instruction OR.
     *
     * @param ligneNettoyee la ligne sans espaces inutiles en début et fin
     * @param ligneSource   la ligne source originale
     * @return l'{@link Instruction} OR construite
     */
    private Instruction traduireInstructionOr(String ligneNettoyee, String ligneSource) {
        return traduireOperationTroisRegistres(ligneNettoyee, ligneSource, "or", TypeInstruction.OR);
    }

    /**
     * Traduit une instruction XOR.
     *
     * @param ligneNettoyee la ligne sans espaces inutiles en début et fin
     * @param ligneSource   la ligne source originale
     * @return l'{@link Instruction} XOR construite
     */
    private Instruction traduireInstructionXor(String ligneNettoyee, String ligneSource) {
        return traduireOperationTroisRegistres(ligneNettoyee, ligneSource, "xor", TypeInstruction.XOR);
    }

    /**
     * Traduit une opération à trois registres avec la destination en premier.
     *
     * @param ligneNettoyee   la ligne sans espaces inutiles en début et fin
     * @param ligneSource     la ligne source originale
     * @param motCle          le mot-clé de l'instruction
     * @param typeInstruction le type d'instruction à construire
     * @return l'{@link Instruction} construite
     */
    private Instruction traduireOperationTroisRegistres(
            String ligneNettoyee,
            String ligneSource,
            String motCle,
            TypeInstruction typeInstruction
    ) {
        String reste = ligneNettoyee.substring(motCle.length()).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length != 3) {
            throw new IllegalArgumentException("Syntaxe " + motCle + " invalide : " + ligneSource);
        }

        // Syntaxe utilisateur : OP destination, source1, source2.
        // Format interne du CPU : source1, source2, destination.
        OperandeRegistre registreDestination = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        OperandeRegistre premierRegistre = lireOperandeRegistre(morceaux[1].trim(), ligneSource);
        OperandeRegistre secondRegistre = lireOperandeRegistre(morceaux[2].trim(), ligneSource);

        return new Instruction(
                typeInstruction,
                List.of(premierRegistre, secondRegistre, registreDestination),
                ligneSource
        );
    }

    /**
     * Traduit une opération à deux sources et deux registres de résultat.
     *
     * @param ligneNettoyee   la ligne sans espaces inutiles en début et fin
     * @param ligneSource     la ligne source originale
     * @param motCle          le mot-clé de l'instruction
     * @param typeInstruction le type d'instruction à construire
     * @return l'{@link Instruction} construite
     */
    private Instruction traduireOperationQuatreRegistres(
            String ligneNettoyee,
            String ligneSource,
            String motCle,
            TypeInstruction typeInstruction
    ) {
        String reste = ligneNettoyee.substring(motCle.length()).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length != 4) {
            throw new IllegalArgumentException("Syntaxe " + motCle + " invalide : " + ligneSource);
        }

        // Syntaxe utilisateur : OP resultat1, resultat2, source1, source2.
        // MUL : resultat1 = poids faible, resultat2 = poids fort.
        // DIV : resultat1 = quotient, resultat2 = reste.
        OperandeRegistre premierResultat = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        OperandeRegistre secondResultat = lireOperandeRegistre(morceaux[1].trim(), ligneSource);
        OperandeRegistre premierRegistre = lireOperandeRegistre(morceaux[2].trim(), ligneSource);
        OperandeRegistre secondRegistre = lireOperandeRegistre(morceaux[3].trim(), ligneSource);

        return new Instruction(
                typeInstruction,
                List.of(premierRegistre, secondRegistre, premierResultat, secondResultat),
                ligneSource
        );
    }

    /**
     * Traduit une instruction JUMP vers une adresse mémoire.
     *
     * @param ligneNettoyee la ligne sans espaces inutiles en début et fin
     * @param ligneSource   la ligne source originale
     * @return l'{@link Instruction} JUMP construite
     */
    private Instruction traduireInstructionJump(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "jump" pour ne garder que l'adresse cible.
        String reste = ligneNettoyee.substring(4).trim();

        // L'instruction JUMP attend obligatoirement une adresse memoire.
        if (!reste.startsWith("@")) {
            throw new IllegalArgumentException("Adresse memoire invalide dans la ligne : " + ligneSource);
        }

        int adresse = lireValeurNumerique(reste.substring(1).trim(), ligneSource);
        OperandeAdresse adresseCible = new OperandeAdresse(adresse);

        return new Instruction(
                TypeInstruction.JUMP,
                List.of(adresseCible),
                ligneSource
        );
    }

    /**
     * Traduit une instruction BEQ vers une adresse mémoire.
     *
     * @param ligneNettoyee la ligne sans espaces inutiles en début et fin
     * @param ligneSource   la ligne source originale
     * @return l'{@link Instruction} BEQ construite
     */
    private Instruction traduireInstructionBeq(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "beq" pour ne garder que les operandes.
        String reste = ligneNettoyee.substring(3).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length != 3) {
            throw new IllegalArgumentException("Syntaxe beq invalide : " + ligneSource);
        }

        // L'instruction BEQ attend deux registres puis une adresse cible.
        OperandeRegistre premierRegistre = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        OperandeRegistre secondRegistre = lireOperandeRegistre(morceaux[1].trim(), ligneSource);
        String troisiemeOperande = morceaux[2].trim();

        if (!troisiemeOperande.startsWith("@")) {
            throw new IllegalArgumentException("Adresse memoire invalide dans la ligne : " + ligneSource);
        }

        int adresse = lireValeurNumerique(troisiemeOperande.substring(1).trim(), ligneSource);
        OperandeAdresse adresseCible = new OperandeAdresse(adresse);

        return new Instruction(
                TypeInstruction.BEQ,
                List.of(premierRegistre, secondRegistre, adresseCible),
                ligneSource
        );
    }

    /**
     * Traduit une instruction BNE vers une adresse mémoire.
     *
     * @param ligneNettoyee la ligne sans espaces inutiles en début et fin
     * @param ligneSource   la ligne source originale
     * @return l'{@link Instruction} BNE construite
     */
    private Instruction traduireInstructionBne(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "bne" pour ne garder que les operandes.
        String reste = ligneNettoyee.substring(3).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length != 3) {
            throw new IllegalArgumentException("Syntaxe bne invalide : " + ligneSource);
        }

        // L'instruction BNE attend deux registres puis une adresse cible.
        OperandeRegistre premierRegistre = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        OperandeRegistre secondRegistre = lireOperandeRegistre(morceaux[1].trim(), ligneSource);
        String troisiemeOperande = morceaux[2].trim();

        if (!troisiemeOperande.startsWith("@")) {
            throw new IllegalArgumentException("Adresse memoire invalide dans la ligne : " + ligneSource);
        }

        int adresse = lireValeurNumerique(troisiemeOperande.substring(1).trim(), ligneSource);
        OperandeAdresse adresseCible = new OperandeAdresse(adresse);

        return new Instruction(
                TypeInstruction.BNE,
                List.of(premierRegistre, secondRegistre, adresseCible),
                ligneSource
        );
    }

    /**
     * Traduit une directive DATA en valeurs brutes.
     *
     * @param ligneNettoyee la ligne sans espaces inutiles en début et fin
     * @param ligneSource   la ligne source originale
     * @return l'{@link Instruction} DATA construite
     */
    private Instruction traduireInstructionData(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "data" pour ne garder que la liste des valeurs.
        String reste = ligneNettoyee.substring(4).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length == 0) {
            throw new IllegalArgumentException("Syntaxe data invalide : " + ligneSource);
        }

        // La directive DATA convertit chaque valeur en octet brut a ecrire en memoire.
        int[] valeurs = new int[morceaux.length];
        for (int i = 0; i < morceaux.length; i++) {
            valeurs[i] = lireValeurNumerique(morceaux[i].trim(), ligneSource);
        }

        OperandeDonnees donnees = new OperandeDonnees(valeurs);
        return new Instruction(
                TypeInstruction.DATA,
                List.of(donnees),
                ligneSource
        );
    }

    /**
     * Traduit une directive STRING en chaîne de caractères.
     *
     * @param ligneNettoyee la ligne sans espaces inutiles en début et fin
     * @param ligneSource   la ligne source originale
     * @return l'{@link Instruction} STRING construite
     */
    private Instruction traduireInstructionString(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "string" pour ne garder que le contenu de la chaine.
        String reste = ligneNettoyee.substring(6).trim();

        // La directive STRING attend une chaine encadree par des guillemets.
        if (reste.length() < 2 || !reste.startsWith("\"") || !reste.endsWith("\"")) {
            throw new IllegalArgumentException("Syntaxe string invalide : " + ligneSource);
        }

        String valeur = reste.substring(1, reste.length() - 1);
        OperandeChaine chaine = new OperandeChaine(valeur);

        return new Instruction(
                TypeInstruction.STRING,
                List.of(chaine),
                ligneSource
        );
    }

    /**
     * Lit un opérande de registre depuis son écriture textuelle.
     *
     * @param texteRegistre le texte représentant le registre, par exemple {@code r0}
     * @param ligneSource   la ligne source originale
     * @return l'{@link OperandeRegistre} correspondant
     */
    private OperandeRegistre lireOperandeRegistre(String texteRegistre, String ligneSource) {
        // Un registre valide doit commencer par la lettre 'r'.
        String registreMinuscule = texteRegistre.toLowerCase();
        if (!registreMinuscule.startsWith("r")) {
            throw new IllegalArgumentException("Registre invalide dans la ligne : " + ligneSource);
        }

        try {
            // On convertit la partie numerique en numero de registre.
            int numeroRegistre = Integer.parseInt(registreMinuscule.substring(1));
            return new OperandeRegistre(numeroRegistre);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Numero de registre invalide dans la ligne : " + ligneSource);
        }
    }

    /**
     * Lit une valeur numérique décimale ou hexadécimale.
     *
     * @param texteValeur le texte représentant la valeur numérique
     * @param ligneSource la ligne source originale
     * @return la valeur entière décodée
     */
    private int lireValeurNumerique(String texteValeur, String ligneSource) {
        try {
            // Le sujet autorise les ecritures decimales et hexadecimales.
            if (texteValeur.startsWith("0x") || texteValeur.startsWith("0X")) {
                return Integer.parseInt(texteValeur.substring(2), 16);
            }
            return Integer.parseInt(texteValeur);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Valeur numerique invalide dans la ligne : " + ligneSource);
        }
    }

    /**
     * Écrit l'encodage machine d'une instruction dans le flux d'octets.
     *
     * @param codeMachine le flux recevant les octets générés
     * @param instruction l'instruction à encoder
     */
    private void ecrireInstruction(ByteArrayOutputStream codeMachine, Instruction instruction) {
        switch (instruction.getTypeInstruction()) {
            case BREAK -> codeMachine.write(OPCODE_BREAK);

            case LOAD_CONSTANTE -> {
                OperandeRegistre registre = (OperandeRegistre) instruction.getOperandes().get(0);
                OperandeConstante constante = (OperandeConstante) instruction.getOperandes().get(1);

                codeMachine.write(OPCODE_LOAD_CONSTANTE);
                codeMachine.write(registre.getNumeroRegistre());
                codeMachine.write(constante.getValeur());
            }

            case LOAD_MEMOIRE -> {
                OperandeRegistre registre = (OperandeRegistre) instruction.getOperandes().get(0);
                OperandeAdresse adresse = (OperandeAdresse) instruction.getOperandes().get(1);

                codeMachine.write(OPCODE_LOAD_MEMOIRE);
                codeMachine.write(registre.getNumeroRegistre());
                ecrireAdresseSurDeuxOctets(codeMachine, adresse.getAdresse());
            }

            case STORE_MEMOIRE -> {
                OperandeRegistre registre = (OperandeRegistre) instruction.getOperandes().get(0);
                OperandeAdresse adresse = (OperandeAdresse) instruction.getOperandes().get(1);

                codeMachine.write(OPCODE_STORE_MEMOIRE);
                codeMachine.write(registre.getNumeroRegistre());
                ecrireAdresseSurDeuxOctets(codeMachine, adresse.getAdresse());
            }

            case ADD -> {
                OperandeRegistre premierRegistre = (OperandeRegistre) instruction.getOperandes().get(0);
                OperandeRegistre secondRegistre = (OperandeRegistre) instruction.getOperandes().get(1);
                OperandeRegistre registreDestination = (OperandeRegistre) instruction.getOperandes().get(2);

                // Format attendu par le CPU : opcode, registre1, registre2, registre destination.
                codeMachine.write(OPCODE_ADD);
                codeMachine.write(premierRegistre.getNumeroRegistre());
                codeMachine.write(secondRegistre.getNumeroRegistre());
                codeMachine.write(registreDestination.getNumeroRegistre());
            }

            case SUB -> {
                OperandeRegistre premierRegistre = (OperandeRegistre) instruction.getOperandes().get(0);
                OperandeRegistre secondRegistre = (OperandeRegistre) instruction.getOperandes().get(1);
                OperandeRegistre registreDestination = (OperandeRegistre) instruction.getOperandes().get(2);

                // Format attendu par le CPU : opcode, registre1, registre2, registre destination.
                codeMachine.write(OPCODE_SUB);
                codeMachine.write(premierRegistre.getNumeroRegistre());
                codeMachine.write(secondRegistre.getNumeroRegistre());
                codeMachine.write(registreDestination.getNumeroRegistre());
            }

            case MUL -> {
                OperandeRegistre premierRegistre = (OperandeRegistre) instruction.getOperandes().get(0);
                OperandeRegistre secondRegistre = (OperandeRegistre) instruction.getOperandes().get(1);
                OperandeRegistre registrePoidsFaible = (OperandeRegistre) instruction.getOperandes().get(2);
                OperandeRegistre registrePoidsFort = (OperandeRegistre) instruction.getOperandes().get(3);

                // Format attendu par le CPU : opcode, registre1, registre2, poids faible, poids fort.
                codeMachine.write(OPCODE_MUL);
                codeMachine.write(premierRegistre.getNumeroRegistre());
                codeMachine.write(secondRegistre.getNumeroRegistre());
                codeMachine.write(registrePoidsFaible.getNumeroRegistre());
                codeMachine.write(registrePoidsFort.getNumeroRegistre());
            }

            case DIV -> {
                OperandeRegistre premierRegistre = (OperandeRegistre) instruction.getOperandes().get(0);
                OperandeRegistre secondRegistre = (OperandeRegistre) instruction.getOperandes().get(1);
                OperandeRegistre registreQuotient = (OperandeRegistre) instruction.getOperandes().get(2);
                OperandeRegistre registreReste = (OperandeRegistre) instruction.getOperandes().get(3);

                // Format attendu par le CPU : opcode, registre1, registre2, quotient, reste.
                codeMachine.write(OPCODE_DIV);
                codeMachine.write(premierRegistre.getNumeroRegistre());
                codeMachine.write(secondRegistre.getNumeroRegistre());
                codeMachine.write(registreQuotient.getNumeroRegistre());
                codeMachine.write(registreReste.getNumeroRegistre());
            }

            case AND -> {
                OperandeRegistre premierRegistre = (OperandeRegistre) instruction.getOperandes().get(0);
                OperandeRegistre secondRegistre = (OperandeRegistre) instruction.getOperandes().get(1);
                OperandeRegistre registreDestination = (OperandeRegistre) instruction.getOperandes().get(2);

                // Format attendu par le CPU : opcode, registre1, registre2, registre destination.
                codeMachine.write(OPCODE_AND);
                codeMachine.write(premierRegistre.getNumeroRegistre());
                codeMachine.write(secondRegistre.getNumeroRegistre());
                codeMachine.write(registreDestination.getNumeroRegistre());
            }

            case OR -> {
                OperandeRegistre premierRegistre = (OperandeRegistre) instruction.getOperandes().get(0);
                OperandeRegistre secondRegistre = (OperandeRegistre) instruction.getOperandes().get(1);
                OperandeRegistre registreDestination = (OperandeRegistre) instruction.getOperandes().get(2);

                // Format attendu par le CPU : opcode, registre1, registre2, registre destination.
                codeMachine.write(OPCODE_OR);
                codeMachine.write(premierRegistre.getNumeroRegistre());
                codeMachine.write(secondRegistre.getNumeroRegistre());
                codeMachine.write(registreDestination.getNumeroRegistre());
            }

            case XOR -> {
                OperandeRegistre premierRegistre = (OperandeRegistre) instruction.getOperandes().get(0);
                OperandeRegistre secondRegistre = (OperandeRegistre) instruction.getOperandes().get(1);
                OperandeRegistre registreDestination = (OperandeRegistre) instruction.getOperandes().get(2);

                // Format attendu par le CPU : opcode, registre1, registre2, registre destination.
                codeMachine.write(OPCODE_XOR);
                codeMachine.write(premierRegistre.getNumeroRegistre());
                codeMachine.write(secondRegistre.getNumeroRegistre());
                codeMachine.write(registreDestination.getNumeroRegistre());
            }

            case JUMP -> {
                OperandeAdresse adresse = (OperandeAdresse) instruction.getOperandes().get(0);

                // Format attendu par le CPU : opcode, adresse sur deux octets.
                codeMachine.write(OPCODE_JUMP);
                ecrireAdresseSurDeuxOctets(codeMachine, adresse.getAdresse());
            }

            case BEQ -> {
                OperandeRegistre premierRegistre = (OperandeRegistre) instruction.getOperandes().get(0);
                OperandeRegistre secondRegistre = (OperandeRegistre) instruction.getOperandes().get(1);
                OperandeAdresse adresse = (OperandeAdresse) instruction.getOperandes().get(2);

                // Format attendu par le CPU : opcode, registre1, registre2, adresse sur deux octets.
                codeMachine.write(OPCODE_BEQ);
                codeMachine.write(premierRegistre.getNumeroRegistre());
                codeMachine.write(secondRegistre.getNumeroRegistre());
                ecrireAdresseSurDeuxOctets(codeMachine, adresse.getAdresse());
            }

            case BNE -> {
                OperandeRegistre premierRegistre = (OperandeRegistre) instruction.getOperandes().get(0);
                OperandeRegistre secondRegistre = (OperandeRegistre) instruction.getOperandes().get(1);
                OperandeAdresse adresse = (OperandeAdresse) instruction.getOperandes().get(2);

                // Format attendu par le CPU : opcode, registre1, registre2, adresse sur deux octets.
                codeMachine.write(OPCODE_BNE);
                codeMachine.write(premierRegistre.getNumeroRegistre());
                codeMachine.write(secondRegistre.getNumeroRegistre());
                ecrireAdresseSurDeuxOctets(codeMachine, adresse.getAdresse());
            }

            case LOAD_INDEXE -> {
                OperandeRegistre registre = (OperandeRegistre) instruction.getOperandes().get(0);
                OperandeAdresseIndexee adresse = (OperandeAdresseIndexee) instruction.getOperandes().get(1);

                // Format attendu par le CPU : opcode, registre destination, adresse sur deux octets, registre d'index.
                codeMachine.write(OPCODE_LOAD_INDEXE);
                codeMachine.write(registre.getNumeroRegistre());
                ecrireAdresseSurDeuxOctets(codeMachine, adresse.getAdresseBase());
                codeMachine.write(adresse.getRegistreIndex());
            }

            case STORE_INDEXE -> {
                OperandeRegistre registre = (OperandeRegistre) instruction.getOperandes().get(0);
                OperandeAdresseIndexee adresse = (OperandeAdresseIndexee) instruction.getOperandes().get(1);

                // Format attendu par le CPU : opcode, registre source, adresse sur deux octets, registre d'index.
                codeMachine.write(OPCODE_STORE_INDEXE);
                codeMachine.write(registre.getNumeroRegistre());
                ecrireAdresseSurDeuxOctets(codeMachine, adresse.getAdresseBase());
                codeMachine.write(adresse.getRegistreIndex());
            }

            case DATA -> {
                OperandeDonnees donnees = (OperandeDonnees) instruction.getOperandes().get(0);

                // La directive DATA ecrit directement les valeurs brutes dans le code machine.
                for (int valeur : donnees.getValeurs()) {
                    codeMachine.write(valeur);
                }
            }

            case STRING -> {
                OperandeChaine chaine = (OperandeChaine) instruction.getOperandes().get(0);

                // La directive STRING ecrit directement les octets UTF-8 de la chaine.
                byte[] bytes = chaine.getValeur().getBytes(StandardCharsets.UTF_8);
                codeMachine.writeBytes(bytes);
            }

            default -> throw new IllegalArgumentException(
                    "Generation du code machine non geree pour : " + instruction.getTypeInstruction()
            );
        }
    }

    /**
     * Écrit une adresse sur deux octets dans le flux de code machine.
     *
     * @param codeMachine le flux recevant les octets générés
     * @param adresse     l'adresse 16 bits à écrire
     */
    private void ecrireAdresseSurDeuxOctets(ByteArrayOutputStream codeMachine, int adresse) {
        // L'adresse 16 bits est ecrite en deux octets : poids fort puis poids faible.
        codeMachine.write((adresse >> 8) & 0xFF);
        codeMachine.write(adresse & 0xFF);
    }
}
