package assembleur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Programme {
    private final List<Instruction> instructions;

    public Programme() {
        this.instructions = new ArrayList<>();
    }

    public void ajouterInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public List<Instruction> getInstructions() {
        return Collections.unmodifiableList(instructions);
    }
}
