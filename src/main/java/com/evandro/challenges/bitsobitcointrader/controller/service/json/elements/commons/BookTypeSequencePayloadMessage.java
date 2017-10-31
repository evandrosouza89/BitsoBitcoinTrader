package com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.commons;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookTypeSequencePayloadMessage<T> extends BookTypePayloadMessage<T> {

    private Integer sequence;

}
