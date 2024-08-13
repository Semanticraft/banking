package verarbeitung;

import org.decimal4j.util.DoubleRounder;

import java.math.RoundingMode;

/**
 * Enum welches die Umrechnungskurse und Symbole von fest an den Euro gebundenen Waehrungen beinhaltet und Methoden zur
 * Waehrungsumrechnung und dem zurueckliefern des Symbols der jeweiligen Waehrung liefert
 * @author Dennis Forster
 */
public enum Waehrung {

    EUR(1, "€"), BGN(1.9558, "лв"), DKK(61.62, "dkr."),
    MKD(7.4604, "Ден");

    private final double umrechnungskurs;
    private final String symbol;

    /**
     * Konstruktor, der den Waehrungen ihre Umrechnungskurse zum Euro zuweist
     * @param umrechnungskurs double
     */
    Waehrung(double umrechnungskurs, String symbol) {
        this.umrechnungskurs = umrechnungskurs;
        this.symbol = symbol;
    }

    /**
     * rechnet den in Euro angegegeben Betrag in die jeweilige Waehrung um
     * @param betrag double
     * @return betrag * umrechnungskurs von this
     * @throws IllegalArgumentException falls betrag unendlich oder keine Zahl ist
     * @throws ArithmeticException falls es zu einem arithmetischen Overflow kommt
     */
    public double euroInWaehrungUmrechnen(double betrag) throws IllegalArgumentException, ArithmeticException {
        if (Double.isNaN(betrag) || Double.isInfinite(betrag)) {
            throw new IllegalArgumentException("Falscher Betrag");
        }
        if(betrag < 0 && (betrag * this.umrechnungskurs) >= 0 || betrag > 0 && (betrag * this.umrechnungskurs) <= 0) {
            throw new ArithmeticException("Betrag ueberschreitet Grenze");
        }
        return DoubleRounder.round((betrag * this.umrechnungskurs), 2, RoundingMode.HALF_EVEN);
    }

    /**
     * rechnet den in this-Waehrung angegebenen Betrag in Euro um
     * @param betrag double
     * @return betrag / umrechnungskurs von this
     * @throws IllegalArgumentException falls betrag unendlich oder keine Zahl ist
     */
    public double waehrungInEuroUmrechnen(double betrag) throws IllegalArgumentException {
        if(Double.isNaN(betrag) || Double.isInfinite(betrag)) {
            throw new IllegalArgumentException("Falscher Betrag");
        }
        return DoubleRounder.round((betrag / this.umrechnungskurs), 2, RoundingMode.HALF_EVEN);
    }

    /**
     * liefert Symbol der this-Waehrung zurueck
     * @return symbol
     */
    public String getSymbol() {
        return symbol;
    }
}
