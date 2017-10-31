package com.evandro.challenges.bitsobitcointrader.controller.commons;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EnumAction {

    @SerializedName("subscribe")
    SUBSCRIBE("subscribe");

    @Getter
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
