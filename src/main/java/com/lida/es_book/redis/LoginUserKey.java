package com.lida.es_book.redis;


/*
 *Created by LidaDu on 2018/1/8.  
 */
public class LoginUserKey extends BasePrefix {

    public static final int EXPIRE_SESSION = 30*60;//30分钟

    public LoginUserKey(String prefix) {
        super(prefix);
    }

    public LoginUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static LoginUserKey session = new LoginUserKey(EXPIRE_SESSION,"lidaes-session:");
}
