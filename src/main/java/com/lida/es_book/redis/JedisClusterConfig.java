package com.lida.es_book.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/*
 *Created by LidaDu on 2018/3/2.  
 */
@Configuration
public class JedisClusterConfig {
    /*@Bean
    public JedisCluster getJedisCluster() {

        String[] serverArray = new String[]{"192.168.60.14:6379","192.168.60.13:6379","192.168.60.12:6379","192.168.60.10:6379","192.168.60.11:6379","192.168.60.8:6379"};
        Set<HostAndPort> nodes = new HashSet<>();

        for (String ipPort : serverArray) {
            String[] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
        }

        return new JedisCluster(nodes, 2000);
    }*/
}
