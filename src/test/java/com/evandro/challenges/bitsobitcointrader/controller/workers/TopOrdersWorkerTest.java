package com.evandro.challenges.bitsobitcointrader.controller.workers;

import com.evandro.challenges.bitsobitcointrader.Main;
import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumBook;
import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumMessageType;
import com.evandro.challenges.bitsobitcointrader.controller.service.WebSocketClient;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.rest.orderbook.Order;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.websocket.difforders.DiffOrder;
import com.evandro.challenges.bitsobitcointrader.controller.workers.utils.Flag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TopOrdersWorkerTest {

    @Mock
    private final Flag flag = new Flag();

    @Mock
    private HttpURLConnection conn;

    @Mock
    private TopOrdersWorker mockedTow;

    private TopOrdersWorker tow;

    @Before
    public void setup() throws URISyntaxException, IOException {
        tow = new TopOrdersWorker(new WebSocketClient(new URI(Main.WS_URL)), Main.REST_URL + EnumMessageType.ORDER_BOOK.toString(), EnumBook.BTC_MXN, 10);
        doCallRealMethod().when(mockedTow).run();
        doCallRealMethod().when(mockedTow).setRun(anyBoolean());
        doNothing().when(mockedTow).requestOrderBook();
        doNothing().when(mockedTow).subscribe();
    }

    @Test
    public void shouldPerformASubscribeAndARequestWhenRun() throws IOException, InterruptedException {
        Thread t = new Thread(mockedTow);
        mockedTow.setRun(true);
        t.start();
        verify(mockedTow, timeout(5000).atLeast(1)).subscribe();
        verify(mockedTow, timeout(5000).atLeast(1)).requestOrderBook();
    }

    @Test
    public void shouldSetGetParametersAndDisconnect() throws IOException {
        tow.requestOrderBook(conn);
        verify(conn, times(1)).setRequestMethod("GET");
        verify(conn, times(1)).setRequestProperty("Accept", "application/json");
        verify(conn, times(1)).setRequestProperty("User-Agent", "");
        verify(conn, times(1)).disconnect();
    }

    @Test
    public void sortByPriceShouldSortByPrice() {
        List<Order> orderList = tow.sortByPrice(buildOrderList(), true);

        assertTrue(Double.valueOf(orderList.get(0).getPrice()) < Double.valueOf(orderList.get(2).getPrice()));

        orderList = tow.sortByPrice(orderList, false);

        assertFalse(Double.valueOf(orderList.get(0).getPrice()) < Double.valueOf(orderList.get(2).getPrice()));
    }

    @Test
    public void serviceTest() throws IOException {
        Thread t = new Thread(tow);
        tow.setRun(true);

        Observer observerDummy = (o, arg) -> {
            assertTrue(arg instanceof Object[]);
            Object[] argAux = (Object[]) arg;
            List<DiffOrder> output = (List<DiffOrder>) argAux[1];
            assertNotEquals(output.size(), 0);
            flag.setFlag(true);
        };

        tow.addObserver(observerDummy);

        t.start();

        verify(flag, timeout(10000).atLeast(1)).setFlag(true);
    }

    private List<Order> buildOrderList() {
        List<Order> orderList = new ArrayList<>();
        orderList.add(buildOrder("1", "1"));
        orderList.add(buildOrder("2", "2"));
        orderList.add(buildOrder("3", "3"));
        return orderList;
    }

    private Order buildOrder(String id, String price) {
        Order order = new Order();
        order.setOrderId(id);
        order.setPrice(price);
        order.setAmount("1");
        return order;
    }
}
