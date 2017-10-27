package com.evandro.challenges.bitsobitcointrader.view.controllers;

import com.evandro.challenges.bitsobitcointrader.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
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
        mValueSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 3));
        TextFormatter formatter = new TextFormatter(mValueSpinner.getValueFactory().getConverter(), mValueSpinner.getValueFactory().getValue());
        mValueSpinner.getEditor().setTextFormatter(formatter);
        mValueSpinner.getValueFactory().valueProperty().bindBidirectional(formatter.valueProperty());
        nValueSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 3));
        TextFormatter formatter2 = new TextFormatter(nValueSpinner.getValueFactory().getConverter(), nValueSpinner.getValueFactory().getValue());
        nValueSpinner.getEditor().setTextFormatter(formatter2);
        nValueSpinner.getValueFactory().valueProperty().bindBidirectional(formatter2.valueProperty());
        xValueSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 999, 10));
        TextFormatter formatter3 = new TextFormatter(xValueSpinner.getValueFactory().getConverter(), xValueSpinner.getValueFactory().getValue());
        xValueSpinner.getEditor().setTextFormatter(formatter3);
        xValueSpinner.getValueFactory().valueProperty().bindBidirectional(formatter3.valueProperty());
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
