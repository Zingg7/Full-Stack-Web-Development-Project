package cn.tedu.store.mapper;

import java.util.List;

import cn.tedu.store.entity.Address;

/**
 * 收货地址对应的持久层接口
 */
public interface AddressMapper {
	
	/**
	 * 增加新的收货地址
	 * @param address 收货地址数据
	 * @return 受影响的行数
	 */
	Integer saveAddress(Address address);

	/**
	 * 根据uid查询用户收货地址条数
	 * @param uid 用户id
	 * @return 用户收货地址条数
	 */
	Integer countByUid(Integer uid);
	
	/**
	 * 获取一个用户的收货地址数据
	 * @param uid 用户的id
	 * @return 用户的收货地址数据
	 */
	List<Address> findByUid(Integer uid);

}




