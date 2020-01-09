package cn.tedu.store;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StoreApplicationTests {
	
	@Autowired
	DataSource ds;

	@Test
	public void contextLoads() {
	}
	
	@Test
	public void getConnection() throws SQLException {
		Connection conn=ds.getConnection();
		System.err.println(conn);
	}
	
	
	@Test
	public void testMD5() {
		/*String password="1234";
		// 81dc9bdb52d04dc20036dbd8313ed055
		// 消息固定生成128位定长2进制数据
		// 一般情况下会转成对应的16进制表示，32
		String md5Str=DigestUtils.md5DigestAsHex(password.getBytes());
		for(int i=1;i<=20;i++) {
			md5Str=DigestUtils.md5DigestAsHex(md5Str.getBytes());
		}
		
		System.err.println(md5Str);*/
		
		String password="1234";
		// String salt=System.currentTimeMillis()+"";
		String salt=UUID.randomUUID().toString();
		System.err.println("uuid="+salt);
		String msg=salt+password+salt;
		String md5=DigestUtils.md5DigestAsHex(msg.getBytes());
		System.err.println(md5);
		
/*		try {
			MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}*/
		
	}
	
	

}
