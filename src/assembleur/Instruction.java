package assembleur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Instruction {
    private final TypeInstruction typeInstruction;
    private final List<Operande> operandes;
    private final String ligneSource;

    public Instruction(TypeInstruction typeInstruction, List<Operande> operandes, String ligneSource) {
        this.typeInstruction = typeInstruction;
        this.operandes = new ArrayList<>(operandes);
        this.ligneSource = ligneSource;
    }

    public TypeInstruction getTypeInstruction() {
        return typeInstruction;
    }

    public List<Operande> getOperandes() {
        return Collections.unmodifiableList(operandes);
    }

    public String getLigneSource() {
        return ligneSource;
    }
}
