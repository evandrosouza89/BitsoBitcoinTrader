package com.evandro.challenges.bitcointrader.controller.service.json.elements.commons;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessPayloadMessageType <T> {

    private Boolean success;

    private T payload;
}
