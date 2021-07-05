package com.sauron.eye;

import com.google.common.collect.Lists;
import com.sauron.eye.config.BasicConfig;
import com.tigerbrokers.stock.openapi.client.constant.ApiServiceType;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.domain.contract.item.ContractItem;
import com.tigerbrokers.stock.openapi.client.https.domain.contract.model.ContractModel;
import com.tigerbrokers.stock.openapi.client.https.domain.contract.model.ContractsModel;
import com.tigerbrokers.stock.openapi.client.https.domain.quote.item.KlineItem;
import com.tigerbrokers.stock.openapi.client.https.domain.quote.item.KlinePoint;
import com.tigerbrokers.stock.openapi.client.https.request.TigerHttpRequest;
import com.tigerbrokers.stock.openapi.client.https.request.contract.ContractRequest;
import com.tigerbrokers.stock.openapi.client.https.request.contract.ContractsRequest;
import com.tigerbrokers.stock.openapi.client.https.request.quote.QuoteKlineRequest;
import com.tigerbrokers.stock.openapi.client.https.request.quote.QuoteSymbolRequest;
import com.tigerbrokers.stock.openapi.client.https.response.TigerHttpResponse;
import com.tigerbrokers.stock.openapi.client.https.response.contract.ContractResponse;
import com.tigerbrokers.stock.openapi.client.https.response.contract.ContractsResponse;
import com.tigerbrokers.stock.openapi.client.https.response.quote.QuoteKlineResponse;
import com.tigerbrokers.stock.openapi.client.https.response.quote.QuoteSymbolResponse;
import com.tigerbrokers.stock.openapi.client.struct.enums.KType;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import com.tigerbrokers.stock.openapi.client.struct.enums.RightOption;
import com.tigerbrokers.stock.openapi.client.struct.enums.SecType;
import com.tigerbrokers.stock.openapi.client.util.builder.AccountParamBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import com.sauron.eye.data_ta.MacdResult;
import com.sauron.eye.service.TechAnalysisService;
import com.sauron.eye.service.impl.StrategyServiceImpl;
import com.sauron.eye.service.impl.TechAnalysisServiceImpl;
import com.sauron.eye.util.ContractUtils;
import com.sauron.eye.util.MarketDataUtils;
import com.sauron.eye.util.TimeUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.sauron.eye.data_core.Constants.*;

@Slf4j
public class BasicConfigTest {

    TigerHttpClient client = new TigerHttpClient(BasicConfig.getDefaultClientConfig());

    @Test
    public void connectionTest() {
        // 查询合约信息
        TigerHttpRequest request = new TigerHttpRequest(ApiServiceType.CONTRACT);

        String bizContent = AccountParamBuilder.instance()
                .account(BasicConfig.paper_account)
                .symbol("AAPL")
                .buildJson();

        request.setBizContent(bizContent);
        TigerHttpResponse resp = client.execute(request);
        if (resp.isSuccess()) {
            log.info("response success: {}", resp.getData());
        } else {
            log.error("response error: {}", resp.getMessage());
        }
    }

    @Test
    public void contractTest() {
        ContractModel contractModel = new ContractModel("JD", SecType.STK.name());
        ContractResponse response = client.execute(ContractRequest.newRequest(contractModel));
        if (response.isSuccess()) {
            System.out.println(response.getItems());
        } else {
            System.out.println("response error:" + response.getMessage());
        }
    }

    @Test
    public void getSymbolsTest() {
        try {
            QuoteSymbolResponse response = client.execute(QuoteSymbolRequest.newRequest(Market.CN));
            if (response.isSuccess()) {
                List<String> symbols = response.getSymbols();
                log.info("total symbol size: {}", symbols.size());
                symbols = symbols.stream().filter(s -> !s.contains(".")).collect(Collectors.toList());
                log.info("total filter symbol size: {}\n", symbols.size());
                Lists.partition(symbols, REQ_LIMIT).stream().forEach(sample -> {
                    log.info("sample size: {}\n {}", sample.size(), sample);
                    ContractsModel contractsModel = new ContractsModel(sample, SecType.STK.name());
                    ContractsResponse resp = client.execute(ContractsRequest.newRequest(contractsModel));
                    if (response.isSuccess()) {
                        log.info("get contract size: {}\n", resp.getItems().size());
                        Map<Integer, List<ContractItem>> map = resp.getItems().stream().collect(Collectors.groupingBy(ContractItem::getStatus, Collectors.toList()));
                        map.forEach((k, v) -> {
                            log.info("status: {}", k);
                            log.info("symbols size: {}", v.size());
                            log.info("symbols: {}\n", v.stream().map(ContractItem::getSymbol).collect(Collectors.toList()));
                        });
                    }
                });
            } else {
                log.error("response error:" + response.getMessage());
            }
        } catch (Exception e) {
            log.error("TigerHttpClient execute error", e);
        }
    }

    @Test
    public void grabQuoteTest() {
        // 抢占行情
        TigerHttpRequest request = new TigerHttpRequest(ApiServiceType.GRAB_QUOTE_PERMISSION);
        String bizContent = AccountParamBuilder.instance()
                .buildJson();
        request.setBizContent(bizContent);
        TigerHttpResponse resp = client.execute(request);
        if (resp.isSuccess()) {
            log.info(resp.getData().toString());
        } else {
            log.error("response error: {}", resp.getMessage());
        }
    }

    @Test
    public void kLineTest() {
        // 获取K线数据
        QuoteKlineResponse resp = client.execute(QuoteKlineRequest
                .newRequest(Lists.newArrayList("AAPL"), KType.day, "2021-01-01", null)
                .withLimit(1000)
                .withRight(RightOption.br));
        if (resp.isSuccess()) {
            log.info(Arrays.toString(resp.getKlineItems().toArray()));
        } else {
            log.error("response error: {}", resp.getMessage());
        }
        ZoneId zoneId = TimeUtils.TIMEZONE_EST;
        Map<Long, Double> closePrice = resp.getKlineItems().get(0).getItems().stream()
                .collect(Collectors.toMap(KlinePoint::getTime, k -> k.getClose(), (o1, o2) -> o1, TreeMap::new));
        TechAnalysisServiceImpl techAanalysisService = new TechAnalysisServiceImpl();
        MacdResult result = techAanalysisService.MACD(closePrice, 12, 26, 9);
        result.getValues().entrySet().forEach(entry -> {
            LocalDate localDate = TimeUtils.toLocalDateInZone(entry.getKey(), zoneId);
            System.out.println(localDate + " " + entry.getValue());
        });
    }

    private void printProcess(int size, AtomicInteger count) {
        int now = count.getAndIncrement();
        log.info("{}, {}, {}", now, (int) ((now + 1) * 10.0 / size), (int) (now * 10.0 / size));
        if ((int) ((now + 1) * 10.0 / size) > (int) (now * 10.0 / size)) {
            log.info("【Current Progress====={}%】\n", (int) ((now + 1) * 100.0 / size));
        }
    }

    @Test
    public void marketCapTest() {
        String symbol = "BGI";
        log.info("market cap: {}", MarketDataUtils.getMarketCap(symbol));
    }

    private List<String> getSymbolsByApi(Market market) {
        try {
            QuoteSymbolResponse response = client.execute(QuoteSymbolRequest.newRequest(market));
            if (response.isSuccess()) {
                return response.getSymbols().stream()
                        .filter(s -> ContractUtils.isValidSymbol(market, s)).collect(Collectors.toList());
            } else {
                log.error("response error:" + response.getMessage());
                return Lists.newArrayList();
            }
        } catch (Exception e) {
            log.error("TigerHttpClient execute error", e);
            return Lists.newArrayList();
        }
    }
}
