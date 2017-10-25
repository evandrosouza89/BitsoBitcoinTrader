package com.evandro.challenges.bitcointrader.controller;

import com.evandro.challenges.bitcointrader.controller.service.json.elements.rest.trades.Trade;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TradingStrategy extends Observable implements Observer {

    private int m;

    private int n;

    private int upTick;

    private int downTick;

    private Double lastPrice;

    private Long lastTid;

    private Queue output;

    public TradingStrategy(int m, int n) {
        this.m = m;
        this.n = n;
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        Collections.reverse((List<Trade>) arg);
        analyseTrades((List<Trade>) arg);
    }

    private Trade buildTrade(String makerSide, String price) {
        Trade t = new Trade();
        t.setAmount("1.0");
        t.setBook("btc_mxn");

        ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

        t.setCreatedAt(date.format(formatter));

        t.setMakerSide(makerSide);
        t.setPrice(price);

        return t;
    }

    private void analyseTrades(List<Trade> tradeList) {
        if (output == null) {
            output = new CircularFifoQueue(tradeList.size());
        }
        for (Trade t : tradeList) {
            if ((t.getPrice() != null && t.getTid() != null) &&
                    (lastTid == null || (t.getTid() > lastTid.longValue()))) {
                output.add(t);
                analyseTrade(t);
            }
        }
        generateOutput();
    }

    private void analyseTrade(Trade t) {
        Double price = Double.valueOf(t.getPrice());
        if (lastPrice != null) {
            if (price > lastPrice.doubleValue()) {
                upTick++;
                downTick = 0;
                if (upTick >= m) {
                    output.add(buildTrade("sell", price.toString()));
                }
            } else if (price < lastPrice.doubleValue()) {
                downTick++;
                upTick = 0;
                if (downTick >= n) {
                    output.add(buildTrade("buy", price.toString()));
                }
            }
        }
        lastPrice = price;
        lastTid = t.getTid();
    }

    private void generateOutput() {
        if (!output.isEmpty()) {
            List<Trade> aux = new ArrayList<>();
            CollectionUtils.addAll(aux, output);
            Collections.reverse(aux);
            setChanged();
            notifyObservers(aux);
        }
    }
}
