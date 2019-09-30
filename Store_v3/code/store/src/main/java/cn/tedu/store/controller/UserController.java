package cn.tedu.store.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tedu.store.entity.User;
import cn.tedu.store.service.IUserService;
import cn.tedu.store.util.JsonResult;

@RestController
@RequestMapping("users")
public class UserController extends BaseController {

	@Autowired
	private IUserService service;
	
	@PostMapping("reg")
	public JsonResult<Void> reg(User user){
		service.reg(user);
		return new JsonResult<>(SUCCESS);
	}
	
	@RequestMapping("change_password")
	public JsonResult<Void> changePassword(@RequestParam("old_password") String oldPassword, @RequestParam("new_password") String newPassword,HttpSession session){
		
		// 从session中获取uid
		Integer uid=Integer.valueOf(session.getAttribute(SESSION_UID).toString());
		
		// 从session中获取username
		String username=session.getAttribute(SESSION_USERNAME).toString();

		service.changePassword(uid,oldPassword,newPassword,username);
		return new JsonResult<Void>(SUCCESS);
	}
	
	
	
	
	@PostMapping("login")
	public JsonResult<User> login(
		String username,String password, HttpSession session){

		// 调用service的login()进行登录
		User user=service.login(username,password);
		
		// 向session中添加uid
		session.setAttribute(SESSION_UID,user.getUid());

		// 向session中添加username
		session.setAttribute(SESSION_USERNAME,user.getUsername());

		return new JsonResult<User>(SUCCESS, user);
	}
	
	
	
	
	
}
