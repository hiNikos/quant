package com.sauron.eye.data_ta;

import com.tictactec.ta.lib.RetCode;
import lombok.Data;

import java.util.Map;

@Data
public class MacdResult implements Result {

    private final RetCode retCode;

    private final Map<Long, MacdItem> values;


    @Override
    public boolean success() {
        return retCode == RetCode.Success;
    }

    @Data
    public static class MacdItem {
        /**
         * DIF线(快线)：12天EMA-26天EMA
         */
        private final Double difValue;

        /**
         * DEA线(慢线)：dif的9天EMA
         */
        private final Double deaValue;

        /**
         * MACD柱状图：dif与dea差值
         */
        private final Double histValue;

        public boolean isValid() {
            return difValue != null && deaValue != null && histValue != null;
        }

        public static MacdItem getZero() {
            return new MacdItem(0d, 0d, 0d);
        }
    }
}
