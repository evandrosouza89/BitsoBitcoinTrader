package com.evandro.challenges.bitsobitcointrader.controller.workers;

import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumAction;
import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumBook;
import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumMessageType;
import com.evandro.challenges.bitsobitcointrader.controller.service.WebSocketClient;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.rest.orderbook.Order;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.rest.orderbook.OrderBook;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.rest.orderbook.OrderBookPayload;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.websocket.commands.Command;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.websocket.difforders.DiffOrder;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.websocket.difforders.DiffOrders;
import com.evandro.challenges.bitsobitcointrader.controller.workers.commons.Worker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static java.lang.Thread.sleep;

/*Worker designated to maintain the most updated state of order book through rest/websocket connections. Its output is limited by the (int)size argument.
It tries to rebuild the connection after 10 seconds without new messages.
Feeds Main screen*/
public class TopOrdersWorker extends Worker implements Runnable {

    private final Logger logger = LogManager.getLogger();

    private static final int CON_TIMEOUT = 10000; // 10 seconds without new messages = reconnect

    private Command subscribeCommand;

    private WebSocketClient wsClient;

    private JsonParser parser;

    private boolean subscribed; // Websocket subscription command sent?

    private boolean orderBookReceived; // Do we have order book initial states?

    private int receivedMessagesCount; // Are we receiving new messages?

    private List<Order> bidOrderBook; // Bids order book state

    private List<Order> askOrderBook; // Asks order book state

    private Queue<DiffOrders> diffOrdersQueue; // Websocket messages are stored here while the initial state of order book is not received.

    private long latestSequence;

    public TopOrdersWorker(WebSocketClient wsClient, String restURL, EnumBook book, int size) throws MalformedURLException {
        super(restURL, book, size);
        this.wsClient = wsClient;
        setup();
    }

    private void setup() throws MalformedURLException {
        run = true;
        gson = new Gson();
        parser = new JsonParser();
        bidOrderBook = new ArrayList<>();
        askOrderBook = new ArrayList<>();
        diffOrdersQueue = new LinkedList<>();

        setupRESTURL();
        setupSubscribeCommand();
        setupMessageHandler();
    }

    private void setupRESTURL() throws MalformedURLException {
        restURL = restURL.concat("?book=");
        restURL = restURL.concat(book.toString());
        restURL = restURL.concat("&aggregate=");
        restURL = restURL.concat("false");
        url = new URL(restURL);
    }

    private void setupSubscribeCommand() {
        subscribeCommand = new Command();
        subscribeCommand.setAction(EnumAction.SUBSCRIBE);
        subscribeCommand.setBook(book);
        subscribeCommand.setType(EnumMessageType.DIFF_ORDERS);
    }

    private void setupMessageHandler() {
        wsClient.addMessageHandler(this::handleWebSocketMessage);
    }

    /*Request Order Book initial state using HttpURLConnection client*/
    protected void requestOrderBook(HttpURLConnection conn) throws IOException {
        setupHttpGETConnection(conn);

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            OrderBook orderBook = gson.fromJson(new InputStreamReader(conn.getInputStream()), OrderBook.class);
            if (orderBook != null && orderBook.getSuccess() != null && orderBook.getSuccess() && orderBook.getPayload() != null) {
                processOrderBookPayload(orderBook.getPayload());
            }
        }

        conn.disconnect();
    }

    protected void requestOrderBook() throws IOException {
        requestOrderBook((HttpURLConnection) url.openConnection());
    }

    /*Received messages from Http client are processed here*/
    private void processOrderBookPayload(OrderBookPayload orderBookPayload) {
        try {
            latestSequence = Long.valueOf(orderBookPayload.getSequence());
        } catch (NumberFormatException e) {
            return;
        }
        bidOrderBook.clear();
        bidOrderBook.addAll(orderBookPayload.getBidList());
        askOrderBook.clear();
        askOrderBook.addAll(orderBookPayload.getAskList());
        processQueuedDiffOrders(); // Process queued messages from websocket connection
        generateOutput();
        orderBookReceived = true;
    }

    private void subscribe() {
        wsClient.sendMessage(gson.toJson(subscribeCommand));
    }

    /*Received messages from websocket are processed here*/
    private void handleWebSocketMessage(String message) {
        logger.debug(message);
        try {
            JsonElement element = parser.parse(message);
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                if (jsonObject.get("type").getAsString().equals(EnumMessageType.KEEP_ALIVE.toString())) { // Ignoring keep alive messages
                    receivedMessagesCount++;
                } else if (jsonObject.get("type").getAsString().equals(EnumMessageType.DIFF_ORDERS.toString())) {
                    DiffOrders diffOrders = gson.fromJson(message, DiffOrders.class);
                    if (diffOrders != null && diffOrders.getPayload() != null) {
                        if (orderBookReceived) {
                            processDiffOrder(diffOrders);
                        } else {
                            diffOrdersQueue.offer(diffOrders);
                        }
                    }
                    receivedMessagesCount++;
                }
            }
        } catch (Exception e) {
            logger.warn(e);
        }
    }


    /*Updates the initial state of the bid/ask order books*/
    private void processQueuedDiffOrders() {
        while (!diffOrdersQueue.isEmpty()) {
            if (diffOrdersQueue.peek().getSequence() != null && diffOrdersQueue.peek().getSequence() > latestSequence) {
                latestSequence = diffOrdersQueue.peek().getSequence();
                processDiffOrder(diffOrdersQueue.poll());
            } else {
                diffOrdersQueue.poll();
            }
        }
    }

    /*Updates the proper outputs by separating messages by its operation type.
    If DiffOrder status is cancelled, it will remove it from the book current state*/
    private void processDiffOrder(DiffOrders diffOrders) {
        diffOrders.getPayload().forEach(o -> {
            if (o.getOperation() == DiffOrder.Operation.BUY) {
                if (o.getStatus() == DiffOrder.Status.OPEN) {
                    askOrderBook.add(buildOrder(o, diffOrders.getBook().toString()));
                } else if (o.getStatus() == DiffOrder.Status.CANCELLED) {
                    askOrderBook.remove(buildOrder(o, diffOrders.getBook().toString()));
                }

            } else if (o.getOperation() == DiffOrder.Operation.SELL) {
                if (o.getStatus() == DiffOrder.Status.OPEN) {
                    bidOrderBook.add(buildOrder(o, diffOrders.getBook().toString()));
                } else if (o.getStatus() == DiffOrder.Status.CANCELLED) {
                    bidOrderBook.remove(buildOrder(o, diffOrders.getBook().toString()));
                }
            }
        });
        generateOutput();
    }

    /*Converts a DiffOrder object into a Order object*/
    protected Order buildOrder(DiffOrder diffOrder, String book) {
        Order o = new Order();
        o.setOrderId(diffOrder.getOrderId());
        o.setAmount(diffOrder.getAmount());
        o.setBook(book);
        o.setPrice(diffOrder.getRate());
        return o;
    }

    /*Feeds the controller of Main screen*/
    private void generateOutput() {
        if (!bidOrderBook.isEmpty()) {
            setChanged();
            notifyObservers(new Object[]{"Bids",
                    sortByPrice(new ArrayList<>(bidOrderBook.subList(0, bidOrderBook.size() > size ? size : bidOrderBook.size())), false)}); // Feeds top bids table of main screen
        }
        if (!askOrderBook.isEmpty()) {
            setChanged();
            notifyObservers(new Object[]{"Asks",
                    sortByPrice(new ArrayList<>(askOrderBook.subList(0, askOrderBook.size() > size ? size : askOrderBook.size())), true)}); // Feeds top asks table of main screen
        }
    }

    protected List<Order> sortByPrice(List<Order> orderList, boolean asc) {
        Collections.sort(orderList, (o1, o2) -> {
            Double o1Rate = Double.valueOf(o1.getPrice());
            Double o2Rate = Double.valueOf(o2.getPrice());
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

                if (!orderBookReceived) {
                    requestOrderBook();
                }
                sleep(100);
            } catch (Exception e) {
                logger.warn(e);
            }
            if (count * 100 >= CON_TIMEOUT) {
                count = 0;
                if (receivedMessagesCount == 0) {
                    orderBookReceived = false;
                    subscribed = false;
                }
                receivedMessagesCount = 0;
            }
            count++;
        }
    }
}