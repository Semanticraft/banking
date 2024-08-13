package verarbeitung;

import java.time.LocalDate;

/**
 * ein Sparbuch, d.h. ein Konto, das nur recht eingeschränkt genutzt
 * werden kann. Insbesondere darf man monatlich nur höchstens 2000€
 * abheben, wobei der Kontostand nie unter 0,50€ fallen darf. 
 * @author Doro
 * @author Dennis Forster
 */
public class Sparbuch extends Konto {
	/**
	 * Zinssatz, mit dem das Sparbuch verzinst wird. 0,03 entspricht 3%
	 */
	private double zinssatz;

	/**
	 * Monatlich erlaubter Gesamtbetrag für Abhebungen
	 */
	public static final double ABHEBESUMME = 2000;

	/**
	 * Betrag, der im aktuellen Monat bereits abgehoben wurde
	 */
	private double bereitsAbgehoben = 0;

	/**
	 * Monat und Jahr der letzten Abhebung
	 */
	private LocalDate zeitpunkt = LocalDate.now();

	/**
	 * ein Standard-Sparbuch
	 */
	public Sparbuch() {
		zinssatz = 0.03;
	}

	/**
	 * ein Standard-Sparbuch, das inhaber gehört und die angegebene Kontonummer hat
	 * @param inhaber der Kontoinhaber
	 * @param kontonummer die Wunsch-Kontonummer
	 * @throws IllegalArgumentException wenn inhaber null ist
	 */
	public Sparbuch(Kunde inhaber, long kontonummer) {
		super(inhaber, kontonummer);
		zinssatz = 0.03;
	}

	@Override
	public String toString() {
		String ausgabe = "-- SPARBUCH --" + System.lineSeparator() +
				super.toString()
				+ "Zinssatz: " + this.zinssatz * 100 +"%" + System.lineSeparator();
		return ausgabe;
	}

	/**
	 * liefert true, falls der Kontostand kleiner als der Betrag ist oder der Betrag und die bereits abhehobenen
	 * Betraege die monatliche Abhebesumme ueberschreiten, false sonst
	 * @param betrag double
	 * @return true, falls der Kontostand kleiner als der Betrag ist oder der Betrag und die bereits abhehobenen
	 * 	 * Betraege die monatliche Abhebesumme ueberschreiten, false sonst
	 */
	@Override
	protected boolean kontoNichtGedeckt(double betrag) {
		return getKontostand() - betrag < 0.50 ||
				bereitsAbgehoben + betrag > getAktuelleWaehrung().euroInWaehrungUmrechnen(Sparbuch.ABHEBESUMME);
	}

	/**
	 * wirft IllegalArgumentException falls der gegebene Betrag keine Zahl oder unendlich ist und, sonst wird false
	 * zurueckgegeben
	 * @param betrag double
	 * @return false, falls Pruefungen geglueckt
	 * @throws IllegalArgumentException falls der gegebene Betrag keine Zahl oder unendlich ist
	 */
	@Override
	protected boolean pruefeObSonstigeAbhebenRegelVerletzt(double betrag) throws IllegalArgumentException {
		if (betrag < 0 || Double.isNaN(betrag)|| Double.isInfinite(betrag)) {
			throw new IllegalArgumentException("Betrag ungültig");
		}
		return false;
	}

	/**
	 * setzt den bereits abgehobenen Betrag auf 0, falls die letzte Abhebung in einem anderen Monat stattgefunden hat und
	 * erhoeht den bereits abgehobenen Betrag um den gegebenen Betrag und setzt den Zeitpunkt auf LocaleDate.now()
	 * @param betrag double
	 */
	@Override
	protected void weitereAbhebeAktionen(double betrag) {
		LocalDate heute = LocalDate.now();
		if(heute.getMonth() != zeitpunkt.getMonth() || heute.getYear() != zeitpunkt.getYear()) {
			this.bereitsAbgehoben = 0;
		}
		bereitsAbgehoben += betrag;
		this.zeitpunkt = LocalDate.now();
	}

}
