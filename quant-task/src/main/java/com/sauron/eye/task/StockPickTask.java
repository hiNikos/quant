package com.sauron.eye.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sauron.eye.service.TigerClientService;
import com.sauron.eye.service.WxPushService;
import com.sauron.eye.util.MarketDataUtils;
import com.sauron.eye.util.TimeUtils;
import com.tigerbrokers.stock.openapi.client.https.domain.quote.item.KlineItem;
import com.tigerbrokers.stock.openapi.client.https.domain.quote.item.SymbolNameItem;
import com.tigerbrokers.stock.openapi.client.struct.enums.KType;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sauron.eye.data_core.Constants.SMALL_REQ_LIMIT;

@Slf4j
@AllArgsConstructor
public class StockPickTask implements Runnable {

    private TigerClientService clientService;

    private Market market;

    private WxPushService wxPushService;


    private static final String SINCE_DATE = TimeUtils.dateToString(LocalDate.now().minusYears(1), TimeUtils.DATE_GAP_FORMAT);

    private static final Double MIN_MARKET_CAP = 10.0;

    private static final Integer SLEEP_TIME = 1000;

    private static final Integer GROUP_ID = 5;

    private static final Integer BEFORE_DAY = ConfigFactory.load().getInt("print_before_days");

    @Override
    public void run() {
        List<SymbolNameItem> symbols = clientService.getSymbolNameItemsByApi(market);
        log.info("running {} market, total symbols: {} \n", market, symbols.size());
        List<String> result = Lists.newArrayList();
        Map<Date, List<PrintItem>> printMap = Maps.newHashMap();
        try {
            Lists.partition(symbols, SMALL_REQ_LIMIT).stream().forEach(symbolNameItems -> {

                // 1.请求历史K线数据
                Map<String, String> symbolMap = symbolNameItems.stream().collect(Collectors.toMap(SymbolNameItem::getSymbol, SymbolNameItem::getName));
                List<String> symbolList = Lists.newArrayList(symbolMap.keySet());
                List<KlineItem> klineItems = clientService.getKLineBySymbols(symbolList, KType.day, SINCE_DATE, null);
                if (CollectionUtils.isEmpty(klineItems)) {
                    log.warn("{} KLine response is empty: {}", market, symbolList);
                    return;
                }
                long startOfDay = TimeUtils.startOfDay(market, -BEFORE_DAY).getTime();

                // 2. 调用策略，根据计算的指标筛选目标合约
                klineItems.forEach(klineItem -> {
                    List<Long> bingoTime = new ArrayList<>();
                    if (bingoTime.size() > 0) {
                        String symbol = klineItem.getSymbol();
                        updatePrintMap(klineItem.getSymbol(), bingoTime, printMap, symbolMap);
                        result.add("{\"group\":\"" + GROUP_ID + "\",\"symbol\":\"" + klineItem.getSymbol() + "\"}");
                    }
                });
            });
        } catch (Exception e) {
            log.error("{} market run bingoTime failed", market, e);
        }
        if (!printMap.isEmpty()) {
            wxPushService.notify(market, convertPrint(market, printMap));
        }
        log.info("Finished {} bingoTime:\n{}\n", market, result);
    }

    private void updatePrintMap(String symbol, List<Long> bingoTime, Map<Date, List<PrintItem>> printMap, Map<String, String> symbolMap) {
        bingoTime.forEach(time -> {
            Date date = new Date(time);
            log.info("bingoTime: {}\n", date);
            List<PrintItem> printItems = printMap.getOrDefault(date, Lists.newArrayList());
            printItems.add(new PrintItem(symbol, symbolMap.get(symbol)));
            printMap.put(date, printItems);
        });
    }

    private String convertPrint(Market market, Map<Date, List<PrintItem>> printMap) {
        return printMap.entrySet().stream().map(entry -> {
            String dateStr = TimeUtils.dateToString(entry.getKey(), TimeUtils.TIME_FORMAT, market);
            return dateStr + "\n" + entry.getValue().toString() + "\n";
        }).collect(Collectors.joining());
    }

    @AllArgsConstructor
    private static class PrintItem {
        private String symbol;
        private String name;

        @Override
        public String toString() {
            return "(\"" + this.symbol + "\": \"" + this.name + "\")";
        }
    }
}
