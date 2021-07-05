package com.sauron.eye.service;

import com.tigerbrokers.stock.openapi.client.https.domain.contract.item.ContractItem;
import com.tigerbrokers.stock.openapi.client.https.domain.quote.item.KlineItem;
import com.tigerbrokers.stock.openapi.client.https.domain.quote.item.SymbolNameItem;
import com.tigerbrokers.stock.openapi.client.struct.enums.KType;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;

import java.util.List;

public interface TigerClientService {

    List<ContractItem> getContractsByConfig(Market market);

    List<ContractItem> getActiveContracts(List<String> symbols);

    List<String> getSymbolsByApi(Market market);

    List<SymbolNameItem> getSymbolNameItemsByApi(Market market);

    List<KlineItem> getKLines(List<ContractItem> contractItems, KType kType, String beginTime, String endTime);

    List<KlineItem> getKLineBySymbols(List<String> symbols, KType kType, String beginTime, String endTime);
}
