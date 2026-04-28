package noyau;

public class ResultatMulti {
    private byte poidsFaible;
    private byte poidsFort;

    public ResultatMulti(byte poidsFaible, byte poidsFort) {
        this.poidsFaible = poidsFaible;
        this.poidsFort = poidsFort;
    }

    public byte getPoidsFaible() {
        return poidsFaible;
    }

    public byte getPoidsFort() {
        return poidsFort;
    }
}
