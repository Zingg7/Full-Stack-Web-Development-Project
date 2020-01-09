package cn.tedu.store.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.tedu.store.entity.Address;
import cn.tedu.store.mapper.AddressMapper;
import cn.tedu.store.service.IAddressService;
import cn.tedu.store.service.ex.AddressCountLimitException;
import cn.tedu.store.service.ex.InsertException;

@Service
public class AddressServiceImpl implements IAddressService {

	@Autowired
	AddressMapper mapper;
	
	
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
		// TODO 补全省市区数据：补充省市区名称
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

}
