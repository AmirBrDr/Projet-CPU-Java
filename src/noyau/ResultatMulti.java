package noyau;

/**
 * Encapsule le résultat d'une multiplication effectuée par l'ALU.
 * <p>
 * Le résultat de la multiplication de deux octets pouvant nécessiter
 * jusqu'à 16 bits, cette classe sépare le résultat en deux octets :
 * le poids faible et le poids fort.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class ResultatMulti {
    private byte poidsFaible;
    private byte poidsFort;

    /**
     * Construit un nouveau résultat de multiplication.
     *
     * @param poidsFaible l'octet de poids faible du résultat
     * @param poidsFort   l'octet de poids fort du résultat
     */
    public ResultatMulti(byte poidsFaible, byte poidsFort) {
        this.poidsFaible = poidsFaible;
        this.poidsFort = poidsFort;
    }

    /**
     * Retourne l'octet de poids faible.
     *
     * @return le poids faible (octet)
     */
    public byte getPoidsFaible() {
        return poidsFaible;
    }

    /**
     * Retourne l'octet de poids fort.
     *
     * @return le poids fort (octet)
     */
    public byte getPoidsFort() {
        return poidsFort;
    }
}
