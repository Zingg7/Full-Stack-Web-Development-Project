package cn.tedu.store.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.tedu.store.entity.Product;
import cn.tedu.store.mapper.ProductMapper;
import cn.tedu.store.service.IProductService;
import cn.tedu.store.service.ex.ProductNotFoundException;

@Service
public class ProductServiceImpl implements IProductService {

	@Autowired
	ProductMapper mapper;
	
	@Override
	public List<Product> getHotList() {
		return findHotList();
	}
	

	@Override
	public Product getById(Integer id) {
		// 使用id查商品数据
		Product product=findById(id);
		if(product==null) {
			throw new ProductNotFoundException("显示商品详情异常！未找到商品数据");
		}
		
		// 将不需要给用户的数据设为null
		product.setPriority(null);
		product.setStatus(null);
		product.setCreatedUser(null);
		product.setCreatedTime(null);
		product.setModifiedUser(null);
		product.setModifiedTime(null);
		
		return product;
	}
	
	/**
	 * 查询优先级排行前4位的商品数据
	 * @return 优先级排行前4位的商品数据
	 */
	private List<Product> findHotList(){
		return mapper.findHotList();
	}
	
	private Product findById(Integer id) {
		return mapper.findById(id);
	}


}
