package noyau;

public class ResultatDiv {
    private byte quotient;
    private byte reste;

    public ResultatDiv(byte quotient, byte reste) {
        this.quotient = quotient;
        this.reste = reste;
    }

    public byte getQuotient() {
        return quotient;
    }

    public byte getReste() {
        return reste;
    }
}
