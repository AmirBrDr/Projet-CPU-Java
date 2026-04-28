package assembleur;

public class OperandeChaine extends Operande {
    private final String valeur;

    public OperandeChaine(String valeur) {
        super(TypeOperande.CHAINE);
        this.valeur = valeur;
    }

    public String getValeur() {
        return valeur;
    }
}
