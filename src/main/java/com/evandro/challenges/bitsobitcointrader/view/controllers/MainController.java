package com.evandro.challenges.bitcointrader.view.controllers;

import com.evandro.challenges.bitcointrader.controller.TradingStrategy;
import com.evandro.challenges.bitcointrader.controller.service.json.elements.rest.trades.Trade;
import com.evandro.challenges.bitcointrader.controller.service.json.elements.websocket.orders.Offer;
import com.evandro.challenges.bitcointrader.controller.workers.TopOrdersWorker;
import com.evandro.challenges.bitcointrader.Main;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainController implements Observer {

    @FXML
    private TableView<Offer> topBidsTableView;

    @FXML
    private TableView<Offer> topAsksTableView;

    @FXML
    private TableView<Trade> recentTradesTableView;

    @FXML
    private TableColumn<Offer, String> topBidsTimeTableColumn;

    @FXML
    private TableColumn<Offer, String> topBidsRateTableColumn;

    @FXML
    private TableColumn<Offer, String> topBidsAmountTableColumn;

    @FXML
    private TableColumn<Offer, String> topBidsValueTableColumn;

    @FXML
    private TableColumn<Offer, String> topAsksTimeTableColumn;

    @FXML
    private TableColumn<Offer, String> topAsksRateTableColumn;

    @FXML
    private TableColumn<Offer, String> topAsksAmountTableColumn;

    @FXML
    private TableColumn<Offer, String> topAsksValueTableColumn;

    @FXML
    private TableColumn<Trade, String> recentTradesTimeTableColumn;

    @FXML
    private TableColumn<Trade, String> recentTradesBookTableColumn;

    @FXML
    private TableColumn<Trade, String> recentTradesMarkerSideTableColumn;

    @FXML
    private TableColumn<Trade, String> recentTradesPriceTableColumn;

    @FXML
    private TableColumn<Trade, String> recentTradesAmountTableColumn;

    @FXML
    private TableColumn<Trade, Long> recentTradesIdTableColumn;

    @FXML
    private TableColumn<Trade, String> recentTradesBotTableColumn;

    @FXML
    private MenuItem settingsMenuItem;

    @FXML
    private MenuItem closeMenuItem;

    public void initialize() {
        initializeMenu();
        initializeTopBidsTableView();
        initializeTopAsksTableView();
        initializeRecentTradesTableView();
    }

    private void initializeMenu() {
        settingsMenuItem.setOnAction(event ->
            Main.getInstance().getSettingsStage().showAndWait()
        );

        closeMenuItem.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private void initializeTopBidsTableView() {
        topBidsTimeTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(

                Date.from(Instant.ofEpochMilli(cellData.getValue().getTime()))).asString()

        );
        topBidsRateTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRate()));
        topBidsAmountTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAmount()));
        topBidsValueTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue()));
    }

    private void initializeTopAsksTableView() {
        topAsksTimeTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(

                Date.from(Instant.ofEpochMilli(cellData.getValue().getTime()))).asString()

        );
        topAsksRateTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRate()));
        topAsksAmountTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAmount()));
        topAsksValueTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue()));
    }

    private void initializeRecentTradesTableView() {
        recentTradesTimeTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCreatedAt()));
        recentTradesBookTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBook()));
        recentTradesMarkerSideTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getMakerSide()));
        recentTradesPriceTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrice()));
        recentTradesAmountTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAmount()));
        recentTradesIdTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTid()));
        recentTradesBotTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTid() == null ? "Yes" : ""));
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof TopOrdersWorker) {
            Object[] data = (Object[]) arg;
            topBidsTableView.setItems(FXCollections.observableList((List<Offer>) data[0]));
            topAsksTableView.setItems(FXCollections.observableList((List<Offer>) data[1]));
        } else if (o instanceof TradingStrategy) {
            recentTradesTableView.setItems(FXCollections.observableList((List<Trade>)arg));
        }
    }
}