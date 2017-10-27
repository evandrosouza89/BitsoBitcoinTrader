package com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.websocket.orders;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Order {

    @SerializedName("o")
    private String orderId;

    @SerializedName("r")
    private String rate;

    @SerializedName("a")
    private String amount;

    @SerializedName("v")
    private String value;

    @SerializedName("t")
    private Operation operation;

    @SerializedName("d")
    private Long time;

    @SerializedName("s")
    private Status status;

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
        return Objects.hash(orderId, rate, amount, status, time, operation, value);
    }

    public enum Operation {

        @SerializedName("0")
        BUY,

        @SerializedName("1")
        SELL
    }

    public enum Status {

        @SerializedName("open")
        OPEN,

        @SerializedName("cancelled")
        CANCELLED

    }
}
