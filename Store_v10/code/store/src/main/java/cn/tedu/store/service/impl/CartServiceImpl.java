package cn.tedu.store.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.tedu.store.entity.Cart;
import cn.tedu.store.entity.CartVO;
import cn.tedu.store.mapper.CartMapper;
import cn.tedu.store.service.ICartService;
import cn.tedu.store.service.IProductService;
import cn.tedu.store.service.ex.AccessDeniedException;
import cn.tedu.store.service.ex.CartNotFoundException;
import cn.tedu.store.service.ex.InsertException;
import cn.tedu.store.service.ex.UpdateException;

@Service
public class CartServiceImpl implements ICartService {

	@Autowired
	CartMapper mapper;
	
	@Autowired
	IProductService productService;
	
	@Override
	public void createCart(Integer num, Integer uid, Integer pid, String username)
			throws UpdateException, InsertException {
		// 使用uid和pid查询是否有购物车数据
		Cart result=getByUidAndPid(uid, pid);
		// 没有：
		if(result==null) {
			// 创建一个Cart对象
			Cart cart=new Cart();
			// 手动添加pid,uid,num到cart
			cart.setPid(pid);
			cart.setUid(uid);
			cart.setNum(num);
			// 使用pid查询商品价格
			Long price=productService.getById(pid).getPrice();
			// 手动添加商品价格到cart
			cart.setPrice(price);
			// 手动添加4个日志数据到cart
			Date now=new Date();
			cart.setCreatedUser(username);
			cart.setCreatedTime(now);
			cart.setModifiedUser(username);
			cart.setModifiedTime(now);
			// 将cart添加到数据库
			saveCart(cart);
			return;
		}
		
		// 有：
		// 从查询结果中获取cid
		Integer cid=result.getCid();
		// 从查询结果中获取原购物车中的数量
		Integer oldNum=result.getNum();
		// 计算出新的商品数量=原数量+num
		int newNum=oldNum+num;
		// 执行更新操作
		updateNum(cid, newNum, username, new Date());
	}
	
	@Override
	public void addNum(Integer cid, Integer num, Integer uid, String username)
			throws CartNotFoundException, AccessDeniedException, UpdateException {
		// 使用cid查购物车记录
		Cart result=findByCid(cid);
		// 判断结果是否为null
		if(result==null) {
			// 是：CartNotFoundException
			throw new CartNotFoundException("增加购物车商品数量异常！记录不存在");
		}
		
		// 判断result中的uid和参数中的uid是否不一致
		if(!result.getUid().equals(uid)) {
			// 是：AccessDeniedException
			throw new AccessDeniedException("增加购物车商品数量异常！没有操作权限");
		}
		
		// 从result中获取之前的num
		Integer oldNum=result.getNum();
		// 计算生成新的num
		Integer newNum=oldNum+num;
		// 执行更新操作
		updateNum(cid, newNum, username, new Date());
	}
	
	
	
	@Override
	public List<CartVO> getCartList(Integer uid) {
		return findCartList(uid);
	}
	
	
	/**
	 *   查询一个用户的所有购物车记录
	 * @param uid 用户id
	 * @return 购物车记录的集合
	 */
	private List<CartVO> findCartList(Integer uid){
		return mapper.findCartList(uid);
	}

	/**
	 *  添加一条购物车记录
	 * @param cart 购物车记录
	 * @return 受影响的行数
	 */
	private void saveCart(Cart cart) throws InsertException {
		Integer row=mapper.saveCart(cart);
		if(row!=1) {
			throw new InsertException("添加购物车异常！请联系管理员");
		}
	}

	/**
	 *  更新一条购物车记录中的商品数量
	 * @param cid 购物车记录id
	 * @param num 商品数量
	 * @param username 最后修改人姓名
	 * @param modifiedTime 最后修改时间
	 * @return 受影响的行数
	 */
	private void updateNum(
			Integer cid, Integer num, 
			String username,Date modifiedTime) throws UpdateException {
		Integer row=mapper.updateNum(cid, num, username, modifiedTime);
		if(row!=1) {
			throw new UpdateException("添加购物车异常！更新数量失败！");
		}
	};

	
	/**
	 * 根据用户id和商品id查购物车记录
	 * @param uid 用户id
	 * @param pid 商品id
	 * @return 购物车记录 或 null
	 */
	private Cart getByUidAndPid(
			Integer uid,Integer pid) {
		return mapper.getByUidAndPid(uid, pid);
	}
	
	
	/**
	 * 根据cid查购物车数据
	 * @param cid 购物车记录id
	 * @return 购物车数据 或 null
	 */
	private Cart findByCid(Integer cid) {
		return mapper.findByCid(cid);
	}

}
