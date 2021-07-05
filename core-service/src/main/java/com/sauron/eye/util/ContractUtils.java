package com.sauron.eye.util;

import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import com.tigerbrokers.stock.openapi.client.util.StringUtils;

public class ContractUtils {

    public static boolean isValidSymbol(Market market, String symbol) {
        if (StringUtils.isEmpty(symbol)) {
            return false;
        }
        if (market == Market.CN) {
            if (symbol.contains(".") || symbol.length() != 6) {
                return false;
            }
            if (symbol.startsWith("60") || symbol.startsWith("00") || symbol.startsWith("30")) {
                return true;
            }
            if (symbol.startsWith("68")) {
                return false;
            }
            return false;
        }
        return true;
    }
}
