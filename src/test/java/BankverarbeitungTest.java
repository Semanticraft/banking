import verarbeitung.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

/**
 * Testklasse für die Bankverarbeitung
 * @author Dennis Forster
 */
public class BankverarbeitungTest {

    Kunde kunde;
    Girokonto girokonto;
    Sparbuch sparbuch;

    /**
     * vor jedem Test wird ein Testkunde kunde, ein Girokonto girokonto und ein Sparbuch sparbuch angelegt und in beide
     * Konten werden 100 Euro eingezahlt
     */
    @BeforeEach
    public void before() {
        kunde = new Kunde("Dorothea", "Hubrich", "zuhause", LocalDate.parse("1976-07-13"));
        girokonto = new Girokonto(kunde, 1234, 1000.0);
        sparbuch = new Sparbuch(kunde, 1234);
        girokonto.einzahlen(100);
        sparbuch.einzahlen(100);
    }

    /**
     * es wird das Einzahlen von Euros und der Waehrungswechsel des Girokontos von Euro auf jeweils alle im Enum Waehrung
     * vorhandenen Waehrungen und dann wieder zurück zum Euro getestet und das Dispo wird auf Korrektheit überprüft
     */
    @Test
    public void waehrungswechselGiroTest() {
        girokonto.waehrungswechsel(Waehrung.MKD);
        Assertions.assertEquals(746.04, girokonto.getKontostand(), 0.001);
        Assertions.assertEquals(Waehrung.MKD, girokonto.getAktuelleWaehrung());
        girokonto.waehrungswechsel(Waehrung.DKK);
        Assertions.assertEquals(6162, girokonto.getKontostand(), 0.001);
        Assertions.assertEquals(Waehrung.DKK, girokonto.getAktuelleWaehrung());
        girokonto.waehrungswechsel(Waehrung.BGN);
        Assertions.assertEquals(195.58, girokonto.getKontostand(), 0.001);
        Assertions.assertEquals(Waehrung.BGN, girokonto.getAktuelleWaehrung());
        girokonto.waehrungswechsel(Waehrung.EUR);
        Assertions.assertEquals(100, girokonto.getKontostand(), 0.001);
        Assertions.assertEquals(Waehrung.EUR, girokonto.getAktuelleWaehrung());
        Assertions.assertEquals(1000, girokonto.getDispo(), 0.001);
    }

    /**
     * es wird das Einzahlen von Euros und der Waehrungswechsel des Sparbuchs von Euro auf jeweils alle im Enum Waehrung
     * vorhandenen Waehrungen und dann wieder zurück zum Euro getestet
     */
    @Test
    public void waehrungswechselSparbuchTest() {
        sparbuch.waehrungswechsel(Waehrung.MKD);
        Assertions.assertEquals(746.04, sparbuch.getKontostand(), 0.001);
        Assertions.assertEquals(Waehrung.MKD, sparbuch.getAktuelleWaehrung());
        sparbuch.waehrungswechsel(Waehrung.DKK);
        Assertions.assertEquals(6162, sparbuch.getKontostand(), 0.001);
        Assertions.assertEquals(Waehrung.DKK, sparbuch.getAktuelleWaehrung());
        sparbuch.waehrungswechsel(Waehrung.BGN);
        Assertions.assertEquals(195.58, sparbuch.getKontostand(), 0.001);
        Assertions.assertEquals(Waehrung.BGN, sparbuch.getAktuelleWaehrung());
        sparbuch.waehrungswechsel(Waehrung.EUR);
        Assertions.assertEquals(100, sparbuch.getKontostand(), 0.001);
        Assertions.assertEquals(Waehrung.EUR, sparbuch.getAktuelleWaehrung());
    }

    /**
     * es wird das Abheben von Euros, sowie das Abheben von BGN in einem in Euro geführten Girokonto getestet
     * @throws GesperrtException falls das Konto gesperrt ist
     */
    @Test
    public void abhebenGiroTest() throws GesperrtException {
        girokonto.abheben(50, Waehrung.EUR);
        Assertions.assertEquals(50, girokonto.getKontostand(), 0.001);
        girokonto.abheben(19.558, Waehrung.BGN);
        Assertions.assertEquals(40, girokonto.getKontostand(), 0.001);
    }

    /**
     * es wird das Abheben von Euros, sowie das Abheben von MKD in einem in Euro geführten Sparbuch getestet
     * @throws GesperrtException falls das Konto gesperrt ist
     */
    @Test
    public void abhebenSparbuchTest() throws GesperrtException {
        sparbuch.abheben(50, Waehrung.EUR);
        Assertions.assertEquals(50, sparbuch.getKontostand(), 0.001);
        sparbuch.abheben(74.604, Waehrung.MKD);
        Assertions.assertEquals(40, sparbuch.getKontostand(), 0.001);
    }
}
