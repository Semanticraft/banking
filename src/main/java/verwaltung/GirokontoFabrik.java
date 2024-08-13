package verwaltung;

import verarbeitung.Girokonto;
import verarbeitung.Konto;
import verarbeitung.Kunde;

/**
 * Fabrik zum erstellen von Girokonten
 */
public class GirokontoFabrik extends KontoFabrik {

    /**
     * erstellt ein neues Girokonto mit gegebenen Inhaber und Kontonummer, sowie Standard-Dispo
     * @param inhaber Kunde
     * @param kontonr long
     * @return neues Girokonto mit gegebenen Inhaber und Kontonummer, sowie Standard-Dispo
     */
    @Override
    public Konto kontoErstellen(Kunde inhaber, long kontonr) {
        return new Girokonto(inhaber, kontonr, Girokonto.STANDARD_DISPO);
    }
}
