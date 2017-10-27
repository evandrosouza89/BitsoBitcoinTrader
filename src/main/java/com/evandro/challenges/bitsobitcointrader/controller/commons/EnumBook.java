package com.evandro.challenges.bitsobitcointrader.controller.commons;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EnumBook {

    BTC_MXN("btc_mxn"),
    ETH_MXN("eth_mxn"),
    XRP_BTC("xrp_btc"),
    XRP_MXN("xrp_mxn"),
    ETH_BTC("eth_btc"),
    BCH_BTC("bch_btc");

    @Getter
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
