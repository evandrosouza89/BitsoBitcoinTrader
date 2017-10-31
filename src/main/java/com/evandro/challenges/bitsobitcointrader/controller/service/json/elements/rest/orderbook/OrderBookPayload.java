package com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.rest.orderbook;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderBookPayload {

    @SerializedName("asks")
    private List<Order> askList;

    @SerializedName("bids")
    private List<Order> bidList;

    private String sequence;

    @SerializedName("updated_at")
    private String updatedAt;

}
