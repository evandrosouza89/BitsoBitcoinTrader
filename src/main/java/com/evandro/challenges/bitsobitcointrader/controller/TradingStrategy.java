package com.evandro.challenges.bitsobitcointrader.controller;

import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumBook;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.rest.trades.Trade;
import com.evandro.challenges.bitsobitcointrader.controller.utils.Utils;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/*This class implements the bot trading strategy. It's fed by the RecentTrades worker. M and N are the strategy algorithm arguments.
After M rate increases the algorithm will sell 1 btc at the highest price.
After N rate decreases the algorithm will buy 1 btc at the lowest price.
It feeds the Main screen.*/
public class TradingStrategy extends Observable implements Observer {

    private int m;

    private int n;

    private int upTick;

    private int downTick;

    private Double lastPrice;

    private Long lastTid;

    private Queue<Trade> output;

    public TradingStrategy(int m, int n) {
        this.m = m;
        this.n = n;
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        Collections.reverse((List<Trade>) arg);
        analyseTrades((List<Trade>) arg);
    }

    /*Builds an imaginary trade that will be added to the recent trades table of main screen.*/
    protected Trade buildTrade(String makerSide, String price) {
        Trade t = new Trade();
        t.setAmount("1.0");
        t.setBook(EnumBook.BTC_MXN.toString());

        ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

        t.setCreatedAt(date.format(formatter));

        t.setMakerSide(makerSide);
        t.setPrice(price);

        return t;
    }

    private void analyseTrades(List<Trade> tradeList) {
        if (output == null) {
            output = new CircularFifoQueue<>(tradeList.size());
        }
        for (Trade t : tradeList) {
            if ((t.getPrice() != null && t.getTid() != null) &&
                    (lastTid == null || (t.getTid() > lastTid))) {
                output.add(t);
                analyseTrade(t);
            }
        }
        generateOutput();
    }

    /*Here the contiguous upticks and downticks are calculated and the logic applied.*/
    private void analyseTrade(Trade t) {
        Double price = Double.valueOf(t.getPrice());
        if (lastPrice != null) {
            if (price > lastPrice) {
                upTick++;
                downTick = 0;
                if (upTick >= m) {
                    output.add(buildTrade("sell", price.toString()));
                }
            } else if (price < lastPrice) {
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
            List<Trade> aux = Utils.queueToList(output);
            Collections.reverse(aux);
            setChanged();
            notifyObservers(aux); // Feeds recent trades table of main screen
        }
    }
}
