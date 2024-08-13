package verwaltung;

import verarbeitung.Konto;
import verarbeitung.Kunde;

/**
 * Fabrik zur Erstellung von abstrakten Konten, die in Mockito als Mocks dienen
 */
public class MockKontoFabrik extends KontoFabrik {

    private final Konto mockKonto;

    /**
     * Konstruktor fuer MockKontoFabrik, setzt this.mockKonto auf mockKonto
     * @param mockKonto Konto
     */
    public MockKontoFabrik(Konto mockKonto) {
        this.mockKonto = mockKonto;
    }

    /**
     * liefert mockKonto
     * @param inhaber Kunde
     * @param kontonr long
     * @return mockKonto
     */
    @Override
    public Konto kontoErstellen(Kunde inhaber, long kontonr) {
        return mockKonto;
    }
}
