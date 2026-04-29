package noyau;

/**
 * Représente l'Unité Arithmétique et Logique (UAL/ALU) du processeur.
 * <p>
 * Cette classe fournit les opérations arithmétiques de base (addition,
 * soustraction, multiplication, division) ainsi que les opérations logiques
 * bit à bit (ET, OU, OU EXCLUSIF) sur des octets.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class ALU {
    
    /**
     * Construit une nouvelle instance de l'unité arithmétique et logique.
     */
    public ALU() {
    }

    /**
     * Additionne deux octets.
     *
     * @param a le premier opérande
     * @param b le deuxième opérande
     * @return la somme des deux opérandes
     */
    public byte additionner(byte a, byte b){
        return (byte) (a+b);
    }

    /**
     * Soustrait un octet d'un autre.
     *
     * @param a le premier opérande (minuende)
     * @param b le deuxième opérande (diminuende)
     * @return le résultat de la soustraction (a - b)
     */
    public byte soustraire(byte a,byte b){
        return (byte) (a-b);
    }

    /**
     * Multiplie deux octets et retourne le résultat sur deux octets (poids fort et poids faible).
     *
     * @param a le multiplicande
     * @param b le multiplicateur
     * @return une instance de {@link ResultatMulti} contenant les octets de poids faible et fort
     */
    public ResultatMulti multiplier(byte a,byte b){
        // 1. On convertit en int pour faire le calcul sur 32 bits sans perte
        // On utilise & 0xFF pour traiter les bytes comme des nombres non-signés (0-255)
        int resInt = a*b;
        // 2. Extraction du poids faible (les 8 bits de droite)
        byte poidsFaible = (byte) (resInt & 0xFF);
        // 3. Extraction du poids fort (les 8 bits de gauche)
        // On décale de 8 bits vers la droite
        byte poidsFort =  (byte) ((resInt >> 8)& 0xFF);

        return new ResultatMulti(poidsFaible,poidsFort);
    }

    /**
     * Divise un octet par un autre et retourne le quotient et le reste.
     *
     * @param a le dividende
     * @param b le diviseur
     * @return une instance de {@link ResultatDiv} contenant le quotient et le reste
     * @throws ArithmeticException si le diviseur <code>b</code> est égal à zéro
     */
    public ResultatDiv diviser(byte a,byte b){
        if (b==0) throw new ArithmeticException("Division par zéro");

        byte q = (byte) (a/b);
        byte r = (byte) (a%b);

        return new ResultatDiv(q,r);
    }

    /**
     * Effectue un ET logique (AND) bit à bit entre deux octets.
     *
     * @param a le premier opérande
     * @param b le deuxième opérande
     * @return le résultat du ET logique
     */
    public byte estBinaire(byte a,byte b){
        return (byte) (a & b);
    }

    /**
     * Effectue un OU logique (OR) bit à bit entre deux octets.
     *
     * @param a le premier opérande
     * @param b le deuxième opérande
     * @return le résultat du OU logique
     */
    public byte ouBinaire(byte a, byte b){
        return (byte) (a|b);
    }

    /**
     * Effectue un OU EXCLUSIF logique (XOR) bit à bit entre deux octets.
     *
     * @param a le premier opérande
     * @param b le deuxième opérande
     * @return le résultat du OU EXCLUSIF logique
     */
    public byte xorBinaire(byte a, byte b){
        return (byte) (a^b);
    }
}
