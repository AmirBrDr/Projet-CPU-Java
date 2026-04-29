package noyau;

/**
 * Représente la banque de registres du processeur.
 * <p>
 * Elle contient 16 registres de 8 bits chacun, permettant un accès
 * rapide aux données pendant l'exécution des instructions.
 *
 * @author Amirmahdi GHASEMI et Dorsa KHOSHNOOD
 * @version 1.0
 */
public class BanqueRegistres {
    private final int nombreRegistres = 16;
    private Registre[] registres;

    /**
     * Construit une nouvelle banque de registres et initialise
     * chacun des 16 registres à zéro.
     */
    public BanqueRegistres() {
        this.registres = new Registre[nombreRegistres];
        for (int i=0; i<nombreRegistres; i++)
            registres[i] = new Registre(i);
    }

    /**
     * Lit la valeur stockée dans un registre spécifique.
     *
     * @param numero le numéro d'identification du registre (entre 0 et 15)
     * @return la valeur d'un octet actuellement stockée dans le registre
     */
    public byte lireRegistre(int numero){
        return registres[numero].lireValeur();
    }

    /**
     * Écrit une valeur dans un registre spécifique.
     * Si le numéro de registre est invalide, affiche une erreur sans modifier d'état.
     *
     * @param numero le numéro du registre cible (entre 0 et 15)
     * @param valeur la valeur à y stocker
     */
    public void ecrireRegistre(int numero,byte valeur){
        if (estNumeroValide(numero))
            registres[numero].ecrireValeur(valeur);
        else System.out.println("Numero de registre invalide");
    }

    /**
     * Récupère l'objet {@link Registre} correspondant au numéro donné.
     *
     * @param numero le numéro du registre (entre 0 et 15)
     * @return l'instance du registre
     */
    public Registre getRegistre(int numero){
        return registres[numero];
    }

    /**
     * Réinitialise entièrement la banque de registres en remettant
     * tous les registres à zéro.
     */
    public void reinitialiser(){
        this.registres = new Registre[nombreRegistres];
        for (int i=0; i<nombreRegistres; i++)
            registres[i] = new Registre(i);
    }

    /**
     * Vérifie si le numéro fourni correspond à un registre existant.
     *
     * @param numero le numéro du registre à vérifier
     * @return <code>true</code> si le numéro est valide (entre 0 et 15 inclus), <code>false</code> sinon
     */
    public boolean estNumeroValide(int numero){
        return (numero<16 && numero>=0);
    }
}
