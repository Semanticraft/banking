import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import verarbeitung.*;
import verwaltung.Bank;
import verwaltung.MockKontoFabrik;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.withSettings;

/**
 * Testklasse für die Bankverwaltung
 */
public class BankverwaltungTest {

    Bank testBank;
    Konto empfaengerMock, senderMock, nichtUeberweisungsfaehigMock, ueberweisungsfehlschlagMock;
    Kunde kundeMock;

    /**
     * es wird eine Testbank mit 4 Mock-Konten vor jedem Test angelegt
     */
    @BeforeEach
    public void mockitoSetup() throws GesperrtException {
        testBank = new Bank(0);

        senderMock = Mockito.mock(Konto.class, withSettings().extraInterfaces(Ueberweisungsfaehig.class));
        empfaengerMock = Mockito.mock(Konto.class, withSettings().extraInterfaces(Ueberweisungsfaehig.class));
        nichtUeberweisungsfaehigMock = Mockito.mock(Konto.class);
        ueberweisungsfehlschlagMock = Mockito.mock(Konto.class, withSettings().extraInterfaces(Ueberweisungsfaehig.class));
        kundeMock = Mockito.mock(Kunde.class);

        long senderMockNummer = testBank.kontoErstellen(new MockKontoFabrik(senderMock), null);
        long empfaengerMockNummer = testBank.kontoErstellen(new MockKontoFabrik(empfaengerMock), null);
        long nichtUeberweisungsfaehigMockNummer = testBank.kontoErstellen(
                new MockKontoFabrik(nichtUeberweisungsfaehigMock), null);
        long ueberweisungsfehlschlagMockNummer = testBank.kontoErstellen(
                new MockKontoFabrik(ueberweisungsfehlschlagMock), null);

        Mockito.when(kundeMock.getName()).thenReturn("Mustermann, Max");

        Mockito.when(empfaengerMock.getKontonummer()).thenReturn(empfaengerMockNummer);
        Mockito.when(empfaengerMock.getKontostand()).thenReturn(50.00);
        Mockito.when(empfaengerMock.getInhaber()).thenReturn(kundeMock);
        Mockito.doThrow(IllegalArgumentException.class)
                .when((Ueberweisungsfaehig) empfaengerMock)
                .ueberweisungEmpfangen(
                        anyDouble(),
                        anyString(),
                        anyLong(),
                        anyLong(),
                        eq(null)
                );

        Mockito.when(senderMock.getKontonummer()).thenReturn((senderMockNummer));
        Mockito.when(senderMock.getKontostand()).thenReturn(50.00);
        Mockito.when(senderMock.getInhaber()).thenReturn(kundeMock);
        Mockito.when(((Ueberweisungsfaehig) senderMock).ueberweisungAbsenden(
                anyDouble(),
                anyString(),
                anyLong(),
                anyLong(),
                anyString())).thenReturn(true);
        Mockito.when(((Ueberweisungsfaehig) senderMock).ueberweisungAbsenden(
                anyDouble(),
                anyString(),
                anyLong(),
                anyLong(),
                eq(null))).thenReturn(true);

        Mockito.when(ueberweisungsfehlschlagMock.getKontonummer()).thenReturn((ueberweisungsfehlschlagMockNummer));
        Mockito.when(ueberweisungsfehlschlagMock.getKontostand()).thenReturn(50.00);
        Mockito.when(ueberweisungsfehlschlagMock.getInhaber()).thenReturn(kundeMock);
        Mockito.when(((Ueberweisungsfaehig) ueberweisungsfehlschlagMock).ueberweisungAbsenden(
                anyDouble(),
                anyString(),
                anyLong(),
                anyLong(),
                anyString())).thenReturn(false);
    }

    /**
     * prueft alle Exception-Faelle von geldUeberweisen()
     * @throws GesperrtException falls das empfangende Konto gesperrt ist
     */
    @Test
    public void geldUeberweisenFehlerfaelleTest() throws GesperrtException {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            testBank.geldUeberweisen(-1, 0, 50.0, "");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            testBank.geldUeberweisen(0, -1, 50.0, "");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            testBank.geldUeberweisen(2, 0, 50.0, "");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            testBank.geldUeberweisen(0, 2, 50.0, "");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            testBank.geldUeberweisen(0, 0, 50, "");
        });
    }

    // TODO: works in debugging mode, but not outside of it, why?
    /**
     * prueft die Rueckgabewerte und inneren Methodenaufrufe von geldUeberweisen()
     * @throws GesperrtException falls das empfangende Konto gesperrt ist
     */
    @Test
    public void geldUeberweisenRueckgabewerteUndMethodenaufrufeTest() throws GesperrtException {
        Assertions.assertTrue(testBank.geldUeberweisen(0, 1, 25, ""));
        Mockito.verify((Ueberweisungsfaehig) empfaengerMock, Mockito.times(1)).ueberweisungEmpfangen(25,
                empfaengerMock.getInhaber().getName(), 0, testBank.getBankleitzahl(), "");
        Mockito.verify((Ueberweisungsfaehig) empfaengerMock, Mockito.times(1)).ueberweisungAbsenden(25,
                senderMock.getInhaber().getName(), 0, testBank.getBankleitzahl(), "");

        Assertions.assertFalse(testBank.geldUeberweisen(0, 1, 50, null));
        // Geld wird wegen Exception bei ueberweisungEmpfangen wieder eingezahlt
        Mockito.verify(senderMock).einzahlen(50);

        Assertions.assertFalse(testBank.geldUeberweisen(3, 1, 25, ""));
        Mockito.verify((Ueberweisungsfaehig) empfaengerMock, Mockito.times(0)).ueberweisungAbsenden(25,
                ueberweisungsfehlschlagMock.getInhaber().getName(), 0, testBank.getBankleitzahl(), "");
        Mockito.verify(ueberweisungsfehlschlagMock, Mockito.times(0)).einzahlen(25);
    }

    /**
     * es wird eine Ueberweisung ueberprueft, die Double.MAX_VALUE ueberschreitet
     */
    @Test
    public void geldUeberweisenRandbedingungenTest() {
        Assertions.assertThrows(Exception.class, () -> {
            testBank.geldEinzahlen(0, Double.MAX_VALUE - 50);
            testBank.geldUeberweisen(0, 2, Double.MAX_VALUE, "");
            testBank.geldEinzahlen(0, 1);
            testBank.geldUeberweisen(0, 2, 1, "");
        });
    }

    /**
     * prueft, ob die Methode kontoLoeschen fuer die Mock-Konten in der Bank-Kontenliste die richtigen Rückgabewerte zurückliefert
     */
    @Test
    public void kontoLoeschenTest() {
        Assertions.assertTrue(testBank.kontoLoeschen(0));
        Assertions.assertFalse(testBank.kontoLoeschen(0));
        Assertions.assertFalse(testBank.kontoLoeschen(-1));
        Assertions.assertFalse(testBank.kontoLoeschen(2));
        Assertions.assertTrue(testBank.kontoLoeschen(1));
        Assertions.assertFalse(testBank.kontoLoeschen(1));
    }

    /**
     * loescht die Mock-Konten und deren testBank Eintraege nach jedem Test
     */
    @AfterEach
    public void tearDown() {
        testBank.kontoLoeschen(0);
        testBank.kontoLoeschen(1);
        testBank.kontoLoeschen(2);
        testBank.kontoLoeschen(3);
        Mockito.reset(senderMock, empfaengerMock, nichtUeberweisungsfaehigMock, ueberweisungsfehlschlagMock);
    }

}
