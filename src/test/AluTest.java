package test;
import noyau.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ALUTest {

    private ALU alu;

    @BeforeEach
    void setUp() {
        alu = new ALU();
    }

    // ──────────────────────────────────────────
    // 1. additionner
    // ──────────────────────────────────────────

    @Test
    void testAdditionSimple() {
        assertEquals((byte) 5, alu.additionner((byte) 2, (byte) 3));
    }

    @Test
    void testAdditionAvecZero() {
        assertEquals((byte) 7, alu.additionner((byte) 7, (byte) 0));
    }

    @Test
    void testAdditionDeuxNegatifs() {
        assertEquals((byte) -3, alu.additionner((byte) -1, (byte) -2));
    }

    @Test
    void testAdditionDebordement() {
        // 127 + 1 = 128, mais en byte signé = -128 (overflow attendu)
        assertEquals((byte) -128, alu.additionner((byte) 127, (byte) 1));
    }

    @Test
    void testAdditionSymetrie() {
        assertEquals(alu.additionner((byte) 3, (byte) 4),
                alu.additionner((byte) 4, (byte) 3));
    }

    // ──────────────────────────────────────────
    // 2. soustraire
    // ──────────────────────────────────────────

    @Test
    void testSoustractionSimple() {
        assertEquals((byte) 3, alu.soustraire((byte) 5, (byte) 2));
    }

    @Test
    void testSoustractionResultatNegatif() {
        assertEquals((byte) -3, alu.soustraire((byte) 2, (byte) 5));
    }

    @Test
    void testSoustractionParSoiMeme() {
        assertEquals((byte) 0, alu.soustraire((byte) 42, (byte) 42));
    }

    @Test
    void testSoustractionParZero() {
        assertEquals((byte) 10, alu.soustraire((byte) 10, (byte) 0));
    }

    @Test
    void testSoustractionDebordement() {
        // -128 - 1 = -129, mais en byte signé = 127 (overflow attendu)
        assertEquals((byte) 127, alu.soustraire((byte) -128, (byte) 1));
    }

    // ──────────────────────────────────────────
    // 3. multiplier
    // ATTENTION : bug détecté ligne poidsFaible — resInt*0xFF doit être resInt & 0xFF
    // Ces tests échoueront tant que le bug n'est pas corrigé
    // ──────────────────────────────────────────

    @Test
    void testMultiplierSimple() {
        // 3 * 4 = 12 → poidsFort = 0, poidsFaible = 12
        ResultatMulti res = alu.multiplier((byte) 3, (byte) 4);
        assertEquals((byte) 12, res.getPoidsFaible());
        assertEquals((byte) 0,  res.getPoidsFort());
    }

    @Test
    void testMultiplierParZero() {
        ResultatMulti res = alu.multiplier((byte) 99, (byte) 0);
        assertEquals((byte) 0, res.getPoidsFaible());
        assertEquals((byte) 0, res.getPoidsFort());
    }

    @Test
    void testMultiplierParUn() {
        ResultatMulti res = alu.multiplier((byte) 55, (byte) 1);
        assertEquals((byte) 55, res.getPoidsFaible());
        assertEquals((byte) 0,  res.getPoidsFort());
    }

    @Test
    void testMultiplierGrandResultat() {
        // En Java, byte est signé : (byte)200 = -56
        // (-56) * (-56) = 3136 = 0x0C40
        // poidsFaible = 0x40 = 64, poidsFort = 0x0C = 12
        byte a = (byte) 200; // = -56
        byte b = (byte) 200; // = -56
        int resInt = a * b;  // = 3136 = 0x0C40

        byte poidsFaibleAttendu = (byte) (resInt & 0xFF);        // 0x40 = 64
        byte poidsFortAttendu   = (byte) ((resInt >> 8) & 0xFF); // 0x0C = 12

        ResultatMulti res = alu.multiplier(a, b);
        assertEquals(poidsFaibleAttendu, res.getPoidsFaible()); // 64
        assertEquals(poidsFortAttendu,   res.getPoidsFort());   // 12 ← et non -100
    }

    // ──────────────────────────────────────────
    // 4. diviser
    // ──────────────────────────────────────────

    @Test
    void testDivisionSimple() {
        ResultatDiv res = alu.diviser((byte) 10, (byte) 3);
        assertEquals((byte) 3, res.getQuotient());
        assertEquals((byte) 1, res.getReste());
    }

    @Test
    void testDivisionExacte() {
        ResultatDiv res = alu.diviser((byte) 12, (byte) 4);
        assertEquals((byte) 3, res.getQuotient());
        assertEquals((byte) 0, res.getReste());
    }

    @Test
    void testDivisionParUn() {
        ResultatDiv res = alu.diviser((byte) 42, (byte) 1);
        assertEquals((byte) 42, res.getQuotient());
        assertEquals((byte) 0,  res.getReste());
    }

    @Test
    void testDivisionZeroParN() {
        ResultatDiv res = alu.diviser((byte) 0, (byte) 5);
        assertEquals((byte) 0, res.getQuotient());
        assertEquals((byte) 0, res.getReste());
    }

    @Test
    void testDivisionParZeroLeveException() {
        assertThrows(ArithmeticException.class,
                () -> alu.diviser((byte) 10, (byte) 0));
    }

    @Test
    void testDivisionMessageException() {
        ArithmeticException ex = assertThrows(ArithmeticException.class,
                () -> alu.diviser((byte) 5, (byte) 0));
        assertEquals("Division par zéro", ex.getMessage());
    }

    // ──────────────────────────────────────────
    // 5. estBinaire (AND)
    // ──────────────────────────────────────────

    @Test
    void testAndCasSimple() {
        // 0b1100 & 0b1010 = 0b1000 = 8
        assertEquals((byte) 8, alu.estBinaire((byte) 12, (byte) 10));
    }

    @Test
    void testAndAvecZero() {
        assertEquals((byte) 0, alu.estBinaire((byte) 0xFF, (byte) 0));
    }

    @Test
    void testAndAvecSoiMeme() {
        assertEquals((byte) 55, alu.estBinaire((byte) 55, (byte) 55));
    }

    @Test
    void testAndTousLesUns() {
        // 0xFF & 0xFF = 0xFF = -1 en byte signé
        assertEquals((byte) -1, alu.estBinaire((byte) -1, (byte) -1));
    }

    // ──────────────────────────────────────────
    // 6. ouBinaire (OR)
    // ──────────────────────────────────────────

    @Test
    void testOrCasSimple() {
        // 0b1100 | 0b0011 = 0b1111 = 15
        assertEquals((byte) 15, alu.ouBinaire((byte) 12, (byte) 3));
    }

    @Test
    void testOrAvecZero() {
        assertEquals((byte) 42, alu.ouBinaire((byte) 42, (byte) 0));
    }

    @Test
    void testOrAvecSoiMeme() {
        assertEquals((byte) 99, alu.ouBinaire((byte) 99, (byte) 99));
    }

    @Test
    void testOrComplementaires() {
        // 0b10101010 | 0b01010101 = 0b11111111 = -1
        assertEquals((byte) -1, alu.ouBinaire((byte) 0b10101010, (byte) 0b01010101));
    }

    // ──────────────────────────────────────────
    // 7. xorBinaire (XOR)
    // ──────────────────────────────────────────

    @Test
    void testXorCasSimple() {
        // 0b1100 ^ 0b1010 = 0b0110 = 6
        assertEquals((byte) 6, alu.xorBinaire((byte) 12, (byte) 10));
    }

    @Test
    void testXorAvecSoiMeme() {
        // a ^ a = 0 toujours
        assertEquals((byte) 0, alu.xorBinaire((byte) 77, (byte) 77));
    }

    @Test
    void testXorAvecZero() {
        // a ^ 0 = a toujours
        assertEquals((byte) 33, alu.xorBinaire((byte) 33, (byte) 0));
    }

    @Test
    void testXorSymetrie() {
        assertEquals(alu.xorBinaire((byte) 5, (byte) 9),
                alu.xorBinaire((byte) 9, (byte) 5));
    }

    @Test
    void testXorDoubleInverse() {
        // (a ^ b) ^ b = a — propriété fondamentale du XOR
        byte a = 42, b = 17;
        byte etape1 = alu.xorBinaire(a, b);
        byte etape2 = alu.xorBinaire(etape1, b);
        assertEquals(a, etape2);
    }
}