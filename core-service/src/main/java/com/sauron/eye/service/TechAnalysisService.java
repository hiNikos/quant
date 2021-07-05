package com.sauron.eye.service;

import lombok.NonNull;
import org.springframework.lang.Nullable;
import com.sauron.eye.data_ta.BollResult;
import com.sauron.eye.data_ta.MacdResult;
import com.sauron.eye.data_ta.CommonResult;

import java.util.Map;

public interface TechAnalysisService {

    /**
     *
     *
     * @param closePrice
     * @param timePeriod
     * @return
     */
    CommonResult MA(@NonNull Map<Long, Double> closePrice,
                    @Nullable Integer timePeriod);

    /**
     *
     *
     * @param closePrice
     * @param timePeriod
     * @return
     */
    CommonResult EMA(@NonNull Map<Long, Double> closePrice,
                     @Nullable Integer timePeriod);

    /**
     * 计算停损点转向指标（SAR）
     *
     * @param highPrice    最高价
     * @param lowPrice     最低价
     * @param acceleration 加速因子（因子 0.02）
     * @param maximum      加速因子最大值（因子 0.2）
     * @return
     */
    CommonResult SAR(@NonNull Map<Long, Double> highPrice, @NonNull Map<Long, Double> lowPrice,
                     @Nullable Double acceleration, @Nullable Double maximum);


    /**
     * 计算布林线指标（BOLL：Bolinger Bands）
     *
     * @param closePrice 收盘价
     * @param timePeriod 均线天数（因子 MA5、MA10、MA20...）
     * @param nbDevUp    上轨线标准差（因子 默认为2）
     * @param nbDevDn    下轨线标准差（因子 默认为2）
     * @return
     */
    BollResult BOLL(@NonNull Map<Long, Double> closePrice,
                    @Nullable Integer timePeriod, @Nullable Double nbDevUp, @Nullable Double nbDevDn);


    /**
     * 计算平滑异同移动平均线(MACD)
     *
     * @param closePrice   收盘价
     * @param fastPeriod   快速移动平均线（因子 12日EMA）
     * @param slowPeriod   慢速移动平均线（因子 26日EMA）
     * @param signalPeriod DEA移动平均线(因子 9日EMA)
     * @return
     */
    MacdResult MACD(@NonNull Map<Long, Double> closePrice,
                    @Nullable Integer fastPeriod, @Nullable Integer slowPeriod, @Nullable Integer signalPeriod);

    /**
     *
     *
     * @param highPrice
     * @param lowPrice
     * @param closePrice
     * @param timePeriod
     * @return
     */
    CommonResult CCI(@NonNull Map<Long, Double> highPrice, @NonNull Map<Long, Double> lowPrice, @NonNull Map<Long, Double> closePrice,
                     @Nullable Integer timePeriod);
}
