package assembleur;

import java.util.List;

/**
 * Traduit le code source en instructions compréhensibles par le processeur.
 * <p>
 * L'assembleur lit un texte ligne par ligne, analyse la syntaxe, et génère un
 * objet {@link Programme} contenant des {@link Instruction} décodées. Il peut 
 * également générer le code machine sous forme de tableau d'octets.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class Assembleur {
    
    /**
     * Compile un code source complet en un programme exécutable.
     * Ignore les lignes vides et traduit chaque ligne séquentiellement.
     *
     * @param source le texte complet du code source en assembleur
     * @return le {@link Programme} contenant toutes les instructions traduites
     * @throws IllegalArgumentException si le code source est null ou contient des erreurs de syntaxe
     */
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

    /**
     * Traduit une seule ligne de code source en une {@link Instruction}.
     * Identifie l'opération (add, load, jump, etc.) et ses opérandes (registres,
     * adresses, valeurs constantes, chaînes).
     *
     * @param ligneSource la ligne de code source brute (ex: "load r0, 5")
     * @return l'{@link Instruction} correspondant à la ligne
     * @throws IllegalArgumentException si la ligne est nulle, vide, ou si l'instruction n'est pas reconnue
     */
    public Instruction traduireLigne(String ligneSource) {
        if (ligneSource == null) {
            throw new IllegalArgumentException("La ligne source ne doit pas etre nulle");
        }

        String ligneNettoyee = ligneSource.trim();
        if (ligneNettoyee.isEmpty()) {
            throw new IllegalArgumentException("La ligne source ne doit pas etre vide");
        }

        String ligneBas = ligneNettoyee.toLowerCase();

        if (ligneBas.equalsIgnoreCase("break")) {
            return new Instruction(TypeInstruction.BREAK, List.of(), ligneSource);
        }

        if (ligneBas.startsWith("load ")) return traduireInstructionLoad(ligneNettoyee, ligneSource);
        if (ligneBas.startsWith("store ")) return traduireInstructionStore(ligneNettoyee, ligneSource);
        
        if (ligneBas.startsWith("add ") || ligneBas.startsWith("sub ") || ligneBas.startsWith("mul ") || 
            ligneBas.startsWith("div ") || ligneBas.startsWith("and ") || ligneBas.startsWith("or ") || 
            ligneBas.startsWith("xor ")) {
            return traduireInstructionArithmetique(ligneNettoyee, ligneSource);
        }
        
        if (ligneBas.startsWith("jump ")) return traduireInstructionJump(ligneNettoyee, ligneSource);
        if (ligneBas.startsWith("beq ") || ligneBas.startsWith("bne ")) return traduireInstructionBranch(ligneNettoyee, ligneSource);
        
        if (ligneBas.startsWith("data ")) return traduireInstructionData(ligneNettoyee, ligneSource);
        if (ligneBas.startsWith("string ")) return traduireInstructionString(ligneNettoyee, ligneSource);

        throw new IllegalArgumentException("Instruction non reconnue : " + ligneSource);
    }

    /**
     * Génère le code binaire (machine) à partir d'un programme assemblé.
     * Le code généré correspond au format d'exécution attendu par la mémoire
     * du {@link noyau.CPU}.
     *
     * @param programme le {@link Programme} contenant les instructions
     * @return un tableau d'octets (`byte[]`) représentant le code machine binaire
     */
    public byte[] genererCodeMachine(Programme programme) {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        for (Instruction instruction : programme.getInstructions()) {
            switch (instruction.getTypeInstruction()) {
                case BREAK -> out.write(0);
                case LOAD_CONSTANTE -> {
                    out.write(1);
                    out.write(((OperandeRegistre) instruction.getOperandes().get(0)).getNumeroRegistre());
                    out.write(((OperandeConstante) instruction.getOperandes().get(1)).getValeur());
                }
                case LOAD_MEMOIRE -> {
                    out.write(2);
                    out.write(((OperandeRegistre) instruction.getOperandes().get(0)).getNumeroRegistre());
                    int adr = ((OperandeAdresse) instruction.getOperandes().get(1)).getAdresse();
                    out.write((adr >> 8) & 0xFF);
                    out.write(adr & 0xFF);
                }
                case STORE_MEMOIRE -> {
                    out.write(3);
                    out.write(((OperandeRegistre) instruction.getOperandes().get(0)).getNumeroRegistre());
                    int adr = ((OperandeAdresse) instruction.getOperandes().get(1)).getAdresse();
                    out.write((adr >> 8) & 0xFF);
                    out.write(adr & 0xFF);
                }
                case ADD -> { out.write(4); ecrireReg3(out, instruction); }
                case SUB -> { out.write(5); ecrireReg3(out, instruction); }
                case MUL -> { out.write(6); ecrireReg3(out, instruction); }
                case DIV -> { out.write(7); ecrireReg3(out, instruction); }
                case AND -> { out.write(8); ecrireReg3(out, instruction); }
                case OR -> { out.write(9); ecrireReg3(out, instruction); }
                case XOR -> { out.write(10); ecrireReg3(out, instruction); }
                case JUMP -> {
                    out.write(11);
                    int adr = ((OperandeAdresse) instruction.getOperandes().get(0)).getAdresse();
                    out.write((adr >> 8) & 0xFF);
                    out.write(adr & 0xFF);
                }
                case BEQ -> {
                    out.write(12);
                    out.write(((OperandeRegistre) instruction.getOperandes().get(0)).getNumeroRegistre());
                    out.write(((OperandeRegistre) instruction.getOperandes().get(1)).getNumeroRegistre());
                    int adr = ((OperandeAdresse) instruction.getOperandes().get(2)).getAdresse();
                    out.write((adr >> 8) & 0xFF);
                    out.write(adr & 0xFF);
                }
                case BNE -> {
                    out.write(13);
                    out.write(((OperandeRegistre) instruction.getOperandes().get(0)).getNumeroRegistre());
                    out.write(((OperandeRegistre) instruction.getOperandes().get(1)).getNumeroRegistre());
                    int adr = ((OperandeAdresse) instruction.getOperandes().get(2)).getAdresse();
                    out.write((adr >> 8) & 0xFF);
                    out.write(adr & 0xFF);
                }
                case LOAD_INDEXE -> {
                    out.write(14);
                    out.write(((OperandeRegistre) instruction.getOperandes().get(0)).getNumeroRegistre());
                    OperandeAdresseIndexee adr = (OperandeAdresseIndexee) instruction.getOperandes().get(1);
                    out.write((adr.getAdresseBase() >> 8) & 0xFF);
                    out.write(adr.getAdresseBase() & 0xFF);
                    out.write(adr.getRegistreIndex());
                }
                case STORE_INDEXE -> {
                    out.write(15);
                    out.write(((OperandeRegistre) instruction.getOperandes().get(0)).getNumeroRegistre());
                    OperandeAdresseIndexee adr = (OperandeAdresseIndexee) instruction.getOperandes().get(1);
                    out.write((adr.getAdresseBase() >> 8) & 0xFF);
                    out.write(adr.getAdresseBase() & 0xFF);
                    out.write(adr.getRegistreIndex());
                }
                case DATA -> {
                    int[] valeurs = ((OperandeDonnees) instruction.getOperandes().get(0)).getValeurs();
                    for (int val : valeurs) out.write(val);
                }
                case STRING -> {
                    byte[] bytes = ((OperandeChaine) instruction.getOperandes().get(0)).getValeur().getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    for (byte b : bytes) out.write(b);
                }
            }
        }
        return out.toByteArray();
    }

    private void ecrireReg3(java.io.ByteArrayOutputStream out, Instruction instruction) {
        out.write(((OperandeRegistre) instruction.getOperandes().get(0)).getNumeroRegistre());
        out.write(((OperandeRegistre) instruction.getOperandes().get(1)).getNumeroRegistre());
        out.write(((OperandeRegistre) instruction.getOperandes().get(2)).getNumeroRegistre());
    }

    private Instruction traduireInstructionLoad(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "load" pour ne garder que les operandes.
        String reste = ligneNettoyee.substring(4).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length == 3) {
            OperandeRegistre registreDestination = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
            String secondOperande = morceaux[1].trim();
            if (!secondOperande.startsWith("@")) throw new IllegalArgumentException("Adresse memoire invalide : " + ligneSource);
            int adresse = lireValeurNumerique(secondOperande.substring(1).trim(), ligneSource);
            OperandeRegistre registreIndex = lireOperandeRegistre(morceaux[2].trim(), ligneSource);
            return new Instruction(
                    TypeInstruction.LOAD_INDEXE,
                    List.of(registreDestination, new OperandeAdresseIndexee(adresse, registreIndex.getNumeroRegistre())),
                    ligneSource
            );
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

    private Instruction traduireInstructionStore(String ligneNettoyee, String ligneSource) {
        // On retire le mot-cle "store" pour ne garder que les operandes.
        String reste = ligneNettoyee.substring(5).trim();
        String[] morceaux = reste.split(",");

        if (morceaux.length == 3) {
            OperandeRegistre registreSource = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
            String secondOperande = morceaux[1].trim();
            if (!secondOperande.startsWith("@")) throw new IllegalArgumentException("Adresse memoire invalide : " + ligneSource);
            int adresse = lireValeurNumerique(secondOperande.substring(1).trim(), ligneSource);
            OperandeRegistre registreIndex = lireOperandeRegistre(morceaux[2].trim(), ligneSource);
            return new Instruction(
                    TypeInstruction.STORE_INDEXE,
                    List.of(registreSource, new OperandeAdresseIndexee(adresse, registreIndex.getNumeroRegistre())),
                    ligneSource
            );
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

    private Instruction traduireInstructionArithmetique(String ligneNettoyee, String ligneSource) {
        String[] parts = ligneNettoyee.split("\\s+", 2);
        TypeInstruction type = TypeInstruction.valueOf(parts[0].toUpperCase());
        String[] morceaux = parts[1].split(",");
        if (morceaux.length != 3) throw new IllegalArgumentException("Syntaxe arithmetique invalide : " + ligneSource);
        OperandeRegistre r1 = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        OperandeRegistre r2 = lireOperandeRegistre(morceaux[1].trim(), ligneSource);
        OperandeRegistre rd = lireOperandeRegistre(morceaux[2].trim(), ligneSource);
        return new Instruction(type, List.of(r1, r2, rd), ligneSource);
    }

    private Instruction traduireInstructionJump(String ligneNettoyee, String ligneSource) {
        String adrToken = ligneNettoyee.substring(5).trim();
        if (!adrToken.startsWith("@")) throw new IllegalArgumentException("Syntaxe jump invalide : " + ligneSource);
        int adr = lireValeurNumerique(adrToken.substring(1).trim(), ligneSource);
        return new Instruction(TypeInstruction.JUMP, List.of(new OperandeAdresse(adr)), ligneSource);
    }

    private Instruction traduireInstructionBranch(String ligneNettoyee, String ligneSource) {
        String[] parts = ligneNettoyee.split("\\s+", 2);
        TypeInstruction type = TypeInstruction.valueOf(parts[0].toUpperCase());
        String[] morceaux = parts[1].split(",");
        if (morceaux.length != 3) throw new IllegalArgumentException("Syntaxe branch invalide : " + ligneSource);
        OperandeRegistre r1 = lireOperandeRegistre(morceaux[0].trim(), ligneSource);
        OperandeRegistre r2 = lireOperandeRegistre(morceaux[1].trim(), ligneSource);
        String adrToken = morceaux[2].trim();
        if (!adrToken.startsWith("@")) throw new IllegalArgumentException("Adresse invalide : " + ligneSource);
        int adr = lireValeurNumerique(adrToken.substring(1).trim(), ligneSource);
        return new Instruction(type, List.of(r1, r2, new OperandeAdresse(adr)), ligneSource);
    }

    private Instruction traduireInstructionData(String ligneNettoyee, String ligneSource) {
        String reste = ligneNettoyee.substring(5).trim();
        String[] morceaux = reste.split(",");
        int[] valeurs = new int[morceaux.length];
        for (int i = 0; i < morceaux.length; i++) {
            valeurs[i] = lireValeurNumerique(morceaux[i].trim(), ligneSource);
        }
        return new Instruction(TypeInstruction.DATA, List.of(new OperandeDonnees(valeurs)), ligneSource);
    }

    private Instruction traduireInstructionString(String ligneNettoyee, String ligneSource) {
        String reste = ligneNettoyee.substring(7).trim();
        if (!reste.startsWith("\"") || !reste.endsWith("\"")) {
            throw new IllegalArgumentException("Syntaxe string invalide : " + ligneSource);
        }
        String valeur = reste.substring(1, reste.length() - 1);
        return new Instruction(TypeInstruction.STRING, List.of(new OperandeChaine(valeur)), ligneSource);
    }
}
