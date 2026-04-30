package test;

import assembleur.Assembleur;
import assembleur.Instruction;
import assembleur.Programme;
import assembleur.TypeInstruction;
import assembleur.TypeOperande;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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

    // Verification d'un ADD avec deux registres source et un registre destination.
    @Test
    void testTraduireLigneAdd() {
        Instruction instruction = assembleur.traduireLigne("add r1, r2, r3");

        // On verifie le type de l'instruction reconnu par le parser.
        assertEquals(TypeInstruction.ADD, instruction.getTypeInstruction());

        // On verifie que les trois operandes sont bien identifies comme des registres.
        assertEquals(3, instruction.getOperandes().size());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(0).getType());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(1).getType());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(2).getType());
    }

    // Verification d'un SUB avec deux registres source et un registre destination.
    @Test
    void testTraduireLigneSub() {
        Instruction instruction = assembleur.traduireLigne("sub r4, r5, r6");

        // On verifie le type de l'instruction reconnu par le parser.
        assertEquals(TypeInstruction.SUB, instruction.getTypeInstruction());

        // On verifie que les trois operandes sont bien identifies comme des registres.
        assertEquals(3, instruction.getOperandes().size());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(0).getType());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(1).getType());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(2).getType());
    }

    // Verification d'un MUL avec deux registres source et un registre destination.
    @Test
    void testTraduireLigneMul() {
        Instruction instruction = assembleur.traduireLigne("mul r7, r8, r9");

        // On verifie le type de l'instruction reconnu par le parser.
        assertEquals(TypeInstruction.MUL, instruction.getTypeInstruction());

        // On verifie que les trois operandes sont bien identifies comme des registres.
        assertEquals(3, instruction.getOperandes().size());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(0).getType());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(1).getType());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(2).getType());
    }

    // Verification d'un DIV avec deux registres source et un registre destination.
    @Test
    void testTraduireLigneDiv() {
        Instruction instruction = assembleur.traduireLigne("div r10, r11, r12");

        // On verifie le type de l'instruction reconnu par le parser.
        assertEquals(TypeInstruction.DIV, instruction.getTypeInstruction());

        // On verifie que les trois operandes sont bien identifies comme des registres.
        assertEquals(3, instruction.getOperandes().size());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(0).getType());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(1).getType());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(2).getType());
    }

    // Verification d'un AND avec deux registres source et un registre destination.
    @Test
    void testTraduireLigneAnd() {
        Instruction instruction = assembleur.traduireLigne("and r1, r3, r5");

        // On verifie le type de l'instruction reconnu par le parser.
        assertEquals(TypeInstruction.AND, instruction.getTypeInstruction());

        // On verifie que les trois operandes sont bien identifies comme des registres.
        assertEquals(3, instruction.getOperandes().size());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(0).getType());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(1).getType());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(2).getType());
    }

    // Verification d'un OR avec deux registres source et un registre destination.
    @Test
    void testTraduireLigneOr() {
        Instruction instruction = assembleur.traduireLigne("or r2, r4, r6");

        // On verifie le type de l'instruction reconnu par le parser.
        assertEquals(TypeInstruction.OR, instruction.getTypeInstruction());

        // On verifie que les trois operandes sont bien identifies comme des registres.
        assertEquals(3, instruction.getOperandes().size());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(0).getType());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(1).getType());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(2).getType());
    }

    // Verification d'un XOR avec deux registres source et un registre destination.
    @Test
    void testTraduireLigneXor() {
        Instruction instruction = assembleur.traduireLigne("xor r3, r6, r9");

        // On verifie le type de l'instruction reconnu par le parser.
        assertEquals(TypeInstruction.XOR, instruction.getTypeInstruction());

        // On verifie que les trois operandes sont bien identifies comme des registres.
        assertEquals(3, instruction.getOperandes().size());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(0).getType());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(1).getType());
        assertEquals(TypeOperande.REGISTRE, instruction.getOperandes().get(2).getType());
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

    // Verification du code machine genere pour les instructions de base de l'etape 1.
    @Test
    void testGenererCodeMachineProgrammeSimple() {
        String source = """
                load r0, 5
                load r2, @100
                store r0, @101
                break
                """;

        Programme programme = assembleur.assembler(source);
        byte[] codeMachine = assembleur.genererCodeMachine(programme);

        byte[] attendu = {
                1, 0, 5,
                2, 2, 0, 100,
                3, 0, 0, 101,
                0
        };

        assertArrayEquals(attendu, codeMachine);
    }

    // Verification du code machine genere pour une instruction ADD.
    @Test
    void testGenererCodeMachineAdd() {
        Programme programme = assembleur.assembler("add r1, r2, r3");
        byte[] codeMachine = assembleur.genererCodeMachine(programme);

        // Le format binaire attendu est : opcode ADD, registre1, registre2, registre destination.
        byte[] attendu = {4, 1, 2, 3};

        assertArrayEquals(attendu, codeMachine);
    }

    // Verification du code machine genere pour une instruction SUB.
    @Test
    void testGenererCodeMachineSub() {
        Programme programme = assembleur.assembler("sub r4, r5, r6");
        byte[] codeMachine = assembleur.genererCodeMachine(programme);

        // Le format binaire attendu est : opcode SUB, registre1, registre2, registre destination.
        byte[] attendu = {5, 4, 5, 6};

        assertArrayEquals(attendu, codeMachine);
    }

    // Verification du code machine genere pour une instruction MUL.
    @Test
    void testGenererCodeMachineMul() {
        Programme programme = assembleur.assembler("mul r7, r8, r9");
        byte[] codeMachine = assembleur.genererCodeMachine(programme);

        // Le format binaire attendu est : opcode MUL, registre1, registre2, registre destination.
        byte[] attendu = {6, 7, 8, 9};

        assertArrayEquals(attendu, codeMachine);
    }

    // Verification du code machine genere pour une instruction DIV.
    @Test
    void testGenererCodeMachineDiv() {
        Programme programme = assembleur.assembler("div r10, r11, r12");
        byte[] codeMachine = assembleur.genererCodeMachine(programme);

        // Le format binaire attendu est : opcode DIV, registre1, registre2, registre destination.
        byte[] attendu = {7, 10, 11, 12};

        assertArrayEquals(attendu, codeMachine);
    }

    // Verification du code machine genere pour une instruction AND.
    @Test
    void testGenererCodeMachineAnd() {
        Programme programme = assembleur.assembler("and r1, r3, r5");
        byte[] codeMachine = assembleur.genererCodeMachine(programme);

        // Le format binaire attendu est : opcode AND, registre1, registre2, registre destination.
        byte[] attendu = {8, 1, 3, 5};

        assertArrayEquals(attendu, codeMachine);
    }

    // Verification du code machine genere pour une instruction OR.
    @Test
    void testGenererCodeMachineOr() {
        Programme programme = assembleur.assembler("or r2, r4, r6");
        byte[] codeMachine = assembleur.genererCodeMachine(programme);

        // Le format binaire attendu est : opcode OR, registre1, registre2, registre destination.
        byte[] attendu = {9, 2, 4, 6};

        assertArrayEquals(attendu, codeMachine);
    }

    // Verification du code machine genere pour une instruction XOR.
    @Test
    void testGenererCodeMachineXor() {
        Programme programme = assembleur.assembler("xor r3, r6, r9");
        byte[] codeMachine = assembleur.genererCodeMachine(programme);

        // Le format binaire attendu est : opcode XOR, registre1, registre2, registre destination.
        byte[] attendu = {10, 3, 6, 9};

        assertArrayEquals(attendu, codeMachine);
    }
}
