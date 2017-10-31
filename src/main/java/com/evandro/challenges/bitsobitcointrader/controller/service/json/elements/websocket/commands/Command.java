package com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.websocket.commands;

import com.evandro.challenges.bitsobitcointrader.controller.commons.EnumAction;
import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.commons.BookTypeMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Command extends BookTypeMessage {

    private EnumAction action;
}
