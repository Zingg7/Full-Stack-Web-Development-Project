package cn.tedu.store.service;

import cn.tedu.store.entity.Address;
import cn.tedu.store.service.ex.AddressCountLimitException;
import cn.tedu.store.service.ex.InsertException;

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

}
