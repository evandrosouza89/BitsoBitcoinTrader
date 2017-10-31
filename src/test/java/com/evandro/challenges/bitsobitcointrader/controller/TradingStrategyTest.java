package com.evandro.challenges.bitsobitcointrader.controller;

import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumBook;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.rest.trades.Trade;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static org.junit.Assert.*;

public class TradingStrategyTest {

    private TradingStrategy ts;

    @Before
    public void setup() {
        ts = new TradingStrategy(1, 1);
    }

    @Test
    public void buildTradeShouldBuildATrade() {
        Trade t = ts.buildTrade("makerSide", "price");
        assertNotNull(t);
        assertNull(t.getTid());
        assertEquals(t.getPrice(), "price");
        assertEquals(t.getMakerSide(), "makerSide");
        assertEquals(t.getAmount(), "1.0");
        assertEquals(t.getBook(), EnumBook.BTC_MXN.toString());
        assertNotNull(t.getCreatedAt());
    }

    @Test
    public void shouldProduceOutputWhenTriggered() {
        ObservableDummy observableDummy = new ObservableDummy();
        observableDummy.addObserver(ts);

        Observer observerDummy = (o, arg) -> assertTrue(arg instanceof List);

        ts.addObserver(observerDummy);

        observableDummy.setChanged();

        observableDummy.notifyObservers(buildTradeListBuyAsc());
    }

    @Test
    public void shouldBuyWhenLow() {
        ObservableDummy observableDummy = new ObservableDummy();
        observableDummy.addObserver(ts);

        Observer observerDummy = (o, arg) -> {
            assertTrue(arg instanceof List);
            List<Trade> output = (List<Trade>) arg;
            assertNull(output.get(0).getTid());
            assertEquals(output.get(0).getMakerSide(), "buy");
            assertEquals(output.get(0).getPrice(), "0.9");
        };

        ts.addObserver(observerDummy);

        observableDummy.setChanged();

        observableDummy.notifyObservers(buildTradeListSellDesc());
    }

    @Test
    public void shouldSellWhenHigh() {
        ObservableDummy observableDummy = new ObservableDummy();
        observableDummy.addObserver(ts);

        Observer observerDummy = (o, arg) -> {
            assertTrue(arg instanceof List);
            List<Trade> output = (List<Trade>) arg;
            assertNull(output.get(0).getTid());
            assertEquals(output.get(0).getMakerSide(), "sell");
            assertEquals(output.get(0).getPrice(), "1.1");
        };

        ts.addObserver(observerDummy);

        observableDummy.setChanged();

        observableDummy.notifyObservers(buildTradeListBuyAsc());
    }

    private List<Trade> buildTradeListBuyAsc() {
        List<Trade> tradeList = new ArrayList();
        tradeList.add(buildTrade(2L, "buy", "1.1"));
        tradeList.add(buildTrade(1L, "buy", "1"));
        return tradeList;
    }

    private List<Trade> buildTradeListSellDesc() {
        List<Trade> tradeList = new ArrayList();
        tradeList.add(buildTrade(2L, "sell", "0.9"));
        tradeList.add(buildTrade(1L, "sell", "1"));
        return tradeList;
    }

    private Trade buildTrade(Long tid, String makerSide, String price) {
        Trade t = ts.buildTrade(makerSide, price);
        t.setTid(tid);
        return t;
    }

    class ObservableDummy extends Observable {
        public void setChanged() {
            super.setChanged();
        }
    }


}
