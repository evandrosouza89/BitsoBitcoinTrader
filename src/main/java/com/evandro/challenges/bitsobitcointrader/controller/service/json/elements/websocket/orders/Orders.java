package com.evandro.challenges.bitcointrader.controller.service.json.elements.websocket.orders;

import com.evandro.challenges.bitcointrader.controller.service.json.elements.commons.BookTypePayloadMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Orders  extends BookTypePayloadMessage<OrdersPayload> {
}
