package cn.tedu.store.service.impl;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.tedu.store.entity.User;
import cn.tedu.store.mapper.UserMapper;
import cn.tedu.store.service.IUserService;
import cn.tedu.store.service.ex.InsertException;
import cn.tedu.store.service.ex.PasswordNotMatchException;
import cn.tedu.store.service.ex.UpdateException;
import cn.tedu.store.service.ex.UserNotFoundException;
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
		// 补全盐值
		String salt=UUID.randomUUID().toString();
		user.setSalt(salt);
		// 获取用户输入的密码
		String password=user.getPassword();
		// 基于盐值对密码进行加密
		String md5Password=getMd5Password(password, salt);
		// 将加密后的密码添加到user中
		user.setPassword(md5Password);
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
			// 是： 抛出InsertException
			throw new InsertException("用户注册异常！请联系管理员");
		}

	}
	
	/**
	 * 对密码进行加密
	 * @param password 原始密码
	 * @param salt 盐值
	 * @return 加密后的密码
	 */
	private String getMd5Password(String password,String salt) {
		// salt+password+salt 进行3次加密
		String msg=salt+password+salt;
		for(int i=0;i<3;i++) {
			msg=DigestUtils.md5DigestAsHex(msg.getBytes());
		}
		return msg;
	}

	@Override
	public User login(String username, String password) throws UserNotFoundException, PasswordNotMatchException {
		// 调用持久层的findByUsername(username) -> User
		User user=mapper.findByUsername(username);
		// 判断User是否为null
		if(user==null) {
			// 是：抛出 UserNotFoundException
			throw new UserNotFoundException("登录异常！用户名不存在");
		}

		// 获取user中的isDelete
		Integer isDelete=user.getIsDelete();
		// 判断isDelete是否为1
		if(isDelete.equals(1)) {
			// 是：抛出 UserNotFoundException
			throw new UserNotFoundException("登录异常！用户名不存在");
		}

		// 获取查询到的盐值
		String salt=user.getSalt();
		// 对用户传入的密码进行加密
		String md5Password=getMd5Password(password, salt);
		// 判断两个密码是否不一致
		if(!md5Password.equals(user.getPassword())) {
			// 是：抛出 PasswordNotMatchException
			throw new PasswordNotMatchException("登录异常！密码不正确");
		}
	
		// 将盐值设为null
		user.setSalt(null);
		// 将密码设为null
		user.setPassword(null);
		// 将isDelete设为null
		user.setIsDelete(null);
		// 返回user 
		return user;
	}

	@Override
	public void changePassword(Integer uid, String oldPassword, String newPassword, String modifiedUser)
			throws UserNotFoundException, PasswordNotMatchException, UpdateException {
		// 使用uid查询用户数据
		User user=mapper.findByUid(uid);
		// 判断返回结果是否为null
		if(user ==null) {
			// 是：UserNotFoundException
			throw new UserNotFoundException("修改密码异常！用户数据不存在");
		}

		// 判断isDelete是否为1
		if(user.getIsDelete().equals(1)) {
			// 是：UserNotFoundException
			throw new UserNotFoundException("修改密码异常！用户数据不存在");	
		}

		// 获取查询到的盐值
		String salt=user.getSalt();
		// 对旧密码进行加密
		String oldMd5Password=getMd5Password(oldPassword, salt);
		// 比较加密结果和查询到的用户密码是否不一致
		if(!oldMd5Password.equals(user.getPassword())) {
			// 是：PasswordNotMatchException
			throw new PasswordNotMatchException("修改密码异常！原始密码错误");
		}

		// 对新密码进行加密
		String newMd5Password=getMd5Password(newPassword, salt);
		// 调用updatePassword()更新密码
		Integer row=mapper.updatePassword(uid, newMd5Password, modifiedUser, new Date());
		// 判断受影响的行数是否不为1
		if(!row.equals(1)) {
			// 是：UpdateException
			throw new UpdateException("修改密码异常！请联系管理员");
		}
		
	}
	
	

}




