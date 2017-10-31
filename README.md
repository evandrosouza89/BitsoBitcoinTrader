# BitsoBitcoinTrader
A Bitso API client sample built in JavaFX

This is a Bitso (https://bitso.com) bitcoin trader API sample client. It is built upon Java 8 and JavaFX.

## Frameworks
- Tyrus (https://tyrus-project.github.io/) as websocket client.
- Lombok (https://projectlombok.org/) as code writing enhancer tool.
- GSON (https://github.com/google/gson) as serialization/deserialization library to convert Java Objects into JSON and back.

## How to run
Download **app.rar**, extract files and execute **bitcointrader-1.0-SNAPSHOT-jfx.jar** (Java 8 required).

## About

This software has a trade bot that will update the UI when it would have traded if this were in a live, production trading strategy. This
strategy will work by counting the M consecutive upticks and N consecutive downticks. A trade that executes at a price that is the same as the price of the trade that executed immediately preceding it is known as a “zero tick”. 

An uptick is when a trade executes at a higher price than the most recent non-zero-tick trade before it. 

A downtick is when a trade executes at a lower price than the most recent non-zero-tick trade before it. 

After M consecutive upticks, the algorithm should sell 1 BTC at the price of the most recent uptick. After N​ consecutive downticks, it should buy 1 BTC at the price of the most recent downtick. 

For example:
a. UP -> UP -> UP = 3 upticks
b. UP -> DOWN -> UP = 1 uptick
c. DOWN -> ZERO -> DOWN -> DOWN = 3 downticks
d. DOWN -> UP -> DOWN -> DOWN = 2 downticks

## Program settings

- X Value: Number of top asks and bids to be shown.
- M Value: Trading bot consecutive upticks that will trigger a sell trade.
- N Value: Trading bot consecutive downticks that will trigger a buy trade.

## Notes

**IMPORTANT NOTE 1:**
Bitso's websocket API uses a self-signed certificate, in order to run this software you will need to import it to your JDK/JRE.

https://docs.oracle.com/javase/tutorial/security/toolsign/rstep2.html

**IMPORTANT NOTE 2:**
If you want to compile this code you will need to setup Lombok framework in your IDE:

https://projectlombok.org/setup/

## Screen

![alt text](https://preview.ibb.co/hqQPvm/Screenshot1.png)

## Checklist


| Feature       | File name     | Method name |
| ------------- | ------------- | ----------- |
| Schedule the polling of trades over REST.                    | RecentTradesWorker.java  | protected void requestRecentTrades(HttpURLConnection conn) |
| Request a book snapshot over REST.                           | TopOrdersWorker.java  | protected void requestOrderBook(HttpURLConnection conn) |
| Listen for diff-orders over websocket.                       | (1) WebSocketClient / (2) TopOrdersWorker.java  | (1) public void onMessage(String message) / (2) private void handleWebSocketMessage(String message) |
| Replay diff-orders after book snapshot.                      | TopOrdersWorker.java  | private void processQueuedDiffOrders() |
| Use config option X to request X most recent trades.         | RecentTradesWorker.java | private void setupRESTURL() / protected void requestRecentTrades(HttpURLConnection conn) |
| Use config option X to limit number of ASKs displayed in UI. | (1) Main.java / (2) TopOrdersWorker.java  | (1) public void startWorkers(int m, int n, int x) / (2) private void generateOutput() |
| The loop that causes the trading algorithm to reevaluate.    | (1) RecentTradesWorker.java / (2) TradingStrategy.java  | (1) public void run() / (2) private void analyseTrades(List<Trade> tradeList) |
