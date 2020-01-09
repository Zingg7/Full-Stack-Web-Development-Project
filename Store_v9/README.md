### 49. 商品-实体类

在`cn.tedu.store.entity.Product`类，继承`BaseEntity`。

### 50. 商品-热销排行-持久层

**(a) 规划SQL语句**

热销排行，是按照商品的`priority`值进行排序，值越高，越优先显示，当前用例中显示`priority`前4的商品的信息。

	select 
		id,title,
		price,image
	from 
		t_product 
	where 
		status=1 and num > 0 
	order by 
		priority desc 
		limit 0,4;

**(b) 接口和抽象方法**

创建`cn.tedu.store.mapper.ProductMapper`接口，并添加以下抽象方法：

	List<Product> findHotList();

**(c) 配置映射**

在`src/main/resources/mappers`下创建`ProductMapper.xml`文件，确定主节点的`namespace`的值是正确的，并添加上面的抽象方法的映射：

	<!-- 查询优先级排行前4位的商品数据 -->
	<!-- List<Product> findHotList() -->
	<select id="findHotList" resultType="cn.tedu.store.entity.Product">
		select 
			id,title,
			price,image
		from 
			t_product
		where 
			status=1 and num > 0 
		order by 
			priority desc 
			limit 0,4
	</select>



开发对应的测试用例：

	@Test
	public void findHotList() {
		List<Product> list=mapper.findHotList();
		for(Product p:list) {
			System.err.println(p);
		}
	}

### 51. 商品-热销排行-业务层

**(a) 规划异常**

无

**(b) 接口和抽象方法**

创建`cn.tedu.store.service.IProductService`接口，并添加抽象方法：

	List<Product> getHotList();


**(c) 实现抽象方法**

创建`cn.tedu.store.service.impl.ProductServiceImpl`类，实现`IProductService`接口：

将持久层的抽象方法复制过来进行私有实现：
	
	/**
	 * 查询优先级排行前4位的商品数据
	 * @return 优先级排行前4位的商品数据
	 */
	private List<Product> findHotList(){
		return mapper.findHotList();
	}

实现接口中定义的抽象方法：

	@Override
	public List<Product> getHotList() {
		return findHotList();
	}

开发对应的测试用例：

	@Test
	public void getHotList() {
		List<Product> list=service.getHotList();
		for(Product p:list) {
			System.err.println(p);
		}
	}


### 52. 商品-热销排行-控制器层

**(a) 统一异常处理**

无

**(b) 设计请求**

	请求路径：/products/hot
	请求参数：无
	请求方式：GET
	响应数据：JsonResult<List<Product>>
	是否拦截：不拦截
	需要放行: /web/index.html
			 /products/**

**(c) 处理请求**

开发`cn.tedu.store.controller.ProudctController`，继承`BaseController`，添加以下方法：

	@GetMapping("hot")
	public JsonResult<List<Product>> getHotList(){
		// 查询
		// 返回
	}

代码实现：

	@GetMapping("hot")
	public JsonResult<List<Product>> getHotList(){
		// 查询
		List<Product> data = service.getHotList();
		// 返回
		return new JsonResult<List<Product>>(SUCCESS, data);
	}


在浏览器使用`localhost:8080/products/hot`进行测试

### 53. 商品-热销排行-前端界面

### 54. 商品-商品详情-持久层

**(a) 规划SQL语句**

显示商品相应，是根据商品的id查询商品数据：

	select * from t_product where id=?

**(b) 接口和抽象方法**

在`ProductMapper.java`中添加抽象方法：

	Product findById(Integer id);

**(c) 配置映射**

	<resultMap id="ProductEntityMap" 
		type="cn.tedu.store.entity.Product" >
		<id column="id" property="id"></id>
		<result column="category_id" property="categoryId"/>	
		<result column="item_type" property="itemType"/>	
		<result column="title" property="title"/>	
		<result column="sell_point" property="sellPoint"/>	
		<result column="price" property="price"/>	
		<result column="num" property="num"/>	
		<result column="image" property="image"/>	
		<result column="status" property="status"/>	
		<result column="priority" property="priority"/>	
		<result column="created_user" property="createdUser" />
		<result column="created_time" property="createdTime" />
		<result column="modified_user" property="modifiedUser" />
		<result column="modified_time" property="modifiedTime" />
	</resultMap>

	<!-- 根据商品id查商品数据 -->
	<!-- Product findById(Integer id) -->
	<select id="findById"
		resultMap="ProductEntityMap">
		select 
			* 
		from 
			t_product 
		where 
			id=#{id}
	</select>


完成后，开发相应的测试用例：

	@Test
	public void findById() {
		System.err.println(mapper.findById(10000001));
	}

### 55. 商品-商品详情-业务层

**(a) 规划异常**

开发`ProductNotFoundException`，继承`ServiceException`

**(b) 接口和抽象方法**

在`IProductService`中提供以下抽象方法：

	Product getById(Integer id);

**(c) 实现抽象方法**

在`ProductServiceImpl`中：

提供持久层方法的私有实现：

	private Product findById(Integer id) {
		return mapper.findById(id);
	}

实现接口中定义的抽象方法：

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

开发对应的测试用例：

	@Test
	public void getById() {
		try {
			Product product=service.getById(100001);
			System.err.println(product);
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}

### 56. 商品-商品详情-控制器层

**(a) 统一异常处理**

在`BaseController`中添加对`ProductNotFoundException`的处理逻辑

**(b) 设计请求**
	
	请求路径：/products/{id}/get
	请求参数：PathVariable("id")Integer id
	请求方式：GET
	相应数据：JsonResult<Product>
  
**(c) 处理请求**

	@GetMapping("{id}/get")
	public JsonResult<Product> getById(@PathVariable("id")Integer id){
		Product product=service.getById(id);
		return new JsonResult<Product>(SUCCESS,product);
	}

测试：在浏览器使用`localhost:8080/products/10000001/get`

将`/web/product.html`添加到白名单中

### 57. 商品-商品详情-前端界面

在`/web/product.html`页面中引入`jquery-getUrlParam.js`文件

	<script src="../js/jquery-getUrlParam.js" type="text/javascript"></script>


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

创建`cn.tedu.store.entity.Cart`实体类，继承`BaseEntity`

### 59. 购物车-添加-持久层

**(a) 规划SQL语句**

将购物车数据添加到数据库，是插入操作：

	insert into t_cart (除cid以外所有字段) values (...)

用户执行添加购物车的操作，需要先判断用户当前购物车中是否已经有该商品，如果有，应该修改该购物车记录中的商品数量：

	update t_cart set num=?(新值),modified_user=?, modefied_time=? where cid=?

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


添加以上3个抽象方法的映射：



开发3个抽象方法对应的测试用例：


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

开发对应的测试用例：


###  -------------------------------
### 缓存

将一组数据临时保存在内存中，再后续的多次查询中，不再从硬盘上读取数据，而是直接返回内存中保存的数据。

Mysql的缓存机制：
	
	1. 将用户查询的SQL语句作为缓存的key,将查询到的结果作为value，保存再内存中
	2. 如果用户对这张表执行了更新操作(增删改)，则清空该缓存数据
	3. 随着在用户访问量的增加，缓存的有效期越来越短，因此mysql在较新的版本中已经不再提供缓存机制。

MyBatis提供的缓存机制：

	mybatis提供了两级缓存机制：
	一级缓存：是SqlSession级别，以用户的一次访问为单位。默认执行，不需要配置。

	二级缓存：是namespace级别，不同用户可以共享缓存数据。使用时，在对应的`xxxMapper.xml`的根节点下添加`<cache></cache>`

	mybaits使用`<resultMap>`节点下的`<id>`节点的值作为缓存数据的`key`，因此在开发`<resultMap>`节点时，一般将数据的主键字段作为`<id>`节点的值，以唯一标识该缓存数据。

	如果项目的业务量不大，是没有必要使用缓存机制的