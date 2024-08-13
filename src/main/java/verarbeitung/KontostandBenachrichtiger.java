package verarbeitung;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class KontostandBenachrichtiger implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("Kontonummer: " + ((Konto) evt.getSource()).getKontonummer() + System.lineSeparator() +
                evt.getPropertyName() + ":" + System.lineSeparator() +
                "Alt: " + evt.getOldValue() + System.lineSeparator() +
                "Neu: " + evt.getNewValue());
    }
}
