package assembleur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente une instruction abstraite décodée par l'assembleur.
 * <p>
 * Chaque instruction est caractérisée par un type d'opération (add, load, jump, etc.)
 * et par une liste de ses opérandes. Elle conserve également la ligne de code source
 * originelle pour faciliter le débogage.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class Instruction {
    private final TypeInstruction typeInstruction;
    private final List<Operande> operandes;
    private final String ligneSource;

    /**
     * Construit une nouvelle instruction décodée.
     *
     * @param typeInstruction le type de l'opération (issu de {@link TypeInstruction})
     * @param operandes       la liste des opérandes associés à l'instruction
     * @param ligneSource     le texte de la ligne source originale
     */
    public Instruction(TypeInstruction typeInstruction, List<Operande> operandes, String ligneSource) {
        this.typeInstruction = typeInstruction;
        this.operandes = new ArrayList<>(operandes);
        this.ligneSource = ligneSource;
    }

    /**
     * Récupère le type de l'instruction.
     *
     * @return le {@link TypeInstruction} identifiant l'opération
     */
    public TypeInstruction getTypeInstruction() {
        return typeInstruction;
    }

    /**
     * Récupère la liste des opérandes de l'instruction.
     *
     * @return une {@link List} non modifiable des instances de {@link Operande}
     */
    public List<Operande> getOperandes() {
        return Collections.unmodifiableList(operandes);
    }

    /**
     * Récupère la ligne de code source brute ayant généré cette instruction.
     *
     * @return une chaîne de caractères contenant la ligne source
     */
    public String getLigneSource() {
        return ligneSource;
    }
}
