package com.sauron.eye.service.impl;

import com.sauron.eye.service.WxPushService;
import com.sauron.eye.util.Json;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class WxPushServiceImpl implements WxPushService {

    private static final String token = ConfigFactory.load().getString("push_plus_token");
    private static final int DEFAULT_TIMEOUT = 6000;
    private static final String CHARSET = "UTF-8";
    private static final String PUSH_PLUS_URL = "http://pushplus.hxtrip.com/send";

    @Override
    public void notify(Market market, String content) {
        try {
            PushData pushData = new PushData(token, market + " Market Test", content);
            Request request = Request.Post(PUSH_PLUS_URL)
                    .bodyString(Json.toString(pushData), ContentType.create("application/json"));
            Response response = request.connectTimeout(DEFAULT_TIMEOUT).socketTimeout(DEFAULT_TIMEOUT).execute();

            HttpResponse httpResponse = response.returnResponse();
            int status = httpResponse.getStatusLine().getStatusCode();
            String resp = EntityUtils.toString(httpResponse.getEntity(), CHARSET);
            if (status != HttpStatus.SC_OK) {
                throw new IOException("push plus pushing failed, response: " + resp);
            }
        } catch (Exception e) {
            log.error("push plus pushing failed", e);
        }
    }

    @Data
    @AllArgsConstructor
    private static class PushData {
        private String token;
        private String title;
        private String content;
    }
}
