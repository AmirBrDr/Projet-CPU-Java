package noyau;

/**
 * Représente la mémoire principale du processeur.
 * <p>
 * La mémoire a une taille de 64 Ko (65536 octets) et permet de stocker
 * les instructions et les données du programme.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class Memoire {
    private final int taille = 65536;
    private byte[] contenu;

    /**
     * Construit une nouvelle instance de mémoire de 64 Ko, initialisée à zéro.
     */
    public Memoire() {
        this.contenu = new byte[taille];
    }

    /**
     * Lit un octet à l'adresse spécifiée.
     *
     * @param address l'adresse mémoire à lire (entre 0 et 65535)
     * @return la valeur de l'octet stockée à cette adresse
     */
    public byte lireOctet(int address){
        return contenu[address];
    }

    /**
     * Écrit la valeur d'un octet à l'adresse spécifiée.
     * Si l'adresse n'est pas valide, affiche une erreur sans modifier la mémoire.
     *
     * @param address l'adresse mémoire où écrire
     * @param valeur  la valeur d'un octet à écrire
     */
    public void ecrireOctet(int address, byte valeur){
        if (estAddressValide(address))
            contenu[address] = valeur;
        else System.out.println("OutOfMemoryError");
    }

    /**
     * Lit un bloc d'octets contigus en mémoire.
     *
     * @param addressDebut l'adresse du premier octet à lire
     * @param taille       le nombre d'octets à lire
     * @return un tableau d'octets contenant les données lues
     */
    public byte[] lireBloc(int addressDebut, int taille){
        byte[] res = new byte[taille];
        int address = addressDebut;
        for (int i=0; i<taille; i++){
            res[i] = contenu[address];
            address+=1;
        }
        return res;
    }

    /**
     * Écrit un bloc d'octets contigus en mémoire.
     *
     * @param addressDebut l'adresse du premier octet où écrire
     * @param donnees      le tableau d'octets contenant les données à écrire
     */
    public void ecrireBloc(int addressDebut, byte[] donnees){
        int address = addressDebut;
        for (byte valeur : donnees) {
            ecrireOctet(address,valeur);
            address += 1;
        }
    }

    /**
     * Vide la mémoire en la réinitialisant complètement à zéro.
     */
    public void vider(){
        contenu = new byte[taille];
    }

    /**
     * Vérifie si l'adresse fournie se trouve dans les limites de la mémoire.
     *
     * @param address l'adresse mémoire à vérifier
     * @return <code>true</code> si l'adresse est valide, <code>false</code> sinon
     */
    public boolean estAddressValide(int address){
        return (address<taille && address>=0);
    }
}
