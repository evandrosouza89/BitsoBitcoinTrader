package com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.websocket.orders;

import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.commons.BookTypeSequencePayloadMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Orders extends BookTypeSequencePayloadMessage<List<Order>> {
}
