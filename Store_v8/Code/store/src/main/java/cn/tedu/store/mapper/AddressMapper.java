package cn.tedu.store.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

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
	 * 将一条收货地址设为默认收货地址
	 * @param aid 收货地址id
	 * @param uesrname 最后修改人姓名
	 * @param modifiedTime 最后修改时间
	 * @return 受影响的行数
	 */
	Integer updateDefault(
			@Param("aid") Integer aid,
			@Param("username") String username,
			@Param("modifiedTime") Date modifiedTime);

	/**
	 * 将一个用户的所有收货地址设为非默认
	 * @param uid 用户id
	 * @return 受影响的行数
	 */
	Integer updateNonDefault(Integer uid);

	
	/**
	 * 根据aid删除一条收货地址记录
	 * @param aid 收货地址id
	 * @return 受影响的行数
	 */
	Integer deleteByAid(Integer aid);
	
	/**
	 * 查询一个用户的最后修改的收货地址
	 * @param uid 用户的id
	 * @return 最后修改的收货地址
	 */
	Address findLastModified(Integer uid);
	
	/**
	 * 根据aid查询一条收货地址数据
	 * @param aid 收货地址id
	 * @return 收货地址数据 或 null
	 */
	Address findByAid(Integer aid);
	
	/**
	 * 获取一个用户的收货地址数据
	 * @param uid 用户的id
	 * @return 用户的收货地址数据
	 */
	List<Address> findByUid(Integer uid);

}




