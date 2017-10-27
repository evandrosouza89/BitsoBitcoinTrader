package com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.rest.trades;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trade {

    private String book;

    @SerializedName("created_at")
    private String createdAt;

    private String amount;

    @SerializedName("maker_side")
    private String makerSide;

    private String price;

    private Long tid;
}
