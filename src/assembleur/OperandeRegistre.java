package assembleur;

public class OperandeRegistre extends Operande {
    private final int numeroRegistre;

    public OperandeRegistre(int numeroRegistre) {
        super(TypeOperande.REGISTRE);
        this.numeroRegistre = numeroRegistre;
    }

    public int getNumeroRegistre() {
        return numeroRegistre;
    }
}
