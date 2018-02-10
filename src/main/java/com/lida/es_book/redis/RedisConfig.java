package com.lida.es_book.redis;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/*
 *Created by LidaDu on 2018/1/8.  
 */
@Configuration
@Data
public class RedisConfig {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.timeout}")
    private int timeout;//秒

    @Value("${redis.password}")
    private String password;

    @Value("${redis.poolMaxTotal}")
    private int poolMaxTotal;

    @Value("${redis.poolMaxIdle}")
    private int poolMaxIdle;

    @Value("${redis.poolMaxWait}")
    private int poolMaxWait;//秒

    @Value("${redis.database}")
    private int dataBase;

}
