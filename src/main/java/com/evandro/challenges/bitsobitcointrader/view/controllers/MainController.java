package com.evandro.challenges.bitsobitcointrader.view.controllers;

import com.evandro.challenges.bitsobitcointrader.Main;
import com.evandro.challenges.bitsobitcointrader.controller.TradingStrategy;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.rest.trades.Trade;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.websocket.orders.Order;
import com.evandro.challenges.bitsobitcointrader.controller.workers.TopOrdersWorker;
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
    private TableView<Order> topBidsTableView;

    @FXML
    private TableView<Order> topAsksTableView;

    @FXML
    private TableView<Trade> recentTradesTableView;

    @FXML
    private TableColumn<Order, String> topBidsTimeTableColumn;

    @FXML
    private TableColumn<Order, String> topBidsRateTableColumn;

    @FXML
    private TableColumn<Order, String> topBidsAmountTableColumn;

    @FXML
    private TableColumn<Order, String> topBidsValueTableColumn;

    @FXML
    private TableColumn<Order, String> topAsksTimeTableColumn;

    @FXML
    private TableColumn<Order, String> topAsksRateTableColumn;

    @FXML
    private TableColumn<Order, String> topAsksAmountTableColumn;

    @FXML
    private TableColumn<Order, String> topAsksValueTableColumn;

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
            if (data[0] == "Bids") {
                topBidsTableView.setItems(FXCollections.observableList((List<Order>) data[1]));
            } else {
                topAsksTableView.setItems(FXCollections.observableList((List<Order>) data[1]));
            }
        } else if (o instanceof TradingStrategy) {
            recentTradesTableView.setItems(FXCollections.observableList((List<Trade>)arg));
        }
    }
}