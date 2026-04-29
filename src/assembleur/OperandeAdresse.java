package assembleur;

/**
 * Représente un opérande de type adresse mémoire.
 * <p>
 * Utilisé pour spécifier une adresse absolue (par ex. @0x00FF)
 * lors des opérations de LOAD, STORE ou JUMP.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class OperandeAdresse extends Operande {
    private final int adresse;

    /**
     * Construit un opérande de type adresse.
     *
     * @param adresse l'adresse mémoire absolue (sur 16 bits)
     */
    public OperandeAdresse(int adresse) {
        super(TypeOperande.ADRESSE);
        this.adresse = adresse;
    }

    /**
     * Retourne l'adresse mémoire associée.
     *
     * @return l'adresse entière
     */
    public int getAdresse() {
        return adresse;
    }
}
