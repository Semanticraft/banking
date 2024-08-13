import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verarbeitung.Kunde;
import verwaltung.Bank;
import verwaltung.GirokontoFabrik;

/**
 * prueft die clone Methode der Bank
 */
public class BankCloneTest {

    private Bank original;
    private double anfangsbetrag;


    /**
     * erstellt vor jedem Test eine neue Bank mit Standard Kunde (Kunde.MUSTERMANN) und fuegt der Bank ein Girokonto mit
     * einem Anfangsbetrag von 500 Euro hinzu
     */
    @BeforeEach
    public void before() {
        anfangsbetrag = 500;
        original = new Bank(0);
        original.kontoErstellen(new GirokontoFabrik(), Kunde.MUSTERMANN);
        original.geldEinzahlen(0, anfangsbetrag);
    }

    /**
     * prueft, ob die Referenzen der Originalbank und dem Klon der Originalbank ungleich sind
     */
    @Test
    public void cloneBankenReferenzenTest() {
        Bank clone = null;
        try {
            clone = original.clone();
        } catch (ClassCastException e) {
            fail(e);
        }
        assertNotEquals(original, clone);
    }

    /**
     * prueft, ob das Konto der Originalbank denselben Anfangsbetrag wie das geklonte Konto hat und ob die Referenzen
     * der Konten ungleich sind
     */
    @Test
    public void cloneKontenReferenzenTest() {
        Bank copy = original.clone();
        assertEquals(anfangsbetrag, copy.getKontostand(0));
        assertEquals(anfangsbetrag, original.getKontostand(0));
        copy.geldEinzahlen(0, 500);
        assertEquals(anfangsbetrag + 500, copy.getKontostand(0));
        assertEquals(anfangsbetrag, original.getKontostand(0));
    }
}
