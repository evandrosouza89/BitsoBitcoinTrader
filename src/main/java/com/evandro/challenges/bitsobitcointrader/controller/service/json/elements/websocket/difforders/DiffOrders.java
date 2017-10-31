package com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.websocket.difforders;

import com.evandro.challenges.bitsobitcointrader.controller.service.json.elements.commons.BookTypeSequencePayloadMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DiffOrders extends BookTypeSequencePayloadMessage<List<DiffOrder>> {
}
