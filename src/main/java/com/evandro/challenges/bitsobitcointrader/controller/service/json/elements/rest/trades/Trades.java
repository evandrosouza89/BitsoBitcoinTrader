package com.evandro.challenges.bitcointrader.controller.service.json.elements.rest.trades;

import com.evandro.challenges.bitcointrader.controller.service.json.elements.commons.SuccessPayloadMessageType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Trades extends SuccessPayloadMessageType<List<Trade>> {
}
