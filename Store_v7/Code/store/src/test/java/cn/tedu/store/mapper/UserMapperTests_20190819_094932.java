package cn.tedu.store.mapper;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.tedu.store.entity.User;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTests {
	
	@Autowired
	UserMapper mapper;
	
	@Test
	public void addnew() {
		User user=new User();
		user.setUsername("admin");
		user.setPassword("1234");
		System.err.println("before uid="+user.getUid());
		Integer row=mapper.addnew(user);
		System.err.println("row="+row);
		System.err.println("after uid="+user.getUid());
		
	}
	
	@Test
	public void updatePassword() {
		Integer uid=9;
		String password="6789";
		String modifiedUser="炒鸡管理员";
		Date modifiedTime=new Date();
		Integer row=mapper.updatePassword(
				uid, password, modifiedUser, modifiedTime);
		System.err.println("row="+row);
	}
	
	@Test
	public void updateAvatar() {
		Integer uid=12;
		String avatar="/abc/1.jpg";
		String modifiedUser="管理员";
		Date modifiedTime=new Date();
		Integer row=mapper.updateAvatar(uid, avatar, modifiedUser, modifiedTime);
		System.err.println("row="+row);
	}
	
	@Test
	public void updateInfo() {
		User user=new User();
		user.setUid(12);
		user.setPhone("12345678");
		user.setEmail("1234@qq.com");
		user.setGender(1);
		user.setModifiedUser("管理员");
		user.setModifiedTime(new Date());
		Integer row=mapper.updateInfo(user);
		System.err.println("row="+row);
	}
	
	
	@Test
	public void findByUsername() {
		User user=mapper.findByUsername("root");
		System.err.println(user);
	}
	
	@Test
	public void findByUid() {
		User user=mapper.findByUid(12);
		System.err.println(user);
	}
	
	
	

}
