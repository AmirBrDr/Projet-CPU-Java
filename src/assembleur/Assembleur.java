package assembleur;

import java.util.List;

public class Assembleur {
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

        throw new IllegalArgumentException("Instruction non reconnue : " + ligneSource);
    }

    public byte[] genererCodeMachine(Programme programme) {
        throw new UnsupportedOperationException("genererCodeMachine(Programme) n'est pas encore implemente");
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
}
