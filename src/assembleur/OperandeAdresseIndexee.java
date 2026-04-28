package assembleur;

public class OperandeAdresseIndexee extends Operande {
    private final int adresseBase;
    private final int registreIndex;

    public OperandeAdresseIndexee(int adresseBase, int registreIndex) {
        super(TypeOperande.ADRESSE_INDEXEE);
        this.adresseBase = adresseBase;
        this.registreIndex = registreIndex;
    }

    public int getAdresseBase() {
        return adresseBase;
    }

    public int getRegistreIndex() {
        return registreIndex;
    }
}
