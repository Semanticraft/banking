package verwaltung;

import verarbeitung.Konto;
import verarbeitung.Kunde;

/**
 * Abstrakte Fabrik zum Erstellen von Konten
 */
public abstract class KontoFabrik {
    /**
     * Erstellt ein Konto mit gegebenem Inhaber und Kontonummer
     * @param inhaber Kunde
     * @param kontonr long
     * @return neues Konto mit gegebenem Inhaber und Kontonummer
     */
    public abstract Konto kontoErstellen(Kunde inhaber, long kontonr);
}
