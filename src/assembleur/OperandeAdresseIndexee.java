package assembleur;

/**
 * Représente un opérande d'adresse indexée (par ex. @0x00FF, r1).
 * <p>
 * L'adresse réelle sera calculée à l'exécution en additionnant l'adresse
 * de base fournie et la valeur contenue dans le registre d'index.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class OperandeAdresseIndexee extends Operande {
    private final int adresseBase;
    private final int registreIndex;

    /**
     * Construit un opérande d'adresse indexée.
     *
     * @param adresseBase   l'adresse de départ (sur 16 bits)
     * @param registreIndex le numéro du registre contenant l'index (0 à 15)
     */
    public OperandeAdresseIndexee(int adresseBase, int registreIndex) {
        super(TypeOperande.ADRESSE_INDEXEE);
        this.adresseBase = adresseBase;
        this.registreIndex = registreIndex;
    }

    /**
     * Retourne l'adresse de base.
     *
     * @return la valeur de l'adresse de base
     */
    public int getAdresseBase() {
        return adresseBase;
    }

    /**
     * Retourne le numéro du registre utilisé comme index.
     *
     * @return le numéro du registre (0 à 15)
     */
    public int getRegistreIndex() {
        return registreIndex;
    }
}
