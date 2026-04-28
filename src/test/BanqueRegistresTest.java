package test;
import noyau.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BanqueRegistresTest {

    private BanqueRegistres banque;

    @BeforeEach
    void setUp() {
        banque = new BanqueRegistres();
    }

    // ──────────────────────────────────────────
    // 1. État initial
    // ──────────────────────────────────────────

    @Test
    void testInitialisationAZero() {
        for (int i = 0; i < 16; i++) {
            assertEquals(0, banque.lireRegistre(i),
                    "Le registre R" + i + " devrait valoir 0 à l'initialisation");
        }
    }

    @Test
    void testGetRegistreNonNull() {
        for (int i = 0; i < 16; i++) {
            assertNotNull(banque.getRegistre(i),
                    "Le registre R" + i + " ne doit pas être null");
        }
    }

    @Test
    void testGetRegistreNumeroCorrect() {
        for (int i = 0; i < 16; i++) {
            assertEquals(i, banque.getRegistre(i).getNumero(),
                    "Le registre à l'index " + i + " doit avoir le numéro " + i);
        }
    }

    // ──────────────────────────────────────────
    // 2. ecrireRegistre / lireRegistre — cas normaux
    // ──────────────────────────────────────────

    @Test
    void testEcrireEtLireRegistre() {
        banque.ecrireRegistre(0, (byte) 42);
        assertEquals(42, banque.lireRegistre(0));
    }

    @Test
    void testEcrireValeurNegative() {
        banque.ecrireRegistre(3, (byte) -1);
        assertEquals((byte) -1, banque.lireRegistre(3));
    }

    @Test
    void testEcrirePremierRegistre() {
        banque.ecrireRegistre(0, (byte) 10);
        assertEquals(10, banque.lireRegistre(0));
    }

    @Test
    void testEcrireDernierRegistre() {
        banque.ecrireRegistre(15, (byte) 99);
        assertEquals(99, banque.lireRegistre(15));
    }

    @Test
    void testEcrireEcraseDonneesPrecedentes() {
        banque.ecrireRegistre(5, (byte) 10);
        banque.ecrireRegistre(5, (byte) 55);
        assertEquals(55, banque.lireRegistre(5));
    }

    @Test
    void testRegistresIndependants() {
        banque.ecrireRegistre(1, (byte) 11);
        banque.ecrireRegistre(2, (byte) 22);
        assertEquals(11, banque.lireRegistre(1));
        assertEquals(22, banque.lireRegistre(2));
    }

    // ──────────────────────────────────────────
    // 3. ecrireRegistre — numéros invalides
    // ──────────────────────────────────────────

    @Test
    void testEcrireNumeroNegatif() {
        assertDoesNotThrow(() -> banque.ecrireRegistre(-1, (byte) 5));
    }

    @Test
    void testEcrireNumeroDepasseMax() {
        assertDoesNotThrow(() -> banque.ecrireRegistre(16, (byte) 5));
    }

    @Test
    void testEcrireNumeroInvalideSansMutation() {
        banque.ecrireRegistre(0, (byte) 7);
        banque.ecrireRegistre(-1, (byte) 99); // doit être ignoré
        assertEquals(7, banque.lireRegistre(0),
                "Un numéro invalide ne doit pas modifier les registres");
    }

    // ──────────────────────────────────────────
    // 4. getRegistre
    // ──────────────────────────────────────────

    @Test
    void testGetRegistreRetourneLeBonObjet() {
        banque.ecrireRegistre(7, (byte) 33);
        Registre r = banque.getRegistre(7);
        assertEquals((byte) 33, r.lireValeur());
    }

    @Test
    void testGetRegistreEtEcrireCoherence() {
        // Modifier via ecrireRegistre, lire via getRegistre
        banque.ecrireRegistre(4, (byte) 77);
        assertEquals((byte) 77, banque.getRegistre(4).lireValeur());
    }

    // ──────────────────────────────────────────
    // 5. reinitialiser
    // ──────────────────────────────────────────

    @Test
    void testReinitialiserRemetAZero() {
        banque.ecrireRegistre(0, (byte) 1);
        banque.ecrireRegistre(8, (byte) 50);
        banque.ecrireRegistre(15, (byte) -1);

        banque.reinitialiser();

        assertEquals(0, banque.lireRegistre(0));
        assertEquals(0, banque.lireRegistre(8));
        assertEquals(0, banque.lireRegistre(15));
    }

    @Test
    void testReinitialiserPuisReEcrire() {
        banque.ecrireRegistre(3, (byte) 42);
        banque.reinitialiser();
        banque.ecrireRegistre(3, (byte) 99);
        assertEquals(99, banque.lireRegistre(3));
    }

    @Test
    void testReinitialiserRecreeObjetsRegistres() {
        Registre avantReinit = banque.getRegistre(0);
        banque.reinitialiser();
        Registre apresReinit = banque.getRegistre(0);
        assertNotSame(avantReinit, apresReinit,
                "reinitialiser() doit créer de nouveaux objets Registre");
    }

    // ──────────────────────────────────────────
    // 6. estNumeroValide
    // ──────────────────────────────────────────

    @Test
    void testNumeroValideMin() {
        assertTrue(banque.estNumeroValide(0));
    }

    @Test
    void testNumeroValideMax() {
        assertTrue(banque.estNumeroValide(15));
    }

    @Test
    void testNumeroValideMillieu() {
        assertTrue(banque.estNumeroValide(8));
    }

    @Test
    void testNumeroInvalideNegatif() {
        assertFalse(banque.estNumeroValide(-1));
    }

    @Test
    void testNumeroInvalide16() {
        assertFalse(banque.estNumeroValide(16)); // hors borne exacte
    }

    @Test
    void testNumeroInvalideTropGrand() {
        assertFalse(banque.estNumeroValide(100));
    }
}