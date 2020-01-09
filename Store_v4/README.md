### 16. 用户-修改个人信息-持久层

**(a) 规划SQL语句**

用户打开“个人信息页面”后，应该马上发送一次请求，获取表单中的相关信息

    select phone, email, gender from t_user where uid=?

经过分析发现，在“个人信息页面”中需要显示用户的“用户名”，这里从数据库直接获取较为准确，因此查询中还应该包含`username`字段：

    select phone, email, gender from t_user where uid=?

用户修改个人信息，属于更新操作，语句大致是：

    update t_user set phone=?, email=?, gender=?, modified_user=?, modified_time=? where uid=?

**(b) 接口和抽象方法**

根据uid查询用户信息的方法之前已经存在，`findByUid()`，这里可以跳过

更新用户信息：

    Integer updateInfo(User user);

**(c) 配置映射**

针对`findByUid()`，之前已经在`UserMapper.xml`中配置过对应的映射，现在去修改映射，添加新的字段：

    <!-- 根据uid查用户信息 -->
    <!-- User findByUid(Integer uid) -->
    <select id="findByUid" 
        resultMap="UserEntityMap">
        select 
            username, password, salt,
            is_delete, phone,
            email, gender
        from 
            t_user 
        where 
            uid=#{uid};
    </select>

针对`updateInfo(User user)`，需要在`UserMapper.xml`中添加新的映射：


    <!-- 修改用户信息 -->
    <!-- Integer updateInfo(User user) -->
    <update id="updateInfo">
        update 
            t_user
        set 
            phone=#{phone},
            email=#{email},
            gender=#{gender},
            modified_user=#{modifiedUser}, 
            modified_time=#{modifiedTime} 
        where 
            uid=#{uid}
    </update>

在`UserMapperTests`中开发上述方法的测试方法，代码如下：

    @Test
    public void updateInfo() {
        User user=new User();
        user.setUid(12);
        user.setPhone("12345678");
        user.setEmail("1234@qq.com");
        user.setGender(1);
        user.setModifiedUser("管理员");
        user.setModifiedTime(new Date());
        Integer row=mapper.updateInfo(user);
        System.err.println("row="+row);
    }

### 17. 用户-修改个人信息-业务层

**(a) 规划异常**

更新操作一定会有`UpdateException`，同时，在更新之前应该检查用户数据是否存在，is_delete是否标记为已删除，对应的异常`UserNotFoundException`

**(b) 接口和抽象方法**

在`cn.tedu.store.service.IUserService`接口中添加以下2个抽象方法：

    void changeInfo(Integer uid, String username, User user) throws UserNotFoundException, UpdateException;

    User getByUid(Integer uid) throws UserNotFoundException;

**(c) 实现接口**

在`cn.tedu.store.service.impl.UserServiceImpl`中实现抽象方法：

    public void changeInfo(Integer uid, String username, User user)throws UserNotFoundException, UpdateException{
        // 根据uid查用户信息
        // 判断是否返回null
        // 是：UserNotFoundException

        // 判断is_delete是否为1
        // 是：UserNotFoundException

        // 将uid添加到user的uid中
        // 将username添加到user的modifiedUser中
        // 向user中添加更新时间
        // 更新用户数据
        // 获取受影响的行数
        // 判断是否不为1
        // 是：UpdateException
    }

    public User getByUid(Integer uid) throws UserNotFoundException{
        // 根据uid查询用户信息
        // 判断是否返回null
        // 是：UserNotFoundException

        // 判断is_delete是否为1
        // 是：UserNotFoundException
        
        // 将user中不相关的数据设为null
        // (可选)创建一个新的User对象，仅添加需要的属性
        // 返回用户信息(新的User对象)
    }

代码实现如下：

    @Override
    public void changeInfo(Integer uid, String username, User user) throws UserNotFoundException, UpdateException {
        // 根据uid查用户信息
        User result=mapper.findByUid(uid);
        // 判断是否返回null
        if(result==null) {
            // 是：UserNotFoundException
            throw new UserNotFoundException("更新用户信息异常！数据不存在");
        }

        // 判断is_delete是否为1
        if(result.getIsDelete().equals(1)) {
            // 是：UserNotFoundException
            throw new UserNotFoundException("更新用户信息异常！数据不存在");
        }

        // 将uid添加到user的uid中
        user.setUid(uid);
        // 将username添加到user的modifiedUser中
        user.setModifiedUser(username);
        // 向user中添加更新时间
        user.setModifiedTime(new Date());
        // 更新用户数据
        // 获取受影响的行数
        Integer row=mapper.updateInfo(user);
        // 判断是否不为1
        if(!row.equals(1)) {
            // 是：UpdateException
            throw new UpdateException("更新用户信息异常！请联系管理员");
        }
    }

    @Override
    public User getByUid(Integer uid) throws UserNotFoundException {
        // 根据uid查询用户信息
        User user=mapper.findByUid(uid);
        // 判断是否返回null
        if(user==null) {
            // 是：UserNotFoundException
            throw new UserNotFoundException("查询用户信息异常！数据不存在");
        }

        // 判断is_delete是否为1
        if(user.getIsDelete().equals(1)) {
            // 是：UserNotFoundException
            throw new UserNotFoundException("查询用户信息异常！数据不存在");
        }
        
        // 将user中不相关的数据设为null
        // (可选)创建一个新的User对象，仅添加需要的属性
        User info=new User();
        info.setUsername(user.getUsername());
        info.setPhone(user.getPhone());
        info.setEmail(user.getEmail());
        info.setGender(user.getGender());
        
        // 返回用户信息(新的User对象)
        return info;
    }

在`UserServiceTests`中开发以上2个方法的测试方法：

    @Test
    public void changeInfo() {
        try {
            User user=new User();
            user.setPhone("222");
            user.setEmail("555@qq.com");
            user.setGender(1);
            Integer uid=18;
            String modifiedUser="管理员123";
            service.changeInfo(uid, modifiedUser, user);
        }catch (Exception e) {
            System.err.println(e.getClass().getName());
            System.err.println(e.getMessage());
        }
    }
    
    @Test
    public void findByUid() {
        try {
            User user=service.getByUid(12);
            System.err.println(user);
        }catch (Exception e) {
            System.err.println(e.getClass().getName());
            System.err.println(e.getMessage());
        }
    }

### 18. 用户-修改个人信息-控制器层

**(a) 统一异常处理**

这里没有新的异常类型，不需要添加新的处理逻辑。

**(b) 设计请求**

设计“根据id查询用户信息”的请求：

    请求路径：/users/get_by_uid
    请求参数：HttpSession session
    请求方式：GET
    响应数据：JsonResult<User>

设计“更新用户信息”的请求：

    请求路径：/users/change_info
    请求参数：User user, HttpSession session
    请求方式：POST
    响应数据：JsonResult<Void>

**(c) 实现请求**

    @RequestMapping("change_info")
    public JsonResult<Void> changeInfo(User user,HttpSession session){
        // 从session中获取uid
        Integer uid=Integer.valueOf(session.getAttribute(SESSION_UID).toString());
        
        // 从session中获取username
        String username=session.getAttribute(SESSION_USERNAME).toString();

        // 调用service方法更新用户数据
        service.changeInfo(uid, username, user);
        
        return new JsonResult<>(SUCCESS);
    }

    @GetMapping("get_by_uid")
    public JsonResult<User> getByUid(HttpSession session){
        // 从session中获取uid
        Integer uid=Integer.valueOf(session.getAttribute(SESSION_UID).toString());

        // 调用service获取用户数据
        User user=service.getByUid(uid);

        // 返回用户数据
        return new JsonResult<User>(SUCCESS, user);
    }


测试：
    `http://localhost:8080/users/get_by_uid`，
    `http://localhost:8080/users/change_info?phone=123456&email=aaa@tedu.cn&gender=1`

测试完成后，应该将`changeInfo`方法上的`@RequestMapping`修改为`@PostMapping`


### 19. 用户-修改个人信息-前端界面

1. 页面上添加JS的内容，发送AJAX请求，获取用户信息，并基于jquery将获取到的用户信息回填到对应的input中

2. 为修改按钮添加点击事件，获取表单数据，提交到服务器

3. 注意：`<input type="radio" value='0'> 女`


### 启动springboot报端口占用的处理方案

1. 在命令行中输入`netstat -ano`查看当前本机端口占用情况
    1. linux对应的命令是`netstat -lnp|grep 8080`
2. 查找8080端口对应的进程id
3. 利用任务管理器关闭该进程id对应的进程
4. 或者可以在命令行通过命令关闭该进程`taskkill /im 3740 /f`
    1. linux对应的命令是`kill -9 3740`

### 20. 上传文件-SpringMVC

1. 准备一个SpringMVC项目
    1. Maven Project 跳过模板选择
        1. GroupId : cn.tedu.spring
        2. ArtifactId : springmvc-upload
        3. packing : war
    2. 让maven生成web.xml文件
    3. 关联targeted runtime
    4. 确认Servers项目是打开状态
    5. pom.xml中添加对springmvc的依赖（复制）
    6. 复制spring.xml到resources目录下
        1. 仅保留基本配置(扫包)(ViewResolver)
    7. 将之前的web.xml文件复制过来
        1. DispatcherServlet
        2. EncodingFilter

[](5.jpg)

2. 准备文件上传页面
    1. 文件上传表单的提交方式必须是POST
    2. `<form>`标签必须显示声明属性`enctype="multipart/form-data"`
    3. `<input type="file" name="file">`
        1. 类型必须是file
        2. 同时必须有name属性

3. 服务器端处理文件上传的前提
在pom.xml中添加对`commons-fileupload`的依赖：

    <dependency>
      <groupId>commons-fileupload</groupId>
      <artifactId>commons-fileupload</artifactId>
      <version>1.4</version>
    </dependency>

4. 在SpringMVC的配置文件`spring.xml`中配置一个Bean：

    `<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
    </bean>`

5. 开发`cn.tedu.spring.UploadController`处理用户的文件上传请求
    
        @Controller
        public class UploadController {
            @RequestMapping("upload.do")
            @ResponseBody
            public String upload(@RequestParam("file") MultipartFile file) throws IllegalStateException, IOException {
                // file代表的就是用户上传的文件
                System.out.println(file.getOriginalFilename());
                // 创建File对象，指定文件的目标保存路径
                File dest=new File("d:/1.jpg");
                
                // 将上传的文件保存到指定的路径
                file.transferTo(dest);
                
                return "OK";
            }
        }


[](7.jpg)

6. 文件上传常见问题：

    1. 文件名重复问题：
        1. 使用UUID+文件后缀
        2. UUID+"_"+原文件名+文件后缀
        3. 代码如下：


        // 文件名问题
        String oFilename=file.getOriginalFilename();
        int index=oFilename.lastIndexOf(".");
        // 文件的后缀
        String suffix="";
        // 防止文件没有后缀产生的问题
        if(index>=0) {
            suffix=oFilename.substring(index);
        }
        // 新文件名 UUID+suffix
        String newFilename=UUID.randomUUID().toString()+suffix;

    2. 文件夹的选择：
        1. 在项目的webapp路径下创建`upload`文件夹
        2. 将用户上传的文件保存在该文件下
        3. 这样，用户后续通过浏览器可以直接访问到他上传的文件
        4. 代码如下：


        // 获取ServletContext对象
        ServletContext sc=request.getServletContext();
        // 获取当前服务器在硬盘上的真实路径
        String dirPath=sc.getRealPath("upload");


[](6.jpg)

#### 小插曲，各种出bug
原因：Eclipse中的**Project**下有个**Build Auto...**需要默认勾选，可以自动编译class文件，如果不勾选，可以导致发布的项目中没有.class文件


### 复习
1. 项目的分析步骤：
    1. 涉及到哪些数据？
    2. 规划数据的开发顺序：优先基础性和简单的数据
    3. 针对一个数据，规划开发的具体功能
    4. 将这些功能按照“增 查 删 改”的顺序来排序
    5. 针对一个具体的业务，按照创建数据表 > 创建实体类 > 持久层 > 业务层 > 控制器层 > 前端页面
    6. 每一层开发完毕后，都必须进行及时的测试

2. 用户的注册
3. 用户的登录
4. 修改密码
    1. 修改密码
    2. 拦截器 interceptor
        1. 如何配置
            1. 开发一个拦截器的类，实现HandlerInterceptor接口
            2. 开发一个配置类，实现WebMvcConfigure
                1. 为拦截器配置黑名单和白名单
        2. 过滤器和拦截器的区别


