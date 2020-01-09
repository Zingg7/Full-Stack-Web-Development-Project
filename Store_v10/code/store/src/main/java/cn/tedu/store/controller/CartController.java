package cn.tedu.store.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tedu.store.entity.CartVO;
import cn.tedu.store.service.ICartService;
import cn.tedu.store.util.JsonResult;

@RestController
@RequestMapping("carts")
public class CartController extends BaseController {

	@Autowired
	ICartService service;
	
	@RequestMapping("create_cart")
	public JsonResult<Void> createCart(Integer num, Integer pid, 
			HttpSession session){
		Integer uid=getUidFromSession(session);
		String username=getUsernameFromSession(session);
		
		service.createCart(num, uid, pid, username);
		return new JsonResult<Void>(SUCCESS);
	}
	
	@RequestMapping("add_num")
	public JsonResult<Void> addNum(Integer cid,Integer num,HttpSession session){
		Integer uid=getUidFromSession(session);
		String username=getUsernameFromSession(session);
		
		service.addNum(cid, num, uid, username);
		return new JsonResult<Void>(SUCCESS);
	}
	
	
	@GetMapping("/")
	public JsonResult<List<CartVO>> getCartList(HttpSession session){
		Integer uid=getUidFromSession(session);
		
		List<CartVO> list=service.getCartList(uid);
		
		return new JsonResult<List<CartVO>>(SUCCESS, list);
	}
	
	
}





