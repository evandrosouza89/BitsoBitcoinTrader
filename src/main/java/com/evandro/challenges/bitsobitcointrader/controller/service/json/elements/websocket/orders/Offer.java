package com.evandro.challenges.bitcointrader.controller.service.json.elements.websocket.orders;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Offer {

    @SerializedName("o")
    private String order;

    @SerializedName("r")
    private String rate;

    @SerializedName("a")
    private String amount;

    @SerializedName("v")
    private String value;

    @SerializedName("t")
    private Integer operation;

    @SerializedName("d")
    private Long time;
}
