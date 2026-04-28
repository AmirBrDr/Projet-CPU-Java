package assembleur;

public abstract class Operande {
    private final TypeOperande type;

    protected Operande(TypeOperande type) {
        this.type = type;
    }

    public TypeOperande getType() {
        return type;
    }
}
