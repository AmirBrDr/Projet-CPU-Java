package assembleur;

public class OperandeConstante extends Operande {
    private final int valeur;

    public OperandeConstante(int valeur) {
        super(TypeOperande.CONSTANTE);
        this.valeur = valeur;
    }

    public int getValeur() {
        return valeur;
    }
}
