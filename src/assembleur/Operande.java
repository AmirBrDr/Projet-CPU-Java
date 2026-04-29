package assembleur;

/**
 * Représente la classe de base abstraite pour tous les types d'opérandes.
 * <p>
 * Les opérandes peuvent être des registres, des adresses mémoires, des constantes, etc.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public abstract class Operande {
    private final TypeOperande type;

    /**
     * Construit un nouvel opérande de base.
     *
     * @param type le type de l'opérande
     */
    protected Operande(TypeOperande type) {
        this.type = type;
    }

    /**
     * Récupère le type de cet opérande.
     *
     * @return le type défini par {@link TypeOperande}
     */
    public TypeOperande getType() {
        return type;
    }
}
