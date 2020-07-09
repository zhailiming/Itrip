package cn.itrip.itripauth.config;

import cn.itrip.beans.vo.order.PreAddOrderVO;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * redis.host=127.0.0.1
 * redis.port=6379
 * redis.pass=
 * redis.default.db=0
 * redis.timeout=3000
 * redis.maxActive=300
 * redis.maxIdle=100
 * redis.maxWait=1000
 */
@Component
@ConfigurationProperties(prefix = "redis")
@PropertySource("database.properties")
public class JedispoolProperty {
    private String host;
    private int port;
    private String pass;
    private int timeout;
    private int defaultdb;
    private int maxActive;
    private int maxIdle;
    private int maxWait;



    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getDefaultdb() {
        return defaultdb;
    }

    public void setDefaultdb(int defaultdb) {
        this.defaultdb = defaultdb;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }
}
