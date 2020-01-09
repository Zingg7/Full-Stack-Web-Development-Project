package cn.tedu.store.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.tedu.store.entity.User;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTests {
	
	@Autowired
	IUserService service;
	
	
	@Test
	public void reg() {
		try {
			User user=new User();
			user.setUsername("Tom");
			user.setPassword("1234");
			service.reg(user);
		} catch (Exception e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	
	@Test
	public void login() {
		try {
			String username="root";
			String password="1234x";
			User user=service.login(username, password);
			System.err.println(user);
		} catch (Exception e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	
	@Test
	public void changePassword() {
		try {
			Integer uid=16;
			String oldPassword="1234";
			String newPassword="5678";
			String modifiedUser="管理员";
			service.changePassword(uid, oldPassword, newPassword, modifiedUser);
		}catch (Exception e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void changeAvatar() {
		try {
			Integer uid=18;
			String avatar="/ccc/3.png";
			String modifiedUser="管理员";
			service.changeAvatar(uid, avatar, modifiedUser);
		}catch (Exception e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void changeInfo() {
		try {
			User user=new User();
			user.setPhone("222");
			user.setEmail("555@qq.com");
			user.setGender(1);
			Integer uid=18;
			String modifiedUser="管理员123";
			service.changeInfo(uid, modifiedUser, user);
		}catch (Exception e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void findByUid() {
		try {
			User user=service.getByUid(12);
			System.err.println(user);
		}catch (Exception e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	

}





