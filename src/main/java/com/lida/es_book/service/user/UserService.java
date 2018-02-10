package com.lida.es_book.service.user;

import com.lida.es_book.base.ApiResponse;
import com.lida.es_book.base.ESConstants;
import com.lida.es_book.entity.User;
import com.lida.es_book.redis.LoginUserKey;
import com.lida.es_book.redis.RedisService;
import com.lida.es_book.repository.UserDao;
import com.lida.es_book.service.ServiceResult;
import com.lida.es_book.util.CookieUtil;
import com.lida.es_book.util.TokenUtil;
import com.lida.es_book.util.encryption.Digests;
import com.lida.es_book.util.encryption.Encodes;
import com.lida.es_book.web.dto.LoginUser;
import com.lida.es_book.web.dto.UserDto;
import com.lida.es_book.web.form.LoginForm;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/*
 *Created by LidaDu on 2018/2/6.  
 */
@Service
public class UserService {

    @Autowired
    private ModelMapper modelMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private UserDao userDao;


    public User findById(String id) {
        return userDao.findOne(id);
    }

    public User findByTelephone(String telephone) {
        return userDao.findByTelephone(telephone);
    }

    public ApiResponse login(LoginForm loginForm, HttpServletResponse response) {
        if (loginForm == null) {
            return ApiResponse.ofMessage(ApiResponse.Status.BAD_REQUEST.getCode(),"登录用户参数错误");
        }
        User user = findByTelephone(loginForm.getUsername());
        if (user == null) {
            return ApiResponse.ofMessage(ApiResponse.Status.USER_NOT_EXIST.getCode(), ApiResponse.Status.USER_NOT_EXIST.getStandardMessage());
        }
        if (!passwordRight(user, loginForm.getPassword())) {
            return ApiResponse.ofMessage(ApiResponse.Status.PASSWORD_ERROR.getCode(), ApiResponse.Status.PASSWORD_ERROR.getStandardMessage());
        }
        String token = TokenUtil.getToken();
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(user, loginUser);
        loginUser.setOnLineTime(new Date());
        addCookie(response, token, loginUser);
        return ApiResponse.ofSuccess(null);
    }

    private void addCookie(HttpServletResponse response, String token, LoginUser loginUser) {
        loginUser.setActiveTime(new Date());
        redisService.set(LoginUserKey.session, token, loginUser);
        Cookie cookie = new Cookie(ESConstants.COOKIE_NAME_TOKEN, token);
        //这里不设置有效期，关闭浏览器时就找不到该cookie则需要重新登录     Expires/Max-Age   Session
        //设置有效期的话会再本地缓存声称临时文件，关闭浏览器后，再输入关闭浏览器之前的地址就可登录，这样是否不安全？
        //cookie.setMaxAge(LoginUserKey.session.expireSeconds());//cookie有效期设置与session一致
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public LoginUser getByToken(HttpServletResponse response, String token) {
        if(StringUtils.isEmpty(token)) {
            return null;
        }
        LoginUser user = redisService.get(LoginUserKey.session, token, LoginUser.class);
        //延长有效期
        if(user != null) {
            addCookie(response, token, user);
        }
        return user;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = CookieUtil.get(request, ESConstants.COOKIE_NAME_TOKEN);
        if (cookie != null) {
            redisService.delete(LoginUserKey.session, cookie.getValue());
            CookieUtil.set(response,
                    ESConstants.COOKIE_NAME_TOKEN,
                    cookie.getValue(),
                    0);
        }

    }

    /**
     * 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
     * 注册时调用
     */
    public User entryptPassword(User user) {
        //生成随机的Byte[]作为salt        SALT_SIZE:byte[]的大小(八位)       即：得到8位盐.
        byte[] salt = Digests.generateSalt(ESConstants.SALT_SIZE);
        //将得到的盐转换为十六进制字符串  Hex编码.
        user.setSalt(Encodes.encodeHex(salt));
        //因为数据库存的是十六进制编码后的salt 所以验证时getSalt()后要将salt进行十六进制解码得到byte[] salt(见LoginController方法2userVerify(request,userName))
        //对输入字符串进行sha1散列.
        /**
         * 对密码加盐进行1024次SHA1加密
         */
        byte[] hashPassword = Digests.sha1(user.getPassword().getBytes(), salt, ESConstants.HASH_ITERATION);
        //将得到的散列转换为十六进制字符串
        user.setPassword(Encodes.encodeHex(hashPassword));
        return user;
    }


    private boolean passwordRight(User user, String plainPass) {
        String saltStr = user.getSalt();
        byte[] salt = Encodes.decodeHex(saltStr);
        byte[] hashPassword = Digests.sha1(plainPass.getBytes(), salt, ESConstants.HASH_ITERATION);
        String plainPassEncrypted = Encodes.encodeHex(hashPassword);
        return user.getPassword().equals(plainPassEncrypted);
    }
}
