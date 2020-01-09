package cn.tedu.store.mapper;

import java.util.List;

import cn.tedu.store.entity.District;

/**
 * 省市区信息的持久层接口
 */
public interface DistrictMapper {
	
	/**
	 * 根据parent查子级区域信息
	 * @param parent 父级区域编号
	 * @return 子级区域信息
	 */
	List<District> findByParent(String parent);

	
}




