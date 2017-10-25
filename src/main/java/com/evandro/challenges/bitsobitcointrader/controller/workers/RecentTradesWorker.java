package com.evandro.challenges.bitcointrader.controller.workers;

import com.evandro.challenges.bitcointrader.controller.service.json.elements.rest.trades.Trades;
import com.google.gson.Gson;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

import static java.lang.Thread.sleep;

public class RecentTradesWorker extends Observable implements Runnable {

    private final Logger logger = LogManager.getLogger();

    private Gson gson;

    private String wsURL;

    private String book;

    @Setter
    private boolean run;

    private int size;

    public RecentTradesWorker(String wsURL, String book, int size) {
        this.wsURL = wsURL;
        this.book = book;
        this.size = size;
        setup();
    }

    private void setup() {
        run = true;
        gson = new Gson();
        wsURL = wsURL.concat("?book=");
        wsURL = wsURL.concat(book);
        wsURL = wsURL.concat("&limit=");
        wsURL = wsURL.concat(String.valueOf(size));
    }

    private void request() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(wsURL).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("User-Agent", "");

        if (conn.getResponseCode() == 200) {
            Trades trades = gson.fromJson(new InputStreamReader(conn.getInputStream()), Trades.class);
            if(trades != null && trades.getSuccess() != null && trades.getSuccess().booleanValue() && trades.getPayload() != null) {
                setChanged();
                notifyObservers(trades.getPayload());
            }
        }
        conn.disconnect();
    }

    public void run() {
        while (run) {
            try {
                request();
                sleep(5000);
            } catch (Exception e) {
                logger.warn(e);
            }
        }
    }
}