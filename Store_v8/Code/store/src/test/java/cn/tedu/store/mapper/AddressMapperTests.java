package cn.tedu.store.mapper;


import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.tedu.store.entity.Address;
import cn.tedu.store.entity.District;

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
	
	@Test
	public void findByUid() {
		List<Address> list=mapper.findByUid(10);
		for(Address address:list) {
			System.err.println(address);
		}
	}
	
	@Test
	public void updateNonDefault() {
		Integer rows=mapper.updateNonDefault(10);
		System.err.println(rows);
	}
	
	@Test
	public void updateDefault() {
		Integer row=mapper.updateDefault(14, "张三丰", new Date());
		System.err.println(row);
	}
	
	@Test
	public void deteleByAid() {
		Integer row=mapper.deleteByAid(6);
		System.err.println(row);
	}
	
	@Test
	public void findLastModified() {
		Address address=mapper.findLastModified(10);
		System.err.println(address);
	}
	
	
	
	@Test
	public void findByAid() {
		Address addr=mapper.findByAid(14);
		System.err.println(addr);
	}
	
	
	
	
	
	
	

}
