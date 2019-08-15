package cn.tedu.store.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.tedu.store.entity.User;
import cn.tedu.store.mapper.UserMapper;
import cn.tedu.store.service.IUserService;
import cn.tedu.store.service.ex.InsertException;
import cn.tedu.store.service.ex.UsernameDuplicateException;

/**
 * 业务层用户功能接口的实现类
 */
@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	UserMapper mapper;
	
	@Override
	public void reg(User user) throws UsernameDuplicateException, InsertException {
		// 基于user的getUsername()获取用户名
		String username=user.getUsername();
		// 调用持久层方法findByUsername，看User是否不为null
		User result=mapper.findByUsername(username);
		// 不为null：抛出UsernameDuplicateException
		if(result!=null) {
			throw new UsernameDuplicateException("用户注册异常！用户名已存在");
		}
		
		// 向user中补全数据
		// TODO 补全盐值
		// TODO 获取用户输入的密码
		// TODO 基于盐值对密码进行加密
		// TODO 将加密后的密码添加到user中
		// 补全is_delete为0
		user.setIsDelete(0);
		
		Date now=new Date();
		// 补全4项日志数据
		user.setCreatedUser(username);
		user.setCreatedTime(now);
		user.setModifiedUser(username);
		user.setModifiedTime(now);

		// 调用mapper的addnew()，将用户数据添加到数据库
		Integer row=mapper.addnew(user);
		// 查看返回的行数是否不为1
		if(!row.equals(1)) {
			// 是： 抛出InsertException		// TODO Auto-generated method stub
			throw new InsertException("用户注册异常！请联系管理员");
		}

	}

}




