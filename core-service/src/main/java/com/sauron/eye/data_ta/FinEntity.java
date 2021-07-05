package com.sauron.eye.data_ta;

import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinEntity {

    private RetCode retCode;

    /**
     * 输出数据的起始下标
     */
    private MInteger outBegIdx;

    /**
     * 输出数据的元素个数
     */
    private MInteger outNBElement;

    /**
     * ma
     */
    private double[] outMa;

    /**
     * ema
     */
    private double[] outEma;

    /**
     * sar
     */
    private double[] outSar;

    /**
     * boll
     */
    private double[] outUpperBand;
    private double[] outMiddleBand;
    private double[] outLowerBand;

    /**
     * macd
     */
    private double[] outMacd;
    private double[] outMacdSignal;
    private double[] outMacdHist;

    /**
     * cci
     */
    private double[] outCci;


    public int outBegIdx() {
        return outBegIdx == null ? 0 : outBegIdx.value;
    }

    public int outNBElement() {
        return outNBElement == null ? 0 : outNBElement.value;
    }
}
