package noyau;

public class Memoire {
    private final int taille = 65536;
    private byte[] contenu;

    public Memoire() {
        this.contenu = new byte[taille];
    }

    public byte lireOctet(int address){
        return contenu[address];
    }

    public void ecrireOctet(int address, byte valeur){
        if (estAddressValide(address))
            contenu[address] = valeur;
        else System.out.println("OutOfMemoryError");
    }

    public byte[] lireBloc(int addressDebut, int taille){
        byte[] res = new byte[taille];
        int address = addressDebut;
        for (int i=0; i<taille; i++){
            res[i] = contenu[address];
            address+=1;
        }
        return res;
    }

    public void ecrireBloc(int addressDebut, byte[] donnees){
        int address = addressDebut;
        for (byte valeur : donnees) {
            ecrireOctet(address,valeur);
            address += 1;
        }
    }

    public void vider(){
        contenu = new byte[taille];
    }

    public boolean estAddressValide(int address){
        return (address<taille && address>=0);
    }




}
