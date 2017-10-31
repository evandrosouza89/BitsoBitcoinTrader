package com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.commons;

import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumBook;
import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumMessageType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookTypeMessage {

    private EnumBook book;

    private EnumMessageType type;
}
