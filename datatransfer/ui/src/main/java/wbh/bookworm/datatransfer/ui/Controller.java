/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.datatransfer.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField hoerbuchkatalogPfad;

    @FXML
    private Button hoerbuchkatalogDurchsuchen;

    @FXML
    private Button hoerbuchkatalogUebertragen;

    @FXML
    private TextField hoererdatenPfad;

    @FXML
    private Button hoererdatenDurchsuchen;

    @FXML
    private Button hoererdatenUebertragen;

    @FXML
    private ToggleButton schalterFuerAutomatischeUebertragung;

    @FXML
    void automatischeUebertragungSchalten(ActionEvent event) {

    }

    @FXML
    void hoerbuchkatalogDateidialogOeffnen(ActionEvent event) {

    }

    @FXML
    void hoerbuchkatalogJetztUebertragen(ActionEvent event) {

    }

    @FXML
    void hoererdatenDateidialogOeffnen(ActionEvent event) {

    }

    @FXML
    void hoererdatenJetztUebertragen(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert hoerbuchkatalogPfad != null : "fx:id=\"hoerbuchkatalogPfad\" was not injected: check your FXML file 'DatatransferUi.fxml'.";
        assert hoerbuchkatalogDurchsuchen != null : "fx:id=\"hoerbuchkatalogDurchsuchen\" was not injected: check your FXML file 'DatatransferUi.fxml'.";
        assert hoerbuchkatalogUebertragen != null : "fx:id=\"hoerbuchkatalogUebertragen\" was not injected: check your FXML file 'DatatransferUi.fxml'.";
        assert hoererdatenPfad != null : "fx:id=\"hoererdatenPfad\" was not injected: check your FXML file 'DatatransferUi.fxml'.";
        assert hoererdatenDurchsuchen != null : "fx:id=\"hoererdatenDurchsuchen\" was not injected: check your FXML file 'DatatransferUi.fxml'.";
        assert hoererdatenUebertragen != null : "fx:id=\"hoererdatenUebertragen\" was not injected: check your FXML file 'DatatransferUi.fxml'.";
        assert schalterFuerAutomatischeUebertragung != null : "fx:id=\"schalterFuerAutomatischeUebertragung\" was not injected: check your FXML file 'DatatransferUi.fxml'.";
    }

}
