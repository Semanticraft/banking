package verwaltung;

import java.time.LocalDate;
import java.util.*;

import verarbeitung.Girokonto;
import verarbeitung.Konto;
import verarbeitung.Kunde;
import verarbeitung.Sparbuch;

/**
 * verwaltet eine Menge von Kunden
 * @author Doro
 * @author Dennis Forster
 */
public class Kundenmenge {
	

	/**
	 * erstellt eine Menge von Kunden und löscht die unnötigen
	 * wieder
	 * @param args
	 */
	public static void main(String[] args) {
		Kunde anna = new Kunde("Anna", "Müller", "hier", LocalDate.parse("1979-05-14"));
		Kunde berta = new Kunde("Berta", "Beerenbaum", "hier", LocalDate.parse("1980-03-15"));
		Kunde chris = new Kunde("Chris", "Tall", "hier", LocalDate.parse("1979-01-07"));
		Kunde anton = new Kunde("Anton", "Meier", "hier", LocalDate.parse("1982-10-23"));
		Kunde bert = new Kunde("Bert", "Chokowski", "hier", LocalDate.parse("1970-12-24"));
		Kunde doro = new Kunde("Doro", "Hubrich", "hier", LocalDate.parse("1976-07-13"));

		//ToDo: TreeSet mit den vorhandenen Kunden anlegen, Aufgaben 1-3
		TreeSet<Kunde> kunden = new TreeSet<>();
		kunden.add(anna);
		kunden.add(berta);
		kunden.add(chris);
		kunden.add(anton);
		kunden.add(bert);
		kunden.add(doro);

		int counter = 0;
		for (Kunde kunde : kunden) {
			counter++;
			System.out.println(kunde);
		}
		System.out.println(counter);


		
		
		Scanner tastatur = new Scanner(System.in);
		System.out.println("Nach welchem Namen wollen Sie suchen? ");
		String gesucht = tastatur.nextLine();
		
		//ToDo: Aufgabe 4-6
		boolean gefunden = false;
		for (Kunde kunde : kunden) {
			if (kunde.getNachname().equals(gesucht)) {
				gefunden = true;
				System.out.println(kunde);
			}
		}
		if (!gefunden) {
			System.out.println("Kunde wurde nicht gefunden");
		}

		// TODO: fix ConcurrenceModificationException
		for (Kunde kunde : kunden) {
			if(kunde.getVorname().charAt(0) == 'A') {
				kunden.remove(kunde);
			}
		}
		for (Kunde kunde : kunden) {
			System.out.println(kunde);
		}

		TreeSet<Kunde> kundenNachGeburtstagSortiert = new TreeSet<>(new GeburtstagsComparator());



		Map<Long, Konto> kontenliste = Map.of(
				1L, new Girokonto(bert, 1, 1000),
				2L, new Girokonto(chris, 2, 1000),
				3L, new Sparbuch(chris, 3),
				4L, new Girokonto(berta, 4, 1000),
				5L, new Sparbuch(berta, 5),
				6L, new Girokonto(bert, 6, 1000),
				7L, new Girokonto(chris, 7, 1000),
				8L, new Girokonto(bert, 8, 1000),
				9L, new Sparbuch(chris, 9));
		
		//ToDo: Aufgabe 7 und 8
	}

}
