package cn.tedu.store.mapper;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.tedu.store.entity.Cart;
import cn.tedu.store.entity.CartVO;
import cn.tedu.store.entity.District;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartMapperTests {
	
	@Autowired
	CartMapper mapper;
	
	@Test
	public void saveCart() {
		Cart cart=new Cart();
		cart.setUid(10);
		cart.setPid(10000001);
		cart.setPrice(1000L);
		cart.setNum(20);
		Integer row=mapper.saveCart(cart);
		System.err.println(row);
	}
	
	@Test
	public void updateNum() {
		Integer row=mapper.updateNum(1, 50, "令狐冲", new Date());
		System.err.println(row);
	}
	
	@Test
	public void getByUidAndPid() {
		Cart cart=mapper.getByUidAndPid(10, 10000001);
		System.err.println(cart);
	}
	
	@Test
	public void findCartList() {
		List<CartVO> list=mapper.findCartList(1);
		for(CartVO vo:list) {
			System.err.println(vo);
		}
	}
	
	@Test
	public void findByCid() {
		Cart cart=mapper.findByCid(2);
		System.err.println(cart);
	}
	

}
