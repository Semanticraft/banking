package oberflaeche;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import verarbeitung.GesperrtException;
import verarbeitung.Girokonto;
import verarbeitung.Kunde;

import javax.swing.*;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Controller und View fuer Girokonto
 */
public class KontoStarter extends Application {

    private Stage primaryStage = new Stage();
    @FXML
    private Girokonto girokonto = new Girokonto(Kunde.MUSTERMANN, 0, Girokonto.STANDARD_DISPO);
    @FXML
    private Text ueberschrift;
    @FXML
    private GridPane anzeige;
    @FXML
    private Text txtNummer;
    /**
     * Anzeige der Kontonummer
     */
    @FXML
    private Text nummer;
    @FXML
    private Text txtStand;
    /**
     * Anzeige des Kontostandes
     */
    @FXML
    private Text stand;
    @FXML
    private Text txtGesperrt;
    /**
     * Anzeige und Änderung des Gesperrt-Zustandes
     */
    @FXML
    private CheckBox gesperrt;
    @FXML
    private Text txtAdresse;
    /**
     * Anzeige und Änderung der Adresse des Kontoinhabers
     */
    @FXML
    private TextArea adresse;
    /**
     * Anzeige von Meldungen über Kontoaktionen
     */
    @FXML
    private Text meldung;
    @FXML
    private HBox aktionen;
    /**
     * Auswahl des Betrags für eine Kontoaktion
     */
    @FXML
    private TextField betrag;
    /**
     * löst eine Einzahlung aus
     */
    @FXML
    private Button einzahlen;
    /**
     * löst eine Abhebung aus
     */
    @FXML
    private Button abheben;

    /**
     * zeigt die Konto-Oberflaeche mit dem im KontoStarter hinterlegten Girokonto girokonto
     * @param stage Stage
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/KontoStarter.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * setzt alle Initialisierungen und Bindungen der View
     */
    @FXML public void initialize() {
        meldung.setText("Willkommen, " + girokonto.getInhaber().getVorname() + " " + girokonto.getInhaber().getNachname());
        adresse.textProperty().bindBidirectional(girokonto.getInhaber().adresseProperty());
        DecimalFormat df = new DecimalFormat("#.##");
        stand.textProperty().bind(girokonto.kontostandProperty().map(df::format));
        stand.fillProperty().bind(girokonto.kontostandImPlusProperty().map(k -> k ? Color.GREEN : Color.RED));
    }

    /**
     * zahlt den angegebenen Betrag in girokonto ein
     */
    @FXML
    protected void einzahlen() {
        girokonto.einzahlen(Double.parseDouble(betrag.getText()));
    }

    /**
     * hebt den angegebenen Betrag von girokonto ab
     * @throws GesperrtException falls das Konto gesperrt ist
     */
    @FXML
    protected void abheben() throws GesperrtException {
        try {
            if (!girokonto.abheben(Double.parseDouble(betrag.getText()))) {
                JOptionPane.showMessageDialog(null, "Dispo ueberziehen nicht moeglich");
            }

        } catch (GesperrtException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    /**
     * setzt die Sperrung des Girokontos girokonto
     */
    @FXML
    public void setSperrung() {
        if (gesperrt.isSelected()) {
            girokonto.sperren();
        } else {
            girokonto.entsperren();
        }
    }

    /**
     * setzt die Adresse des Inhabers von girokonto
     */
    @FXML
    public void setAdresse() {
        girokonto.getInhaber().setAdresse(adresse.textProperty().get());
    }

    /**
     * liefert Girokonto als Model des Controllers
     * @return girokonto
     */
    @FXML
    public Girokonto getGirokonto() {
        return girokonto;
    }
}
