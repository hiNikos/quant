package com.sauron.eye.config;

import com.tigerbrokers.stock.openapi.client.constant.ApiServiceType;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.request.TigerHttpRequest;
import com.tigerbrokers.stock.openapi.client.https.response.TigerHttpResponse;
import com.tigerbrokers.stock.openapi.client.util.builder.AccountParamBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TigerClientConfig {

    @Bean
    public TigerHttpClient initClient() {
        TigerHttpClient client = new TigerHttpClient(BasicConfig.getDefaultClientConfig());
        grabQuote(client);
        return client;
    }

    private void grabQuote(TigerHttpClient client) {
        TigerHttpRequest request = new TigerHttpRequest(ApiServiceType.GRAB_QUOTE_PERMISSION);
        String bizContent = AccountParamBuilder.instance()
                .buildJson();
        request.setBizContent(bizContent);
        TigerHttpResponse resp = client.execute(request);
        if (resp.isSuccess()) {
            log.info("grab quote success: {}", resp.getData());
        } else {
            log.error("grab quote error: {}", resp.getMessage());
            throw new RuntimeException(resp.getMessage());
        }
    }
}
