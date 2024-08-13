package verarbeitung;

/**
 * Ein Girokonto, d.h. ein Konto mit einem Dispo und der Fähigkeit,
 * Überweisungen zu senden und zu empfangen.
 * Grundsätzlich sind Überweisungen und Abhebungen möglich bis
 * zu einem Kontostand von -this.dispo
 * @author Doro
 * @author Dennis Forster
 */
public class Girokonto extends Konto implements Ueberweisungsfaehig {
	/**
	 * Wert, bis zu dem das Konto überzogen werden darf
	 */
	private double dispo;

	public static final double STANDARD_DISPO = 500;
	/**
	 * erzeugt ein leeres, nicht gesperrtes Standard-Girokonto
	 * von Max MUSTERMANN
	 */
	public Girokonto()
	{
		super(Kunde.MUSTERMANN, 99887766);
		this.dispo = STANDARD_DISPO;
	}
	
	/**
	 * erzeugt ein Girokonto mit den angegebenen Werten
	 * @param inhaber Kontoinhaber
	 * @param nummer Kontonummer
	 * @param dispo Dispo
	 * @throws IllegalArgumentException wenn der inhaber null ist oder der angegebene dispo negativ bzw. NaN ist
	 */
	public Girokonto(Kunde inhaber, long nummer, double dispo)
	{
		super(inhaber, nummer);
		if(dispo < 0 || Double.isNaN(dispo)|| Double.isInfinite(dispo))
			throw new IllegalArgumentException("Der Dispo ist nicht gültig!");
		this.dispo = dispo;
	}
	
	/**
	 * liefert den Dispo
	 * @return Dispo von this
	 */
	public double getDispo() {
		return dispo;
	}

	/**
	 * setzt den Dispo neu
	 * @param dispo muss größer sein als 0
	 * @throws IllegalArgumentException wenn dispo negativ bzw. NaN ist
	 */
	public void setDispo(double dispo) {
		if(dispo < 0 || Double.isNaN(dispo)|| Double.isInfinite(dispo))
			throw new IllegalArgumentException("Der Dispo ist nicht gültig!");
		this.dispo = dispo;
	}

	/**
	 *
	 * @param neu Waehrung
	 */
	@Override
	public void waehrungswechsel(Waehrung neu) {
		super.waehrungswechsel(neu);
		if (!getAktuelleWaehrung().equals(Waehrung.EUR)) {
			dispo = getAktuelleWaehrung().waehrungInEuroUmrechnen(dispo);
		}
		dispo = neu.euroInWaehrungUmrechnen(dispo);
		setWaehrung(neu);
	}

	@Override
    public boolean ueberweisungAbsenden(double betrag, 
    		String empfaenger, long nachKontonr, 
    		long nachBlz, String verwendungszweck) 
    				throws GesperrtException 
    {
      if (this.isGesperrt())
            throw new GesperrtException(this.getKontonummer());
        if (betrag < 0 || Double.isNaN(betrag) || Double.isInfinite(betrag)|| empfaenger == null || verwendungszweck == null)
            throw new IllegalArgumentException("Parameter fehlerhaft");
        if (getKontostand() - betrag >= - dispo)
        {
            setKontostand(getKontostand() - betrag);
            return true;
        }
        else
        	return false;
    }

    @Override
    public void ueberweisungEmpfangen(double betrag, String vonName, long vonKontonr, long vonBlz, String verwendungszweck)
    {
        if (betrag < 0 || Double.isNaN(betrag) || Double.isInfinite(betrag)|| vonName == null || verwendungszweck == null)
            throw new IllegalArgumentException("Parameter fehlerhaft");
		setKontostand(getKontostand() + betrag);
    }
    
    @Override
    public String toString()
    {
    	String ausgabe = "-- GIROKONTO --" + System.lineSeparator() +
    	super.toString()
    	+ "Dispo: " + this.dispo + System.lineSeparator();
    	return ausgabe;
    }

	/**
	 * liefert true, falls der Kontostand minus dem abzuhebenden Betrag  groesser oder gleich dem negativen Dispo sind,
	 * false sonst
	 * @param betrag double
	 * @return true, falls der Kontostand minus dem abzuhebenden Betrag  groesser oder gleich dem negativen Dispo sind,
	 * false sonst
	 */
	@Override
	protected boolean kontoNichtGedeckt(double betrag) {
		return getKontostand() - betrag < -dispo;
	}

	/**
	 * wirft IllegalArgumentException falls der gegebene Betrag keine Zahl oder unendlich ist, sonst wird false
	 * zurueckgegeben
	 * @param betrag double
	 * @return false, falls Pruefung geglueckt
	 * @throws IllegalArgumentException falls der gegebene Betrag keine Zahl oder unendlich ist
	 */
	@Override
	protected boolean pruefeObSonstigeAbhebenRegelVerletzt(double betrag) throws IllegalArgumentException {
		if (Double.isNaN(betrag)|| Double.isInfinite(betrag)) {
			throw new IllegalArgumentException("Betrag ungültig");
		}
		return false;
	}
}