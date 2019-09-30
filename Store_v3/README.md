### 9. 用户-登录-业务层

**(a) 规划异常**

"用户名不存在","密码错误","数据被标记为已删除"，因此应该创建一下异常类：

    cn.tedu.store.service.ex.UserNotFoundException

    cn.tedu.store.service.ex.PasswordNotMatchException

查询操作，从本质上来讲，查不到不算异常，不会单独设计一个`SelectException`


**(b) 接口和抽象方法**

在`IUserService`下添加一个新的抽象方法：

    User login(String username,String password) throws UserNotFoundException, PasswordNotMatchException;


**(c) 实现接口**

在`UserServiceImpl`中添加对以上方法的实现：
    
    public User login(String username,String password) throws UserNotFoundException, PasswordNotMatchException{
        // 获取User中的username
        // 调用持久层的findByUsername(username) -> User
        // 判断User是否为null
        // 是：抛出 UserNotFoundException

        // 获取user中的isDelete
        // 判断isDelete是否为1
        // 是：抛出 UserNotFoundException

        // 获取查询到的盐值
        // 对用户传入的密码进行加密
        // 获取查询到的密码
        // 判断两个密码是否不一致
        // 是：抛出 PasswordNotMatchException
    
        // 将盐值设为null
        // 将密码设为null
        // 将isDelete设为null
        // 返回user 
    }

在`src/test/java`的`cn.tedu.store.service.UserServiceTests`中开发测试方法，测试登录效果，具体代码如下：

    @Test
    public void login() {
        try {
            String username="root";
            String password="1234x";
            User user=service.login(username, password);
            System.err.println(user);
        } catch (Exception e) {
            System.err.println(e.getClass().getName());
            System.err.println(e.getMessage());
        }
    }


### 10. 用户-登录-控制器层

**(a) 统一异常处理**

在`cn.tedu.store.controller.BaseController`中添加对`UserNotFoundException`和`PasswordNotMatchException`的处理。

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public JsonResult<Void> handlerException(Throwable e){
        // 根据不同异常的类型提供不同的处理方式
        // 现在的处理方式是根据不同的类型，返回不同的状态码
        JsonResult<Void> jr=new JsonResult<>();
        
        if(e instanceof UsernameDuplicateException) {
            jr.setState(ERROR_USERNAME_DUPLICATE);
            jr.setMessage(e.getMessage());
        }else if(e instanceof UserNotFoundException) {
            jr.setState(31);
            jr.setMessage(e.getMessage());
        }else if(e instanceof PasswordNotMatchFoundException) {
            jr.setState(32);
            jr.setMessage(e.getMessage());
        }else if(e instanceof InsertException) {
            jr.setState(40);
            jr.setMessage(e.getMessage());
        }
        
        return jr;
    }


**(b) 设计请求**

设计“用户登录”的请求方式：

    请求路径：/users/login
    请求参数：String username,String password, HttpSession session
    请求方式：POST
    响应数据：JsonResult<User>


**(c) 实现请求**

在`cn.tedu.store.controller.UserController`中添加`login`方法，处理用户的登录请求：

    @RequestMapping("login")
    public JsonResult<User> login(String username,String password, HttpSession session){

        // 调用service的login()进行登录
        User user=service.login(username,password);
        
        // 向session中添加uid
        session.setAttribute("uid",user.getUid());

        // 向session中添加username
        session.setAttribute("username",user.getUsername());

        JsonResult<User> jr=new JsonResult();
        jr.setData(user);
        jr.setState(SUCCESS);

        return jr;
    }

[](4.JPG)

在浏览器使用`http://localhost:8080/users/login?username=root&password=1234`进行测试。

测试通过后，将方法前的`@RequestMapping`改为`@PostMapping`

### 11. 用户-登录-前端界面

注意，需要将“登录”按钮的类型从"submit"改为"button"

### 12. 用户-修改密码-持久层

**(a) 规划SQL语句**

“修改密码”是一个更新操作，大致的语句应该是：

    update 
        t_user
    set 
        password=?,
        modified_user=?, 
        modified_time=? 
    where 
        uid=?

在修改之前，应该先验证用户输入的“原密码”是否正确，但是不应该是这样：

    update t_user set password=? where uid=? and password=?

应该尽量不在SQL语句中去验证密码：

    1. mysql数据库大小写不敏感，而密码是敏感的，验证可能存在问题
    2. 需要“在修改密码前验证原密码”属于业务层的需求，应该优先考虑在业务层来实现

需要提供一个根据uid查询用户密码的方法：

    select password, salt, is_delete from t_user where uid=?;


**(b) 接口和抽象方法**

在`cn.tedu.store.mapper.UserMapper`接口中添加以下2个抽象方法：

    User findByUid(Integer uid);

    Integer updatePassword(
        @Param("uid") Integer uid,
        @Param("password") String password,
        @Param("modifiedUser") String modifiedUser,
        @Param("modifiedTime") Date modifiedTime);

**(c) 配置映射**

可以在`UserMapper.xml`中的根节点下配置`<resultMapp>`节点，指定数据库表的**列名(column)**和实体类的**属性名(property)**的对应关系：

    <resultMap id="UserEntityMap" type="cn.tedu.store.entity.User" >
        <id column="uid" property="uid" />
        <result column="username" property="username" />
        <result column="password" property="password" />
        <result column="salt" property="salt" />
        <result column="is_delete" property="isDelete" />
        <result column="phone" property="phone" />
        <result column="email" property="email" />    
        <result column="gender" property="gender" />    
        <result column="avatar" property="avatar" />    
        <result column="created_user" property="createdUser" />    
        <result column="created_time" property="createdTime" />    
        <result column="modified_user" property="modifiedUser" />    
        <result column="modified_time" property="modifiedTime" />    
    </resultMap>

并在对应的`<select>`节点使用`resultMap`属性替代`resultType`属性，则在SQL语句中无需再使用**as**来指定别名。

在`java/main/resources`下的`mappers`中的`UserMapper.xml`中添加以上2个抽象方法的映射：

    <!-- 根据uid查用户信息 -->
    <!-- User findByUid(Integer uid) -->
    <select id="findByUid" 
        resultType="cn.tedu.store.entity.User">
        select 
            password, salt,
            is_delete
        from 
            t_user 
        where 
            uid=#{uid};
    </select>

    <!-- 更新密码 -->
    <!-- Integer updatePassword(
        @Param("uid") Integer uid,
        @Param("password") String password,
        @Param("modifiedUser") String modifiedUser,
        @Param("modifiedTime") Date modifiedTime)  -->
    <update id="updatePassword">
        update 
            t_user
        set 
            password=#{password},
            modified_user=#{modifiedUser}, 
            modified_time=#{modifiedTime} 
        where 
            uid=#{uid}
    </update>

在`src/test/java`中的`cn.tedu.store.mapper.UserMapperTests`中开发测试方法，测试以上2个方法：

    @Test
    public void findByUid() {
        User user=mapper.findByUid(11);
        System.err.println(user);
    }

    @Test
    public void updatePassword() {
        Integer uid=9;
        String password="6789";
        String modifiedUser="炒鸡管理员";
        Date modifiedTime=new Date();
        Integer row=mapper.updatePassword(
                uid, password, modifiedUser, modifiedTime);
        System.err.println("row="+row);
    }

注意：这里的测试会对用户数据造成破坏，因此可以新注册一个用户来测试，不要对**root**用户进行测试。

### 13. 用户-修改密码-业务层

**(a) 规划异常**

更新操作，应该有一个异常来代表那些原因不好描述的更新异常`cn.tedu.store.service.ex.UpdateException`

在验证原始密码是否正确之前，应该验证该数据是否有效，is_delete不为1，如果为1，可以使用`UserNotFoundException`

原始密码错误，则不能更新新密码，可以使用`PasswordNotMatchException`


**(b) 接口和抽象方法**

在`IUserService`中添加以下抽象方法：

    void changePassword (Integer uid, String oldPassword, String newPassword, String modifiedUser) throws UserNotFoundException, PasswordNotMatchException, UpdateException;

**(c) 实现接口**

[](5.JPG)

在`UserServiceImpl`中实现上面的抽象方法

    public void changePassword (Integer uid, String oldPassword, String newPassword, String modifiedUser)throws UserNotFoundException, PasswordNotMatchException, UpdateException{
        // 使用uid查询用户数据
        // 判断返回结果是否为null
        // 是：UserNotFoundException

        // 判断isDelete是否为1
        // 是：UserNotFoundException

        // 获取查询到的盐值
        // 对旧密码进行加密
        // 比较加密结果和查询到的用户密码是否不一致
        // 是：PasswordNotMatchException

        // 对新密码进行加密
        // 调用updatePassword()更新密码
        // 判断受影响的行数是否不为1
        // 是：UpdateException
        
    }

[](6.JPG)

实现代码如下：

    @Override
    public void changePassword(Integer uid, String oldPassword, String newPassword, String modifiedUser) throws UserNotFoundException, PasswordNotMatchException, UpdateException {
        // 使用uid查询用户数据
        User user=mapper.findByUid(uid);
        // 判断返回结果是否为null
        if(user ==null) {
            // 是：UserNotFoundException
            throw new UserNotFoundException("修改密码异常！用户数据不存在");
        }

        // 判断isDelete是否为1
        if(user.getIsDelete().equals(1)) {
            // 是：UserNotFoundException
            throw new UserNotFoundException("修改密码异常！用户数据不存在");    
        }

        // 获取查询到的盐值
        String salt=user.getSalt();
        // 对旧密码进行加密
        String oldMd5Password=getMd5Password(oldPassword, salt);
        // 比较加密结果和查询到的用户密码是否不一致
        if(!oldMd5Password.equals(user.getPassword())) {
            // 是：PasswordNotMatchException
            throw new PasswordNotMatchException("修改密码异常！原始密码错误");
        }

        // 对新密码进行加密
        String newMd5Password=getMd5Password(newPassword, salt);
        // 调用updatePassword()更新密码
        Integer row=mapper.updatePassword(uid, newMd5Password, modifiedUser, new Date());
        // 判断受影响的行数是否不为1
        if(!row.equals(1)) {
            // 是：UpdateException
            throw new UpdateException("修改密码异常！请联系管理员");
        }
        
    }


在`src/test/java`的`cn.tedu.store.service.UserServiceTests`中开发测试方法，测试修改密码的效果，具体代码如下：

    @Test
    public void changePassword() {
        try {
            Integer uid=16;
            String oldPassword="1234";
            String newPassword="5678";
            String modifiedUser="管理员";
            service.changePassword(uid, oldPassword, newPassword, modifiedUser);
        }catch (Exception e) {
            System.err.println(e.getClass().getName());
            System.err.println(e.getMessage());
        }
    }

刚刚的异常使用Maven的UpdateProject后自动解决



### 14. 用户-修改密码-控制器层

**(a) 统一异常处理**

在`cn.tedu.store.controller.BaseController`中添加对`UpdateException`的处理。

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public JsonResult<Void> handlerException(Throwable e){
        // 根据不同异常的类型提供不同的处理方式
        // 现在的处理方式是根据不同的类型，返回不同的状态码
        JsonResult<Void> jr=new JsonResult<>(e.getMessage());
        
        if(e instanceof UsernameDuplicateException) {
            jr.setState(ERROR_USERNAME_DUPLICATE);
        }else if(e instanceof UserNotFoundException) {
            jr.setState(31);
        }else if(e instanceof PasswordNotMatchFoundException) {
            jr.setState(32);
        }else if(e instanceof InsertException) {
            jr.setState(40);
        }else if(e instanceof UpdateException) {
            jr.setState(41);
        }
        
        return jr;
    }

**(b) 设计请求**

设计“修改密码”的请求方式

    请求路径：/users/change_password
    请求参数：@RequestParam("old_password") String oldPassword, @RequestParam("new_password") String newPassword,HttpSession session
    请求方式：POST
    响应数据：JsonResult<Void>

**(c) 实现请求**

在`cn.tedu.store.controller.UserController`中添加对应的方法`changePassword()`来处理修改密码的请求：

    @RequestMapping("change_password")
    public JsonResult<Void> changePassword(@RequestParam("old_password") String oldPassword, @RequestParam("new_password") String newPassword,HttpSession session){
        
        // 从session中获取uid
        Integer uid=Integer.valueOf(session.getAttribute("uid").toString());
        
        // 从session中获取username
        String username=session.getAttribute("username").toString();

        service.changePassword(uid,oldPassword,newPassword,username);
        return new JsonResult<Void>(SUCCESS);
    }

测试之前，必须先登录**root**用户

完成后，可以在浏览器上使用`localhost:8080/users/change_password?old_password=1234&new_password=5678`;



“修改密码”是用户登录之后才能执行的操作，在修改之前，应该检查用户的登录状态。

可以使用过滤器或拦截器实现相关功能。

[](7.JPG)

在这里使用拦截器`cn.tedu.store.interceptor.LoginInterceptor`，封装的是拦截请求后执行的逻辑：

    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session=request.getSession();
        
        if(session.getAttribute("uid")==null) {
            // 重定向到登录页面
            response.sendRedirect("http://localhost:8080/web/login.html");
            return false;
        }
        
        // 放行
        return true;
    }

为了让拦截器生效，需要开发一个拦截器的配置类`cn.tedu.store.config.InterceptorConfigurer`，应该实现`WebMvcConfigurer`,重写`addInterceptors`，在其中注册我们自己开发的拦截器：

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        HandlerInterceptor loginInterceptor=new LoginInterceptor();
        
        InterceptorRegistration ir=registry.addInterceptor(loginInterceptor);
        // 黑名单
        ir.addPathPatterns("/**");
        
        // 不拦 注册/登录 静态资源
        List<String> patterns=new ArrayList<String>();
        // 静态资源
        patterns.add("/bootstrap3/**");
        patterns.add("/css/**");
        patterns.add("/images/**");
        patterns.add("/js/**");
        // 注册和登录页面
        patterns.add("/web/login.html");
        patterns.add("/web/register.html");
        // 注册和登录控制器
        patterns.add("/users/login");
        patterns.add("/users/reg");
        
        // 白名单
        ir.excludePathPatterns(patterns);
    
    }

该配置类如果想要生效，必须在类前添加`@Configuration`

拦截器生效后，用户未登录时，请求`/web/password.html`，会被自动重定向到登录页面


### 15. 用户-修改密码-前端界面





### Spring返回的JSON数据处理

1. 在Spring默认生成的JSON数据中，有一些值为null的属性，我们希望它不要添加到JSON字符串中
2. 解决方案：使用 @JsonInclude

    1. 添加在类前
        1. @JsonInclude(value=Include.ALWAYS)代表将该类中所有的属性添加到JSON中，不论其值是否为NULL
        2. @JsonInclude(value=Include.NON_NULL)代表如果一个属性的值为NULL，在生成的JSON字符串中不添加该属性

    2. 添加在属性前
        1. 可以对属性进行更精准的控制
        2. 如果该属性的值为NULL，并设置为NON_NULL，那么不会被添加到JSON中

    3. 可以在application.properties中添加一行配置：`spring.jackson.default-property-inclusion=NON_NULL`，默认采用NON_NULL的策略
