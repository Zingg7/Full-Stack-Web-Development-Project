package cn.tedu.store.service;

import cn.tedu.store.entity.User;
import cn.tedu.store.service.ex.InsertException;
import cn.tedu.store.service.ex.UsernameDuplicateException;

/**
 * 业务层用户功能的父接口
 */
public interface IUserService {
	
	/**
	 * 用户注册
	 * @param user 用户数据
	 * @throws UsernameDuplicateException 用户名重复时抛出的异常
	 * @throws InsertException 插入操作时其他原因导致的异常
	 */
	void reg(User user) throws UsernameDuplicateException, InsertException;

}



