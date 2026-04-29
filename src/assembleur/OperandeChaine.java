package assembleur;

/**
 * Représente un opérande de type chaîne de caractères.
 * <p>
 * Utilisé avec la directive STRING pour insérer une chaîne encodée
 * en mémoire.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class OperandeChaine extends Operande {
    private final String valeur;

    /**
     * Construit un opérande de chaîne de caractères.
     *
     * @param valeur la chaîne de caractères (sans les guillemets)
     */
    public OperandeChaine(String valeur) {
        super(TypeOperande.CHAINE);
        this.valeur = valeur;
    }

    /**
     * Retourne la valeur de la chaîne.
     *
     * @return la chaîne de caractères
     */
    public String getValeur() {
        return valeur;
    }
}
