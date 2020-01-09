package cn.tedu.store.service;

import cn.tedu.store.entity.User;
import cn.tedu.store.service.ex.InsertException;
import cn.tedu.store.service.ex.PasswordNotMatchException;
import cn.tedu.store.service.ex.UpdateException;
import cn.tedu.store.service.ex.UserNotFoundException;
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

	
	/**
	 * 修改用户密码
	 * @param uid 用户id
	 * @param oldPassword 旧密码
	 * @param newPassword 新密码
	 * @param modifiedUser 最后修改用户
	 * @throws UserNotFoundException 用户数据不存在或标记为已删除
	 * @throws PasswordNotMatchException 用户输入的旧密码不正确
	 * @throws UpdateException 更新中发生的其他异常
	 */
	void changePassword (Integer uid,String oldPassword,
			String newPassword,String modifiedUser) throws 	
					UserNotFoundException, PasswordNotMatchException, UpdateException;
	
	/**
	 * 更新用户头像
	 * @param uid 用户id
	 * @param avatar 头像路径
	 * @param modifiedUser 最后修改用户
	 * @throws UserNotFoundException
	 * @throws UpdateException
	 */
	void changeAvatar (Integer uid,String avatar,
			String modifiedUser)throws UserNotFoundException, UpdateException;
	
	/**
	 * 更新用户信息
	 * @param uid 用户id
	 * @param username 用户名
	 * @param user 封装了用户信息的对象
	 * @throws UserNotFoundException
	 * @throws UpdateException
	 */
	void changeInfo(Integer uid, String username, 
			User user)throws UserNotFoundException, 
				UpdateException;
	
	
	/**
	 * 用户登录
	 * @param username 用户名
	 * @param password 密码
	 * @return 封装了用户信息的User对象 或 null
	 * @throws UserNotFoundException 用户名不存在 或 is_delete标记为1 
	 * @throws PasswordNotMatchException 密码错误
	 */
	User login(String username,String password) 
			throws UserNotFoundException, PasswordNotMatchException;
	
	/**
	 * 根据id获取用户信息
	 * @param uid 用户id
	 * @return 封装了用户数据的User对象
	 * @throws UserNotFoundException
	 */
	User getByUid(Integer uid) throws UserNotFoundException;
	
}



