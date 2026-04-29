package assembleur;

/**
 * Représente un opérande de type registre.
 * <p>
 * Indique le numéro du registre (de 0 à 15) à lire ou écrire
 * lors d'une instruction (par ex. r0, r5, etc.).
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class OperandeRegistre extends Operande {
    private final int numeroRegistre;

    /**
     * Construit un opérande de registre.
     *
     * @param numeroRegistre le numéro du registre (de 0 à 15)
     */
    public OperandeRegistre(int numeroRegistre) {
        super(TypeOperande.REGISTRE);
        this.numeroRegistre = numeroRegistre;
    }

    /**
     * Retourne le numéro de ce registre.
     *
     * @return le numéro de registre (entier)
     */
    public int getNumeroRegistre() {
        return numeroRegistre;
    }
}
