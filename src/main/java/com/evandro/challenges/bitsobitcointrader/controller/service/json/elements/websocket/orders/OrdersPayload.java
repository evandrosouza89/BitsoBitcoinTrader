package com.evandro.challenges.bitcointrader.controller.service.json.elements.websocket.orders;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrdersPayload {

    private List<Offer> bids;

    private List<Offer> asks;
}
