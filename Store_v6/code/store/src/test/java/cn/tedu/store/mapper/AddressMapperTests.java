package cn.tedu.store.mapper;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.tedu.store.entity.Address;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddressMapperTests {
	
	@Autowired
	AddressMapper mapper;
	
	@Test
	public void saveAddress() {
		Address add=new Address();
		add.setUid(7);
		add.setName("小明同学2");;
		Integer row=mapper.saveAddress(add);
		System.err.println("row="+row);
	}
	
	@Test
	public void countByUid() {
		Integer count=mapper.countByUid(9);
		System.err.println("count="+count);
	}
	
	
	
	
	
	
	

}
