package com.evandro.challenges.bitsobitcointrader.controller.workers.commons;

import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumBook;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Observable;

public class Worker extends Observable {

    protected Gson gson;

    protected EnumBook book;

    protected int size;

    protected String restURL;

    protected URL url;

    @Getter
    @Setter
    protected boolean run;

    public Worker(String restURL, EnumBook book, int size) {
        this.restURL = restURL;
        this.book = book;
        this.size = size;
    }

    protected void setupHttpGETConnection(HttpURLConnection conn) throws ProtocolException {
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("User-Agent", "");
    }
}
