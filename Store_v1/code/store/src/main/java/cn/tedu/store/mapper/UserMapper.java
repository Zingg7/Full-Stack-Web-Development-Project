package cn.tedu.store.mapper;

import cn.tedu.store.entity.User;

/**
 * 用户功能的持久层接口
 */
public interface UserMapper {
	
	/**
	 * 用户注册的方法
	 * @param user 用户数据
	 * @return 受影响的行数
	 */
	Integer addnew(User user);

	/**
	 * 根据用户名查询用户
	 * @param username 用户名
	 * @return 封装了用户数据的实体类 或 没查到返回null
	 */
	User findByUsername(String username);

}






