package cn.tedu.store.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.tedu.store.entity.District;
import cn.tedu.store.entity.Product;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductMapperTests {
	
	@Autowired
	ProductMapper mapper;

	@Test
	public void findHotList() {
		List<Product> list=mapper.findHotList();
		for(Product p:list) {
			System.err.println(p);
		}
	}
	
	@Test
	public void findById() {
		System.err.println(mapper.findById(10000001));
	}
	

}





