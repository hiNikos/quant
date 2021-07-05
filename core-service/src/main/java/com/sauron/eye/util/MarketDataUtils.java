package com.sauron.eye.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

@Slf4j
public class MarketDataUtils {

    private static final String YAHOO_URL = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=";

    private static final String SINA_US_URL = "https://hq.sinajs.cn/list=" + "gb_";

    private static final String SINA_HK_URL = "https://hq.sinajs.cn/list=" + "hk";

    private static final String SINA_CN_URL = "https://hq.sinajs.cn/list=" + "sh";

    private static final String TIGR_URL = "https://hq.laohu8.com/{}stock_info/detail/{}?_s={}&lang=zh_CN&lang_content=cn&region=NZL&deviceId=web-daaecab122&appVer=4.12.0&appName=laohu8&vendor=web&platform=web&edition=full";

    private static final Integer UNIT = 100000000;

    private static CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    public static double getMarketCap(String symbol) {
        String resp = "";
        try {
            String url = SINA_US_URL + symbol.toLowerCase();
            HttpGet httpGet = new HttpGet(url);
            if (url.contains("sina")) {
                httpGet.setHeader("referer", "http://finance.sina.com.cn");
            }
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                resp = EntityUtils.toString(response.getEntity());
                return Double.valueOf(resp.split(",")[12]) / UNIT;
            } else {
                log.error("http request error, url = {}", url);
            }
        } catch (Exception e) {
            log.error("get marketCap failed, symbol: {}, resp: {}", symbol, resp, e);
        }
        return 0d;
    }

    /**
     * 目前只支持US/HK
     */
    public static double getMarketCapByTiger(Market market, String symbol) {
        String resp = "";
        String domain_path = market == Market.HK ? "hkstock/" : "";
        try {
            String url = TextUtils.format(TIGR_URL, domain_path, symbol.toUpperCase(), System.currentTimeMillis());
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Host", "hq.laohu8.com");
            httpGet.setHeader("Connection", "keep-alive");
            httpGet.setHeader("sec-ch-ua", "\"Chromium\";v=\"110\", \"Not A(Brand\";v=\"24\", \"Google Chrome\";v=\"110\"");
            httpGet.setHeader("Accept", "application/json, text/plain, */*");
            httpGet.setHeader("X-Requested-With", "XMLHttpRequest");
            httpGet.setHeader("sec-ch-ua-mobile", "?0");
            httpGet.setHeader("Authorization", "");
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
            httpGet.setHeader("sec-ch-ua-platform", "macOS");
            httpGet.setHeader("Origin", "https://www.laohu8.com");
            httpGet.setHeader("Sec-Fetch-Site", "same-site");
            httpGet.setHeader("Sec-Fetch-Mode", "cors");
            httpGet.setHeader("Sec-Fetch-Dest", "empty");
            httpGet.setHeader("Referer", "https://www.laohu8.com/");
            httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
            httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                resp = EntityUtils.toString(response.getEntity());
                JsonNode jsonNode = Json.asJsonNode(resp).get("items").get(0);
                return jsonNode.get("latestPrice").asDouble() * jsonNode.get("shares").asDouble() / UNIT;
            } else {
                log.error("http request error, url = {}", url);
            }
        } catch (Exception e) {
            log.error("get marketCap failed, symbol: {}, resp: {}", symbol, resp, e);
        }
        return 0d;
    }
}
