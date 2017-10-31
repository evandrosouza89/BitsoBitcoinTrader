package com.evandro.challenges.bitsobitcointrader.controller.commons;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EnumMessageType {

    @SerializedName("ka")
    KEEP_ALIVE("ka"),

    @SerializedName("available_books")
    AVAILABLE_BOOKS("available_books"),

    @SerializedName("ticker")
    TICKER("ticker"),

    @SerializedName("order_book")
    ORDER_BOOK("order_book"),

    @SerializedName("trades")
    TRADES("trades"),

    @SerializedName("diff-orders")
    DIFF_ORDERS("diff-orders");

    @Getter
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
