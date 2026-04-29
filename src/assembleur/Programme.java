package assembleur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente un programme complet composé d'une suite d'instructions.
 * <p>
 * Un objet {@code Programme} encapsule l'ensemble du code analysé
 * par l'assembleur avant son chargement dans la mémoire du processeur.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class Programme {
    private final List<Instruction> instructions;

    /**
     * Construit un nouveau programme vide.
     */
    public Programme() {
        this.instructions = new ArrayList<>();
    }

    /**
     * Ajoute une instruction à la fin du programme.
     *
     * @param instruction l'{@link Instruction} à ajouter à la séquence
     */
    public void ajouterInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    /**
     * Retourne la liste des instructions constituant le programme.
     * La liste retournée est non modifiable pour garantir l'intégrité du programme.
     *
     * @return une {@link List} non modifiable contenant les instructions
     */
    public List<Instruction> getInstructions() {
        return Collections.unmodifiableList(instructions);
    }
}
