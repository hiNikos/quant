package com.sauron.eye.util;

import com.tigerbrokers.stock.openapi.client.util.StreamUtil;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;

@Slf4j
public class SignatureUtils {

    private static final String private_key_path =  ConfigFactory.load().getString("key_path");

    public static String getPrivateKey() {
        try {
            return StreamUtil.readText(new BufferedReader(new FileReader(private_key_path)))
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("\n", "").trim();
        } catch (Exception e) {
            log.error("parse private key error, path={}", private_key_path, e);
        }
        return null;
    }
}
