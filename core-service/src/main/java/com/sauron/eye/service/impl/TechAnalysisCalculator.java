package com.sauron.eye.service.impl;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import com.sauron.eye.data_ta.FinEntity;

public class TechAnalysisCalculator {

    // Talib核心
    private Core taLib = new Core();

    public FinEntity MA(double[] closeArray, int optInTimePeriod) {
        int startIdx = 0;
        int endIdx = closeArray.length - 1;
        MInteger outBegIdx = new MInteger();
        MInteger outNBElement = new MInteger();
        double[] outMa = new double[closeArray.length];

        RetCode retCode = taLib.movingAverage(
                startIdx, endIdx,
                closeArray,
                optInTimePeriod,
                MAType.Sma,
                outBegIdx, outNBElement,
                outMa
        );
        return FinEntity.builder()
                .retCode(retCode)
                .outBegIdx(outBegIdx)
                .outNBElement(outNBElement)
                .outMa(outMa)
                .build();
    }

    public FinEntity EMA(double[] closeArray, int optInTimePeriod) {
        int startIdx = 0;
        int endIdx = closeArray.length - 1;
        MInteger outBegIdx = new MInteger();
        MInteger outNBElement = new MInteger();
        double[] outEma = new double[closeArray.length];

        RetCode retCode = taLib.ema(
                startIdx, endIdx,
                closeArray,
                optInTimePeriod,
                outBegIdx, outNBElement,
                outEma
        );
        return FinEntity.builder()
                .retCode(retCode)
                .outBegIdx(outBegIdx)
                .outNBElement(outNBElement)
                .outEma(outEma)
                .build();
    }

    public FinEntity SAR(double[] highArray, double[] lowArray, double optInAcceleration, double optInMaximum) {
        int startIdx = 0;
        int endIdx = highArray.length - 1;
        MInteger outBegIdx = new MInteger();
        MInteger outNBElement = new MInteger();
        double[] outSar = new double[highArray.length];

        RetCode retCode = taLib.sar(
                startIdx, endIdx,
                highArray, lowArray,
                optInAcceleration, optInMaximum,
                outBegIdx, outNBElement,
                outSar
        );
        return FinEntity.builder()
                .retCode(retCode)
                .outBegIdx(outBegIdx)
                .outNBElement(outNBElement)
                .outSar(outSar)
                .build();
    }

    public FinEntity BOLL(double[] closeArray, int optInTimePeriod, double optInNbDevUp, double optInNbDevDn) {
        int startIdx = 0;
        int endIdx = closeArray.length - 1;
        MInteger outBegIdx = new MInteger();
        MInteger outNBElement = new MInteger();
        double[] outUpperBand = new double[closeArray.length];
        double[] outMiddleBand = new double[closeArray.length];
        double[] outLowerBand = new double[closeArray.length];

        RetCode retCode = taLib.bbands(
                startIdx, endIdx,
                closeArray,
                optInTimePeriod, optInNbDevUp, optInNbDevDn,
                MAType.Sma,
                outBegIdx, outNBElement,
                outUpperBand, outMiddleBand, outLowerBand
        );
        return FinEntity.builder()
                .retCode(retCode)
                .outBegIdx(outBegIdx)
                .outNBElement(outNBElement)
                .outUpperBand(outUpperBand)
                .outMiddleBand(outMiddleBand)
                .outLowerBand(outLowerBand)
                .build();

    }

    public FinEntity MACD(double[] closeArray, int optInFastPeriod, int optInSlowPeriod, int optInSignalPeriod) {
        int startIdx = 0;
        int endIdx = closeArray.length - 1;
        MInteger outBegIdx = new MInteger();
        MInteger outNBElement = new MInteger();
        double[] outMacd = new double[closeArray.length];
        double[] outMacdSignal = new double[closeArray.length];
        double[] outMacdHist = new double[closeArray.length];

        RetCode retCode = taLib.macd(
                startIdx, endIdx,
                closeArray,
                optInFastPeriod, optInSlowPeriod, optInSignalPeriod,
                outBegIdx, outNBElement,
                outMacd, outMacdSignal, outMacdHist
        );
        return FinEntity.builder()
                .retCode(retCode)
                .outBegIdx(outBegIdx)
                .outNBElement(outNBElement)
                .outMacd(outMacd)
                .outMacdSignal(outMacdSignal)
                .outMacdHist(outMacdHist)
                .build();
    }

    public FinEntity CCI(double[] highArray, double[] lowArray, double[] closeArray, int optInTimePeriod) {
        int startIdx = 0;
        int endIdx = closeArray.length - 1;
        MInteger outBegIdx = new MInteger();
        MInteger outNBElement = new MInteger();
        double[] outCci = new double[closeArray.length];

        RetCode retCode = taLib.cci(
                startIdx, endIdx,
                highArray, lowArray, closeArray,
                optInTimePeriod,
                outBegIdx, outNBElement,
                outCci
        );
        return FinEntity.builder()
                .retCode(retCode)
                .outBegIdx(outBegIdx)
                .outNBElement(outNBElement)
                .outCci(outCci)
                .build();
    }
}
