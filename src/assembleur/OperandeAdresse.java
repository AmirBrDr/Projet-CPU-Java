package assembleur;

public class OperandeAdresse extends Operande {
    private final int adresse;

    public OperandeAdresse(int adresse) {
        super(TypeOperande.ADRESSE);
        this.adresse = adresse;
    }

    public int getAdresse() {
        return adresse;
    }
}
