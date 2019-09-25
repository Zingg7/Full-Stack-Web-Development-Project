###1. 项目分析

首先，分析这个项目可能用到哪些种类的数据。本项目中：用户，商品，商品种类，收货地址，收藏，购物车，订单。

	1. 企业中这个阶段必须分析的尽量详尽
	2. 学习阶段这个过程适可而止

然后，确定这些数据相关的功能的开发顺序，优先开发基础数据和简单数据相关的功能。

	基础数据：和其他数据没有关联，是其他数据存在的基础
	简单数据：字段少，逻辑清晰，和其他表关联少
因此，上面数据的开发顺序：用户 > 收货地址 > 商品种类 > 商品 > 收藏 >购物车 > 订单 

接下来，分析1个数据相关的功能：注册，登录，修改密码，修改个人资料，上传头像

分析完成后，确定以上功能的开发顺序，一般按照“增 查 删 改”，因此“用户”相关的功能的开发顺序：注册 > 登录 > 修改密码 > 修改个人资料 > 上传头像

针对一个功能，确定开发顺序：创建数据表 > 创建实体类 > 持久层 > 业务层 > 控制器层 > 前端界面

###2. 用户-创建数据库表

创建数据库

	CREATE DATABASE tedu_store;

使用数据库

	USE tedu_store;

创建“用户”表

	CREATE TABLE t_user(
		uid INT AUTO_INCREMENT COMMENT '用户id',
		username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
		password CHAR(32) NOT NULL COMMENT '密码',
		salt CHAR(36) COMMENT '盐值',
		is_delete INT COMMENT '是否删除，0代表未删除，1代表已删除',
		phone VARCHAR(20) COMMENT '电话号码',
		email VARCHAR(50) COMMENT '邮箱',
		gender INT COMMENT '性别,0代表女，1代表男',
		avatar VARCHAR(100) COMMENT '头像',
		created_user VARCHAR(50) COMMENT '创建用户',
		created_time DATETIME COMMENT '创建时间',
		modified_user VARCHAR(50) COMMENT '最后修改用户',
		modified_time DATETIME COMMENT '最后修改时间',
		PRIMARY KEY (uid)
	)DEFAULT CHARSET=utf8;

###3. 用户-创建实体类

初始化springboot项目：

1. 在`https://start.spring.io`网站上初始化一个springboot项目
	1. war
	2. (dependencies)关联mysql驱动，mybatis框架
2. 将下载好的压缩文件解压缩，将文件夹拷贝到工作空间下
3. 在Eclipse通过"Import" -> "Existing Maven Projects"导入该项目
4. 修改pom.xml，springboot版本修改为2.1.3
5. 使用“Maven”->"Update Project..."->勾选Fource up....
6. 如果直接使用`src/test/java`中的测试类测试，会抛出异常，需要引入数据库的配置
7. 将之前配置的数据库连接信息拷贝到新项目中，再次测试，测试通过
8. 在默认测试类中开发一个测试方法`getConnection()`，获取一个数据库连接对象，测试数据库连接是否正常：


	@Autowired
	DataSource ds;

	@Test
	public void getConnection() throws SQLException {
		Connection conn=ds.getConnection();
		System.err.println(conn);
	}

首先，创建`cn.tedu.store.entity.BaseEntity`类，作为所有实体类的父类，封装所有实体类共同的属性，该类应该实现`Serializable`接口。

	abstract class BaseEntity implements Serializable{
		private String createdUser;
		private Date createdTime;
		private String modifiedUser;
		private Date modifiedTime;

	}


创建`cn.tedu.store.entity.User`用户实体类，继承自`BaseEntity`，并提供必要的GET/SET方法，基于uid生成`hashCode()`和`equals()`方法。

	public class User{

		private Integer uid;
		private String username;
		private String password;
		private String salt;
		private Integer isDelete;
		private String phone;
		private String email;
		private Integer gender;
		private String avatar;

		//GET/SET/基于uid生成hashCode和equals方法/toString()
	}

hashcode和equals的用途：
	1. 将对象存入HashSet或者HashMap
		1. Set是无序不可重复
		2. HashSet本质上是一个只有key,没有value的HashMap
		3. 先调用hashCode()方法，算出保存该对象的位置
		4. 会使用equals()方法判断存入的对象和之前的对象不重复
	2. Web容器(Tomcat)会提供一些缓存机制，也会用到对象的hashCode和equals方法


###4. 用户-注册-持久层

**(a) 规划SQL语句**

当前功能实际上是将用户数据保存到数据库中，

	INSERT INTO t_user(
		除uid以外的字段列表
	) VALUES(
		匹配的值
	);

根据"用户名唯一"约束，在插入一条数据之前，应该先检查数据库中是否存在同名的数据：

	select uid from t_user where username=?;

**(b) 接口和抽象方法**

创建`cn.tedu.store.mapper.UserMapper`接口，接口中声明以下2个抽象方法：

	public interface UserMapper{

			Integer addnew(User user);

			User findByUsername(String username);

	}

在启动类前需要添加一个`@MapperScan("cn.tedu.store.mapper")`注解，通知Spring所有持久层的接口在哪个包下


**(c) 配置映射**

在`src/main/resources`目录下添加一个**mappers**文件夹，在其中添加`UserMapper.xml`，从之前的文件中复制xml的声明，确保根节点下的`namespace="cn.tedu.store.mapper.UserMapper"`是正确的，内容如下：

[](8.jpg)	

	<!-- 注册用户 -->
	<!-- Integer addnew(User user) -->
	<insert id="addnew" useGeneratedKeys="true" keyProperty="uid">
		INSERT INTO t_user(
			username, password,
			salt,is_delete,
			phone,email,
			gender,avatar,
			created_user,created_time,
			modified_user,modified_time
		) VALUES(
			#{username}, #{password},
			#{salt},#{isDelete},
			#{phone},#{email},
			#{gender},#{avatar},
			#{createdUser},#{createdTime},
			#{modifiedUser},#{modifiedTime}
		)
	</insert>

	<!-- 使用用户名查询用户 -->
	<!-- User findByUsername(String username)-->
	<select id="findByUsername" resultType="cn.tedu.store.entity.User" >
		select 
			uid 
		from 
			t_user 
		where 
			username=#{username}
	</select>

在`src/test/java`中开发测试用例`cn.tedu.store.mapper.UesrMapperTests`，注意，该类前面应该添加`@RunWith(SpringRunner.class)`和`@SpringBootTest`注解，在里面开发以上2个方法对应的测试方法：

	@Autowired
	UserMapper mapper;
	
	@Test
	public void addnew() {
		User user=new User();
		user.setUsername("admin");
		user.setPassword("1234");
		System.err.println("before uid="+user.getUid());
		Integer row=mapper.addnew(user);
		System.err.println("row="+row);
		System.err.println("after uid="+user.getUid());
		
	}
	
	@Test
	public void findByUsername() {
		User user=mapper.findByUsername("root");
		System.err.println(user);
	}


###5. 用户-注册-业务层

**(a) 规划异常**

首先，分析用户在执行当前功能中，可能出现哪些错误情况。每一种错误情况，可以使用一种异常来表示。

先创建`cn.tedu.store.service.ex.ServiceException`，作为所有业务层异常的父类，继承`RuntimeException`。

针对“用户名已经存在”可以设计一个异常"UsernameDuplicateException"，`cn.tedu.store.service.ex.UsernameDuplicateException`,继承`ServiceException`。

还有一种情况是，执行插入操作时，莫名的出现异常或原因不好描述的异常，归为`cn.tedu.store.service.ex.InsertException`，继承`ServiceException`。

[](9.jpg)

**(b) 接口和抽象方法**

首先，创建业务层的接口`cn.tedu.store.service.IUserService`，在该接口中声明抽象方法：
	
	public interface IUserService{

		void reg(User user);

	}


在设计业务层的方法时，返回值仅考虑业务成功的情况，失败的情况以抛出异常来解决


**(c) 实现抽象方法**

在`cn.tedu.store.service.impl.UserServiceImpl`，实现`IUserService`接口，重写抽象方法：

	public class UserServiceImpl implements IUserService{

		@Autowired
		private UserMapper userMapper;

		public void reg(User user){
			// 基于user的getUsername()获取用户名
			// 调用持久层方法findByUsername，看User是否不为null
			// 不为null：抛出UsernameDuplicateException

			// 向user中补全数据
			// TODO 补全盐值
			// TODO 获取用户输入的密码
			// TODO 基于盐值对密码进行加密
			// TODO 将加密后的密码添加到user中
			// TODO 补全is_delete为0

			// 补全4项日志数据

			// 调用mapper的addnew()，将用户数据添加到数据库
			// 查看返回的行数是否不为1
			// 是： 抛出InsertException

		}
	}

实际代码如下：

	@Autowired
	UserMapper mapper;
	
	@Override
	public void reg(User user) throws UsernameDuplicateException, InsertException {
		// 基于user的getUsername()获取用户名
		String username=user.getUsername();
		// 调用持久层方法findByUsername，看User是否不为null
		User result=mapper.findByUsername(username);
		// 不为null：抛出UsernameDuplicateException
		if(result!=null) {
			throw new UsernameDuplicateException("用户注册异常！用户名已存在");
		}
		
		// 向user中补全数据
		// TODO 补全盐值
		// TODO 获取用户输入的密码
		// TODO 基于盐值对密码进行加密
		// TODO 将加密后的密码添加到user中
		// 补全is_delete为0
		user.setIsDelete(0);
		
		Date now=new Date();
		// 补全4项日志数据
		user.setCreatedUser(username);
		user.setCreatedTime(now);
		user.setModifiedUser(username);
		user.setModifiedTime(now);

		// 调用mapper的addnew()，将用户数据添加到数据库
		Integer row=mapper.addnew(user);
		// 查看返回的行数是否不为1
		if(!row.equals(1)) {
			// 是： 抛出InsertException		// TODO Auto-generated method stub
			throw new InsertException("用户注册异常！请联系管理员");
		}

	}

完成后，在`src/test/java`中开发测试用例`cn.tedu.store.service.UserServcieTests`类，对上面的方法进行测试：
	
	@Autowired
	IUserService service;
	
	@Test
	public void reg() {
		try {
			User user=new User();
			user.setUsername("Tom");
			user.setPassword("1234");
			service.reg(user);
		} catch (Exception e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	

###6. 用户-注册-控制器层

###7. 用户-注册-前端界面

###8. 用户-登录-持久层

###9. 用户-登录-业务层

###10. 用户-登录-控制器层

###11. 用户-登录-前端界面

###12. 用户-修改密码-持久层

###13. 用户-修改密码-业务层

###14. 用户-修改密码-控制器层

###15. 用户-修改密码-前端界面




