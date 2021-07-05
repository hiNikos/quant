package com.sauron.eye.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import com.sauron.eye.data_ta.BollResult;
import com.sauron.eye.data_ta.FinEntity;
import com.sauron.eye.data_ta.MacdResult;
import com.sauron.eye.data_ta.CommonResult;
import com.sauron.eye.service.TechAnalysisService;

import java.util.List;
import java.util.Map;

@Service
public class TechAnalysisServiceImpl implements TechAnalysisService {

    private TechAnalysisCalculator calculator = new TechAnalysisCalculator();

    @Override
    public CommonResult MA(@NonNull Map<Long, Double> closePrice, Integer timePeriod) {
        if (timePeriod == null) {
            timePeriod = Integer.MIN_VALUE;
        }
        double[] closeArray = closePrice.values().stream().mapToDouble(this::getOrDefaultZero).toArray();
        FinEntity finEntity = calculator.MA(closeArray, timePeriod);

        List<Long> dateTimes = Lists.newArrayList(closePrice.keySet());
        Map<Long, Double> maMap = Maps.newTreeMap();
        int begIdx = finEntity.outBegIdx();
        double[] ma = finEntity.getOutMa();
        for (int i = 0; i < dateTimes.size(); i++) {
            if (i < begIdx) {
                maMap.put(dateTimes.get(i), null);
            } else {
                maMap.put(dateTimes.get(i), ma[i - begIdx]);
            }
        }
        return new CommonResult(finEntity.getRetCode(), maMap);
    }

    @Override
    public CommonResult EMA(@NonNull Map<Long, Double> closePrice, Integer timePeriod) {
        if (timePeriod == null) {
            timePeriod = Integer.MIN_VALUE;
        }
        double[] closeArray = closePrice.values().stream().mapToDouble(this::getOrDefaultZero).toArray();
        FinEntity finEntity = calculator.EMA(closeArray, timePeriod);

        List<Long> dateTimes = Lists.newArrayList(closePrice.keySet());
        Map<Long, Double> emaMap = Maps.newTreeMap();
        int begIdx = finEntity.outBegIdx();
        double[] ema = finEntity.getOutEma();
        for (int i = 0; i < dateTimes.size(); i++) {
            if (i < begIdx) {
                emaMap.put(dateTimes.get(i), null);
            } else {
                emaMap.put(dateTimes.get(i), ema[i - begIdx]);
            }
        }
        return new CommonResult(finEntity.getRetCode(), emaMap);
    }

    @Override
    public CommonResult SAR(@NonNull Map<Long, Double> highPrice, @NonNull Map<Long, Double> lowPrice, Double acceleration, Double maximum) {
        if (acceleration == null) {
            acceleration = 0.02D;
        }
        if (maximum == null) {
            maximum = 0.2D;
        }

        double[] highArray = highPrice.values().stream().mapToDouble(this::getOrDefaultZero).toArray();
        double[] lowArray = lowPrice.values().stream().mapToDouble(this::getOrDefaultZero).toArray();
        FinEntity finEntity = calculator.SAR(highArray, lowArray, acceleration, maximum);

        List<Long> dateTimes = Lists.newArrayList(highPrice.keySet());
        Map<Long, Double> sarMap = Maps.newTreeMap();
        int begIdx = finEntity.outBegIdx();
        double[] sar = finEntity.getOutSar();
        for (int i = 0; i < dateTimes.size(); i++) {
            if (i < begIdx) {
                sarMap.put(dateTimes.get(i), null);
            } else {
                sarMap.put(dateTimes.get(i), sar[i - begIdx]);
            }
        }
        return new CommonResult(finEntity.getRetCode(), sarMap);
    }

    @Override
    public BollResult BOLL(@NonNull Map<Long, Double> closePrice, Integer timePeriod, Double nbDevUp, Double nbDevDn) {
        if (timePeriod == null) {
            timePeriod = Integer.MIN_VALUE;
        }
        if (nbDevUp == null) {
            nbDevUp = 2.0D;
        }
        if (nbDevDn == null) {
            nbDevDn = 2.0D;
        }

        double[] closeArray = closePrice.values().stream().mapToDouble(this::getOrDefaultZero).toArray();
        FinEntity finEntity = calculator.BOLL(closeArray, timePeriod, nbDevUp, nbDevDn);

        List<Long> dateTimes = Lists.newArrayList(closePrice.keySet());
        Map<Long, BollResult.BollItem> bollItemMap = Maps.newTreeMap();
        int begIdx = finEntity.outBegIdx();
        double[] upperBand = finEntity.getOutUpperBand();
        double[] middleBand = finEntity.getOutMiddleBand();
        double[] lowerBand = finEntity.getOutLowerBand();
        for (int i = 0; i < dateTimes.size(); i++) {
            if (i < begIdx) {
                bollItemMap.put(dateTimes.get(i), null);
            } else {
                BollResult.BollItem item = new BollResult.BollItem(upperBand[i - begIdx], middleBand[i - begIdx], lowerBand[i - begIdx]);
                bollItemMap.put(dateTimes.get(i), item);
            }
        }
        return new BollResult(finEntity.getRetCode(), bollItemMap);
    }

    @Override
    public MacdResult MACD(@NonNull Map<Long, Double> closePrice, Integer fastPeriod, Integer slowPeriod, Integer signalPeriod) {
        if (fastPeriod == null) {
            fastPeriod = Integer.MIN_VALUE;
        }
        if (slowPeriod == null) {
            slowPeriod = Integer.MIN_VALUE;
        }
        if (signalPeriod == null) {
            signalPeriod = Integer.MIN_VALUE;
        }

        double[] closeArray = closePrice.values().stream().mapToDouble(this::getOrDefaultZero).toArray();
        FinEntity finEntity = calculator.MACD(closeArray, fastPeriod, slowPeriod, signalPeriod);

        List<Long> dateTimes = Lists.newArrayList(closePrice.keySet());
        Map<Long, MacdResult.MacdItem> macdItemMap = Maps.newTreeMap();
        int begIdx = finEntity.outBegIdx();
        double[] macd = finEntity.getOutMacd();
        double[] macdSignal = finEntity.getOutMacdSignal();
        double[] macdHist = finEntity.getOutMacdHist();
        for (int i = 0; i < dateTimes.size(); i++) {
            if (i < begIdx) {
                macdItemMap.put(dateTimes.get(i), null);
            } else {
                MacdResult.MacdItem item = new MacdResult.MacdItem(macd[i - begIdx], macdSignal[i - begIdx], macdHist[i - begIdx] * 2);
                macdItemMap.put(dateTimes.get(i), item);
            }
        }
        return new MacdResult(finEntity.getRetCode(), macdItemMap);
    }

    @Override
    public CommonResult CCI(@NonNull Map<Long, Double> highPrice, @NonNull Map<Long, Double> lowPrice, @NonNull Map<Long, Double> closePrice, Integer timePeriod) {
        if (timePeriod == null) {
            timePeriod = Integer.MIN_VALUE;
        }
        double[] highArray = highPrice.values().stream().mapToDouble(this::getOrDefaultZero).toArray();
        double[] lowArray = lowPrice.values().stream().mapToDouble(this::getOrDefaultZero).toArray();
        double[] closeArray = closePrice.values().stream().mapToDouble(this::getOrDefaultZero).toArray();
        FinEntity finEntity = calculator.CCI(highArray, lowArray, closeArray, timePeriod);

        List<Long> dateTimes = Lists.newArrayList(highPrice.keySet());
        Map<Long, Double> cciMap = Maps.newTreeMap();
        int begIdx = finEntity.outBegIdx();
        double[] cci = finEntity.getOutCci();
        for (int i = 0; i < dateTimes.size(); i++) {
            if (i < begIdx) {
                cciMap.put(dateTimes.get(i), null);
            } else {
                cciMap.put(dateTimes.get(i), cci[i - begIdx]);
            }
        }
        return new CommonResult(finEntity.getRetCode(), cciMap);
    }

    private double getOrDefaultZero(Double d) {
        return d == null ? 0d : d.doubleValue();
    }
}
