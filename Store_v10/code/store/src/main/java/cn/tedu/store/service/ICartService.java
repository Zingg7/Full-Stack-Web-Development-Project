package cn.tedu.store.service;

import java.util.List;

import cn.tedu.store.entity.CartVO;
import cn.tedu.store.service.ex.AccessDeniedException;
import cn.tedu.store.service.ex.CartNotFoundException;
import cn.tedu.store.service.ex.InsertException;
import cn.tedu.store.service.ex.UpdateException;

/**
 * 购物车的业务层接口
 */
public interface ICartService {
	
	/**
	 * 创建购物车记录
	 * @param num 商品数量
	 * @param uid 用户id
	 * @param pid 商品id
	 * @param username 创建人姓名
	 * @throws UpdateException
	 * @throws InsertException
	 */
	void createCart(
			Integer num,
			Integer uid, 
			Integer pid,
			String username)
					throws UpdateException,
					InsertException;
	
	/**
	 *  增加购物车中的商品的数量
	 * @param cid 商品的id
	 * @param num 数量的增量
	 * @param uid 用户的id
	 * @param username 最后修改人姓名
	 */
	void addNum(Integer cid, Integer num, 
			Integer uid, String username)throws CartNotFoundException,
			AccessDeniedException, UpdateException;
	
	
	/**
	 *  获取一个用户的所有购物车记录
	 * @param uid 用户id
	 * @return 所有购物车记录
	 */
	List<CartVO> getCartList(Integer uid);

}
