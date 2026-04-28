package noyau;

public class BanqueRegistres {
    private final int nombreRegistres = 16;
    private Registre[] registres;

    public BanqueRegistres() {
        this.registres = new Registre[nombreRegistres];
        for (int i=0; i<nombreRegistres; i++)
            registres[i] = new Registre(i);
    }

    public byte lireRegistre(int numero){
        return registres[numero].lireValeur();
    }

    public void ecrireRegistre(int numero,byte valeur){
        if (estNumeroValide(numero))
            registres[numero].ecrireValeur(valeur);
        else System.out.println("Numero de registre invalide");
    }

    public Registre getRegistre(int numero){
        return registres[numero];
    }

    public void reinitialiser(){
        this.registres = new Registre[nombreRegistres];
        for (int i=0; i<nombreRegistres; i++)
            registres[i] = new Registre(i);
    }

    public boolean estNumeroValide(int numero){
        return (numero<16 && numero>=0);
    }
}
