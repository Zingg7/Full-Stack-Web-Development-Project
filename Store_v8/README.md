### 40. 收货地址-设为默认-持久层

**(a) 规划SQL语句**

要修改一个地址数据的is_default属性的值，改为1：

	update t_address set is_default=1, modified_user=?,modified_time=? where aid=?;   

应该将原来的默认收货地址数据设为非默认，如果要针对原来的默认地址进行修改，则需要额外进行一次查询，这里可以简化成将该用户所有的收货地址设为非默认。这里不会修改`modified_time`，避免对后续的查询造成影响:

	update t_address set is_default=0 where uid=?;

在设为默认收货地址之前，应该对前台传来的`aid`进行验证：

	select * from t_address where aid=?

这个查询查什么都可以，重点在于是否能够查到数据。

在更新之前，还应该验证当前用户是否有操作该地址数据的权限：
	
	select uid from t_address where aid=?


**(b) 接口和抽象方法**

在`AddressMapper.java`接口中添加以下3个抽象方法：

	Integer updateDefault(
			@Param("aid") Integer aid,
			@Param("username") String username,
			@Param("modifiedTime") Date modifiedTime);

	Integer updateNonDefault(Integer uid);

	Address findByAid(Integer aid);

**(c) 配置映射**

在`AddressMapper.xml`中配置对应的映射：

	<!-- 将一条收货地址设为默认收货地址 -->
	<!-- Integer updateDefault(
			@Param("aid") Integer aid,
			@Param("username") String username,
			@Param("modifiedTime") Date modifiedTime) -->
	<update id="updateDefault">
		update 
			t_address 
		set 
			is_default=1, 
			modified_user=#{username},
			modified_time=#{modifiedTime}
		where 
			aid=#{aid};
	</update>
	
	<!-- 将一个用户的所有收货地址设为非默认 -->
	<!-- Integer updateNonDefault(Integer uid) -->
	<update id="updateNonDefault">
		update 
			t_address 
		set 
			is_default=0 
		where 
			uid=#{uid};
	</update>
	
	<!-- 根据aid查询一条收货地址数据 -->
	<!-- Address findByAid(Integer aid) -->
	<select id="findByAid"
		resultMap="AddressEntityMap">
		select 
			* 
		from 
			t_address 
		where 
			aid=#{aid}
	</select>

在`AddressMapperTests`中开发测试方法：

	@Test
	public void updateNonDefault() {
		Integer rows=mapper.updateNonDefault(10);
		System.err.println(rows);
	}
	
	@Test
	public void updateDefault() {
		Integer row=mapper.updateDefault(14, "张三丰", new Date());
		System.err.println(row);
	}
	
	@Test
	public void findByAid() {
		Address addr=mapper.findByAid(14);
		System.err.println(addr);
	}

### 41. 收货地址-设为默认-业务层

**(a) 规划异常**

更新操作，可能抛出`UpdateException`。

使用aid查询地址数据是否存在，可能抛出`AddressNotFoundException`。

查询到的地址的uid和当前操作用户的uid不一致，可能抛出`AccessDeniedException`


**(b) 接口和抽象方法**

在`IAddressService`接口中定义以下抽象方法：

	void setDefault(Integer aid,Integer uid,String username)throws AddressNotFoundException, AccessDeniedException, UpdateException;

**(c) 实现接口**

在`AddressServiceImpl`中添加持久层`AddressMapper`中声明的3个抽象方法的私有实现：
	
	/**
	 * 将一条收货地址设为默认收货地址
	 * @param aid 收货地址id
	 * @param uesrname 最后修改人姓名
	 * @param modifiedTime 最后修改时间
	 * @return 受影响的行数
	 */
	private void updateDefault(Integer aid, String username, Date modifiedTime) throws UpdateException {
		Integer row=mapper.updateDefault(aid, username, modifiedTime);
		if(row!=1) {
			throw new UpdateException("设置默认收货地址异常！请联系管理员");
		}
		
	}

	/**
	 * 将一个用户的所有收货地址设为非默认
	 * @param uid 用户id
	 * @return 受影响的行数
	 */
	private void updateNonDefault(Integer uid) throws UpdateException{
		Integer rows=mapper.updateNonDefault(uid);
		if(rows<1) {
			throw new UpdateException("设置默认收货地址异常！请联系管理员");
		}
	}

	/**
	 * 根据aid查询一条收货地址数据
	 * @param aid 收货地址id
	 * @return 收货地址数据 或 null
	 */
	private Address findByAid(Integer aid) {
		return mapper.findByAid(aid);
	}


在`AddressServiceImpl`中实现`IAddressService`声明的抽象方法：

	public void setDefault(Integer aid,Integer uid,String username)throws AddressNotFoundException, AccessDeniedException, UpdateException{
		// 使用aid查地址数据
		// 判断结果是否为null
		// 是：AddressNotFoundException

		// 查询结果中的uid和方法参数的uid是否不一致
		// 是：AccessDeniedException

		// 将该用户的所有收货地址设为非默认

		// 将该用户指定的收货地址设为默认
	}

代码实现为：

	@Override
	@Transactional
	public void setDefault(Integer aid, Integer uid, String username) throws AddressNotFoundException, AccessDeniedException, UpdateException {
		// 使用aid查地址数据
		Address address=findByAid(aid);
		// 判断结果是否为null
		if(address==null) {
			// 是：AddressNotFoundException
			throw new AddressNotFoundException("设置默认收货地址异常！地址数据不存在");
		}

		// 查询结果中的uid和方法参数的uid是否不一致
		if(!address.getUid().equals(uid)) {
			// 是：AccessDeniedException
			throw new AccessDeniedException("设置默认收货地址异常！访问权限不足");
		}

		// 将该用户的所有收货地址设为非默认
		updateNonDefault(uid);
		// 将该用户指定的收货地址设为默认
		updateDefault(aid, username, new Date());
	}

开发对应的测试用例

### 42. 收货地址-设为默认-控制器层

**(a) 统一异常处理**

在`BaseController`添加2种新异常的处理逻辑：

**(b) 设计请求**

	请求路径：/addresses/set_default
	请求参数：Integer aid, HttpSession session
	请求方式：Post
	响应数据：JsonResult<Void>

当前比较流行的一种设计风格：RESTful

	请求url的设计：/资源/id/命令
	使用这种设计有个前提：使用的框架要支持RESTful

	第一步：在请求路径中使用{xxx}作为参数的占位符
	第二步：在方法中获取参数时，前面添加@PathVariable("{xxx}")

	这种url的设计风格适合参数少且固定的场景

	请求路径：/addresses/{aid}/set_default
	请求参数：@PathVariable("{aid}") Integer aid, HttpSession session
	请求方式：Post
	响应数据：JsonResult<Void>


**(c) 实现请求**

	@RequestMapping("{aid}/set_default")
	public JsonResult<Void> setDefault(@PathVariable("aid") Integer aid, HttpSession session){
		String username=getUsernameFromSession(session);
		Integer uid=getUidFromSession(session);
		service.setDefault(aid,uid,username);
		return new JsonResult<>(SUCCESS);
	}

在浏览器地址栏进行测试：`localhost:8080/addresses/14/set_default`

### 43. 收货地址-设为默认-前端界面

### 44. 收货地址-删除-持久层

**(a) 规划SQL语句**

删除一条数据，大概的语句：

	delete from t_address where aid=?

删除之前，应该验证aid对应的数据是否存在，然后还应该验证当前用户是否有操作该数据的权限，在之前已经实现该功能。

如果用户删除的是默认收货地址，且删除之后，该用户已经没有任何收货地址了，则不需要进行额外的操作。查询该用户当前有几条收货地址，已经通过`countByUid()`实现过。

如果用户删除的是默认收货地址，且删除之后，该用户还有其它收货地址，则应该选择“最后更新”的那一条收货地址，作为用户的默认收货地址。需要查找该用户“最后更新”的那条收货地址记录：

	select * from t_address where uid=? order by modified_time desc limit 0,1;

**(b) 接口和抽象方法**

在`AddressMapper.java`中添加2个抽象方法：

	Integer deleteByAid(Integer aid);

	Address findLastModified(Integer uid);


**(c) 配置映射**

	<!-- 根据aid删除一条收货地址记录 -->
	<!-- Integer deleteByAid(Integer aid) -->
	<delete id="deleteByAid">
		delete from 
			t_address 
		where aid=#{aid}
	</delete>
	
	<!-- 查询一个用户的最后修改的收货地址 -->
	<!-- Address findLastModified(Integer uid) -->
	<select id="findLastModified"
		resultMap="AddressEntityMap">
		select 
			* 
		from 
			t_address 
		where 
			uid=#{uid} 
		order by 
			modified_time desc 
		limit 0,1;
	</select>

开发对应的测试用例：

	@Test
	public void deteleByAid() {
		Integer row=mapper.deleteByAid(6);
		System.err.println(row);
	}
	
	@Test
	public void findLastModified() {
		Address address=mapper.findLastModified(10);
		System.err.println(address);
	}

### 45. 收货地址-删除-业务层

**(a) 规划异常**

删除时：DeleteException

删除前：AddressNotFoundException, AccessDeniedException

删除后，可能将一条记录设为默认收货地址：UpdateException

**(b) 接口和抽象方法**

在`IAddressService`接口中添加以下抽象方法：

	void removeAddress(Integer aid, Integer uid, String username) throws AddressNotFoundException, AccessDeniedException, DeleteException,UpdateException;


**(c) 实现接口**

在`AddressServiceImpl`中提供持久层2个抽象方法的私有实现：

	/**
	 * 根据aid删除一条收货地址记录
	 * @param aid 收货地址id
	 * @return 受影响的行数
	 */
	private void deleteByAid(Integer aid) throws DeleteException {
		Integer row=mapper.deleteByAid(aid);
		if(row!=1) {
			throw new DeleteException("删除收货地址异常！请联系管理员");
		}
	};
	
	/**
	 * 查询一个用户的最后修改的收货地址
	 * @param uid 用户的id
	 * @return 最后修改的收货地址
	 */
	private Address findLastModified(Integer uid) {
		return mapper.findLastModified(uid);
	};

在`AddressServiceImpl`中实现接口中定义的抽象方法：

	@Transactional
	public void removeAddress(Integer aid,
		Integer uid, String username) throws AddressNotFoundException, AccessDeniedException, DeleteException,UpdateException{

		// 使用aid查地址数据
		// 判断结果是否为null
		// 是：AddressNotFoundException

		// 查询结果中的uid和方法参数的uid是否不一致
		// 是：AccessDeniedException

		// 删除aid对应的地址数据

		// 判断刚才的查询结果中的isDefault是否不为1
		// return;

		// 查看当前用户剩余的收货地址条数
		// 判断条数是否为0
		// 是：return;

		// 查询该用户的最后修改的收货地址
		// 将该条记录设为该用户的默认收货地址
	}

对应的代码实现：

	@Override
	@Transactional
	public void removeAddress(Integer aid, Integer uid, String username)
			throws AddressNotFoundException, AccessDeniedException, DeleteException, UpdateException {
		// 使用aid查地址数据
		Address result=findByAid(aid);
		// 判断结果是否为null
		if(result==null) {
			// 是：AddressNotFoundException
			throw new AddressNotFoundException("删除收货地址异常！地址数据不存在");
		}

		// 查询结果中的uid和方法参数的uid是否不一致
		if(!result.getUid().equals(uid)) {
			// 是：AccessDeniedException
			throw new AccessDeniedException("删除收货地址异常！访问权限不足");
		}

		// 删除aid对应的地址数据
		deleteByAid(aid);
		
		// 判断刚才的查询结果中的isDefault是否不为1
		if(result.getIsDefault()!=1) {
			return;
		}

		// 查看当前用户剩余的收货地址条数
		Integer count = countByUid(uid);
		// 判断条数是否为0
		if(count==0) {
			return;
		}

		// 查询该用户的最后修改的收货地址
		Address lastModifiedAddress=findLastModified(uid);
		// 将该条记录设为该用户的默认收货地址
		updateDefault(lastModifiedAddress.getAid(), username, new Date());
	}

开发对应的测试用例：

	@Test
	public void removeAddress() {
		try {
			service.removeAddress(15, 10, "张翠山");
		}catch(ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}

### 46. 收货地址-删除-控制器层

**(a) 统一异常处理**

处理`DeleteException`

**(b) 设计请求**
	
	/资源/id/命令

	请求路径：/addresses/{aid}/delete
	请求参数：@PathVariable("aid") Integer aid, HttpSession session
	请求方式：POST
	响应数据：JsonResult<Void>

**(c) 实现请求**

	@RequestMapping("{aid}/delete")
	public JsonResult<Void> removeAddress(@PathVariable("aid") Integer aid, HttpSession session){
		Integer uid=getUidFromSession(session);
		String username=getUsernameFromSession(session);
		return new JsonResult<>(SUCCESS);
	}

完成后，通过`localhost:8080/addresses/15/delete`进行测试，测试通过后，将控制器层的`RequestMapping`修改为`PostMapping`

### 47. 收货地址-删除-前端界面

### 48. 商品-分析

导入商品和商品种类表及数据：

首先应该选择`tedu_store`库，在使用`source 文件路径`导入文件

对`t_product`和`t_product_category`表中的字段加以熟悉

`t_product_category`表在后续的用例中用不上，所以仅需要开发`t_product`表对应的实体类


### -----------------------------
### 基于spring-jdbc的事务

事务的概念：事务指逻辑上对数据库的一组操作，组成这组操作的每一项操作，要么都成功，要么都不成功。主流的数据库，都提供了对事务的支持。

什么时候用事务：在一组数据库操作中，存在2个及以上的更新操作(增、删、改)时，应该使用事务

如何实现事务？

命令式事务：
	
	start transaction;
	commit;
	rollback;

编程式事务：
	
	使用Java JDBC的api:
	conn.setAutoCommit(false);
	conn.commit();
	conn.rollback();

声明式事务：

	使用Spring提供的注解： @Transactional

@Transactional的大致实现原理：

	// 启动事务
	try{
		调用添加了@Transactional的业务层方法
		提交事务
	}catch(RuntimeException e){
		回滚事务
	}

@Transactional默认仅对`RuntimeException`及其子类异常回滚，因此我们在设计异常时，要求`ServiceException`继承`RuntimeException`，以保证事务正常回滚。

可以通过显式声明`rollbackfor`属性，来配置该注解对`Exception`回滚：`@Transactional(rollbackFor=Exception.class)`

`@Transactional`也可以添加到类上，代表该类中所有的方法都是在事务下运行的，但是不推荐这么做