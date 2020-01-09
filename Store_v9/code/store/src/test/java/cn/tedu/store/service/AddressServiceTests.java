package cn.tedu.store.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.tedu.store.entity.Address;
import cn.tedu.store.entity.User;
import cn.tedu.store.service.ex.ServiceException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddressServiceTests {
	
	@Autowired
	IAddressService service;
	
	@Test
	public void createAddress() {
		try {
			Address add=new Address();
			add.setName("小张同学");
			add.setAddress("程序员之家");
			service.createAddress(6, "管理员", add);
		}catch(ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void setDefault() {
		try {
			service.setDefault(7, 10, "张无忌");
		}catch(ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void removeAddress() {
		try {
			service.removeAddress(15, 10, "张翠山");
		}catch(ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	
	
	
	@Test
	public void listByUid() {
		List<Address> list=service.listByUid(10);
		for(Address addr:list) {
			System.err.println(addr);
		}
	}

}





