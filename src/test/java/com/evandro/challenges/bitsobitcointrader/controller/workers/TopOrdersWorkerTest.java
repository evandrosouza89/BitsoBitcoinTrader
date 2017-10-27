package com.evandro.challenges.bitsobitcointrader.controller.workers;

import com.evandro.challenges.bitsobitcointrader.Main;
import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumBook;
import com.evandro.challenges.bitsobitcointrader.controller.service.WebSocketClient;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.websocket.orders.Order;
import com.evandro.challenges.bitsobitcointrader.controller.workers.utils.Flag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import static com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.websocket.orders.Order.Operation;
import static com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.websocket.orders.Order.Status;
import static org.junit.Assert.*;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TopOrdersWorkerTest {

    @Mock
    final Flag flag = new Flag();
    private TopOrdersWorker tow;
    @Mock
    private TopOrdersWorker mockedTow;

    @Before
    public void setup() throws URISyntaxException {
        tow = new TopOrdersWorker(new WebSocketClient(new URI(Main.WS_URI)), EnumBook.BTC_MXN.toString(), 10);
    }

    @Test
    public void sortByRateShouldSortByRate() {
        List<Order> orderList = tow.sortByRate(buildOrderList(), true);

        assertTrue(Double.valueOf(orderList.get(0).getRate()) < Double.valueOf(orderList.get(2).getRate()));

        orderList = tow.sortByRate(orderList, false);

        assertFalse(Double.valueOf(orderList.get(0).getRate()) < Double.valueOf(orderList.get(2).getRate()));
    }

    @Test
    public void serviceTest() throws IOException {
        Thread t = new Thread(tow);
        tow.setRun(true);

        Observer observerDummy = (o, arg) -> {
            assertTrue(arg instanceof Object[]);
            Object[] argAux = (Object[]) arg;
            List<Order> output = (List<Order>) argAux[1];
            assertNotEquals(output.size(), 0);
            flag.setFlag(true);
        };

        tow.addObserver(observerDummy);

        t.start();

        verify(flag, timeout(10000).atLeast(1)).setFlag(true);
    }

    private List<Order> buildOrderList() {
        List<Order> orderList = new ArrayList<>();
        orderList.add(buildOrder("1", Operation.BUY, Status.OPEN, "1"));
        orderList.add(buildOrder("2", Operation.BUY, Status.OPEN, "2"));
        orderList.add(buildOrder("3", Operation.BUY, Status.OPEN, "3"));
        return orderList;
    }

    private Order buildOrder(String id, Operation operation, Status status, String rate) {
        Order order = new Order();
        order.setOrderId(id);
        order.setRate(rate);
        order.setAmount("1");
        order.setValue("1");
        order.setOperation(operation);
        order.setTime(1l);
        order.setStatus(status);
        return order;
    }
}
