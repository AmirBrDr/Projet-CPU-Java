package assembleur;

/**
 * Représente un opérande de type constante entière (valeur immédiate).
 * <p>
 * Utilisé principalement avec l'instruction LOAD_CONSTANTE.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class OperandeConstante extends Operande {
    private final int valeur;

    /**
     * Construit un opérande constant.
     *
     * @param valeur la valeur entière de la constante
     */
    public OperandeConstante(int valeur) {
        super(TypeOperande.CONSTANTE);
        this.valeur = valeur;
    }

    /**
     * Retourne la valeur de la constante.
     *
     * @return la valeur numérique stockée
     */
    public int getValeur() {
        return valeur;
    }
}
