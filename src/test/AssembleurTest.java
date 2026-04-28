package test;

import assembleur.Assembleur;
import assembleur.Instruction;
import assembleur.Programme;
import assembleur.TypeInstruction;
import assembleur.TypeOperande;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssembleurTest {

    private Assembleur assembleur;

    @BeforeEach
    void setUp() {
        assembleur = new Assembleur();
    }

    // Verification de l'instruction BREAK sans operande.
    @Test
    void testTraduireLigneBreak() {
        Instruction instruction = assembleur.traduireLigne("break");

        assertEquals(TypeInstruction.BREAK, instruction.getTypeInstruction());
        assertEquals(0, instruction.getOperandes().size());
        assertEquals("break", instruction.getLigneSource());
    }

    // Verification d'un LOAD avec une valeur immediate.
    @Test
    void testTraduireLigneLoadConstante() {
        Instruction instruction = assembleur.traduireLigne("load r0, 5");

        assertEquals(TypeInstruction.LOAD_CONSTANTE, instruction.getTypeInstruction());
        assertEquals(2, instruction.getOperandes().size());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(0).getType());
        assertEquals(TypeOperande.CONSTANTE, instruction.getOperandes().get(1).getType());
    }

    // Verification d'un LOAD qui lit une valeur depuis la memoire.
    @Test
    void testTraduireLigneLoadMemoire() {
        Instruction instruction = assembleur.traduireLigne("load r2, @100");

        assertEquals(TypeInstruction.LOAD_MEMOIRE, instruction.getTypeInstruction());
        assertEquals(2, instruction.getOperandes().size());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(0).getType());
        assertEquals(TypeOperande.ADRESSE, instruction.getOperandes().get(1).getType());
    }

    // Verification d'un STORE qui ecrit la valeur d'un registre en memoire.
    @Test
    void testTraduireLigneStoreMemoire() {
        Instruction instruction = assembleur.traduireLigne("store r0, @101");

        assertEquals(TypeInstruction.STORE_MEMOIRE, instruction.getTypeInstruction());
        assertEquals(2, instruction.getOperandes().size());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(0).getType());
        assertEquals(TypeOperande.ADRESSE, instruction.getOperandes().get(1).getType());
    }

    // Verification de l'assemblage d'un petit programme sur plusieurs lignes.
    @Test
    void testAssemblerProgrammeSimple() {
        String source = """
                load r0, 5
                load r2, @100
                store r0, @101
                break
                """;

        Programme programme = assembleur.assembler(source);

        assertEquals(4, programme.getInstructions().size());
        assertEquals(TypeInstruction.LOAD_CONSTANTE, programme.getInstructions().get(0).getTypeInstruction());
        assertEquals(TypeInstruction.LOAD_MEMOIRE, programme.getInstructions().get(1).getTypeInstruction());
        assertEquals(TypeInstruction.STORE_MEMOIRE, programme.getInstructions().get(2).getTypeInstruction());
        assertEquals(TypeInstruction.BREAK, programme.getInstructions().get(3).getTypeInstruction());
    }
}
