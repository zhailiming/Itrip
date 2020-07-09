package cn.itrip.itripauth.config;

import org.springframework.beans.factory.annotation.Value;

public class DataSource {

    @Value("spring.datasource.url")
    private String url;
    @Value("spring.datasource.username")
    private String username;
}
