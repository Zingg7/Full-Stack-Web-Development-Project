package cn.tedu.store.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.tedu.store.entity.Cart;
import cn.tedu.store.entity.CartVO;

/**
 *  购物车数据的持久层接口
 */
public interface CartMapper {
	
	/**
	 *  添加一条购物车记录
	 * @param cart 购物车记录
	 * @return 受影响的行数
	 */
	Integer saveCart(Cart cart);

	/**
	 *  更新一条购物车记录中的商品数量
	 * @param cid 购物车记录id
	 * @param num 商品数量
	 * @param username 最后修改人姓名
	 * @param modifiedTime 最后修改时间
	 * @return 受影响的行数
	 */
	Integer updateNum(
			@Param("cid") Integer cid, 
			@Param("num") Integer num, 
			@Param("username") String username,
			@Param("modifiedTime") Date modifiedTime);

	
	/**
	 * 根据用户id和商品id查购物车记录
	 * @param uid 用户id
	 * @param pid 商品id
	 * @return 购物车记录 或 null
	 */
	Cart getByUidAndPid(
			@Param("uid") Integer uid,
			@Param("pid") Integer pid);
	
	/**
	 *   查询一个用户的所有购物车记录
	 * @param uid 用户id
	 * @return 购物车记录的集合
	 */
	List<CartVO> findCartList(Integer uid);
	
	/**
	 * 根据cid查购物车数据
	 * @param cid 购物车记录id
	 * @return 购物车数据 或 null
	 */
	Cart findByCid(Integer cid);
	

}
