package com.lida.es_book;

import com.lida.es_book.base.ESConstants;
import com.lida.es_book.entity.Book;
import com.lida.es_book.entity.Category;
import com.lida.es_book.entity.User;
import com.lida.es_book.repository.CategoryDao;
import com.lida.es_book.repository.UserDao;
import com.lida.es_book.util.encryption.Digests;
import com.lida.es_book.util.encryption.Encodes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsBookApplicationTests {

	@Resource
	private UserDao userDao;
	@Resource
	private CategoryDao categoryDao;

	@Test
	public void contextLoads() {
	}

	@Test
	public void addUser() {
		User user = new User();
		user.setTelephone("15027766011");
		user.setPassword("123456");
		user.setNickname("lida");
		user = entryptPassword(user);
		userDao.save(user);
	}

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

	@Test
	public void addBook() {
		List<Category> categories = (List<Category>) categoryDao.findAll();
		for (int i = 0; i < 100000; i++) {
			Book book = new Book();
		}
	}

}
