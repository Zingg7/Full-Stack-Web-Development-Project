### 58. 购物车-分析

购物车：添加，修改数量(增)

数据库表：

	CREATE TABLE t_cart(
		cid INT AUTO_INCREMENT COMMENT '购物车id',
		uid INT COMMENT '用户id',
		pid INT COMMENT '商品id',
		price BIGINT COMMENT '商品单价',
		num INT COMMENT '数量',
		created_user VARCHAR(50) COMMENT '创建用户',
		created_time DATETIME COMMENT '创建时间',
		modified_user VARCHAR(50) COMMENT '最后修改用户',
		modified_time DATETIME COMMENT '最后修改时间',
		PRIMARY KEY (cid)
	)DEFAULT CHARSET=utf8;

开发对应的实体类：

创建`cn.tedu.store.entity.Cart`实体类，继承`BaseEntity`：

	public class Cart extends BaseEntity {
		private Integer cid;
		private Integer uid;
		private Integer pid;
		private Long price;
		private Integer num;
		//GET/SET/基于cid生成hashCode和equals/toString
	}

### 59. 购物车-添加-持久层

**(a) 规划SQL语句**

将购物车数据添加到数据库，是插入操作：

	insert into t_cart (除cid以外所有字段) values (...)

用户执行添加购物车的操作，需要先判断用户当前购物车中是否已经有该商品，如果有，应该修改该购物车记录中的商品数量：

	update t_cart set num=?(新值),modified_user=?, modified_time=? where cid=?

判断用户当前购物车中是否已经有该商品，是一次查询操作：

	select * from t_cart where uid=? and pid=?

**(b) 接口和抽象方法**

创建`cn.tedu.store.mapper.CartMapper`接口，并添加以下3个抽象方法：

	Integer saveCart(Cart cart);

	Integer updateNum(@Param("cid") Integer cid, @Param("num") Integer num, @Param("username") String username, @Param("modifiedTime") Date modifiedTime);

	Cart getByUidAndPid(@Param("uid") Integer uid,@Param("pid") Integer pid);

**(c) 配置映射**

复制得到`CartMapper.xml`，删除除主节点以外的所有子节点，保证`namespace`是正确的：

添加`<resultMap id="CartEntityMap">`：

	<resultMap id="CartEntityMap" 
		type="cn.tedu.store.entity.Cart" >
		<id column="cid" property="cid"/>	
		<result column="pid" property="pid" />
		<result column="uid" property="uid" />		
		<result column="price" property="price" />		
		<result column="num" property="num" />		
		<result column="created_user" property="createdUser" />	
		<result column="created_time" property="createdTime" />	
		<result column="modified_user" property="modifiedUser" />	
		<result column="modified_time" property="modifiedTime" />	
	</resultMap>


添加以上3个抽象方法的映射：

	<!-- 添加一条购物车记录 -->
	<!-- Integer saveCart(Cart cart) -->
	<insert id="saveCart"
		useGeneratedKeys="true"
		keyProperty="cid">
		insert into 
			t_cart (
				uid, pid,
				price, num,
				created_user, created_time,
				modified_user, modified_time
			) 
		values (
				#{uid}, #{pid},
				#{price}, #{num},
				#{createdUser}, #{createdTime},
				#{modifiedUser}, #{modifiedTime}
		)
	</insert>
	
	
	<!-- 更新一条购物车记录中的商品数量 -->
	<!-- Integer updateNum(
			@Param("cid") Integer cid, 
			@Param("num") Integer num, 
			@Param("username") String username,
			@Param("modifiedTime") Date modifiedTime) -->
	<update id="updateNum">
		update 
			t_cart 
		set 
			num=#{num},
			modified_user=#{username},
			modified_time=#{modifiedTime} 
		where 
			cid=#{cid}
	</update>
	
	<!-- 根据用户id和商品id查购物车记录 -->
	<!-- Cart getByUidAndPid(
			@Param("uid") Integer uid,
			@Param("pid") Integer pid) -->
	<select id="getByUidAndPid"
		resultMap="CartEntityMap">
		select 
			* 
		from 
			t_cart 
		where 
			uid=#{uid} 
		and 
			pid=#{pid}
	</select>

开发3个抽象方法对应的测试用例：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class CartMapperTests {
	
		@Autowired
		CartMapper mapper;
		
		@Test
		public void saveCart() {
			Cart cart=new Cart();
			cart.setUid(10);
			cart.setPid(10000001);
			cart.setPrice(1000L);
			cart.setNum(20);
			Integer row=mapper.saveCart(cart);
			System.err.println(row);
		}
		
		@Test
		public void updateNum() {
			Integer row=mapper.updateNum(1, 50, "令狐冲", new Date());
			System.err.println(row);
		}
		
		@Test
		public void getByUidAndPid() {
			Cart cart=mapper.getByUidAndPid(10, 10000001);
			System.err.println(cart);
		}

	}


### 60. 购物车-添加-业务层

**(a) 规划异常**

查询没有对应的异常

更新和插入操作对应`UpdateException`和`InsertException`

**(b) 接口和抽象方法**

创建`cn.tedu.store.service.ICartService`接口，添加抽象方法：

	void createCart(Integer num,Integer uid, Integer pid,String username)throws UpdateException,InsertException;

**(c) 实现抽象方法**

创建`cn.tedu.store.service.impl.CartServiceImpl`类，继承`ICartService`:

提供持久层3个抽象方法的私有实现：

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

添加抽象方法：

	public void createCart(Integer num,Integer uid, Integer pid,String username)throws UpdateException,InsertException{
		// 使用uid和pid查询是否有购物车数据
		// 没有：
		// 创建一个Cart对象
		// 手动添加pid,uid,num到cart
		// 使用pid查询商品价格
		// 手动添加商品价格到cart
		// 手动添加4个日志数据到cart
		// 将cart添加到数据库

		// 有：
		// 从查询结果中获取cid
		// 从查询结果中获取原购物车中的数量
		// 计算出新的商品数量=原数量+num
		// 执行更新操作
	}

代码实现如下：
	
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

开发对应的测试用例：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class CartServiceTests {
	
		@Autowired
		ICartService service;
		
		@Test
		public void createCart() {
			try {
				service.createCart(33, 10, 10000017, "令狐冲");
			}catch(ServiceException e) {
				System.err.println(e.getClass().getName());
				System.err.println(e.getMessage());
			}
		}
	
	}	

### 61. 购物车-添加-控制器层

**(a) 统一异常处理**

无

**(b) 设计请求**

	请求路径：/carts/create_cart
	请求参数：Integer num, Integer pid, HttpSession session
	请求方式：POST
	响应数据：JsonResult<Void>

**(c) 处理请求**

开发`cn.tedu.store.controller.CartController`，继承`BaseController`，类上添加`@RestController`，`@RequestMapping("carts")`。

开发对应的方法：


通过浏览器地址栏进行测试：`localhost:8080/carts/create_cart?num=30&pid=10000001`,该测试需要登录之后才能做。测试后，将方法前的`@RequestMapping`修改为`@PostMapping`


### 62. 购物车-添加-前端界面


### 63. 购物车-列表-持久层

**(a) 规划SQL语句**

将购物车列表中所需展示的数据从数据库中查询出来：
	select  
		t1.cid, t1.pid, t2.image, t2.title, t2.price, t1.price, t1.num
	from 
		t_cart t1 left join t_product t2
	on
		t1.pid = t2.id and t1.uid=?
	order by
		t1.modified_time desc;

如何生成一个复杂的SQL：

	查哪些列：t1.cid, t1.pid, t2.image, t2.title, t2.price(真实), t1.price(添加时), t1.num(购物车中商品的数量)
	查哪张表：t_cart t1 left join t_product t2
	限定条件：t1.pid = t2.id and t1.uid=?
	排序：t1.modified_time desc

	select  
		t1.cid, t1.pid, t2.image, t2.title, t2.price, t1.price, t1.num
	from 
		t_cart t1 left join t_product t2
	on
		t1.pid = t2.id 
	where
		t1.uid=?
	order by
		t1.modified_time desc;


需要开发一个`cn.tedu.store.entity.CartVO`类，封装本次查询的结果：

	public class CartVO{
		private Integer cid;
		private Integer pid;
		private String image;
		private String title;
		private Long realPrice;
		private Long price;
		private Integer num;

		// GET/SET/cid-hashcode,equals/toString
	}
	
**(b) 接口和抽象方法**

在`CartMapper.java`中添加抽象方法：

  List<CartVO> findCartList(Integer uid);

**(c) 配置映射**

在`CartMapper.xml`中配置以上方法的映射：

	<!-- 查询一个用户的所有购物车记录 -->
	<!-- List<CartVO> findCartList(Integer uid) -->
	<select id="findCartList"
		resultType="cn.tedu.store.entity.CartVO">
		select  
			t1.cid, t1.pid, 
			t2.image, t2.title, 
			t2.price as realPrice, t1.price, 
			t1.num
		from 
			t_cart t1 left join t_product t2
		on
			t1.pid = t2.id
		where
			t1.uid=#{uid}
		order by
			t1.modified_time desc;
	</select>


并开发对应的测试用例：

	@Test
	public void findCartList() {
		List<CartVO> list=mapper.findCartList(1);
		for(CartVO vo:list) {
			System.err.println(vo);
		}
	}

### 64. 购物车-列表-业务层

**(a) 规划异常**

无

**(b) 接口和抽象方法**

在`ICartService`中添加抽象方法：

	List<CartVO> getCartList(Integer uid);

**(c) 实现抽象方法**

在`CartServiceImpl`中提供持久层抽象方法的私有实现：
	
	/**
	 *   查询一个用户的所有购物车记录
	 * @param uid 用户id
	 * @return 购物车记录的集合
	 */
	private List<CartVO> findCartList(Integer uid){
		return mapper.findCartList(uid);
	}

实现接口中定义的抽象方法：

	@Override
	public List<CartVO> getCartList(Integer uid) {
		return findCartList(uid);
	}


### 65. 购物车-列表-控制器层

**(a) 统一异常处理**

无

**(b) 设计请求**

	请求路径：/carts/
	请求参数：HttpSession session
	请求方式：get
	响应数据：JsonResult<List<CartVO>>

**(c) 处理请求**

在`CartController`中添加对应的处理逻辑：

	@GetMapping("/")
	public JsonResult<List<CartVO>> getCartList(HttpSession session){
		Integer uid=getUidFromSession(session);
		
		List<CartVO> list=service.getCartList(uid);
		
		return new JsonResult<List<CartVO>>(SUCCESS, list);
	}

通过浏览器地址栏`localhost:8080/carts/`进行测试，要求先登录

### 66. 购物车-列表-前端页面

	$(function() {
		showList();
	})
			
	function showList(){
		$("#cart-list").empty();
		
		$.ajax({
            "url":"/carts/",
            "type":"get",
            "dataType":"json",
            "success":function(json) {
            	var data=json.data;
            	console.log(data.length);
            	
            	for(var i=0;i<data.length;i++){
			
					var tr='<tr>'+
					'<td><input type="checkbox" class="ckitem" /></td>'+
					'<td><img src="..#{image}collect.png" class="img-responsive" /></td>'+
					'<td>#{title}</td>'+
					'<td>¥<span id="goodsPrice1">#{realPrice}</span></td>'+
					'<td>'+
						'<input type="button" value="-" class="num-btn" onclick="reduceNum(1)" />'+
						'<input id="goodsCount1" type="text" size="2" readonly="readonly" class="num-text" value="#{num}">'+
						'<input class="num-btn" type="button" value="+" onclick="addNum(1)" />'+
					'</td>'+
					'<td><span id="goodsCast1">￥#{totalPrice}</span></td>'+
					'<td><input type="button" onclick="delCartItem(this)" class="cart-del btn btn-default btn-xs" value="删除" /></td>'+
					'</tr>';
					
					tr=tr.replace("#{image}",data[i].image);
					tr=tr.replace("#{title}",data[i].title);
					tr=tr.replace("#{realPrice}",data[i].realPrice);
					tr=tr.replace("#{num}",data[i].num);
					tr=tr.replace("#{totalPrice}",data[i].num*data[i].realPrice);
					
					// tr=tr.replace(/#{aid}/g,data[i].aid);
					$("#cart-list").append(tr);
            	}
            }
          });
	}



### 67. 购物车-增加商品数量-持久层

**(a) 规划SQL语句**

基于cid修改购物车记录中的商品数量属于更新操作，该语句已经存在，可以直接使用:

	update t_cart set num=?, modified_user=?, modified_time=? where cid=?;

在执行更新操作之前，需要对数据是否存在，及用户是否有操作权限进行验证：

	select * from t_cart where cid=?

**(b) 接口和抽象方法**

在`CartMapper`接口中添加以下抽象方法：

	Cart findByCid(Integer cid);

**(c) 配置映射**

在`CartMapper.xml`中配置抽象方法的映射：
	
	<!-- 根据cid查购物车数据 -->
	<!-- Cart findByCid(Integer cid) -->
	<select id="findByCid"
		resultMap="CartEntityMap">
		select 
			* 
		from 
			t_cart 
		where 
			cid=#{cid}
	</select>

开发对应的测试用例：

	@Test
	public void findByCid() {
		Cart cart=mapper.findByCid(2);
		System.err.println(cart);
	}

### 68. 购物车-增加商品数量-业务层

**(a) 规划异常**

购物车记录不存在，对应`CartNotFoundException`

用户没有操作权限，对应`AccessDeniedException`

更新操作，对应`UpdateException`


**(b) 接口和抽象方法**

在`ICartService`中添加以下抽象方法：

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

**(c) 实现抽象方法**

在`CartServiceImple`提供持久层方法的私有实现：

	/**
	 * 根据cid查购物车数据
	 * @param cid 购物车记录id
	 * @return 购物车数据 或 null
	 */
	private Cart findByCid(Integer cid) {
		return mapper.findByCid(cid);
	}

在`CartServiceImple`实现对应的抽象方法：

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

进行必要的测试：

	@Test
	public void addNum() {
		try {
			service.addNum(6, 100, 10, "林平之");
		}catch(ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}

### 69. 购物车-增加商品数量-控制器层

**(a) 统一异常处理**

在`BaseController`中处理`CartNotFoundException`

**(b) 设计请求**

	请求路径：/carts/add_num
	请求参数：Integer cid,Integer num,HttpSession session
	请求方式：POST
	响应数据：JsonResult<Void>

**(c) 实现请求**

	@RequestMapping("add_num")
	public JsonResult<Void> addNum(Integer cid,Integer num,HttpSession session){
		Integer uid=getUidFromSession(session);
		String username=getUsernameFromSession(session);
		
		service.addNum(cid, num, uid, username);
		return new JsonResult<Void>(SUCCESS);
	}
	
测试：`http://localhost:8080/carts/add_num?cid=10&num=200`

### 70. 购物车-增加商品数量-前端界面

### 71. 购物车-减少商品数量

### 72. 购物车-删除

### ---------------
### 列出所有2级商品类型的名称

id,  parent_id, name,
1      0        图书
2      1        电子书

实现逻辑：当前这条记录的`parent_id`对应的记录的`parent_id`等于0

查哪些列:t1.id,t1.parent_id,t1.name, t2.id,t2.parent_id,t2.name
查哪张表: t_product_categor t1 join t_product_categor t2 
连接条件：on t1.parent_id = t2.id
筛选条件：where t2.parent_id=0

	select
		t1.id,t1.parent_id,t1.name, t2.id,t2.parent_id,t2.name
	from
		t_product_category t1 join t_product_category t2
	on
		t1.parent_id = t2.id
	where
		t2.parent_id=0;




