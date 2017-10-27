package com.evandro.challenges.bitsobitcointrader;

import com.evandro.challenges.bitsobitcointrader.controller.TradingStrategy;
import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumBook;
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

public class Main extends Application {

    private final Logger logger = LogManager.getLogger();

    public static final String WS_URI = "wss://ws.bitso.com";

    private static Main instance = null;

    private FXMLLoader loader;

    private WebSocketClient clientEndPoint;

    private TopOrdersWorker tow;

    private RecentTradesWorker rtw;

    @Getter
    private Stage settingsStage;

    public Main(){
        super();
        synchronized(Main.class) {
            if(instance != null) throw new UnsupportedOperationException(
                    getClass()+" is singleton but constructor called more than once");
            instance = this;
        }
    }

    public static Main getInstance() {
        if(instance == null) {
            instance = new Main();
        }
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Bitso Bitcoin Trader");
        primaryStage.setScene(new Scene(root));

        setupSettingStage();
        setupWebSocket();

        primaryStage.setMaximized(true);
        primaryStage.setResizable(true);
        primaryStage.setOnCloseRequest(e -> {Platform.exit(); System.exit(0);});
        primaryStage.show();
        root.requestFocus();
        settingsStage.showAndWait();
    }

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

    private void setupWebSocket() {
        try {
            clientEndPoint = new WebSocketClient(new URI(WS_URI));
        } catch (URISyntaxException e) {
            logger.error(e);
        }
    }

    public void startWorkers(int m, int n, int x) {
        tow = new TopOrdersWorker(clientEndPoint, EnumBook.BTC_MXN.toString(), x);
        tow.addObserver(loader.getController());
        Thread t1 = new Thread(tow);
        t1.start();

        try {
            rtw = new RecentTradesWorker("https://api.bitso.com/v3/trades/", EnumBook.BTC_MXN.toString(), x);
        } catch (MalformedURLException e) {
            logger.error(e);
        }

        TradingStrategy tw = new TradingStrategy(m, n);
        tw.addObserver(loader.getController());
        rtw.addObserver(tw);

        Thread t2 = new Thread(rtw);
        t2.start();
    }

    public void stopWorkers(){
        if(tow != null) {
            tow.setRun(false);
        }
        if(rtw != null) {
            rtw.setRun(false);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
