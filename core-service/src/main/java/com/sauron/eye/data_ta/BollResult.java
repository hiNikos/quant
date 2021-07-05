package com.sauron.eye.data_ta;

import com.tictactec.ta.lib.RetCode;
import lombok.Data;

import java.util.Map;

@Data
public class BollResult implements Result {

    private final RetCode retCode;

    private final Map<Long, BollItem> values;


    @Override
    public boolean success() {
        return retCode == RetCode.Success;
    }

    @Data
    public static class BollItem {
        /**
         * 布林线上轨
         */
        private final Double upperBandValue;

        /**
         * 布林线中轨
         */
        private final Double middleBandValue;

        /**
         * 布林线下轨
         */
        private final Double lowerBandValue;
    }
}
