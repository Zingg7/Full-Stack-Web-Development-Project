package cn.tedu.store.service;

import java.util.List;

import cn.tedu.store.entity.Product;

public interface IProductService {
	
	
	/**
	 * 查询热销商品
	 * @return 热销商品数据
	 */
	List<Product> getHotList();
	
	/**
	 * 使用商品id查商品数据
	 * @param id 商品id
	 * @return 商品数据
	 */
	Product getById(Integer id);

}
