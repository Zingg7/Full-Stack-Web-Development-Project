### 收货地址-页面中显示省市区列表

导入省市区数据到数据库中：
在mysql终端下，先使用`tedu_store`库，再使用以下命令：

	source sql文件在硬盘的路径;(可以直接将文件拖拽进终端，linux系统需要删除自动添加的首位单引号)

查看表结构：

	desc t_dict_district;

查看10条数据：

	select * from t_dict_district limit 10;

查看所有的省级数据：

	select * from t_dict_district where parent=86;

创建`entity.District`，添加必要的属性：

	public class District{
		private Integer id;
		private String parent;
		private String code;
		private String name;
		
		//GET/SET/toString()
	}

接下来，基于`t_dict_district`开发省市区列表的查询业务，要求是根据父级编码，查询所有子级内容。

### 32. 收货地址-页面中显示省市区列表-持久层

**(a) 规划SQL**

	select * from t_dict_district where parent=?

** (b) 接口和抽象方法 **

创建`mapper.DistrictMapper`，封装持久层操作的抽象方法：

	List<District> findByParent(String parent);

** (c) 配置映射 **

在`src/main/resources/mappers`中创建`DistrictMapper.xml`，复制文件的根节点信息，修改`namespace`属性的值，添加上述抽象方法的映射：

	<!-- 根据parent查子级地区信息 -->
	<!-- District findByParent(String parent) -->
	<select id="findByParent" resultType="entity.District">
		SELECT
			id, parent,
			code, name
		FROM
			t_dict_district
		WHERE
			parent=#{parent}
	</select>

在`src/test/java`中开发`mapper.DistrictMapperTests`类，并添加以上抽象方法的测试方法：
	
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

### 33. 收货地址-页面中显示省市区列表-业务层

** (a) 规划异常 **

** (b) 接口和抽象方法 **

创建`service.IDistrictService`接口，并添加抽象方法：

	List<District> listByParent(String parent);

** (c) 实现抽象方法** 

创建`service.impl.DistrictServiceImpl`接口，并实现上述抽象方法：
	
	@Service
	public class DistrictServiceImpl implements IDistrictService{

		@Autowired
		DistrictMapper mapper;

		public List<District> listByParent(String parent){
			return findByParent(parent);
		}

		private List<District> findByParent(String parent){
			return mapper.findByParent(parent);
		}
	}


### 34. 收货地址-页面中显示省市区列表-控制器层

** (a) 设计请求 **

	请求路径：/districts/
	请求参数：String parent
	请求方式：Get
	响应数据：JsonResult<List<District>>
	是否拦截：否，在白名单添加请求路径

** (b) 实现请求 **

开发`controller.DistrictController`类，接收用户请求：

	@RestController
	@RequestMapping("districts")
	public class DistrictController extends BaseController{
	
		@Autowired
		IDistrictService service;
		
		@GetMapping("/")
		public JsonResult<List<District>> listByParent(String parent){
			List<District> list = service.listByParent(parent);
			return new JsonResult<List<District>>(SUCCESS,list);
		}
	}

在`confit.InterceptorConfigurer`类中添加白名单配置`patterns.add("/districts/**");`

完成后，在浏览器地址栏通过`http://localhost:8080/districts/?parent=86`进行测试

### 35. 收货地址-页面中显示省市区列表-前端界面

在所有的下拉列表中添加一个默认项：
实现思路：通过jquery的`append()`方法，将一个`<option value='0'>---- 请选择器 ----</option>`拼接到`<select>`的内部。注意，应该先为每个`<select>`添加对应的id，分别是`province,city,area`。

页面就绪后自动添加省级列表：
页面就绪后，自动向服务器发送一个AJAX请求，请求省级数据列表，遍历收到的数据，每一条数据生成一个`<option>`，添加到`<select id="province">`组件内部，以上逻辑可以单独封装成一个方法`function appendList()`：

	// 页面就绪事件
	$(function(){
		var defaultOption=
			"<option value='0'>---- 请选择 ----</option>";
		
		// 为3个<select>添加默认选项
		$("#province").append(defaultOption);
		$("#city").append(defaultOption);
		$("#area").append(defaultOption);
		
		// 自动生成省级列表
		appendList(86,"province");
	});
	
	// parent是父级编号，sid是select标签的id属性的值
	function appendList(parent,sid){
		// 发送ajax请求
		$.ajax({
			"url":"/districts/",
			"data":"parent="+parent,
			"type":"get",
			"dataType":"json",
			"success":function(json) {
        		// 获取列表的数组
        		var list = json.data;
        		// 遍历数组
        		for(var i=0;i<list.length;i++){
            		// 每一条记录生成一个option
            		var option="<option value='"+list[i].code+"'>"+list[i].name+"</option>";	
            		// 将option添加到select内部
            		$("#"+sid).append(option);
        		}
			}
		});
	}


省级列表改变时，自动添加市级列表：
为省级`<select>`添加一个`onchange`事件，当省级内容改变时，自动发送一个AJAX请求，获取当前选中的省对应的市级目录，并显示在市级`<select>`中。


### 33. 收货地址-增加-业务层(补):

在业务层方法中，需要补充省市区信息的名称，`address`对象中已经封装了省市区对应的编号，现在需要根据该地区的编号(code)查询对应的名称(name)。

应该在`mapper.DistrictMapper`中添加抽象方法：

	District findByCode(String code);

应该在`src/main/resources/mappers`中的`DistrictMapper.xml`中添加以上抽象方法的映射：

	<!-- 根据code查询地区信息 -->
	<!-- District findByCode(String code) -->
	<select id="findByCode" resultType="entity.District">
		SELECT
			id, parent,
			code, name
		FROM
			t_dict_district
		WHERE
			code=#{code}
	</select>

添加对应的测试方法：

	@Test
	public void findByCode(){
		String code="110000";
		District dist=mapper.findByCode(code);
		System.err.println(dist);
	}

为了实现解耦，增加代码的可维护性，规定在一个业务层中，只能调用与之相对应的持久层的方法。但是业务层可以调用其他业务层的方法。

因此，需要在`service.IDistrictService`中添加抽象方法：

	District getByCode(String code);

在`service.impl.DistrictServiceImpl`中实现该抽象方法：

	public District getByCode(String code){
		return findByCode(code);
	}

	private District findByCode(String code){
		return mapper.findByCode(code);
	}


在`AddressServiceImpl`中需要添加`@Autowired IDistrictService service;`，在方法中调用`service`的`getByCode(String code)`查询地区信息。为了简化开发，可以在`AddressServiceImpl`中声明一个新的方法：


	public String getNameByCode(String code){
		District dist=service.getByCode(code);
		return dist==null?"":dist.getName();
	}

然后补全`AddressServiceImpl`中`createAddress()`中的代码：

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
		// 补全省市区数据：补充省市区名称
		String provinceName=getNameByCode(address.getProvinceCode().toString());
		String cityName=getNameByCode(address.getCityCode().toString());
		String areaName=getNameByCode(address.getAreaCode().toString());
		address.setProvinceName(provinceName);
		address.setCityName(cityName);
		address.setAreaName(areaName);
		
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

完成后，在添加地址表单中选择具体的省市区信息，添加一个地址，查看数据库中的省市区名称和省市区编码是否正确添加。

### 36. 收货地址-地址列表-持久层

**(a) 规划SQL**

	select * from t_address where uid=? order by is_default desc, modified_time desc;

**(b) 接口和抽象方法**

在`AddressMapper.java`接口中添加对应的抽象方法：

	List<Address> findByUid(Integer uid);

**(c) 配置映射**

在`AddressMapper.xml`添加一个`<resultMap>`节点，配置`address`表字段与`Address`实体类的映射关系：

在`AddressMapper.xml`中配置上述方法的映射：

	<select id="findByUid" resultMap="AddressEntityMap">
		select
			*
		from
			t_address
		where 
			uid=#{uid} 
		order by 
			is_default desc, 
			modified_time desc;
	</select>

在`AddressMapperTests`中添加测试方法：

	@Test
	public void findByUid() {
		List<Address> list=mapper.findByUid(10);
		for(Address address:list) {
			System.err.println(address);
		}
	}

### 37. 收货地址-地址列表-业务层

**(a) 规划异常**

无

**(b) 接口和抽象方法**

在`IAddressService`中添加以下抽象方法：

	List<Address> listByUid(Integer uid);

**(c) 实现抽象方法**

在`AddressServiceImpl`中添加持久层接口中定义的抽象方法的私有实现：

	private List<Address> findByUid(Integer uid){
		List<Address> list=mapper.findByUid(uid);
		for(Address addr:list){
			addr.setZip(null);
			addr.setTel(null);
			// 将4项日志数据设为null
		}
		return list;
	}

在`AddressServiceImpl`中添加业务层抽象方法的实现：

	public List<Address> listByUid(Integer uid){
		return findByUid(uid);
	}

在`AddressServiceTests`中开发以上方法的测试方法：

	@Test
	public void listByUid() {
		List<Address> list=service.listByUid(10);
		for(Address addr:list) {
			System.err.println(addr);
		}
	}


### 38. 收货地址-地址列表-控制器层

**(a) 统一异常处理**

无

**(b) 设计请求**

	请求路径：/addresses/list
	请求参数：HttpSession session
	请求方式：Get
	响应数据：JsonResult<List<Address>>

**(c) 实现请求**

在`AddressController`中添加方法，处理请求：

	@GetMapping("list")
	public JsonResult<List<Address>> listByUid(HttpSession session){
		Integer uid=getUidFromSession(session);
		List<Address> data=service.listByUid(uid);
		return new JsonResult<>(SUCCESS,data);
	}

写完后，在浏览器通过`localhost:8080/addresses/list`，前提是先登录


### 39. 收货地址-地址列表-前端界面








### 为什么要在业务层中添加持久层的抽象方法的私有实现
1. 一个持久层的抽象方法可能会被业务层在多个方法中重复调用
2. 在业务层调用持久层方法之前，应该对传入的参数进行合理性验证，如参数类型，是否为Null，参数长度等等
3. 如果在每个业务层方法中都添加验证的逻辑，则代码重复，可以将这部分验证逻辑放在业务层中的持久层方法实现中去
4. 同样的，在“增删改”操作后，都需要对结果进行验证，这部分验证逻辑，也可以放在对应的持久层方法实现中