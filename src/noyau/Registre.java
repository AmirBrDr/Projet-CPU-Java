package noyau;

public class Registre {
    private int numero;
    private byte valeur = 0;

    public Registre(int numero) {
        this.numero = numero;
    }

    public int getNumero() {
        return numero;
    }

    public byte lireValeur(){
        return valeur;
    }

    public void ecrireValeur(byte valeur){
        this.valeur = valeur;
    }

    public void reinitialiser(){
        valeur = 0;
    }
}
