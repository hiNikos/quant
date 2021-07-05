package com.sauron.eye.config;

import com.typesafe.config.ConfigFactory;

import java.util.List;

public class StockConfig {

    public static List<String> US_SYMBOLS = ConfigFactory.load().getStringList("stock.us");

    public static List<String> HK_SYMBOLS = ConfigFactory.load().getStringList("stock.hk");

    public static List<String> CN_SYMBOLS = ConfigFactory.load().getStringList("stock.cn");
}
