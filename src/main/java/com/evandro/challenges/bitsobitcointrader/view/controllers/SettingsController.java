package com.evandro.challenges.bitcointrader.view.controllers;

import com.evandro.challenges.bitcointrader.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

public class SettingsController {

    @FXML
    private Spinner<Integer> mValueSpinner;

    @FXML
    private Spinner<Integer> nValueSpinner;

    @FXML
    private Spinner<Integer> xValueSpinner;

    @FXML
    private Button cancelButton;

    @FXML
    private Button okButton;

    public void initialize() {
        initializeSpinners();
        initializeButtons();
    }

    private void initializeSpinners() {
        mValueSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 3));
        nValueSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 3));
        xValueSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 20, 10));
    }

    private void initializeButtons() {
        cancelButton.setOnAction(event -> {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });

        okButton.setOnAction(event -> {
            Main.getInstance().stopWorkers();
            Main.getInstance().startWorkers(mValueSpinner.getValue(), nValueSpinner.getValue(), xValueSpinner.getValue());
            Stage stage = (Stage) okButton.getScene().getWindow();
            stage.close();
        });
    }
}
