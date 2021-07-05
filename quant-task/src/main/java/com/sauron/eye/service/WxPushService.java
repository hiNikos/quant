package com.sauron.eye.service;

import com.tigerbrokers.stock.openapi.client.struct.enums.Market;

public interface WxPushService {

    void notify(Market market, String content);
}
