package com.sauron.eye.service.impl;

import com.google.common.collect.Lists;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.domain.contract.item.ContractItem;
import com.tigerbrokers.stock.openapi.client.https.domain.contract.model.ContractsModel;
import com.tigerbrokers.stock.openapi.client.https.domain.quote.item.KlineItem;
import com.tigerbrokers.stock.openapi.client.https.domain.quote.item.SymbolNameItem;
import com.tigerbrokers.stock.openapi.client.https.request.contract.ContractsRequest;
import com.tigerbrokers.stock.openapi.client.https.request.quote.QuoteKlineRequest;
import com.tigerbrokers.stock.openapi.client.https.request.quote.QuoteSymbolNameRequest;
import com.tigerbrokers.stock.openapi.client.https.request.quote.QuoteSymbolRequest;
import com.tigerbrokers.stock.openapi.client.https.response.contract.ContractResponse;
import com.tigerbrokers.stock.openapi.client.https.response.contract.ContractsResponse;
import com.tigerbrokers.stock.openapi.client.https.response.quote.QuoteKlineResponse;
import com.tigerbrokers.stock.openapi.client.https.response.quote.QuoteSymbolNameResponse;
import com.tigerbrokers.stock.openapi.client.https.response.quote.QuoteSymbolResponse;
import com.tigerbrokers.stock.openapi.client.struct.enums.KType;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import com.tigerbrokers.stock.openapi.client.struct.enums.RightOption;
import com.tigerbrokers.stock.openapi.client.struct.enums.SecType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sauron.eye.config.BasicConfig;
import com.sauron.eye.config.StockConfig;
import com.sauron.eye.exception.OutOfSizeException;
import com.sauron.eye.service.TigerClientService;
import com.sauron.eye.util.ContractUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.sauron.eye.data_core.Constants.REQ_LIMIT;
import static com.sauron.eye.data_core.Constants.SMALL_REQ_LIMIT;

@Slf4j
@Service
public class TigerClientServiceImpl implements TigerClientService {

    @Autowired
    private TigerHttpClient client;

    @Override
    public List<ContractItem> getContractsByConfig(Market market) {
        List<String> symbols;
        switch (market) {
            case US:
                symbols = StockConfig.US_SYMBOLS;
                break;
            case HK:
                symbols = StockConfig.HK_SYMBOLS;
                break;
            case CN:
                symbols = StockConfig.CN_SYMBOLS;
                break;
            default:
                log.error("not supported market: {}", market);
                return Lists.newArrayList();
        }
        return Lists.partition(symbols, symbols.size() / REQ_LIMIT).stream()
                .map(this::getActiveContracts).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public List<ContractItem> getActiveContracts(List<String> symbols) {
        if (symbols.size() > REQ_LIMIT) {
            throw new OutOfSizeException("symbols size out of limit: " + REQ_LIMIT);
        }
        try {
            ContractsModel contractsModel = new ContractsModel(symbols, SecType.STK.name());
            ContractsResponse response = client.execute(ContractsRequest.newRequest(contractsModel));
            if (response.isSuccess()) {
                return response.getItems().stream()
                        .filter(ContractItem::isTradeable)
                        .collect(Collectors.toList());
            } else {
                log.error("response error:" + response.getMessage());
                return Lists.newArrayList();
            }
        } catch (Exception e) {
            log.error("TigerHttpClient execute error", e);
            return Lists.newArrayList();
        }
    }

    @Override
    public List<String> getSymbolsByApi(Market market) {
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

    @Override
    public List<SymbolNameItem> getSymbolNameItemsByApi(Market market) {
        try {
            QuoteSymbolNameResponse response = client.execute(QuoteSymbolNameRequest.newRequest(market));
            if (response.isSuccess()) {
                return response.getSymbolNameItems().stream()
                        .filter(s -> ContractUtils.isValidSymbol(market, s.getSymbol())).collect(Collectors.toList());
            } else {
                log.error("response error:" + response.getMessage());
                return Lists.newArrayList();
            }
        } catch (Exception e) {
            log.error("TigerHttpClient execute error", e);
            return Lists.newArrayList();
        }
    }


    @Override
    public List<KlineItem> getKLines(List<ContractItem> contractItems, KType kType, String beginTime, String endTime) {
        List<String> symbols = contractItems.stream().map(ContractItem::getSymbol).collect(Collectors.toList());
        return getKLineBySymbols(symbols, kType, beginTime, endTime);
    }

    @Override
    public List<KlineItem> getKLineBySymbols(List<String> symbols, KType kType, String beginTime, String endTime) {
        if (symbols.size() > SMALL_REQ_LIMIT) {
            throw new OutOfSizeException("symbols size out of limit: " + SMALL_REQ_LIMIT);
        }
        QuoteKlineResponse resp = client.execute(QuoteKlineRequest
                .newRequest(symbols, kType, beginTime, endTime)
                .withRight(RightOption.br));
        if (resp.isSuccess()) {
            log.debug("get KLineItems size: {}", resp.getKlineItems().size());
            return resp.getKlineItems();
        } else {
            log.error("response error: {}", resp.getMessage());
            return Lists.newArrayList();
        }
    }
}
