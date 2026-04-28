package noyau;

public class ALU {
    public ALU() {
    }

    public byte additionner(byte a, byte b){
        return (byte) (a+b);
    }

    public byte soustraire(byte a,byte b){
        return (byte) (a-b);
    }

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

    public ResultatDiv diviser(byte a,byte b){
        if (b==0) throw new ArithmeticException("Division par zéro");

        byte q = (byte) (a/b);
        byte r = (byte) (a%b);

        return new ResultatDiv(q,r);
    }

    public byte estBinaire(byte a,byte b){
        return (byte) (a & b);
    }

    public byte ouBinaire(byte a, byte b){
        return (byte) (a|b);
    }

    public byte xorBinaire(byte a, byte b){
        return (byte) (a^b);
    }
}
