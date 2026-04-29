package assembleur;

/**
 * Représente un opérande de type données brutes (tableau d'entiers).
 * <p>
 * Utilisé avec la directive DATA pour insérer une suite de valeurs
 * octets directement en mémoire.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class OperandeDonnees extends Operande {
    private final int[] valeurs;

    /**
     * Construit un opérande de données.
     *
     * @param valeurs le tableau contenant les valeurs entières
     */
    public OperandeDonnees(int[] valeurs) {
        super(TypeOperande.DONNEE);
        this.valeurs = valeurs;
    }

    /**
     * Retourne le tableau des valeurs.
     *
     * @return un tableau d'entiers
     */
    public int[] getValeurs() {
        return valeurs;
    }
}
