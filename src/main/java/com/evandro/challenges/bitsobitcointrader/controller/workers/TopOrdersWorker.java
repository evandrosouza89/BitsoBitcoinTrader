package com.evandro.challenges.bitsobitcointrader.controller.workers;

import com.evandro.challenges.bitsobitcointrader.controller.service.WebSocketClient;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.websocket.commands.Command;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.websocket.orders.Order;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.websocket.orders.Orders;
import com.evandro.challenges.bitsobitcointrader.controller.utils.Utils;
import com.evandro.challenges.bitsobitcointrader.controller.workers.commons.Worker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Queue;

import static java.lang.Thread.sleep;

public class TopOrdersWorker extends Worker implements Runnable {

    private final Logger logger = LogManager.getLogger();

    private static final int CON_TIMEOUT = 10000;

    private Command subscribeCommand;

    private WebSocketClient wsClient;

    private JsonParser parser;

    private boolean subscribed;

    private int receivedMessagesCount;

    private Queue<Order> bidsOutput;

    private Queue<Order> asksOutput;

    public TopOrdersWorker(WebSocketClient wsClient, String book, int size) {
        this.wsClient = wsClient;
        this.book = book;
        this.size = size;
        setup();
    }

    private void setup() {
        run = true;
        gson = new Gson();
        parser = new JsonParser();
        bidsOutput = new CircularFifoQueue<>(size);
        asksOutput = new CircularFifoQueue<>(size);
        setupSubscribeCommand();
        setupMessageHandler();
    }

    private void setupSubscribeCommand() {
        subscribeCommand = new Command();
        subscribeCommand.setAction("subscribe");
        subscribeCommand.setBook(book);
        subscribeCommand.setType("diff-orders");
    }

    private void setupMessageHandler() {
        wsClient.addMessageHandler(this::handleMessage);
    }

    private void handleMessage(String message) {
        try {
            JsonElement element = parser.parse(message);
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                if (jsonObject.get("type").getAsString().equals("ka")) {
                    receivedMessagesCount++;
                } else if (jsonObject.get("type").getAsString().equals("diff-orders")) {
                    Orders orders = gson.fromJson(message, Orders.class);
                    if (orders != null && orders.getPayload() != null) {
                        processOrderMessage(orders);
                    }
                    receivedMessagesCount++;
                }
            }
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    private void processOrderMessage(Orders orders) {
        orders.getPayload().forEach(o -> {
            if (o.getOperation() == Order.Operation.BUY) {
                if (o.getStatus() == Order.Status.OPEN) {
                    asksOutput.add(o);
                } else if (o.getStatus() == Order.Status.CANCELLED) {
                    asksOutput.remove(o);
                }

            } else if (o.getOperation() == Order.Operation.SELL) {
                if (o.getStatus() == Order.Status.OPEN) {
                    bidsOutput.add(o);
                } else if (o.getStatus() == Order.Status.CANCELLED) {
                    bidsOutput.remove(o);
                }
            }
        });
        generateOutput();
    }

    private void subscribe() {
        wsClient.sendMessage(gson.toJson(subscribeCommand));
    }

    private void generateOutput() {
        if (!bidsOutput.isEmpty()) {
            setChanged();
            notifyObservers(new Object[]{"Bids", sortByRate(Utils.queueToList(bidsOutput), false)});
        }
        if (!asksOutput.isEmpty()) {
            setChanged();
            notifyObservers(new Object[]{"Asks", sortByRate(Utils.queueToList(asksOutput), true)});
        }
    }

    protected List<Order> sortByRate(List<Order> orderList, boolean asc) {
        Collections.sort(orderList, (o1, o2) -> {
            Double o1Rate = new Double(o1.getRate());
            Double o2Rate = new Double(o2.getRate());
            if (o1Rate == o2Rate)
                return 0;
            if (asc) {
                return o1Rate < o2Rate ? -1 : 1;
            } else {
                return o1Rate > o2Rate ? -1 : 1;
            }
        });
        return orderList;
    }

    public void run() {
        int count = 0;
        while (run) {
            try {
                if (!subscribed) {
                    subscribe();
                    subscribed = true;
                }
                sleep(100);
            } catch (Exception e) {
                logger.warn(e);
            }
            if (count * 100 >= CON_TIMEOUT) {
                count = 0;
                if (receivedMessagesCount == 0) {
                    subscribed = false;
                }
                receivedMessagesCount = 0;
            }
            count++;
        }
    }
}