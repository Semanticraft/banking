package verwaltung;

import verarbeitung.Kunde;

import java.util.Comparator;

public class GeburtstagsComparator implements Comparator<Kunde> {
    @Override
    public int compare(Kunde kunde1, Kunde kunde2) {
        if(kunde1.getGeburtstag().getYear() != kunde2.getGeburtstag().getYear()) {
            return kunde1.getGeburtstag().getYear() < kunde2.getGeburtstag().getYear() ? -1 : 1;
        }
        if(kunde1.getGeburtstag().getMonth() != kunde2.getGeburtstag().getMonth()) {
            return kunde1.getGeburtstag().getMonth().getValue() < kunde2.getGeburtstag().getMonth().getValue() ? -1 : 1;
        }
        if(kunde1.getGeburtstag().getDayOfMonth() != kunde2.getGeburtstag().getDayOfMonth()) {
            return kunde1.getGeburtstag().getDayOfMonth() < kunde2.getGeburtstag().getDayOfMonth() ? -1 : 1;
        }
        return 0;
    }
}
