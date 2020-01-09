package cn.tedu.store.service;

import java.util.List;

import cn.tedu.store.entity.Address;
import cn.tedu.store.service.ex.AccessDeniedException;
import cn.tedu.store.service.ex.AddressCountLimitException;
import cn.tedu.store.service.ex.AddressNotFoundException;
import cn.tedu.store.service.ex.DeleteException;
import cn.tedu.store.service.ex.InsertException;
import cn.tedu.store.service.ex.UpdateException;

public interface IAddressService {
	
	int ADDRESS_MAX_COUNT=3;
	
	/**
	 * 新增收货地址
	 * @param uid 用户id
	 * @param username 创建者姓名
	 * @param address 收货地址数据
	 * @throws AddressCountLimitException
	 * @throws InsertException
	 */
	void createAddress(Integer uid,
			String username, Address address)
					throws AddressCountLimitException, InsertException;

	/**
	 * 将一条收货地址设为默认收货地址
	 * @param aid 收货地址id
	 * @param uid 用户id
	 * @param username 用户名
	 * @throws AddressNotFoundException
	 * @throws AccessDeniedException
	 * @throws UpdateException
	 */
	void setDefault(Integer aid,Integer uid,String username)
			throws AddressNotFoundException, 
			AccessDeniedException, UpdateException;
	
	/**
	 * 根据收货地址id删除一条收货地址数据
	 * @param aid 收货地址id
	 * @param uid 用户id
	 * @param username 最后修改人姓名
	 * @throws AddressNotFoundException
	 * @throws AccessDeniedException
	 * @throws DeleteException
	 * @throws UpdateException
	 */
	void removeAddress(Integer aid,
			Integer uid, String username) 
					throws AddressNotFoundException, 
					AccessDeniedException, 
					DeleteException,UpdateException;
	
	
	
	/**
	 * 查询一个用户收货地址数据
	 * @param uid 用户id
	 * @return 该用户收货地址数据
	 */
	List<Address> listByUid(Integer uid);
	
	
	
	
}
