package verarbeitung;

import com.google.common.primitives.Doubles;
import javafx.beans.property.*;
import javafx.beans.binding.Bindings;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

// TODO: final wiederherstellen, Serialization*, Javadoc*
/**
 * stellt ein allgemeines Bank-Konto dar
 */
public abstract class Konto implements Comparable<Konto>, Serializable
{
	/** 
	 * der Kontoinhaber
	 */
	private Kunde inhaber;

	/**
	 * die Kontonummer
	 */
	private final long nummer;

	/**
	 * der aktuelle Kontostand
	 */
	private ReadOnlyDoubleWrapper kontostand = new ReadOnlyDoubleWrapper();

	private Waehrung waehrung = Waehrung.EUR;

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private ReadOnlyBooleanWrapper kontostandImPlus = new ReadOnlyBooleanWrapper(false);

	/**
	 * Methode gibt false zurueck, falls Konto nicht gedeckt und wirft Exceptions im Fall von gesperrten Konten, einem
	 * nicht positiven Betrag oder sonstigen Verstoessen gegen Abheberegeln, sonst wird true zurueckgegeben
	 * @param betrag double
	 * @return true, falls Pruefungen geglueckt, false sonst
	 * @throws GesperrtException falls Konto gesperrt
	 * @throws IllegalArgumentException falls Betrag nicht positiv
	 */
	private boolean abhebenPruefungen(double betrag) throws GesperrtException, IllegalArgumentException {
		if (isGesperrt()) {
			throw new GesperrtException(getKontonummer());
		}
		if (betrag <= 0) {
			throw new IllegalArgumentException("Betrag muss positiv sein");
		}
		if (kontoNichtGedeckt(betrag)) {
			return false;
		}
		return !pruefeObSonstigeAbhebenRegelVerletzt(betrag);
	}

	/**
	 * verringert den Kontostand um den angegebenen Betrag der jeweiligen Waehrung, falls das Konto gedeckt ist
	 * @param betrag double
	 * @param w Waehrung
	 * @return false falls Konto nicht gedeckt, sonst true
	 * @throws GesperrtException falls this gesperrt ist
	 */
	public final boolean abheben(double betrag, Waehrung w) throws GesperrtException {
		double betragInAktuellerWaehrung = betragAnKontoWaehrungAngleichen(betrag, w);
		weitereAbhebeAktionen(betrag);
		if (!abhebenPruefungen(betragInAktuellerWaehrung)) {
			return false;
		}
		kontostand.set(kontostand.get() - betragInAktuellerWaehrung);
		return true;
	}

	/**
	 * gleicht den gegebenen Betrag mit der gegebenen Waehrung an die Kontowaehrung an
	 * @param betrag double
	 * @param waehrung Waehrung
	 * @returngegebenen Betrag mit der gegebenen Waehrung an die Kontowaehrung angeglichen
	 */
	private double betragAnKontoWaehrungAngleichen(double betrag, Waehrung waehrung) {
		if (getAktuelleWaehrung().equals(Waehrung.EUR) && waehrung.equals(Waehrung.EUR)) {
			return betrag;
		} else if (!getAktuelleWaehrung().equals(Waehrung.EUR) && waehrung.equals(Waehrung.EUR)) {
			return getAktuelleWaehrung().euroInWaehrungUmrechnen(betrag);
		} else if (getAktuelleWaehrung().equals(Waehrung.EUR) && !waehrung.equals(Waehrung.EUR)) {
			return waehrung.waehrungInEuroUmrechnen(betrag);
		} else {
			double betragInEuro = waehrung.waehrungInEuroUmrechnen(betrag);
			return getAktuelleWaehrung().euroInWaehrungUmrechnen(betragInEuro);
		}
	}

	/**
	 * gibt true zureucek, falls Konto nicht gedeckt, false sonst
	 * @param betrag double
	 * @return true, falls Konto nicht gedeckt, false sonst
	 */
	protected abstract boolean kontoNichtGedeckt(double betrag);

	/**
	 * gibt true zurueck, falls sonstige abheben() Regeln verletzt wurden, false sonst
	 */
	protected boolean pruefeObSonstigeAbhebenRegelVerletzt(double betrag) {return false;}

	/**
	 * fuehrt weitere abheben() Operationen aus, die spezifisch fuer Unterklassen von Konto sind
	 */
	protected void weitereAbhebeAktionen(double betrag) {}

	/**
	 * fuegt den betrag in der angegebenen Waehrung zum Kontostand hinzu
	 * @param betrag double
	 * @param w Waehrung
	 */
	public void einzahlen(double betrag, Waehrung w) {
		// TODO: Code Dopplung eliminieren
		if (betrag < 0 || !Doubles.isFinite(betrag)) {
			throw new IllegalArgumentException("Falscher Betrag");
		}
		double betragInAktuellerWaehrung = w.waehrungInEuroUmrechnen(betrag);
		betragInAktuellerWaehrung = getAktuelleWaehrung().euroInWaehrungUmrechnen(betragInAktuellerWaehrung);
		kontostand.set(kontostand.get() + betragInAktuellerWaehrung);
	}

	/**
	 * liefert die aktuelle Waehrung des Kontos zurueck
	 * @return die aktuelle Waehrung des Kontos
	 */
	public Waehrung getAktuelleWaehrung() {
		return waehrung;
	}

	/**
	 * fuehrt einen Waehrungswechsel des Kontos von der aktuellen Waehrung zur neuen Waehrung durch
	 * @param neu Waehrung
	 */
	public void waehrungswechsel(Waehrung neu) {
		if (!getAktuelleWaehrung().equals(Waehrung.EUR)) {
			setKontostand(getAktuelleWaehrung().waehrungInEuroUmrechnen(getKontostand()));
			setWaehrung(neu);
		}
		setKontostand(neu.euroInWaehrungUmrechnen(getKontostand()));
		setWaehrung(neu);
	}

	/**
	 * setzt den aktuellen Kontostand auf den neuen Kontostand und benachrichtigt alle Beobachter ueber die Aenderung
	 * @param kontostand neuer Kontostand
	 */
	protected void setKontostand(double kontostand) {
		propertyChangeSupport.firePropertyChange("kontostand", this.kontostand.get(), kontostand);
		this.kontostand.set(kontostand);
	}

	// TODO: ersetzen der Methode durch super-Aufrufe am Ende der Waehrungswechselmethoden
	/**
	 * setzt die Kontowaehrung auf die neue Waehrung
	 * @param neu Waehrung
	 */
	protected void setWaehrung(Waehrung neu) {
		waehrung = neu;
	}

	/**
	 * Wenn das Konto gesperrt ist (gesperrt = true), können keine Aktionen daran mehr vorgenommen werden,
	 * die zum Schaden des Kontoinhabers wären (abheben, Inhaberwechsel)
	 */
	private BooleanProperty gesperrt = new SimpleBooleanProperty(false);

	/**
	 * Setzt die beiden Eigenschaften kontoinhaber und kontonummer auf die angegebenen Werte,
	 * der anfängliche Kontostand wird auf 0 gesetzt.
	 *
	 * @param inhaber der Inhaber
	 * @param kontonummer die gewünschte Kontonummer
	 * @throws IllegalArgumentException wenn der inhaber null ist
	 */
	public Konto(Kunde inhaber, long kontonummer) {
		if(inhaber == null)
			throw new IllegalArgumentException("Inhaber darf nicht null sein!");
		this.inhaber = inhaber;
		this.nummer = kontonummer;
		kontostand.set(0);
		gesperrt.set(false);
		kontostandImPlus.bind(Bindings.greaterThanOrEqual(kontostand, 0));
	}
	
	/**
	 * setzt alle Eigenschaften des Kontos auf Standardwerte
	 */
	public Konto() {
		this(Kunde.MUSTERMANN, 1234567);
		kontostandImPlus.bind(kontostandProperty().greaterThanOrEqualTo(0));
	}

	/**
	 * liefert den Kontoinhaber zurück
	 * @return   der Inhaber
	 */
	public final Kunde getInhaber() {
		return this.inhaber;
	}
	
	/**
	 * setzt den Kontoinhaber
	 * @param kinh   neuer Kontoinhaber
	 * @throws GesperrtException wenn das Konto gesperrt ist
	 * @throws IllegalArgumentException wenn kinh null ist
	 */
	public final void setInhaber(Kunde kinh) throws GesperrtException{
		if (kinh == null)
			throw new IllegalArgumentException("Der Inhaber darf nicht null sein!");
		if(isGesperrt())
			throw new GesperrtException(this.nummer);        
		this.inhaber = kinh;

	}
	
	/**
	 * liefert den aktuellen Kontostand
	 * @return   Kontostand
	 */
	public double getKontostand() {
		return kontostand.get();
	}

	/**
	 * liefert die Kontonummer zurück
	 * @return   Kontonummer
	 */
	public final long getKontonummer() {
		return nummer;
	}

	/**
	 * liefert zurück, ob das Konto gesperrt ist oder nicht
	 * @return true, wenn das Konto gesperrt ist
	 */
	public final boolean isGesperrt() {
		return gesperrt.get();
	}
	
	/**
	 * Erhöht den Kontostand um den eingezahlten Betrag.
	 *
	 * @param betrag double
	 * @throws IllegalArgumentException wenn der betrag negativ ist 
	 */
	public void einzahlen(double betrag) {
		if (betrag < 0 || !Doubles.isFinite(betrag)) {
			throw new IllegalArgumentException("Falscher Betrag");
		}
		setKontostand(getKontostand() + betrag);
	}
	
	@Override
	public String toString() {
		String ausgabe;
		ausgabe = "Kontonummer: " + this.getKontonummerFormatiert()
				+ System.getProperty("line.separator");
		ausgabe += "Inhaber: " + this.inhaber;
		ausgabe += "Aktueller Kontostand: " + getKontostandFormatiert() + " ";
		ausgabe += this.getGesperrtText() + System.getProperty("line.separator");
		return ausgabe;
	}

	/**
	 * Mit dieser Methode wird der geforderte Betrag vom Konto abgehoben, wenn es nicht gesperrt ist
	 * und die speziellen Abheberegeln des jeweiligen Kontotyps die Abhebung erlauben
	 *
	 * @param betrag double
	 * @throws GesperrtException wenn das Konto gesperrt ist
	 * @throws IllegalArgumentException wenn der betrag negativ oder unendlich oder NaN ist 
	 * @return true, wenn die Abhebung geklappt hat, 
	 * 		   false, wenn sie abgelehnt wurde
	 */
	public final boolean abheben(double betrag)
								throws GesperrtException {
		// TODO: Feedback implementieren
		weitereAbhebeAktionen(betrag);
		if (!abhebenPruefungen(betrag)) {
			return false;
		}
		kontostand.set(kontostand.get() - betrag);
		return true;
	}
	
	/**
	 * sperrt das Konto, Aktionen zum Schaden des Benutzers sind nicht mehr möglich.
	 */
	public final void sperren() {
		gesperrt.set(true);
	}

	/**
	 * entsperrt das Konto, alle Kontoaktionen sind wieder möglich.
	 */
	public final void entsperren() {
		gesperrt.set(false);
	}

	/**
	 * liefert eine String-Ausgabe, wenn das Konto gesperrt ist
	 * @return "GESPERRT", wenn das Konto gesperrt ist, ansonsten ""
	 */
	public final String getGesperrtText()
	{
		if (isGesperrt())
		{
			return "GESPERRT";
		}
		else
		{
			return "";
		}
	}
	
	/**
	 * liefert die ordentlich formatierte Kontonummer
	 * @return auf 10 Stellen formatierte Kontonummer
	 */
	public String getKontonummerFormatiert()
	{
		return String.format("%10d", this.nummer);
	}
	
	/**
	 * liefert den ordentlich formatierten Kontostand
	 * @return formatierter Kontostand mit 2 Nachkommastellen und Währungssymbol
	 */
	public String getKontostandFormatiert()
	{
		return String.format("%10.2f " + getAktuelleWaehrung().getSymbol(), this.getKontostand());
	}

	/**
	 * Vergleich von this mit other; Zwei Konten gelten als gleich,
	 * wen sie die gleiche Kontonummer haben
	 * @param other das Vergleichskonto
	 * @return true, wenn beide Konten die gleiche Nummer haben
	 */
	@Override
	public boolean equals(Object other)
	{
		if(this == other)
			return true;
		if(other == null)
			return false;
		if(this.getClass() != other.getClass())
			return false;
		if(this.nummer == ((Konto)other).nummer)
			return true;
		else
			return false;
	}
	
	@Override
	public int hashCode()
	{
		return 31 + (int) (this.nummer ^ (this.nummer >>> 32));
	}

	@Override
	public int compareTo(Konto other)
	{
		if(other.getKontonummer() > this.getKontonummer())
			return -1;
		if(other.getKontonummer() < this.getKontonummer())
			return 1;
		return 0;
	}

	/**
	 * fuegt Beobachter fuer Aenderungen an Konto-Eigenschaften hinzu
	 * @param pcl PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		propertyChangeSupport.addPropertyChangeListener(pcl);
	}

	/**
	 * entfernt Beobachter
	 * @param pcl PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		propertyChangeSupport.removePropertyChangeListener(pcl);
	}

	/**
	 * liefert kontostand als ReadOnlyDoubleWrapper
	 * @return kontostand als ReadOnlyDoubleWrapper
	 */
	public ReadOnlyDoubleProperty kontostandProperty() {
		return kontostand.getReadOnlyProperty();
	}

	/**
	 * liefert gesperrt als BooleanProperty
	 * @return gesperrt als BooleanProperty
	 */
	public BooleanProperty gesperrtProperty() {
		return gesperrt;
	}

	/**
	 * liefert den booleschen Wert in kontostandImPlus
	 * @return boolescher Wert in kontostandImPlus
	 */
	public boolean getKontostandImPlus() {
		return kontostandImPlus.get();
	}

	/**
	 * setzt den Wert in kontostandImPlus auf den angegebenen Boolean
	 * @param kontostandImPlus boolean
	 */
	private void setKontostandImPlus(boolean kontostandImPlus) {
		this.kontostandImPlus.set(kontostandImPlus);
	}

	/**
	 * liefert kontostandImPlus als ReadOnlyBooleanProperty
	 * @return kontostandImPlus als ReadOnlyBooleanProperty
	 */
	public ReadOnlyBooleanProperty kontostandImPlusProperty() {
		return kontostandImPlus.getReadOnlyProperty();
	}
}
