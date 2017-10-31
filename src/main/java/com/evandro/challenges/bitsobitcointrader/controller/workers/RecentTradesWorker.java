package com.evandro.challenges.bitsobitcointrader.controller.workers;

import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumBook;
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

/*Worker designed to request the most recent trades from a REST service. Its output is limited by the (int)size argument.
Feeds TradingStrategy object*/
public class RecentTradesWorker extends Worker implements Runnable {

    private final Logger logger = LogManager.getLogger();

    public RecentTradesWorker(String restURL, EnumBook book, int size) throws MalformedURLException {
        super(restURL, book, size);
        setup();
    }

    private void setup() throws MalformedURLException {
        run = true;
        gson = new Gson();

        setupRESTURL();
    }

    private void setupRESTURL() throws MalformedURLException {
        restURL = restURL.concat("?book=");
        restURL = restURL.concat(book.toString());
        restURL = restURL.concat("&limit=");
        restURL = restURL.concat(String.valueOf(size));
        url = new URL(restURL);
    }


    /*Request recent trades list using HttpURLConnection client*/
    protected void requestRecentTrades(HttpURLConnection conn) throws IOException {
        setupHttpGETConnection(conn);

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            Trades trades = gson.fromJson(new InputStreamReader(conn.getInputStream()), Trades.class);
            if (trades != null && trades.getSuccess() != null && trades.getSuccess() && trades.getPayload() != null) {
                setChanged();
                notifyObservers(trades.getPayload()); // Feeds TradingStrategy object
            }
        }

        conn.disconnect();
    }

    protected void requestRecentTrades() throws IOException {
        requestRecentTrades((HttpURLConnection) url.openConnection());
    }

    public void run() {
        while (run) {
            try {
                requestRecentTrades();
                sleep(5000);
            } catch (Exception e) {
                logger.warn(e);
            }
        }
    }
}