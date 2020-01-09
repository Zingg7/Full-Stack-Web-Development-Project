package cn.tedu.store.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.tedu.store.entity.District;
import cn.tedu.store.mapper.DistrictMapper;
import cn.tedu.store.service.IDistrictService;

@Service
public class DistrictServiceImpl implements IDistrictService {

	@Autowired
	DistrictMapper mapper;
	
	public List<District> listByParent(String parent){
		return findByParent(parent);
	}

	private List<District> findByParent(String parent){
		return mapper.findByParent(parent);
	}
	
	public District getByCode(String code){
		return findByCode(code);
	}

	private District findByCode(String code){
		return mapper.findByCode(code);
	}

}





