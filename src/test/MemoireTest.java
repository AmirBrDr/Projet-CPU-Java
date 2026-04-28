package test;
import noyau.Memoire;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MemoireTest {

    private Memoire mem;

    @BeforeEach
    void setUp() {
        mem = new Memoire();
    }

    // ──────────────────────────────────────────
    // 1. État initial
    // ──────────────────────────────────────────

    @Test
    void testInitialisationAZero() {
        for (int i = 0; i < 65536; i++) {
            assertEquals(0, mem.lireOctet(i),
                    "L'octet à l'adresse " + i + " devrait être 0 à l'initialisation");
        }
    }

    // ──────────────────────────────────────────
    // 2. lireOctet / ecrireOctet — cas normaux
    // ──────────────────────────────────────────

    @Test
    void testEcrireEtLireOctet() {
        mem.ecrireOctet(100, (byte) 42);
        assertEquals(42, mem.lireOctet(100));
    }

    @Test
    void testEcrireOctetValeurNegative() {
        mem.ecrireOctet(200, (byte) -1);
        assertEquals((byte) -1, mem.lireOctet(200));
    }

    @Test
    void testEcrireOctetPremierAdresse() {
        mem.ecrireOctet(0, (byte) 10);
        assertEquals(10, mem.lireOctet(0));
    }

    @Test
    void testEcrireOctetDerniereAdresse() {
        mem.ecrireOctet(65535, (byte) 99);
        assertEquals(99, mem.lireOctet(65535));
    }

    // ──────────────────────────────────────────
    // 3. ecrireOctet — adresses invalides
    // ──────────────────────────────────────────

    @Test
    void testEcrireOctetAdresseNegative() {
        // Ne doit pas lever d'exception, et la mémoire ne doit pas être corrompue
        assertDoesNotThrow(() -> mem.ecrireOctet(-1, (byte) 5));
    }

    @Test
    void testEcrireOctetAdresseDepasseTaille() {
        assertDoesNotThrow(() -> mem.ecrireOctet(65536, (byte) 5));
    }

    @Test
    void testEcrireOctetAdresseInvalideSansMutation() {
        mem.ecrireOctet(0, (byte) 7);
        mem.ecrireOctet(-1, (byte) 99); // doit être ignoré
        assertEquals(7, mem.lireOctet(0), "L'adresse invalide ne doit pas modifier la mémoire");
    }

    // ──────────────────────────────────────────
    // 4. lireBloc
    // ──────────────────────────────────────────

    @Test
    void testLireBlocTailleCorrecte() {
        byte[] bloc = mem.lireBloc(0, 10);
        assertEquals(10, bloc.length);
    }

    @Test
    void testLireBlocContenu() {
        mem.ecrireOctet(50, (byte) 1);
        mem.ecrireOctet(51, (byte) 2);
        mem.ecrireOctet(52, (byte) 3);

        byte[] bloc = mem.lireBloc(50, 3);

        assertEquals((byte) 1, bloc[0]);
        assertEquals((byte) 2, bloc[1]);
        assertEquals((byte) 3, bloc[2]);
    }

    @Test
    void testLireBlocUnSeulOctet() {
        mem.ecrireOctet(10, (byte) 55);
        byte[] bloc = mem.lireBloc(10, 1);
        assertEquals(1, bloc.length);
        assertEquals((byte) 55, bloc[0]);
    }

    // ──────────────────────────────────────────
    // 5. ecrireBloc
    // ──────────────────────────────────────────

    @Test
    void testEcrireBlocEtVerifier() {
        byte[] donnees = {10, 20, 30, 40, 50};
        mem.ecrireBloc(100, donnees);

        for (int i = 0; i < donnees.length; i++) {
            assertEquals(donnees[i], mem.lireOctet(100 + i));
        }
    }

    @Test
    void testEcrireBlocVide() {
        // Un tableau vide ne doit rien changer
        assertDoesNotThrow(() -> mem.ecrireBloc(0, new byte[]{}));
        assertEquals(0, mem.lireOctet(0));
    }

    @Test
    void testEcrireBlocEcraseDonneesPrecedentes() {
        mem.ecrireOctet(5, (byte) 99);
        mem.ecrireBloc(5, new byte[]{1, 2, 3});
        assertEquals((byte) 1, mem.lireOctet(5));
    }

    // ──────────────────────────────────────────
    // 6. vider
    // ──────────────────────────────────────────

    @Test
    void testViderRemetAZero() {
        mem.ecrireOctet(0, (byte) 42);
        mem.ecrireOctet(100, (byte) 77);
        mem.ecrireOctet(65535, (byte) -1);

        mem.vider();

        assertEquals(0, mem.lireOctet(0));
        assertEquals(0, mem.lireOctet(100));
        assertEquals(0, mem.lireOctet(65535));
    }

    @Test
    void testViderPuisReEcrire() {
        mem.ecrireOctet(10, (byte) 50);
        mem.vider();
        mem.ecrireOctet(10, (byte) 99);
        assertEquals(99, mem.lireOctet(10));
    }

    // ──────────────────────────────────────────
    // 7. estAddressValide
    // ──────────────────────────────────────────

    @Test
    void testAdresseValidePremiere() {
        assertTrue(mem.estAddressValide(0));
    }

    @Test
    void testAdresseValideDerniere() {
        assertTrue(mem.estAddressValide(65535));
    }

    @Test
    void testAdresseValideMillieu() {
        assertTrue(mem.estAddressValide(1000));
    }

    @Test
    void testAdresseInvalideNegative() {
        assertFalse(mem.estAddressValide(-1));
    }

    @Test
    void testAdresseInvalideTailleExacte() {
        assertFalse(mem.estAddressValide(65536)); // hors borne (max = 65535)
    }

    @Test
    void testAdresseInvalideTropGrande() {
        assertFalse(mem.estAddressValide(100000));
    }
}