### 6. 用户-注册-控制器层

**(a) 统一异常处理**

创建`cn.tedu.store.controller.BaseController`，作为所有控制器类的父类，并在其中添加统一异常处理的方法。

    public abstract class BaseController{

        @ExceptionHandler(ServiceException.class)
        @ResponseBody
        public JsonResult handleException(Throwable e){
            JsonResult<void> jr=new JsonResult();
            
            if(e instanceof UsernameDuplicateException){
                jr.setState(1);
                jr.setMessage(e1.getMessage());
            }eles if(e instanceof InsertException){
                jr.setState(2);
                jr.setMessage(e1.getMessage());
            }

            return jr;
        }
    }


**(b) 设计请求**

设计“用户注册”的请求方式。

    请求路径：/users/reg
    请求参数：User user
    请求方式：POST
    响应数据：JsonResult<Void>

[](3.jpg)

**(c) 实现请求**

首先，开发`cn.tedu.store.util.JsonResult`类，封装服务器给浏览器的响应数据。


    public class JsonResult<T>{
        
        private Integer state;
        private String message;
        private T data;

        // get.set
    }

开发`cn.tedu.store.controller.UserController`类，响应用户的请求，继承`cn.tedu.store.controller.BaseController`

    @RestController
    @RequestMapping("users")
    public class UserController extends BaseController{

        @Autowired
        private IUserService service;

        @RequestMapping("reg")
        public JsonResult<Void> reg(User user){
            JsonResult<Void> jr=new JsonResult();
            service.reg(user);
            jr.setState(0);
            return jr;
        }
    } 

完成后，通过**启动类**启动当前项目，之后在浏览器上通过`http://localhost:8080/users/reg?username=boot&password=1234`来进行测试。


### 7. 用户-注册-前端界面

从`http://doc.tedu.cn`中下载**学子商城v2**的html内容，解压缩后，将5个文件夹拷贝到**store**项目的`java/main/resources`目录下的`static`文件夹中。

在`web/register.html`页面中添加JS的代码，基于jQuery向服务器发送AJAX请求，进行用户注册。

注意：JS的内容与页面表单组件的id应该匹配

测试：注册一个不存在的用户，查看是否能注册成功，成功后再次注册，查看是否正确提示错误信息。

### 8. 用户-登录-持久层

**(a) 规划SQL语句**

查询，使用用户名和密码查数据库

    select * from t_user where username=? and password=?

    select * from t_user where username=?;

需要查的数据：salt,password,is_delete

    select password, is_delete,salt from t_user where username=?;

如果用户登录成功，Session中应该保存用户的相关，前端页面也会需要相关数据：username,uid,avatar

    select 
        uid,username,
        password, is_delete,
        salt, avatar
    from 
        t_user 
    where 
        username=?;


**(b) 接口和抽象方法**

已经存在`cn.tedu.store.mapper.UserMapper`，并且其中存在`findByUsername(String username)`，对该方法进行完善即可。

**(c) 配置映射**

    <!-- 根据用户名查询用户 -->
    <!-- User findByUsername(String username) -->
    <select id="findByUsername" resultType="cn.tedu.store.entity.User">
        SELECT 
            uid, username,
            password, avatar,
            salt, is_delete as isDelete
        FROM 
            t_user 
        WHERE 
            username=#{username}
    </select>

完成后，再次使用`src/test/java`下的测试类进行测试。

保证现在数据库中有一条记录，用户名为"root"，原始密码为"1234"。

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

在`src/test/java`的`cn.tedu.store.service.UserServiceTests`中开发测试方法，测试登录效果



### 10. 用户-登录-控制器层

### 11. 用户-登录-前端界面

### 12. 用户-修改密码-持久层

### 13. 用户-修改密码-业务层

### 14. 用户-修改密码-控制器层

### 15. 用户-修改密码-前端界面

###  -----------------------------
每个功能实现的步骤：

### 1. 创建数据表

### 2. 创建实体类

### 3. 用户-XXX-持久层

**(a) 规划SQL语句**

**(b) 接口和抽象方法**

**(c) 配置映射**

### 2. 用户-XXX-业务层

**(a) 规划异常**

**(b) 接口和抽象方法**

**(c) 实现接口**

### 3. 用户-XXX-控制器层

**(a) 统一异常处理**

**(b) 设计请求**

**(c) 实现请求**

### 4. 用户-XXX-前端界面




# 密码加密
[](1.jpg)
1. 用户密码在数据库中以明文形式存储，存在很大的安全隐患
2. 密码加密的方式：
    1. 使用加密算法：
        1. 123456 -> 每个数字加1 -> 234567
        2. 加密算法主要为了保证数据传输阶段的安全，要求必须可以通过密文反推回明文
        3. 公司内部员工，在泄漏密文的同时，也可能泄漏加密算法和使用的参数，不法分子还是可以通过密文反推回明文
    2. 消息摘要算法：
        1. 消息一致的情况下，使用相同算法，生成的摘要一定一致
        2. 不管消息有多大，生成的摘要都是固定长度的
        3. 消息不一致的情况下，生成的摘要重复的概率非常低
            1. 以一个MD5算法为例，生成的摘要128位定长的2进制数据
        4. 虽然算法公开，但是只能通过消息生成摘要，但是无法通过摘要反推出消息，因为在计算过程中存在精度的缺失
        5. 常用的消息摘要算法：
            1. SHA：SHA1 SHA256 SHA384 SHA512
            2. MD: MD5

3. 关于消息摘要算法的安全性
    1. 是否可以通过密文反推回明文
        1. 科学的推算：研究摘要碰撞的概率
        2. 利用“消息一致，摘要一定一致”的特点，在数据库中记录消息和摘要的对应关系
            1. 1位  70种可能    70个键值对
            2. 2位  70*70       4900
            3. 3位  5000*70     350000
            4. 4位  350000*70  
            5. 以此类推，位数越多，需要穷举的键值对越多
            
    2. 如何提高密码的安全性：
        1. 要求用户使用强度更高的密码：P@ssw0rd，不容易被穷举到
        2. 多次加密
        3. 使用长度更长的消息摘要算法
        4. 加盐
            1. 普通字符串
            2. 加时间戳
            3. 加UUID
        5. 以上方法综合使用

4. 利用代码实现数据摘要
    1. 利用DigestUtils的API实现，主要是MD5数据摘要算法
    2. 如果想要在编程中使用其他的数据摘要算法：
        1. 利用Java原生的工具类：MessageDigest.getInstance("md5");
        2. 利用commons-codec.jar:DigestUtils.进行各类消息摘要算法的计算

[](2.jpg)


# 复习
1. 如何分析一个项目，将项目拆解成可实现的步骤，来进行迭代的开发
2. 分析流程
    1. 数据：该项目涉及哪些种类的数据
    2. 确定数据相关功能的开发顺序：
        1. 先开发基础性数据和简单数据相关的功能
    3. 分析一个数据“用户”相关的功能：注册、登录、修改密码、修改个人资料、上传头像
    4. 确定功能的开发顺序，按“增 查 删 改”
    5. 针对一个具体功能，例如“注册”，按照创建表>实体类>持久层>业务层>控制器层>前端界面

3. 创建表：
4. 创建实体类：
    1. BaseEntity：所有实体类的父类，封装了创建用户，创建时间，最后修改用户，最后修改时间，实现Serializable接口
    2. User
5. 持久层开发
    1. **(a) 规划SQL语句**
    2. **(b) 接口和抽象方法**
    3. **(c) 配置映射**
        1. 测试
6. 业务层开发
    1. **(a) 规划异常**
    2. **(b) 接口和抽象方法**
    3. **(c) 实现接口**
        1. 测试
7. 密码加密
