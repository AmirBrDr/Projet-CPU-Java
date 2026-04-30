package assembleur;

import java.io.ByteArrayOutputStream;
import java.util.List;

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

    public Programme assembler(String source) {
        // Verification de l'entree avant de commencer l'assemblage.
        if (source == null) {
            throw new IllegalArgumentException("Le code source ne doit pas etre nul");
        }

        Programme programme = new Programme();
        String[] lignes = source.split("\\R");

        // Chaque ligne non vide est traduite puis ajoutee au programme final.
        for (String ligne : lignes) {
            String ligneNettoyee = ligne.trim();
            if (ligneNettoyee.isEmpty()) {
                continue;
            }

            Instruction instruction = traduireLigne(ligne);
            programme.ajouterInstruction(instruction);
        }

        return programme;
    }

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

        throw new IllegalArgumentException("Instruction non reconnue : " + ligneSource);
    }

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

    private Instruction traduireInstructionLoad(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "load" pour ne garder que les operandes.
        String reste = ligneNettoyee.substring(4).trim();
        String[] morceaux = reste.split(",");

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

    private Instruction traduireInstructionStore(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "store" pour ne garder que les operandes.
        String reste = ligneNettoyee.substring(5).trim();
        String[] morceaux = reste.split(",");

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

    private Instruction traduireInstructionAdd(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "add" pour ne garder que les trois registres.
        String reste = ligneNettoyee.substring(3).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length != 3) {
            throw new IllegalArgumentException("Syntaxe add invalide : " + ligneSource);
        }

        // L'instruction ADD utilise deux registres source et un registre destination.
        OperandeRegistre premierRegistre = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        OperandeRegistre secondRegistre = lireOperandeRegistre(morceaux[1].trim(), ligneSource);
        OperandeRegistre registreDestination = lireOperandeRegistre(morceaux[2].trim(), ligneSource);

        return new Instruction(
                TypeInstruction.ADD,
                List.of(premierRegistre, secondRegistre, registreDestination),
                ligneSource
        );
    }

    private Instruction traduireInstructionSub(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "sub" pour ne garder que les trois registres.
        String reste = ligneNettoyee.substring(3).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length != 3) {
            throw new IllegalArgumentException("Syntaxe sub invalide : " + ligneSource);
        }

        // L'instruction SUB utilise deux registres source et un registre destination.
        OperandeRegistre premierRegistre = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        OperandeRegistre secondRegistre = lireOperandeRegistre(morceaux[1].trim(), ligneSource);
        OperandeRegistre registreDestination = lireOperandeRegistre(morceaux[2].trim(), ligneSource);

        return new Instruction(
                TypeInstruction.SUB,
                List.of(premierRegistre, secondRegistre, registreDestination),
                ligneSource
        );
    }

    private Instruction traduireInstructionMul(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "mul" pour ne garder que les trois registres.
        String reste = ligneNettoyee.substring(3).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length != 3) {
            throw new IllegalArgumentException("Syntaxe mul invalide : " + ligneSource);
        }

        // L'instruction MUL utilise deux registres source et un registre destination.
        OperandeRegistre premierRegistre = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        OperandeRegistre secondRegistre = lireOperandeRegistre(morceaux[1].trim(), ligneSource);
        OperandeRegistre registreDestination = lireOperandeRegistre(morceaux[2].trim(), ligneSource);

        return new Instruction(
                TypeInstruction.MUL,
                List.of(premierRegistre, secondRegistre, registreDestination),
                ligneSource
        );
    }

    private Instruction traduireInstructionDiv(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "div" pour ne garder que les trois registres.
        String reste = ligneNettoyee.substring(3).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length != 3) {
            throw new IllegalArgumentException("Syntaxe div invalide : " + ligneSource);
        }

        // L'instruction DIV utilise deux registres source et un registre destination.
        OperandeRegistre premierRegistre = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        OperandeRegistre secondRegistre = lireOperandeRegistre(morceaux[1].trim(), ligneSource);
        OperandeRegistre registreDestination = lireOperandeRegistre(morceaux[2].trim(), ligneSource);

        return new Instruction(
                TypeInstruction.DIV,
                List.of(premierRegistre, secondRegistre, registreDestination),
                ligneSource
        );
    }

    private Instruction traduireInstructionAnd(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "and" pour ne garder que les trois registres.
        String reste = ligneNettoyee.substring(3).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length != 3) {
            throw new IllegalArgumentException("Syntaxe and invalide : " + ligneSource);
        }

        // L'instruction AND utilise deux registres source et un registre destination.
        OperandeRegistre premierRegistre = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        OperandeRegistre secondRegistre = lireOperandeRegistre(morceaux[1].trim(), ligneSource);
        OperandeRegistre registreDestination = lireOperandeRegistre(morceaux[2].trim(), ligneSource);

        return new Instruction(
                TypeInstruction.AND,
                List.of(premierRegistre, secondRegistre, registreDestination),
                ligneSource
        );
    }

    private Instruction traduireInstructionOr(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "or" pour ne garder que les trois registres.
        String reste = ligneNettoyee.substring(2).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length != 3) {
            throw new IllegalArgumentException("Syntaxe or invalide : " + ligneSource);
        }

        // L'instruction OR utilise deux registres source et un registre destination.
        OperandeRegistre premierRegistre = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        OperandeRegistre secondRegistre = lireOperandeRegistre(morceaux[1].trim(), ligneSource);
        OperandeRegistre registreDestination = lireOperandeRegistre(morceaux[2].trim(), ligneSource);

        return new Instruction(
                TypeInstruction.OR,
                List.of(premierRegistre, secondRegistre, registreDestination),
                ligneSource
        );
    }

    private Instruction traduireInstructionXor(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "xor" pour ne garder que les trois registres.
        String reste = ligneNettoyee.substring(3).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length != 3) {
            throw new IllegalArgumentException("Syntaxe xor invalide : " + ligneSource);
        }

        // L'instruction XOR utilise deux registres source et un registre destination.
        OperandeRegistre premierRegistre = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        OperandeRegistre secondRegistre = lireOperandeRegistre(morceaux[1].trim(), ligneSource);
        OperandeRegistre registreDestination = lireOperandeRegistre(morceaux[2].trim(), ligneSource);

        return new Instruction(
                TypeInstruction.XOR,
                List.of(premierRegistre, secondRegistre, registreDestination),
                ligneSource
        );
    }

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
                OperandeRegistre registreDestination = (OperandeRegistre) instruction.getOperandes().get(2);

                // Format attendu par le CPU : opcode, registre1, registre2, registre destination.
                codeMachine.write(OPCODE_MUL);
                codeMachine.write(premierRegistre.getNumeroRegistre());
                codeMachine.write(secondRegistre.getNumeroRegistre());
                codeMachine.write(registreDestination.getNumeroRegistre());
            }

            case DIV -> {
                OperandeRegistre premierRegistre = (OperandeRegistre) instruction.getOperandes().get(0);
                OperandeRegistre secondRegistre = (OperandeRegistre) instruction.getOperandes().get(1);
                OperandeRegistre registreDestination = (OperandeRegistre) instruction.getOperandes().get(2);

                // Format attendu par le CPU : opcode, registre1, registre2, registre destination.
                codeMachine.write(OPCODE_DIV);
                codeMachine.write(premierRegistre.getNumeroRegistre());
                codeMachine.write(secondRegistre.getNumeroRegistre());
                codeMachine.write(registreDestination.getNumeroRegistre());
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

            default -> throw new IllegalArgumentException(
                    "Generation du code machine non geree pour : " + instruction.getTypeInstruction()
            );
        }
    }

    private void ecrireAdresseSurDeuxOctets(ByteArrayOutputStream codeMachine, int adresse) {
        // L'adresse 16 bits est ecrite en deux octets : poids fort puis poids faible.
        codeMachine.write((adresse >> 8) & 0xFF);
        codeMachine.write(adresse & 0xFF);
    }
}
