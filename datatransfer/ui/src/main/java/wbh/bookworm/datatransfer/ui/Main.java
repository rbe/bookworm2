/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.datatransfer.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    Controller controller;

    @Override
    public void start(final Stage stage) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader();
            final Parent parent = fxmlLoader.load(getClass().getResourceAsStream("/DatatransferUi.fxml"));
            controller = fxmlLoader.getController();
            final Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }

}
