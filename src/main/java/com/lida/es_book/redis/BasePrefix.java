package com.lida.es_book.redis;

/*
 *Created by LidaDu on 2018/1/8.  
 */
public abstract class BasePrefix implements KeyPrefix {

    private int expireSeconds;//int seconds =  prefix.expireSeconds(); 在set key的时候从expire中获取

    private String prefix;

    public BasePrefix(String prefix) {//0代表永不过期
        this(0, prefix);
    }

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {//默认0代表永不过期
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className+":" + prefix;
    }

}
