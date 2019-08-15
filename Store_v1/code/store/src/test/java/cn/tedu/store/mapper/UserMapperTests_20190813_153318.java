package cn.tedu.store.mapper;

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
	public void findByUsername() {
		User user=mapper.findByUsername("root");
		System.err.println(user);
	}
	
	
	

}
