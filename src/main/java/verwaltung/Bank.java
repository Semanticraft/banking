package verwaltung;

import verarbeitung.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Klasse zur Verwaltung von Konten
 */
public class Bank implements Cloneable, Serializable {

    private final long bankleitzahl;
    private long kontonummerZaehler;
    private HashMap<Long, Konto> kontenliste = new HashMap<>();

    /**
     * Konstruktor für eine Bank
     * @param bankleitzahl long
     * @throws IllegalArgumentException falls angegebene Bankleitzahl neagtiv ist
     */
    public Bank(long bankleitzahl) throws IllegalArgumentException {
        if(bankleitzahl < 0) {
            throw new IllegalArgumentException("Bankleitzahl kann nicht negativ sein");
        }
        this.bankleitzahl = bankleitzahl;
    }

    /**
     * liefert die Bankleitzahl von this
     * @return Bankleitzahl von this
     */
    public long getBankleitzahl() {
        return bankleitzahl;
    }

    /**
     * fügt der Kontoliste ein Konto für den angegebenen Inhaber hinzu
     * @param inhaber Kunde
     * @return kontonummer des neuen Kontos
     */
    public long kontoErstellen(KontoFabrik fabrik, Kunde inhaber) {
        kontenliste.put(kontonummerZaehler, fabrik.kontoErstellen(inhaber, kontonummerZaehler));
        return kontonummerZaehler++;
    }

    /**
     * liefert einen String aller Konten in der Kontoliste
     * @return String aller Konten in der Kontoliste
     */
    public String getAlleKonten() {
        StringBuilder alleKonten = new StringBuilder();
        for (Konto konto : kontenliste.values()) {
            alleKonten.append(konto).append(System.lineSeparator());
        }
        return alleKonten.toString();
    }

    /**
     * liefert eine ArrayList von Konten in der Kontoliste
     * @return ArrayList von Konten in Kontoliste
     */
    public List<Long> getAlleKontonummern() {
        return new ArrayList<>(kontenliste.keySet());
    }

    /**
     * hebt den angegebenen Betrag vom Konto mit der Kontonummer von ab
     * @param von long
     * @param betrag double
     * @return true, falls die Abhebung erfolgreich war, sonst false
     * @throws GesperrtException falls das Konto gesperrt ist
     */
    public boolean geldAbheben(long von, double betrag) throws GesperrtException {
        return kontenliste.get(von).abheben(betrag);
    }

    /**
     * zahlt den angegebenen Betrag auf das Konto mit der Kontonummer auf ein
     * @param auf long
     * @param betrag double
     */
    public void geldEinzahlen(long auf, double betrag) {
        kontenliste.get(auf).einzahlen(betrag);
    }

    /**
     * loescht das Konto mit der angegebenen Kontonummer aus der Kontoliste
     * @param nummer long
     * @return true, falls die Kontonummer existiert, false sonst
     */
    public boolean kontoLoeschen(long nummer) {
        return kontenliste.remove(nummer) != null;
    }

    /**
     * liefert den Kontostand des Kontos mit der angegebenen Kontonummer
     * @param nummer long
     * @return Kontostand des Kontos mit der angegebenen Kontonummer
     */
    public double getKontostand(long nummer) {
        return kontenliste.get(nummer).getKontostand();
    }

    /**
     * überweist den genannten Betrag vom überweisungsfähigen Konto mit der Nummer vonKontonr zum
     * überweisungsfähigen Konto mit der Nummer nachKontonr und gibt zurück, ob die Überweisung geklappt hat
     * @param vonKontonr long
     * @param nachKontonr long
     * @param betrag double
     * @param verwendungszweck String
     * @return false, falls ueberweisungEmpfangen eine Exception wirft, oder ueberweisungAbsenden false zurückgibt, true sonst
     * @throws GesperrtException wenn das Absender-Konto gesperrt ist
     * @throws IllegalArgumentException falls eines der Konten nicht existiert oder nicht ueberweisungsfaehig ist
     */
    public boolean geldUeberweisen(long vonKontonr, long nachKontonr, double betrag, String verwendungszweck)
            throws IllegalArgumentException, GesperrtException {
        if ((kontenliste.get(vonKontonr) == null) || (kontenliste.get(nachKontonr) == null) ||
                (kontenliste.get(nachKontonr) == kontenliste.get(vonKontonr))) {
            throw new IllegalArgumentException("Die angegebenen Kontonummer(n) existieren nicht");
        }
        if ((kontenliste.get(vonKontonr) instanceof Ueberweisungsfaehig) && (kontenliste.get(nachKontonr) instanceof
                Ueberweisungsfaehig)) {
            if (((Ueberweisungsfaehig) kontenliste.get(vonKontonr)).ueberweisungAbsenden(betrag,
                    kontenliste.get(nachKontonr).getInhaber().getName(), nachKontonr, bankleitzahl, verwendungszweck)) {
                try {
                    ((Ueberweisungsfaehig) kontenliste.get(nachKontonr)).ueberweisungEmpfangen(betrag,
                            kontenliste.get(vonKontonr).getInhaber().getName(), vonKontonr, bankleitzahl, verwendungszweck);
                } catch (Exception e) {
                    this.geldEinzahlen(vonKontonr, betrag);
                    return false;
                }
                return true;
            } else {
                return false;
            }
        } else {
            throw new IllegalArgumentException("Mindestens eine der beiden Konten ist nicht überweisungsfähig");
        }
    }

    /**
     * die Methode sperrt alle Konten, deren Kontostand im Minus ist
     */
    public void pleitegeierSperren() {
        kontenliste.values().forEach(konto -> {if(konto.getKontostand() < 0) konto.sperren();});
    }

    /**
     * gibt eine Liste aller Kunden, dessen Kontostand über dem angegebenen Minimum liegen
     * @param minimum double
     * @return Liste aller Kunden, dessen Kontostand über dem angegebenen Minimum liegen
     */
    public List<Kunde> getKundenMitVollemKonto(double minimum) {
        return kontenliste.values().stream().
                filter(konto -> (konto.getKontostand() > minimum)).
                map(Konto::getInhaber).
                collect(Collectors.toList());
    }

    /**
     * liefert die Namen und Adressen aller Kunden der Bank, sortiert nach Vornamen und ohne Duplikate
     * @return die Namen und Adressen aller Kunden der Bank, sortiert nach Vornamen und ohne Duplikate
     */
    public String getKundenadressen() {
        return kontenliste.values().stream().
                map(Konto::getInhaber).
                distinct().
                sorted(Comparator.comparing(Kunde::getVorname)).
                map(k -> k.getName() + "; " + k.getAdresse()).
                collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * liefert eine Liste aller freien Kontonummern, die im vergebenen Bereich liegen
     * @return eine Liste aller freien Kontonummern, die im vergebenen Bereich liegen
     */
    public List<Long> getKontonummernLuecken() {
        return LongStream.
                iterate(0, i -> i + 1).
                boxed().
                filter(l -> !kontenliste.containsKey(l)).
                limit(kontonummerZaehler).
                collect(Collectors.toList());
    }

    /**
     * gibt eine tiefe Kopie des Bank Objekts zurueck
     * @return eine tiefe Kopie des Bank Objekts
     */
    @Override
    public Bank clone() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos);) {
            oos.writeObject(this);
        } catch (IOException e) {}

        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
             ObjectInputStream ois = new ObjectInputStream(bais);) {
            return (Bank) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {}
        }

    }
}
