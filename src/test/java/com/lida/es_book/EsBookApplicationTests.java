package com.lida.es_book;

import com.lida.es_book.base.ESConstants;
import com.lida.es_book.entity.Book;
import com.lida.es_book.entity.Category;
import com.lida.es_book.entity.User;
import com.lida.es_book.esSearch.BookIndexMessage;
import com.lida.es_book.esSearch.SearchService;
import com.lida.es_book.redis.LoginUserKey;
import com.lida.es_book.redis.RedisService;
import com.lida.es_book.repository.BookDao;
import com.lida.es_book.repository.CategoryDao;
import com.lida.es_book.repository.UserDao;
import com.lida.es_book.util.encryption.Digests;
import com.lida.es_book.util.encryption.Encodes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsBookApplicationTests {

	@Resource
	private UserDao userDao;
	@Resource
	private CategoryDao categoryDao;
	@Resource
	private BookDao bookDao;
	@Resource
	private RedisService redisService;
	@Resource
	private SearchService searchService;

	@Test
	public void removeIndex() {
		BookIndexMessage message = new BookIndexMessage("1520778929371355996", BookIndexMessage.REMOVE, 0);
		searchService.removeIndex(message);
	}

	@Test
	public void addUser() {
		/*User user = new User();
		user.setTelephone("15027766011");
		user.setPassword("123456");
		user.setNickname("lida");
		user = entryptPassword(user);
		userDao.save(user);*/
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

		/*for (int i = 0; i <=100000; i++) {
			Book b = getBook();
			bookDao.save(b);
		}*/

	}

	public Book getBook() {
		Random r = new Random();
		List<Category> categories = (List<Category>) categoryDao.findAll();
		Book b = new Book();
		Category c = categories.get(r.nextInt(categories.size() - 1));
		b.setCategoryName(c.getName());
		b.setCategoryId(String.valueOf(c.getId()));
		b.setAuthor(getName());
		b.setName(getbookName());
		b.setPublishingTime(getRondomDate());
		b.setPrice(getPrice());
		return b;
	}

	public BigDecimal getPrice() {
		Random r = new Random();
		NumberFormat formatter = new DecimalFormat("#.##");
		return new BigDecimal(formatter.format(r.nextDouble() + r.nextDouble()*100));
	}

	public String getbookName() {
		Random r = new Random();
		String books = "赤兔马,的卢,爪黄飞电,绝影,白龙,大宛马,果下马,白马,凉州马,乌丸马,匈奴马,斩蛇剑,七星宝刀,干将剑,倚天剑,莫邪剑,青缸剑,雌雄一对,宝剑,古锭刀,锯齿刀,宝刀,神刀,百辟刀,吴钩,环首刀,青龙偃月,三尖刀,凤嘴刀,眉尖刀,大杆刀,大刀,丈八蛇矛,铁脊蛇矛,金马槊,三丈矛,枣木槊,方天画戟,双铁戟,蚩尤斧,大斧,铁鞭,地瓜锤,铜锤,梅花袖箭,袖箭,短戟,手戟,养由基弓,李广弓,貊弓,东胡飞弓,檀弓,箭,孙子兵法书,六韬,三略,司马法,吴子,孙膑兵法,尉缭子,孟德新书,墨子,魏公子兵法,帛,左伯纸,蔡候纸,纸,竹简,韩非子,管子,商君书,晏子春秋,周书阴符,四月民令,盐铁论,春秋左氏传,战国策,吴越春秋,史记,汉书,淮南子,吕氏春秋,列女传,老子,易经,庄子,论语,诗经,书经,礼记,孝经传,青囊书,太平清领道,伤寒杂病论,毛毡,玉璧,大克鼎,毛公鼎,大盂鼎,加彩博山炉,父己角,吕氏镜,长生镜,四灵文镜,智君子鉴,茶,貂裘,龙方壶,金象嵌壶,铜马车,铜马,鹿形饰板,马面饰品,桂枝,吴茱萸,蜀椒,芍药,酥,羽扇,神兽砚,长信宫灯,牛灯,青釉谷仓,算盘,黄帝四经,狐裘,羊裘,饕餮纹鼎,祖乙尊,服方尊,羽人兽文镜,龙子镜,曾带纹鉴,瑟,筑,当归,远志,涂漆鼎,青铜博山炉,羊尊,酒杯,编钟,陶器,麻沸散,漆叶青黏散,亭历犬血散,五石散,武侯行军散,屠苏延命散,仪狄酒,杜康酒,酪酒,沈齐,泛齐,醴齐,果酒,事酒,清酒,昔酒,保宁压酒,醴酒,人参酒,葡萄酒,造清,恬酒,药酒,黄酒,老酒,醪酒";
		String[] boos2 = books.split(",");
		String name = boos2[r.nextInt(boos2.length - 1)];
		return name;
	}

	public String getName() {
		Random random = new Random();
		String[] Surname = {"赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许",
				"何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章", "云", "苏", "潘", "葛", "奚", "范", "彭", "郎",
				"鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳", "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺", "倪", "汤", "滕", "殷",
				"罗", "毕", "郝", "邬", "安", "常", "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "平", "黄", "和",
				"穆", "萧", "尹", "姚", "邵", "湛", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明", "臧", "计", "伏", "成", "戴", "谈", "宋", "茅", "庞", "熊", "纪", "舒",
				"屈", "项", "祝", "董", "梁", "杜", "阮", "蓝", "闵", "席", "季"};
		String girl = "秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽 ";
		String boy = "伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达安岩中茂进林有坚和彪博诚先敬震振壮会思群豪心邦承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽晨辰士以建家致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘";
		int index = random.nextInt(Surname.length - 1);
		String name = Surname[index]; //获得一个随机的姓氏
		int i = random.nextInt(3);//可以根据这个数设置产生的男女比例
		if(i==2){
			int j = random.nextInt(girl.length()-2);
			if (j % 2 == 0) {
				//name = "女-" + name + girl.substring(j, j + 2);
				name =  name + girl.substring(j, j + 2);
			} else {
				//name = "女-" + name + girl.substring(j, j + 1);
				name =  name + girl.substring(j, j + 1);
			}

		}
		else{
			int j = random.nextInt(girl.length()-2);
			if (j % 2 == 0) {
				//name = "男-" + name + boy.substring(j, j + 2);
				name =  name + boy.substring(j, j + 2);
			} else {
				//name = "男-" + name + boy.substring(j, j + 1);
				name = name + boy.substring(j, j + 1);
			}

		}

		return name;
	}

	public Date getRondomDate() {
		Date date = randomDate("1866-07-01","2018-2-01");
		return date;
	}


	/**
	 * 获取随机日期
	 * @param beginDate 起始日期，格式为：yyyy-MM-dd
	 * @param endDate 结束日期，格式为：yyyy-MM-dd
	 * @return
	 */
	private Date randomDate(String beginDate, String endDate){
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date start = format.parse(beginDate);
			Date end = format.parse(endDate);

			if(start.getTime() >= end.getTime()){
				return null;
			}

			long date = random(start.getTime(),end.getTime());

			return new Date(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private  long random(long begin,long end){
		long rtn = begin + (long)(Math.random() * (end - begin));
		if(rtn == begin || rtn == end){
			return random(begin,end);
		}
		return rtn;
	}

	@Test
	public void testRedis() {
		/*List<Book> bookList = (List<Book>) bookDao.findAll();
		for (Book b : bookList) {
			redisService.set(LoginUserKey.session, b.getId(), b );
		}*/
	}

}
