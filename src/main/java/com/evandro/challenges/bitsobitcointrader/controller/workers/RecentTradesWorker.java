package com.evandro.challenges.bitsobitcointrader.controller.workers;

import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.rest.trades.Trades;
import com.evandro.challenges.bitsobitcointrader.controller.workers.commons.Worker;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.Thread.sleep;

public class RecentTradesWorker extends Worker implements Runnable {

    private final Logger logger = LogManager.getLogger();

    private String wsURL;

    private URL url;

    public RecentTradesWorker(String wsURL, String book, int size) throws MalformedURLException {
        this.wsURL = wsURL;
        this.book = book;
        this.size = size;
        setup();
    }

    private void setup() throws MalformedURLException {
        run = true;
        gson = new Gson();
        wsURL = wsURL.concat("?book=");
        wsURL = wsURL.concat(book);
        wsURL = wsURL.concat("&limit=");
        wsURL = wsURL.concat(String.valueOf(size));
        url = new URL(wsURL);
    }

    protected void request(HttpURLConnection conn) throws IOException {
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("User-Agent", "");

        if (conn.getResponseCode() == 200) {
            Trades trades = gson.fromJson(new InputStreamReader(conn.getInputStream()), Trades.class);
            if (trades != null && trades.getSuccess() != null && trades.getSuccess() && trades.getPayload() != null) {
                setChanged();
                notifyObservers(trades.getPayload());
            }
        }
        conn.disconnect();
    }

    protected void request() throws IOException {
        request((HttpURLConnection) url.openConnection());
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