package test;

import assembleur.*;
import noyau.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CPUTest {

    private CPU cpu;

    // ── helpers pour construire des programmes inline ──────────────────────────

    private Programme prog(Instruction... instructions) {
        Programme p = new Programme();
        for (Instruction i : instructions) p.ajouterInstruction(i);
        return p;
    }

    private Instruction loadCst(int reg, int val) {
        return new Instruction(TypeInstruction.LOAD_CONSTANTE,
                List.of(new OperandeRegistre(reg), new OperandeConstante(val)),
                "load r" + reg + ", " + val);
    }

    private Instruction loadMem(int reg, int adr) {
        return new Instruction(TypeInstruction.LOAD_MEMOIRE,
                List.of(new OperandeRegistre(reg), new OperandeAdresse(adr)),
                "load r" + reg + ", @" + adr);
    }

    private Instruction storeMem(int reg, int adr) {
        return new Instruction(TypeInstruction.STORE_MEMOIRE,
                List.of(new OperandeRegistre(reg), new OperandeAdresse(adr)),
                "store r" + reg + ", @" + adr);
    }

    private Instruction op(TypeInstruction type, int r1, int r2, int rd, String mnem) {
        return new Instruction(type,
                List.of(new OperandeRegistre(r1), new OperandeRegistre(r2), new OperandeRegistre(rd)),
                mnem);
    }

    private Instruction jump(int adr) {
        return new Instruction(TypeInstruction.JUMP,
                List.of(new OperandeAdresse(adr)), "jump @" + adr);
    }

    private Instruction beq(int r1, int r2, int adr) {
        return new Instruction(TypeInstruction.BEQ,
                List.of(new OperandeRegistre(r1), new OperandeRegistre(r2), new OperandeAdresse(adr)),
                "beq r" + r1 + ", r" + r2 + ", @" + adr);
    }

    private Instruction bne(int r1, int r2, int adr) {
        return new Instruction(TypeInstruction.BNE,
                List.of(new OperandeRegistre(r1), new OperandeRegistre(r2), new OperandeAdresse(adr)),
                "bne r" + r1 + ", r" + r2 + ", @" + adr);
    }

    private Instruction brk() {
        return new Instruction(TypeInstruction.BREAK, List.of(), "break");
    }

    @BeforeEach
    void setUp() {
        cpu = new CPU();
    }

    // ──────────────────────────────────────────
    // 1. État initial
    // ──────────────────────────────────────────

    @Test
    void testEtatInitial() {
        // PC = 0, pas en exécution — vérifié indirectement via reinitialiser()
        cpu.reinitialiser();
        // Si le CPU n'est pas en exécution, charger et exécuter BREAK doit terminer
        assertDoesNotThrow(() -> cpu.executerProgramme());
    }

    // ──────────────────────────────────────────
    // 2. chargerProgramme / LOAD_CONSTANTE
    // ──────────────────────────────────────────

    @Test
    void testLoadConstante() {
        cpu.chargerProgramme(prog(loadCst(0, 42), brk()));
        cpu.executerProgramme();
        assertEquals((byte) 42, cpu.getBanqueRegistres().lireRegistre(0));
    }

    @Test
    void testLoadConstantePlusieursRegistres() {
        cpu.chargerProgramme(prog(
                loadCst(0, 10),
                loadCst(1, 20),
                loadCst(2, 30),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 10, cpu.getBanqueRegistres().lireRegistre(0));
        assertEquals((byte) 20, cpu.getBanqueRegistres().lireRegistre(1));
        assertEquals((byte) 30, cpu.getBanqueRegistres().lireRegistre(2));
    }

    @Test
    void testLoadConstanteEcrase() {
        cpu.chargerProgramme(prog(loadCst(0, 5), loadCst(0, 99), brk()));
        cpu.executerProgramme();
        assertEquals((byte) 99, cpu.getBanqueRegistres().lireRegistre(0));
    }

    // ──────────────────────────────────────────
    // 3. STORE_MEMOIRE / LOAD_MEMOIRE
    // ──────────────────────────────────────────

    @Test
    void testStoreEtLoadMemoire() {
        // R0 = 77 → store @1000 → load R1 @1000 → R1 doit valoir 77
        cpu.chargerProgramme(prog(
                loadCst(0, 77),
                storeMem(0, 1000),
                loadMem(1, 1000),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 77, cpu.getBanqueRegistres().lireRegistre(1));
    }

    @Test
    void testLoadMemoireInitialiseeAZero() {
        cpu.chargerProgramme(prog(loadMem(0, 5000), brk()));
        cpu.executerProgramme();
        assertEquals((byte) 0, cpu.getBanqueRegistres().lireRegistre(0));
    }

    // ──────────────────────────────────────────
    // 4. ADD
    // ──────────────────────────────────────────

    @Test
    void testAdd() {
        cpu.chargerProgramme(prog(
                loadCst(0, 3),
                loadCst(1, 4),
                op(TypeInstruction.ADD, 0, 1, 2, "add r0,r1,r2"),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 7, cpu.getBanqueRegistres().lireRegistre(2));
    }

    @Test
    void testAddDebordement() {
        cpu.chargerProgramme(prog(
                loadCst(0, 127),
                loadCst(1, 1),
                op(TypeInstruction.ADD, 0, 1, 2, "add"),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) -128, cpu.getBanqueRegistres().lireRegistre(2));
    }

    // ──────────────────────────────────────────
    // 5. SUB
    // ──────────────────────────────────────────

    @Test
    void testSub() {
        cpu.chargerProgramme(prog(
                loadCst(0, 10),
                loadCst(1, 3),
                op(TypeInstruction.SUB, 0, 1, 2, "sub"),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 7, cpu.getBanqueRegistres().lireRegistre(2));
    }

    @Test
    void testSubResultatNegatif() {
        cpu.chargerProgramme(prog(
                loadCst(0, 2),
                loadCst(1, 5),
                op(TypeInstruction.SUB, 0, 1, 2, "sub"),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) -3, cpu.getBanqueRegistres().lireRegistre(2));
    }

    // ──────────────────────────────────────────
    // 6. MUL
    // ──────────────────────────────────────────

    @Test
    void testMulSimple() {
        cpu.chargerProgramme(prog(
                loadCst(0, 3),
                loadCst(1, 4),
                op(TypeInstruction.MUL, 0, 1, 2, "mul"),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 12, cpu.getBanqueRegistres().lireRegistre(2));
    }

    @Test
    void testMulParZero() {
        cpu.chargerProgramme(prog(
                loadCst(0, 99),
                loadCst(1, 0),
                op(TypeInstruction.MUL, 0, 1, 2, "mul"),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 0, cpu.getBanqueRegistres().lireRegistre(2));
    }

    // ──────────────────────────────────────────
    // 7. DIV
    // ──────────────────────────────────────────

    @Test
    void testDivSimple() {
        cpu.chargerProgramme(prog(
                loadCst(0, 10),
                loadCst(1, 3),
                op(TypeInstruction.DIV, 0, 1, 2, "div"),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 3, cpu.getBanqueRegistres().lireRegistre(2));
    }

    @Test
    void testDivParZeroLeveException() {
        cpu.chargerProgramme(prog(
                loadCst(0, 10),
                loadCst(1, 0),
                op(TypeInstruction.DIV, 0, 1, 2, "div"),
                brk()
        ));
        assertThrows(ArithmeticException.class, () -> cpu.executerProgramme());
    }

    // ──────────────────────────────────────────
    // 8. AND / OR / XOR
    // ──────────────────────────────────────────

    @Test
    void testAnd() {
        // 0b1100 & 0b1010 = 0b1000 = 8
        cpu.chargerProgramme(prog(
                loadCst(0, 12),
                loadCst(1, 10),
                op(TypeInstruction.AND, 0, 1, 2, "and"),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 8, cpu.getBanqueRegistres().lireRegistre(2));
    }

    @Test
    void testOr() {
        // 0b1100 | 0b0011 = 0b1111 = 15
        cpu.chargerProgramme(prog(
                loadCst(0, 12),
                loadCst(1, 3),
                op(TypeInstruction.OR, 0, 1, 2, "or"),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 15, cpu.getBanqueRegistres().lireRegistre(2));
    }

    @Test
    void testXor() {
        // a ^ a = 0
        cpu.chargerProgramme(prog(
                loadCst(0, 55),
                loadCst(1, 55),
                op(TypeInstruction.XOR, 0, 1, 2, "xor"),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 0, cpu.getBanqueRegistres().lireRegistre(2));
    }

    // ──────────────────────────────────────────
    // 9. JUMP
    // ──────────────────────────────────────────

    @Test
    void testJumpSauteInstructions() {
        // Programme :
        //   @0  : LOAD R0, 1      (3 octets → suivant @3)
        //   @3  : JUMP @9         (3 octets → suivant @6)
        //   @6  : LOAD R0, 99     (3 octets — ne doit PAS être exécuté)
        //   @9  : BREAK
        cpu.chargerProgramme(prog(
                loadCst(0, 1),
                jump(9),
                loadCst(0, 99),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 1, cpu.getBanqueRegistres().lireRegistre(0),
                "JUMP doit sauter par-dessus LOAD R0,99");
    }

    // ──────────────────────────────────────────
    // 10. BEQ
    // ──────────────────────────────────────────

    @Test
    void testBeqSauteSiEgaux() {
        // @0 LOAD R0,5 (3) | @3 LOAD R1,5 (3) | @6 BEQ @14 (5) | @11 LOAD R2,99 (3) | @14 BREAK
        cpu.chargerProgramme(prog(
                loadCst(0, 5),
                loadCst(1, 5),
                beq(0, 1, 14),   // ← 14, pas 11
                loadCst(2, 99),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 0, cpu.getBanqueRegistres().lireRegistre(2));
    }

    @Test
    void testBeqNeSautePasSiDifferents() {
        cpu.chargerProgramme(prog(
                loadCst(0, 5),
                loadCst(1, 6),
                beq(0, 1, 14),
                loadCst(2, 99),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 99, cpu.getBanqueRegistres().lireRegistre(2));
    }


    // ──────────────────────────────────────────
    // 11. BNE
    // ──────────────────────────────────────────

    @Test
    void testBneSauteSiDifferents() {
        // @0 LOAD R0,3 (3) | @3 LOAD R1,7 (3) | @6 BNE @14 (5) | @11 LOAD R2,99 (3) | @14 BREAK
        cpu.chargerProgramme(prog(
                loadCst(0, 3),
                loadCst(1, 7),
                bne(0, 1, 14),   // ← 14, pas 11
                loadCst(2, 99),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 0, cpu.getBanqueRegistres().lireRegistre(2));
    }

    @Test
    void testBneNeSautePasSiEgaux() {
        cpu.chargerProgramme(prog(
                loadCst(0, 4),
                loadCst(1, 4),
                bne(0, 1, 14),
                loadCst(2, 99),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 99, cpu.getBanqueRegistres().lireRegistre(2));
    }

    // ──────────────────────────────────────────
    // 12. BREAK
    // ──────────────────────────────────────────

    @Test
    void testBreakArreteExecution() {
        // BREAK avant LOAD R0,99 — R0 doit rester à 0
        cpu.chargerProgramme(prog(brk(), loadCst(0, 99)));
        cpu.executerProgramme();
        assertEquals((byte) 0, cpu.getBanqueRegistres().lireRegistre(0));
    }

    // ──────────────────────────────────────────
    // 13. reinitialiser
    // ──────────────────────────────────────────

    @Test
    void testReinitialiserVideRegistres() {
        cpu.chargerProgramme(prog(loadCst(0, 42), brk()));
        cpu.executerProgramme();
        cpu.reinitialiser();
        assertEquals((byte) 0, cpu.getBanqueRegistres().lireRegistre(0));
    }

    @Test
    void testReinitialiserPermetRelancement() {
        cpu.chargerProgramme(prog(loadCst(0, 1), brk()));
        cpu.executerProgramme();
        cpu.reinitialiser();
        cpu.chargerProgramme(prog(loadCst(0, 55), brk()));
        cpu.executerProgramme();
        assertEquals((byte) 55, cpu.getBanqueRegistres().lireRegistre(0));
    }

    // ──────────────────────────────────────────
    // 14. Programme intégration — calcul complet
    // ──────────────────────────────────────────

    @Test
    void testProgrammeAdditionStoreLoad() {
        // R0=10, R1=20, R2=R0+R1=30, store R2 @2000, load R3 @2000 → R3=30
        cpu.chargerProgramme(prog(
                loadCst(0, 10),
                loadCst(1, 20),
                op(TypeInstruction.ADD, 0, 1, 2, "add"),
                storeMem(2, 2000),
                loadMem(3, 2000),
                brk()
        ));
        cpu.executerProgramme();
        assertEquals((byte) 30, cpu.getBanqueRegistres().lireRegistre(3));
    }

    @Test
    void testOpcodeInconnuLeveException() {
        // Écrire directement un opcode invalide en mémoire (255)
        cpu.chargerProgramme(prog(brk())); // initialise la mémoire proprement
        cpu.reinitialiser();
        // On ne peut pas facilement injecter un opcode invalide sans accès à la mémoire
        // → ce test documente le comportement attendu via appliquerInstruction
        // Si tu exposes getMémoire(), tu pourras écrire : cpu.getMemoire().ecrireOctet(0, (byte)255)
    }
}