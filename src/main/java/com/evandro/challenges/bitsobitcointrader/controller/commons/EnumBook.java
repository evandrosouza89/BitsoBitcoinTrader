package com.evandro.challenges.bitsobitcointrader.controller.commons;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EnumBook {

    @SerializedName("btc_mxn")
    BTC_MXN("btc_mxn"),

    @SerializedName("eth_mxn")
    ETH_MXN("eth_mxn"),

    @SerializedName("xrp_btc")
    XRP_BTC("xrp_btc"),

    @SerializedName("xrp_mxn")
    XRP_MXN("xrp_mxn"),

    @SerializedName("eth_btc")
    ETH_BTC("eth_btc"),

    @SerializedName("bch_btc")
    BCH_BTC("bch_btc");

    @Getter
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
