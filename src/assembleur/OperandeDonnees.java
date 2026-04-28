package assembleur;

public class OperandeDonnees extends Operande {
    private final int[] valeurs;

    public OperandeDonnees(int[] valeurs) {
        super(TypeOperande.DONNEE);
        this.valeurs = valeurs;
    }

    public int[] getValeurs() {
        return valeurs;
    }
}
