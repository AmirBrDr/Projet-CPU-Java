package noyau;

/**
 * Encapsule le résultat d'une division effectuée par l'ALU.
 * <p>
 * Ce résultat est composé de deux octets : le quotient et le reste.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class ResultatDiv {
    private byte quotient;
    private byte reste;

    /**
     * Construit un nouveau résultat de division.
     *
     * @param quotient la valeur du quotient
     * @param reste    la valeur du reste
     */
    public ResultatDiv(byte quotient, byte reste) {
        this.quotient = quotient;
        this.reste = reste;
    }

    /**
     * Retourne le quotient de la division.
     *
     * @return le quotient (octet)
     */
    public byte getQuotient() {
        return quotient;
    }

    /**
     * Retourne le reste de la division.
     *
     * @return le reste (octet)
     */
    public byte getReste() {
        return reste;
    }
}
