### 25. 收货地址功能分析

与“收货地址”数据相关的功能有：新增，修改，删除，设为默认，地址列表

以上功能的开发顺序，"增 查 删 改"，新增 > 地址列表 >设为默认 > 删除 > 修改 

### 26. 创建数据表

表名和编码规则 > 主键字段 > 注释 > 字段名 > 类型 > 约束

    CREATE TABLE t_address(
        aid INT AUTO_INCREMENT COMMENT '地址id',
        uid INT COMMENT '用户id',
        name VARCHAR(50) COMMENT '收货人姓名',
        province_name VARCHAR(20) COMMENT '省名称',
        province_code INT COMMENT '省代码',
        city_name VARCHAR(20) COMMENT '市名称',
        city_code INT COMMENT '市代码',
        area_name VARCHAR(50) COMMENT '区名称',
        area_code INT COMMENT '区代码',
        zip INT COMMENT '邮政编码',
        address VARCHAR(50) COMMENT '详细地址',
        phone VARCHAR(20) COMMENT '手机号',
        tel VARCHAR(20) COMMENT '固话',
        tag VARCHAR(20) COMMENT '地址类型',
        is_default INT COMMENT '是否为默认地址,0-不是，1-是',
        created_user VARCHAR(50) COMMENT '创建人',
        created_time DATETIME COMMENT '创建时间',
        modified_user VARCHAR(50) COMMENT '最后修改人',
        modified_time DATETIME COMMENT '最后修改时间',
        PRIMARY KEY (aid)
    )DEFAULT CHARSET=utf8;


### 27. 创建实体类

创建`cn.tedu.store.entity.Address`实体类，封装收货地址数据，继承`cn.tedu.store.entity.BaseEntity`

    public class Address{
        private Integer aid;
        private Integer uid;
        private String name;
        private String provinceName;
        private Integer provinceCode;
        private String cityName;
        private Integer cityCode;
        private String areaName;
        private Integer areaCode;
        private Integer zip;
        private String address;
        private String phone;
        private String tel;
        private String tag;
        private Integer isDefault;

        // GET/SET/基于aid生成hashCode和equals方法/toString方法
    }

### 28. 收货地址-新增-持久层

**(a) 规划SQL语句**

“新增收货地址”属于插入操作，大概的SQL语句是：

    insert into t_address(除aid以外的所有字段) values(与之匹配的所有属性);

“默认地址”的设置逻辑是：如果当前用户没有任何收货地址，添加的第一条收货地址为“默认收货地址”，如果用户已经存在其他收货地址，则添加的新地址不会作为“默认收货地址”，因此，在插入之前，应该查询当前用户已有几条收货地址：

    select count(*) from t_address where uid=?

每个用户的收货地址条数有一个上限，在插入新地址之前，应该检查该用户已有的收货地址是否达到上限，该功能也可以通过上述语句实现。

添加地址时是否需要验证用户uid是否有效？即使uid失效，添加一条无效的地址数据，对业务没有实质影响，因此可以考虑不验证。

**(b) 接口和抽象方法**

创建`cn.tedu.store.mapper.AddressMapper`接口，作为收货地址功能的持久层接口，并添加以下2个抽象方法：

    Integer saveAddress(Address address);

    Integer countByUid(Integer uid);

**(c) 配置映射**

在`src/main/resources/mappers`中创建`AddressMapper.xml`，从之前的`UserMapper.xml`中复制所有内容进新文件，删除所有子节点，修改根节点中的`namespace="cn.tedu.store.mapper.AddressMapper"`，之后添加以上2个抽象方法的映射：

    <!-- 新增收货地址 -->
    <!-- Integer saveAddress(Address address) -->
    <insert id="saveAddress"
        useGeneratedKeys="true"
        keyProperty="aid">
        INSERT INTO
            t_address(
            uid, name,
            province_name, province_code,
            city_name, city_code,
            area_name, area_code,
            zip, address,
            phone, tel,
            tag, is_default,
            created_user, created_time,
            modified_user, modified_time
        )VALUES(
            #{uid}, #{name},
            #{provinceName}, #{provinceCode},
            #{cityName}, #{cityCode},
            #{areaName}, #{areaCode},
            #{zip}, #{address},
            #{phone}, #{tel},
            #{tag}, #{isDefault},
            #{createdUser}, #{createdTime},
            #{modifiedUser}, #{modifiedTime}
        )
    </insert>

    <!-- 查询用户收货地址条数 -->
    <!-- Integer countByUid(Integer uid) -->
    <select id="countByUid"
        resultType="java.lang.Integer">
        SELECT
            count(*)
        FROM
            t_address
        WHERE
            uid=#{uid}
    </select>

完成之后，在`src/test/java`下开发`cn.tedu.store.mapper.AddressMapperTests`类，并添加上述2个抽象方法的测试方法：


### 29. 收货地址-新增-业务层

**(a) 规划异常**

插入操作，有`InsertException`。

一个用户的收货地址数量超过上限，对应的异常`cn.tedu.store.service.ex.AddressCountLimitException`，继承`ServiceException`

**(b) 接口和抽象方法**

创建`cn.tedu.store.service.IAddressService`接口，在其中添加抽象方法：

    void createAddress(Integer uid,String username, Address address)throws AddressCountLimitException, InsertException;


**(c) 实现接口**

创建`cn.tedu.store.service.impl.AddressServiceImpl`类，实现`IAddressService`接口，并实现其中的抽象方法：

    @Service
    public class AddressServiceImpl{

        @Autowired
        AddressMapper mapper;
        
        public void createAddress(Integer uid,String username, Address address)throws AddressCountLimitException, InsertException{
            // 根据uid查询收货地址条数
            // 条数是否达到上限 3
            // 是：AddressCountLimitException
    
            // 补全uid
            // 补全isDefault，根据上面查询到的收货地址条数进行判断
            // TODO 补全省市区数据：补充省市区名称
            // 创建当前时间对象
            // 补全4项日志数据
    
            // 执行添加操作
            saveAddress(address);
        }

    }

代码实现如下：

    @Override
    public void createAddress(Integer uid, String username, Address address)
            throws AddressCountLimitException, InsertException {
        // 根据uid查询收货地址条数
        Integer count=countByUid(uid);
        // 条数是否达到上限 3
        if(count >= ADDRESS_MAX_COUNT) {
            // 是：AddressCountLimitException
            throw new AddressCountLimitException("新增收货地址异常！最大收货地址条数为"+ADDRESS_MAX_COUNT);
        }

        // 补全uid
        address.setUid(uid);
        // 补全isDefault，根据上面查询到的收货地址条数进行判断
        int isDefault=count==0 ? 1 : 0;
        address.setIsDefault(isDefault);
        // TODO 补全省市区数据：补充省市区名称
        // 创建当前时间对象
        Date now =new Date();
        // 补全4项日志数据
        address.setCreatedUser(username);
        address.setCreatedTime(now);
        address.setModifiedUser(username);
        address.setModifiedTime(now);
        // 执行添加操作
        saveAddress(address);
    }

为了减少代码的重复性，提高可维护性，可以将持久层的2个抽象方法复制到业务层实现类中，提供方法的私有实现：

    private Integer countByUid(Integer uid){
        // 对参数的合理性进行判断    
        if(uid==null || uid<1){
            throw new IllegalArgumentException();
        }
        return mapper.countByUid(uid);
    }

    private void saveAddress(Address address){
        Integer row=mapper.saveAddress(address);
        if(row!=1){
            throw new InsertException("添加收货地址异常！请联系管理员");
        }
    }

完成后，在`src/test/java`中开发`cn.tedu.store.serivce.AddressServiceTests`类，并开发`createAddress()`的测试方法：

    @RunWith(SpringRunner.class)
    @SpringBootTest
    public class AddressServiceTests {
    
        @Autowired
        IAddressService service;
        
        @Test
        public void createAddress() {
            try {
                Address add=new Address();
                add.setName("小张同学");
                add.setAddress("程序员之家");
                service.createAddress(6, "管理员", add);
            }catch(ServiceException e) {
                System.err.println(e.getClass().getName());
                System.err.println(e.getMessage());
            }
        }
    }


### 30. 收货地址-新增-控制器层

**(a) 统一异常处理**

在`BaseController`中添加对`AddressCountLimitException`的处理逻辑

**(b) 设计请求**

设计“新增收货地址”的请求方式：

    请求路径：/addresses/create_address
    请求参数：Address address,HttpSession session
    请求方式：POST
    响应数据：JsonResult<Void>

**(c) 实现请求**

开发`cn.tedu.store.controller.AddressController`，继承`BaseController`：

    @RestController
    @RequestMapping("addresses")
    public class AddressController extends BaseController{
        
        @Autowired
        IAddressService service;

        @RequestMapping("create_address")
        public JsonResult<Void> createAddress(Address address,HttpSession session){
            // 获取用户的uid
            // 获取用户的username
            // 执行添加操作
            // 返回JsonResult对象
        }
    }

实现如下：

    @RequestMapping("create_address")
    public JsonResult<Void> createAddress(Address address,HttpSession session){
        // 获取用户的uid
        Integer uid=getUidFromSession(session);
        // 获取用户的username
        String username=getUsernameFromSession(session);
        // 执行添加操作
        service.createAddress(uid, username, address);
        // 返回JsonResult对象
        return new JsonResult<>(SUCCESS);
    }

在浏览器使用`http://localhost:8080/addresses/create_address?name=Tom`

测试通过之后，将`@RequstMapping`改为`@PostMapping`，再将地址条数的上限修改为15

### 31. 收货地址-新增-前端界面

### 32. 收货地址-页面中显示省市区列表

导入省市区数据到数据库中：
在mysql终端下，先使用`tedu_store`库，再使用以下命令：

    source sql文件在硬盘的路径;(可以直接将文件拖拽进终端，linux系统需要删除自动添加的首位单引号)

查看表结构：

    desc t_dict_district;

查看10条数据：

    select * from t_dict_district limit 10;

查看所有的省级数据：

    select * from t_dict_district where parent=86;

创建`cn.tedu.store.entity.District`，添加必要的属性：

    public class District{
        private Integer id;
        private String parent;
        private String code;
        private String name;
        
        //GET/SET/toString()
    }

接下来，基于`t_dict_district`开发省市区列表的查询业务，要求是根据父级编码，查询所有子级内容。


持久层-规划SQL语句

    select * from t_dict_district where parent=?

持久层-接口和抽象方法：创建`cn.tedu.store.mapper.DistrictMapper`，封装持久层操作的抽象方法：

    List<District> findByParent(String parent);

持久层-配置映射：在`src/main/resources/mappers`中创建`DistrictMapper.xml`，复制文件的根节点信息，修改`namespace`属性的值，添加上述抽象方法的映射：

    <!-- 根据parent查子级地区信息 -->
    <!-- District findByParent(String parent) -->
    <select id="findByParent" 
    resultType="cn.tedu.store.entity.District">
        SELECT
            id, parent,
            code, name
        FROM
            t_dict_district
        WHERE
            parent=#{parent}
    </select>

在`src/test/java`中开发`cn.tedu.store.mapper.DistrictMapperTests`类，并添加以上抽象方法的测试方法：
    
    @RunWith(SpringRunner.class)
    @SpringBootTest
    public class DistrictMapperTests{

        @Autowired
        DistrictMapper mapper;

        @Test
        public void findByParent(){
            String parent="86";
            List<District> list=mapper.findByParent(parent);
            for(District dist:list){
                System.err.println(dist);
            }
        }
    }

业务层-规划异常：

业务层-接口和抽象方法：

创建`cn.tedu.store.service.IDistrictService`接口，并添加抽象方法：

    List<District> listByParent(String parent);

业务层-实现抽象方法：

创建`cn.tedu.store.service.impl.DistrictServiceImpl`接口，并实现上述抽象方法：
    
    @Service
    public class DistrictServiceImpl implements IDistrictService{

        public List<District> listByParent(String parent){
            
        }

    }
