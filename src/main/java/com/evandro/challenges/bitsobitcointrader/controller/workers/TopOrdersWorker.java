package com.evandro.challenges.bitcointrader.controller.workers;

import com.evandro.challenges.bitcointrader.controller.service.WebSocketClient;
import com.evandro.challenges.bitcointrader.controller.service.json.elements.websocket.commands.Command;
import com.evandro.challenges.bitcointrader.controller.service.json.elements.websocket.orders.Offer;
import com.evandro.challenges.bitcointrader.controller.service.json.elements.websocket.orders.Orders;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Observable;

import static java.lang.Thread.sleep;

public class TopOrdersWorker extends Observable implements Runnable {

    private final Logger logger = LogManager.getLogger();

    private static final int CON_TIMEOUT = 10000;

    private Gson gson;

    private Command subscribeCommand;

    @Getter
    private List<Offer> bestBidsList;

    @Getter
    private List<Offer> bestAsksList;

    private WebSocketClient wsClient;

    private JsonParser parser;

    @Setter
    private boolean run;

    private boolean subscribed;

    private int receivedMessagesCount;

    private String book;

    public TopOrdersWorker(WebSocketClient wsClient, String book) {
        this.wsClient = wsClient;
        this.book = book;
        setup();
    }

    private void setup() {
        run = true;
        gson = new Gson();
        parser = new JsonParser();
        setupSubscribeCommand();
        setupMessageHandler();
    }

    private void setupSubscribeCommand() {
        subscribeCommand = new Command();
        subscribeCommand.setAction("subscribe");
        subscribeCommand.setBook(book);
        subscribeCommand.setType("orders");
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
                } else if (jsonObject.get("type").getAsString().equals("orders")) {
                    Orders orders = gson.fromJson(message, Orders.class);
                    if (orders != null && orders.getPayload() != null) {
                        bestBidsList = orders.getPayload().getBids();
                        bestAsksList = orders.getPayload().getAsks();
                        setChanged();
                        notifyObservers(new Object[]{bestBidsList, bestAsksList});
                    }
                    receivedMessagesCount++;
                }
            }
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    private void subscribe() {
        wsClient.sendMessage(gson.toJson(subscribeCommand));
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
                if(receivedMessagesCount == 0) {
                    subscribed = false;
                }
                receivedMessagesCount = 0;
            }
            count++;
        }
    }
}