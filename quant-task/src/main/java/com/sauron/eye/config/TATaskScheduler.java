package com.sauron.eye.config;

import com.sauron.eye.service.WxPushService;
import com.sauron.eye.task.StockPickTask;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import com.sauron.eye.service.StrategyService;
import com.sauron.eye.service.TigerClientService;
import com.sauron.eye.util.TimeUtils;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Slf4j
@Configuration
public class TATaskScheduler {

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private TigerClientService clientService;

    @Autowired
    private WxPushService wxPushService;

    @Value("${stockPickCron.HK}")
    private String pickCronHK;

    @Value("${stockPickCron.US}")
    private String pickCronUS;

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }


    @PostConstruct
    public void startTask() {
        runStockPick();
    }

    public void runStockPick() {
        Market hkMarket = Market.HK;
        log.info("technical analysis, {} stockPickTask start >>>>>>", hkMarket);
        StockPickTask pickTaskHK = new StockPickTask(clientService, strategyService, hkMarket, wxPushService);
        threadPoolTaskScheduler.schedule(pickTaskHK, triggerContext ->
                new CronTrigger(pickCronHK, TimeZone.getTimeZone(TimeUtils.TIMEZONE_GMT8_NAME)).nextExecutionTime(triggerContext)
        );
        log.info("technical analysis, {} stockPickTask end <<<<<<", hkMarket);

        Market usMarket = Market.US;
        log.info("technical analysis, {} stockPickTask start >>>>>>", usMarket);
        StockPickTask pickTaskUS = new StockPickTask(clientService, strategyService, usMarket, wxPushService);
        threadPoolTaskScheduler.schedule(pickTaskUS, triggerContext ->
                new CronTrigger(pickCronUS, TimeZone.getTimeZone(TimeUtils.TIMEZONE_EST_NAME)).nextExecutionTime(triggerContext)
        );
        log.info("technical analysis, {} stockPickTask end <<<<<<", usMarket);
    }
}
