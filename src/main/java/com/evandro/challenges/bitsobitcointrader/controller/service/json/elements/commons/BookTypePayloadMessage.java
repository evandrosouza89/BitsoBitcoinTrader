package com.evandro.challenges.bitcointrader.controller.service.json.elements.commons;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookTypePayloadMessage<T> extends BookTypeMessage {

    private T payload;
}
