package cn.tedu.store.mapper;

import java.util.List;

import cn.tedu.store.entity.Product;

/**
 * 商品数据的持久层接口
 */
public interface ProductMapper {
	
	/**
	 * 查询优先级排行前4位的商品数据
	 * @return 优先级排行前4位的商品数据
	 */
	List<Product> findHotList(); 
	
	/**
	 * 根据商品id查商品数据
	 * @param id 商品id
	 * @return 商品数据
	 */
	Product findById(Integer id);

}



