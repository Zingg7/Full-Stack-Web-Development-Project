package cn.tedu.store.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.tedu.store.entity.Address;
import cn.tedu.store.entity.District;
import cn.tedu.store.entity.Product;
import cn.tedu.store.entity.User;
import cn.tedu.store.service.ex.ServiceException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceTests {
	
	@Autowired
	IProductService service;
	
	@Test
	public void getHotList() {
		List<Product> list=service.getHotList();
		for(Product p:list) {
			System.err.println(p);
		}
	}
	
	@Test
	public void getById() {
		try {
			Product product=service.getById(100001);
			System.err.println(product);
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}

}





