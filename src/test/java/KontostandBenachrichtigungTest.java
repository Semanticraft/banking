import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import verarbeitung.*;

/**
 * Tests zur Ueberpruefung, ob angemeldete Kontostand Benachrichtiger in den korrekten Situationen bei Kontostandsaenderungen
 * benachrichtigt werden
 */
public class KontostandBenachrichtigungTest {

    private Girokonto giroSubjekt;
    private Sparbuch sparbuchSubjekt;
    private KontostandBenachrichtiger beobachter;

    /**
     * vor jedem Test wird ein Mock-Objekt eines KontostandBenachrichtigers "beobachter" und ein Girokonto und Sparbuch
     * als Subjekte angelegt, die den Beobachter anmelden
     */
    @BeforeEach
    public void before() {
        beobachter = Mockito.mock(KontostandBenachrichtiger.class);
        giroSubjekt = new Girokonto();
        giroSubjekt.addPropertyChangeListener(beobachter);
        sparbuchSubjekt = new Sparbuch();
        sparbuchSubjekt.addPropertyChangeListener(beobachter);
    }

    /**
     * es wird geprueft, ob beim einfachen und korrekten einzahlen und abheben in Girokonten, der Beobachter eine
     * Benachrichtigung erhaelt
     * @throws GesperrtException falls das Konto gesperrt ist
     */
    @Test
    public void einfachesEinzahlenUndAbhebenGiroTest() throws GesperrtException {
        giroSubjekt.einzahlen(500);
        Mockito.verify(beobachter, Mockito.times(1)).propertyChange(Mockito.any());
        giroSubjekt.abheben(250);
        Mockito.verify(beobachter, Mockito.times(2)).propertyChange(Mockito.any());
    }

    /**
     * es wird geprueft, ob beim einfachen und korrekten einzahlen und abheben in Sparbuechern, der Beobachter eine
     * Benachrichtigung erhaelt
     * @throws GesperrtException falls das Konto gesperrt ist
     */
    @Test
    public void einfachesEinzahlenUndAbhebenSparbuchTest() throws GesperrtException {
        sparbuchSubjekt.einzahlen(500);
        Mockito.verify(beobachter, Mockito.times(1)).propertyChange(Mockito.any());
        sparbuchSubjekt.abheben(250);
        Mockito.verify(beobachter, Mockito.times(2)).propertyChange(Mockito.any());
    }

    /**
     * es wird geprueft, ob beim korrekten einzahlen und abheben in Girokonten, mit angegebener Waehrung, der Beobachter
     * eine Benachrichtigung erhaelt
     * @throws GesperrtException falls das Konto gesperrt ist
     */
    @Test
    public void einzahlenUndAbhebenMitWaehrungGiroTest() throws GesperrtException {
        giroSubjekt.einzahlen(500, Waehrung.BGN);
        Mockito.verify(beobachter, Mockito.times(1)).propertyChange(Mockito.any());
        giroSubjekt.abheben(250, Waehrung.DKK);
        Mockito.verify(beobachter, Mockito.times(2)).propertyChange(Mockito.any());
    }

    /**
     * es wird geprueft, ob beim korrekten einzahlen und abheben in Girokonten, mit angegebener Waehrung, der Beobachter
     * eine Benachrichtigung erhaelt
     * @throws GesperrtException falls das Konto gesperrt ist
     */
    @Test
    public void einzahlenUndAbhebenMitWaehrungSparbuchTest() throws GesperrtException {
        sparbuchSubjekt.einzahlen(500, Waehrung.EUR);
        Mockito.verify(beobachter, Mockito.times(1)).propertyChange(Mockito.any());
        sparbuchSubjekt.abheben(250, Waehrung.MKD);
        Mockito.verify(beobachter, Mockito.times(2)).propertyChange(Mockito.any());
    }

    /**
     * es wird geprueft, ob nach Abmeldung des Beobachters keine Benachrichtigungen bei Kontostandsaenderungen mehr weitergegeben
     * werden
     */
    @Test
    public void abmeldungTest() {
        giroSubjekt.removePropertyChangeListener(beobachter);
        giroSubjekt.einzahlen(500);
        Mockito.verify(beobachter, Mockito.times(0)).propertyChange(Mockito.any());
    }
}
