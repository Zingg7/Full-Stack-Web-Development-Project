package cn.tedu.store.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.tedu.store.service.ex.InsertException;
import cn.tedu.store.service.ex.ServiceException;
import cn.tedu.store.service.ex.UsernameDuplicateException;
import cn.tedu.store.util.JsonResult;

/**
 * 所用控制器类的父类
 */
public abstract class BaseController {
	
	protected static final Integer SUCCESS=20;
	// 静态常量的命名：所有字母都大写，单词用_隔开，尽量说明白，不要嫌长
	protected static final Integer ERROR_USERNAME_DUPLICATE=30;
	/**
	 * 对控制器中的异常进行统一处理
	 * @param e 异常对象
	 * @return JsonResult封装响应信息
	 */
	@ExceptionHandler(ServiceException.class)
	@ResponseBody
	public JsonResult<Void> handlerException(Throwable e){
		// 根据不同异常的类型提供不同的处理方式
		// 现在的处理方式是根据不同的类型，返回不同的状态码
		JsonResult<Void> jr=new JsonResult<>();
		
		if(e instanceof UsernameDuplicateException) {
			jr.setState(ERROR_USERNAME_DUPLICATE);
			jr.setMessage(e.getMessage());
		}else if(e instanceof InsertException) {
			jr.setState(40);
			jr.setMessage(e.getMessage());
		}
		
		return jr;
	}
	

}




