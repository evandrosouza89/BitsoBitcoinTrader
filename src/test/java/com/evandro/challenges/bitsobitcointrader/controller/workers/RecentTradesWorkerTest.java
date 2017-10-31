package com.evandro.challenges.bitsobitcointrader.controller.workers;

import com.evandro.challenges.bitsobitcointrader.Main;
import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumBook;
import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumMessageType;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.rest.trades.Trade;
import com.evandro.challenges.bitsobitcointrader.controller.workers.utils.Flag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Observer;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RecentTradesWorkerTest {

    @Mock
    private final Flag flag = new Flag();

    private RecentTradesWorker rtw;

    @Mock
    private RecentTradesWorker mockedRtw;

    @Mock
    private HttpURLConnection conn;

    @Before
    public void setup() throws IOException {
        rtw = new RecentTradesWorker(Main.REST_URL + EnumMessageType.TRADES.toString(), EnumBook.BTC_MXN, 10);
        doCallRealMethod().when(mockedRtw).run();
        doCallRealMethod().when(mockedRtw).setRun(anyBoolean());
        doNothing().when(mockedRtw).requestRecentTrades();
    }

    @Test
    public void shouldPerformARequestWhenRun() throws IOException, InterruptedException {
        Thread t = new Thread(mockedRtw);
        mockedRtw.setRun(true);
        t.start();
        verify(mockedRtw, timeout(5000).atLeast(1)).requestRecentTrades();
    }

    @Test
    public void shouldSetGetParametersAndDisconnect() throws IOException {
        rtw.requestRecentTrades(conn);
        verify(conn, times(1)).setRequestMethod("GET");
        verify(conn, times(1)).setRequestProperty("Accept", "application/json");
        verify(conn, times(1)).setRequestProperty("User-Agent", "");
        verify(conn, times(1)).disconnect();
    }

    @Test
    public void serviceTest() throws IOException {
        Thread t = new Thread(rtw);
        rtw.setRun(true);

        Observer observerDummy = (o, arg) -> {
            assertTrue(arg instanceof List);
            List<Trade> output = (List<Trade>) arg;
            assertNotEquals(output.size(), 0);
            flag.setFlag(true);
        };

        rtw.addObserver(observerDummy);

        t.start();

        verify(flag, timeout(10000).atLeast(1)).setFlag(true);
    }
}
