package com.sauron.eye;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = "com.sauron.eye", exclude = {DataSourceAutoConfiguration.class})
public class ServiceTestConfiguration {
    
}
