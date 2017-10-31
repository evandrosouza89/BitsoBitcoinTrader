package com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.rest.orderbook;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Order {

    @SerializedName("oid")
    private String orderId;

    @SerializedName("book")
    private String book;

    @SerializedName("price")
    private String price;

    @SerializedName("amount")
    private String amount;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Order o = (Order) obj;

        return o.getOrderId().equals(this.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, book, price, amount);
    }
}
