package com.evandro.challenges.bitsobitcointrader;

import com.evandro.challenges.bitsobitcointrader.controller.TradingStrategy;
import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumBook;
import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumMessageType;
import com.evandro.challenges.bitsobitcointrader.controller.service.WebSocketClient;
import com.evandro.challenges.bitsobitcointrader.controller.workers.RecentTradesWorker;
import com.evandro.challenges.bitsobitcointrader.controller.workers.TopOrdersWorker;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/*The starting point. Everything is setup and started here, and everything could be stopped here as well.
This class provides a singleton. */
public class Main extends Application {

    private final Logger logger = LogManager.getLogger();

    public static final String REST_URL = "https://api.bitso.com/v3/";

    public static final String WS_URL = "wss://ws.bitso.com";

    private static Main instance = null;

    private FXMLLoader loader;

    private WebSocketClient clientEndPoint;

    private TopOrdersWorker tow;

    private RecentTradesWorker rtw;
    @Getter
    private Stage settingsStage;

    public Main() {
        super();
        synchronized (Main.class) {
            if (instance != null) throw new UnsupportedOperationException(
                    getClass() + " is singleton but constructor called more than once");
            instance = this;
        }
    }

    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Bitso Bitcoin Trader");
        primaryStage.setScene(new Scene(root));

        setupSettingStage();
        setupWebSocket();

        primaryStage.setMaximized(true);
        primaryStage.setResizable(true);
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
        root.requestFocus();
        settingsStage.showAndWait();
    }

    /*Settings window setup*/
    private void setupSettingStage() {
        settingsStage = new Stage();
        Scene settingsScene = null;
        try {
            settingsScene = new Scene(FXMLLoader.load(getClass().getResource("/fxml/Settings.fxml")));
        } catch (IOException e) {
            logger.error(e);
        }
        settingsStage.setScene(settingsScene);
        settingsStage.setResizable(false);
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.setTitle("Settings");
    }

    /*Websocket connection setup*/
    private void setupWebSocket() {
        try {
            clientEndPoint = new WebSocketClient(new URI(WS_URL));
        } catch (URISyntaxException e) {
            logger.error(e);
        }
    }

    /*Workers start*/
    public void startWorkers(int m, int n, int x) {
        try {
            tow = new TopOrdersWorker(clientEndPoint, REST_URL + EnumMessageType.ORDER_BOOK.toString(), EnumBook.BTC_MXN, x);
        } catch (MalformedURLException e) {
            logger.error(e);
        }
        tow.addObserver(loader.getController());
        Thread t1 = new Thread(tow);
        t1.start();

        try {
            rtw = new RecentTradesWorker(REST_URL + EnumMessageType.TRADES.toString(), EnumBook.BTC_MXN, x);
        } catch (MalformedURLException e) {
            logger.error(e);
        }

        TradingStrategy ts = new TradingStrategy(m, n);
        ts.addObserver(loader.getController());
        rtw.addObserver(ts);

        Thread t2 = new Thread(rtw);
        t2.start();
    }

    /*Workers stop*/
    public void stopWorkers() {
        if (tow != null) {
            tow.setRun(false);
        }

        if (rtw != null) {
            rtw.setRun(false);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
