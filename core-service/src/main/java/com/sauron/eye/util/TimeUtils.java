package com.sauron.eye.util;

import com.tigerbrokers.stock.openapi.client.struct.enums.Market;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimeUtils {

    public static final String TIMEZONE_EST_NAME = "US/Eastern";
    public static final ZoneId TIMEZONE_EST = ZoneId.of(TIMEZONE_EST_NAME);
    public static final String TIMEZONE_GMT8_NAME = "GMT+8";
    public static final ZoneId TIMEZONE_GMT8 = ZoneId.of(TIMEZONE_GMT8_NAME);

    /**
     * 常用时间转换格式
     */
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_NO_GAP_FORMAT = "yyyyMMdd";
    public static final String DATE_GAP_FORMAT = "yyyy-MM-dd";
    public static final String TIME_HH_MM_FORMAT = "HHmm";
    public static final Map<String, DateTimeFormatter> DATE_TIME_FORMAT_MAP = new HashMap<String, DateTimeFormatter>() {
        {
            put(TIME_FORMAT, DateTimeFormatter.ofPattern(TIME_FORMAT));
            put(DATE_NO_GAP_FORMAT, DateTimeFormatter.ofPattern(DATE_NO_GAP_FORMAT));
            put(DATE_GAP_FORMAT, DateTimeFormatter.ofPattern(DATE_GAP_FORMAT));
            put(TIME_HH_MM_FORMAT, DateTimeFormatter.ofPattern(TIME_HH_MM_FORMAT));
        }
    };

    public static ZoneId getTimeZone(Market market) {
        switch (market) {
            case US:
                return TIMEZONE_EST;
            case HK:
            case CN:
                return TIMEZONE_GMT8;
            default:
                throw new IllegalArgumentException("Unknown market " + market);
        }
    }

    /**
     * 转换为yyyy-mm-dd
     */
    public static LocalDate toLocalDateInZone(Date date, ZoneId zoneId) {
        return date.toInstant().atZone(zoneId).toLocalDate();
    }

    public static LocalDate toLocalDateInZone(Long timeStamp, ZoneId zoneId) {
        return Instant.ofEpochMilli(timeStamp).atZone(zoneId).toLocalDate();
    }

    /**
     * 一天的开始时间
     */
    public static Date startOfDay(LocalDate localDate, Market market) {
        return startOfDay(localDate, getTimeZone(market));
    }

    public static Date startOfDay(LocalDate localDate, ZoneId zoneId) {
        return toDate(toDateInZone(localDate, zoneId));
    }

    public static Date startOfDay(ZonedDateTime dateTime) {
        return toDate(dateTime.truncatedTo(ChronoUnit.DAYS));
    }

    /**
     * 获取所在时区当前时刻加 {plusDays} 后的那天的开始时间
     */
    public static Date startOfDay(ZoneId zoneId, int plusDays) {
        return startOfDay(nowInZone(zoneId).plusDays(plusDays));
    }

    public static Date startOfDay(Market market, int plusDays) {
        return startOfDay(nowInMarket(market).plusDays(plusDays));
    }

    /**
     * 一天的开始时间（当前市场）
     */
    public static Date startOfToday(Market market) {
        return startOfDay(nowInMarket(market));
    }

    /**
     * 返回所在时区的当前时间
     */
    public static ZonedDateTime nowInZone(ZoneId zoneId) {
        return ZonedDateTime.now(zoneId);
    }

    /**
     * 返回所在市场的当前时间
     */
    public static ZonedDateTime nowInMarket(Market market) {
        ZoneId zoneId= getTimeZone(market);
        return ZonedDateTime.now(zoneId);
    }


    public static ZonedDateTime toDateInZone(LocalDate date, ZoneId zoneId) {
        return date.atStartOfDay(zoneId);
    }

    public static Date toDate(ZonedDateTime dateTime) {
        return Date.from(dateTime.toInstant());
    }

    /**
     * 转换日期输出格式
     */
    public static String dateToString(Date date, String format, ZoneId zoneId) {
        DateTimeFormatter formatter = getDateTimeFormatter(format).withZone(zoneId);
        return formatter.format(date.toInstant());
    }

    /**
     * 转换日期输出格式
     */
    public static String dateToString(Date date, String format, Market market) {
        return dateToString(date, format, getTimeZone(market));
    }

    /**
     * 转换日期输出格式
     */
    public static String dateToString(long datetime, String format, Market market) {
        Date date = new Date(datetime);
        return dateToString(date, format, getTimeZone(market));
    }

    /**
     * 转换日期输出格式
     */
    public static String dateToString(LocalDate date, String format) {
        return date.format(getDateTimeFormatter(format));
    }

    public static DateTimeFormatter getDateTimeFormatter(String format) {
        if (DATE_TIME_FORMAT_MAP.containsKey(format)) {
            return (DateTimeFormatter)DATE_TIME_FORMAT_MAP.get(format);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            DATE_TIME_FORMAT_MAP.put(format, formatter);
            return formatter;
        }
    }
}
