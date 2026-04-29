package noyau;

/**
 * Représente un registre individuel au sein du processeur.
 * <p>
 * Un registre stocke une valeur sur 8 bits (un octet) et possède
 * un numéro d'identification unique (généralement de 0 à 15).
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class Registre {
    private int numero;
    private byte valeur = 0;

    /**
     * Construit un nouveau registre.
     *
     * @param numero le numéro d'identification du registre
     */
    public Registre(int numero) {
        this.numero = numero;
    }

    /**
     * Retourne le numéro du registre.
     *
     * @return le numéro d'identification
     */
    public int getNumero() {
        return numero;
    }

    /**
     * Lit la valeur actuellement stockée dans le registre.
     *
     * @return la valeur (octet)
     */
    public byte lireValeur(){
        return valeur;
    }

    /**
     * Écrit une nouvelle valeur dans le registre.
     *
     * @param valeur la nouvelle valeur à stocker (octet)
     */
    public void ecrireValeur(byte valeur){
        this.valeur = valeur;
    }

    /**
     * Réinitialise la valeur du registre à zéro.
     */
    public void reinitialiser(){
        valeur = 0;
    }
}
