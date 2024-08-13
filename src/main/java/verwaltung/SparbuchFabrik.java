package verwaltung;

import verarbeitung.Konto;
import verarbeitung.Kunde;
import verarbeitung.Sparbuch;

/**
 * Fabrik zum erstellen von Sparbuechern
 */
public class SparbuchFabrik extends KontoFabrik {
    /**
     * erstellt Konto mit gegebenen Inhaber und Kontonummer
     * @param inhaber Kunde
     * @param kontonr long
     * @return neues Konto mit gegebenen Inhaber und Kontonummer
     */
    @Override
    public Konto kontoErstellen(Kunde inhaber, long kontonr) {
        return new Sparbuch(inhaber, kontonr);
    }
}
