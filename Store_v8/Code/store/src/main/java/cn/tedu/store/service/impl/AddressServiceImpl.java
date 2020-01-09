package cn.tedu.store.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tedu.store.entity.Address;
import cn.tedu.store.entity.District;
import cn.tedu.store.mapper.AddressMapper;
import cn.tedu.store.mapper.DistrictMapper;
import cn.tedu.store.service.IAddressService;
import cn.tedu.store.service.IDistrictService;
import cn.tedu.store.service.ex.AccessDeniedException;
import cn.tedu.store.service.ex.AddressCountLimitException;
import cn.tedu.store.service.ex.AddressNotFoundException;
import cn.tedu.store.service.ex.DeleteException;
import cn.tedu.store.service.ex.InsertException;
import cn.tedu.store.service.ex.UpdateException;

@Service
public class AddressServiceImpl implements IAddressService {

	@Autowired
	AddressMapper mapper;
	
	@Autowired
	IDistrictService districtService;
	
	
	@Override
	public void createAddress(Integer uid, String username, Address address)
			throws AddressCountLimitException, InsertException {
		// 根据uid查询收货地址条数
		Integer count=countByUid(uid);
		// 条数是否达到上限 3
		if(count >= ADDRESS_MAX_COUNT) {
			// 是：AddressCountLimitException
			throw new AddressCountLimitException("新增收货地址异常！最大收货地址条数为"+ADDRESS_MAX_COUNT);
		}

		// 补全uid
		address.setUid(uid);
		// 补全isDefault，根据上面查询到的收货地址条数进行判断
		int isDefault=count==0 ? 1 : 0;
		address.setIsDefault(isDefault);
		// 补全省市区数据：补充省市区名称
		String provinceName=getNameByCode(address.getProvinceCode().toString());
		String cityName=getNameByCode(address.getCityCode().toString());
		String areaName=getNameByCode(address.getAreaCode().toString());
		address.setProvinceName(provinceName);
		address.setCityName(cityName);
		address.setAreaName(areaName);
		
		// 创建当前时间对象
		Date now =new Date();
		// 补全4项日志数据
		address.setCreatedUser(username);
		address.setCreatedTime(now);
		address.setModifiedUser(username);
		address.setModifiedTime(now);
		// 执行添加操作
		saveAddress(address);
	}
	
	@Override
	@Transactional
	public void setDefault(Integer aid, Integer uid, String username)
			throws AddressNotFoundException, AccessDeniedException, UpdateException {
		// 使用aid查地址数据
		Address address=findByAid(aid);
		// 判断结果是否为null
		if(address==null) {
			// 是：AddressNotFoundException
			throw new AddressNotFoundException("设置默认收货地址异常！地址数据不存在");
		}

		// 查询结果中的uid和方法参数的uid是否不一致
		if(!address.getUid().equals(uid)) {
			// 是：AccessDeniedException
			throw new AccessDeniedException("设置默认收货地址异常！访问权限不足");
		}

		// 将该用户的所有收货地址设为非默认
		updateNonDefault(uid);
		// 将该用户指定的收货地址设为默认
		updateDefault(aid, username, new Date());
	}
	
	@Override
	@Transactional
	public void removeAddress(Integer aid, Integer uid, String username)
			throws AddressNotFoundException, AccessDeniedException, DeleteException, UpdateException {
		// 使用aid查地址数据
		Address result=findByAid(aid);
		// 判断结果是否为null
		if(result==null) {
			// 是：AddressNotFoundException
			throw new AddressNotFoundException("删除收货地址异常！地址数据不存在");
		}

		// 查询结果中的uid和方法参数的uid是否不一致
		if(!result.getUid().equals(uid)) {
			// 是：AccessDeniedException
			throw new AccessDeniedException("删除收货地址异常！访问权限不足");
		}

		// 删除aid对应的地址数据
		deleteByAid(aid);
		
		// 判断刚才的查询结果中的isDefault是否不为1
		if(result.getIsDefault()!=1) {
			return;
		}

		// 查看当前用户剩余的收货地址条数
		Integer count = countByUid(uid);
		// 判断条数是否为0
		if(count==0) {
			return;
		}

		// 查询该用户的最后修改的收货地址
		Address lastModifiedAddress=findLastModified(uid);
		// 将该条记录设为该用户的默认收货地址
		updateDefault(lastModifiedAddress.getAid(), username, new Date());
	}

	public List<Address> listByUid(Integer uid){
		return findByUid(uid);
	}
	
	private Integer countByUid(Integer uid){
		// 对参数的合理性进行判断	
		if(uid==null || uid<1){
			throw new IllegalArgumentException();
		}
		return mapper.countByUid(uid);
	}

	private void saveAddress(Address address){
		Integer row=mapper.saveAddress(address);
		if(row!=1){
			throw new InsertException("添加收货地址异常！请联系管理员");
		}
	}
	
	private String getNameByCode(String code){
		District dist=districtService.getByCode(code);
		return dist==null?"":dist.getName();
	}
	
	private List<Address> findByUid(Integer uid){
		List<Address> list=mapper.findByUid(uid);
		for(Address addr:list){
			addr.setZip(null);
			addr.setTel(null);
			// 将4项日志数据设为null
			addr.setCreatedUser(null);
			addr.setCreatedTime(null);
			addr.setModifiedUser(null);
			addr.setModifiedTime(null);
		}
		return list;
	}

	
	/**
	 * 将一条收货地址设为默认收货地址
	 * @param aid 收货地址id
	 * @param uesrname 最后修改人姓名
	 * @param modifiedTime 最后修改时间
	 * @return 受影响的行数
	 */
	private void updateDefault(Integer aid,
			String username, Date modifiedTime) throws UpdateException {
		Integer row=mapper.updateDefault(aid, username, modifiedTime);
		if(row!=1) {
			throw new UpdateException("设置默认收货地址异常！请联系管理员");
		}
		
	}

	/**
	 * 将一个用户的所有收货地址设为非默认
	 * @param uid 用户id
	 * @return 受影响的行数
	 */
	private void updateNonDefault(Integer uid) throws UpdateException{
		Integer rows=mapper.updateNonDefault(uid);
		if(rows<1) {
			throw new UpdateException("设置默认收货地址异常！请联系管理员");
		}
	}

	/**
	 * 根据aid查询一条收货地址数据
	 * @param aid 收货地址id
	 * @return 收货地址数据 或 null
	 */
	private Address findByAid(Integer aid) {
		return mapper.findByAid(aid);
	}
	
	/**
	 * 根据aid删除一条收货地址记录
	 * @param aid 收货地址id
	 * @return 受影响的行数
	 */
	private void deleteByAid(Integer aid) throws DeleteException {
		Integer row=mapper.deleteByAid(aid);
		if(row!=1) {
			throw new DeleteException("删除收货地址异常！请联系管理员");
		}
	};
	
	/**
	 * 查询一个用户的最后修改的收货地址
	 * @param uid 用户的id
	 * @return 最后修改的收货地址
	 */
	private Address findLastModified(Integer uid) {
		return mapper.findLastModified(uid);
	};



}
