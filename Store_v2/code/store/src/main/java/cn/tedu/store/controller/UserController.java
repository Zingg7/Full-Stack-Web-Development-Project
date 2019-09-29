package cn.tedu.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tedu.store.entity.User;
import cn.tedu.store.service.IUserService;
import cn.tedu.store.util.JsonResult;

@RestController
@RequestMapping("users")
public class UserController extends BaseController {

	@Autowired
	private IUserService service;
	
	@RequestMapping("reg")
	public JsonResult<Void> reg(User user){
		service.reg(user);
		return new JsonResult<>(SUCCESS);
	}
	
}
