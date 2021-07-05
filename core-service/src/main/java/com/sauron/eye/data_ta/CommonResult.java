package com.sauron.eye.data_ta;

import com.tictactec.ta.lib.RetCode;
import lombok.Data;

import java.util.Map;

@Data
public class CommonResult implements Result {

    private final RetCode retCode;

    /**
     * key=时间, value=计算值
     */
    private final Map<Long, Double> values;


    @Override
    public boolean success() {
        return retCode == RetCode.Success;
    }
}
