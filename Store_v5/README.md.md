### 20. 上传文件-SpringMVC

#### 20.1 准备一个SpringMVC项目
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

#### 20.2 准备文件上传页面
    1. 文件上传表单的提交方式必须是`POST`
    2. `<form>`标签必须显示声明属性`enctype="multipart/form-data"`
    3. `<input type="file" name="file">`
        1. 类型必须是file
        2. 同时必须有name属性

#### 20.3 服务器端处理文件上传的前提
在pom.xml中添加对`commons-fileupload`的依赖：

    <dependency>
      <groupId>commons-fileupload</groupId>
      <artifactId>commons-fileupload</artifactId>
      <version>1.4</version>
    </dependency>

#### 20.4 在SpringMVC的配置文件`spring.xml`中配置一个Bean：

    `<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
    </bean>`

#### 20.5 开发`cn.tedu.spring.UploadController`处理用户的文件上传请求
    
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


#### 20.6 文件上传常见问题：

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

#### 20.7 MultipartFile的常用API：

- `String getOriginalFilename()`：返回原始的文件名，即上传的文件在客户端上的名字

- `boolean isEmpty()`：判断上传的文件是否为空，如果用户在表单中没有选择文件，或者选择的文件长度为0，该方法返回`true`

- `long getSize()`：获取上传文件的大小，以字节为单位

- `String getContentType()`：返回上传文件的类型MIME，需要注意该方法基于文件的扩展名来判断文件类型，可能不准确

- `InputStream getInputStream()`：获取上传文件的输入流对象，如果需要实现自定义缓冲操作，则使用该方法。注意，该方法与`transferTo(File dest)`互斥，只能调用1个

- `void transferTo(File dest)`：将上传的文件保存到目标路径下


#### 20.8 MultipartResolver配置的属性：

- `maxUploadSize`：配置允许上传的文件的大小上限，指一次上传的所有文件的总大小，是整个项目的统一配置，一般写的大一些，在每个控制器中再使用`getSize()`进行精细化的配置

- `maxUploadSizePerFile`：配置允许上传的每个文件的大小上限。如果配置为1MB，则每个文件不能超过1MB，如果上传2个1MB的文件，是可以的

[](1.jpg)

### 21. 用户-上传头像-持久层

**(a) 规划SQL语句**

“上传头像”是将头像图片在服务器上的路径添加到数据库中，实际是更新用户信息，添加avatar的信息：

    update t_user set avatar=?, modified_user=?, modified_time=? where uid=?

更新操作前，应该检查该数据是否存在，相关的操作之前已经实现了，这里不用再次实现

**(b) 接口和抽象方法**

在`UserMapper.java`中添加抽象方法：

    Integer updateAvatar(
            @Param("uid") Integer uid,
            @Param("avatar") String avatar,
            @Param("modifiedUser") String modifiedUser,
            @Param("modifiedTime") Date modifiedTime);

**(c) 配置映射**

    <!-- 更新头像 -->
    <!--     Integer updateAvatar(
            @Param("uid") Integer uid,
            @Param("avatar") String avatar,
            @Param("modifiedUser") String modifiedUser,
            @Param("modifiedTime") Date modifiedTime);  -->
    <update id="updateAvatar">
        update 
            t_user
        set 
            avatar=#{avatar},
            modified_user=#{modifiedUser}, 
            modified_time=#{modifiedTime} 
        where 
            uid=#{uid}
    </update>

在`UserMapperTests`中开发对应的测试方法：

    @Test
    public void updateAvatar() {
        Integer uid=12;
        String avatar="/abc/1.jpg";
        String modifiedUser="管理员";
        Date modifiedTime=new Date();
        Integer row=mapper.updateAvatar(uid, avatar, modifiedUser, modifiedTime);
        System.err.println("row="+row);
    }


### 22. 用户-上传头像-业务层

**(a) 规划异常**

更新操作需要`UpdateException`，更新前查询数据有效，需要`UserNotFoundException`。不需要再添加新的异常

**(b) 接口和抽象方法**

    void changeAvatar (
        Integer uid,
        String avatar,
        String modifiedUser)
            throws     
                UserNotFoundException, UpdateException;

**(c) 实现接口**

    public void changeAvatar (
        Integer uid,
        String avatar,
        String modifiedUser)
            throws     
                UserNotFoundException, UpdateException{
        // 使用uid查询用户数据
        // 判断结果是否为null：UserNotFoundException

        // 判断isDelete是否为1
        // 是：UserNotFoundException

        // 更新用户头像
        // 判断受影响的行数是否不为1
        // 是：UpdateException
    }

实现如下：

    @Override
    public void changeAvatar(Integer uid, String avatar, String modifiedUser)
            throws UserNotFoundException, UpdateException {
        // 使用uid查询用户数据
        User user=mapper.findByUid(uid);
        // 判断返回结果是否为null
        if(user ==null) {
            // 是：UserNotFoundException
            throw new UserNotFoundException("上传头像异常！用户数据不存在");
        }

        // 判断isDelete是否为1
        if(user.getIsDelete().equals(1)) {
            // 是：UserNotFoundException
            throw new UserNotFoundException("上传头像异常！用户数据不存在");    
        }

        // 更新用户头像
        Integer row=mapper.updateAvatar(uid, avatar, modifiedUser, new Date());
        // 判断受影响的行数是否不为1
        if(!row.equals(1)) {
            // 是：UpdateException
            throw new UpdateException("上传头像异常！请联系管理员");
        }
    }

在`UserServiceTests`中开发对应的测试方法：

    public void changeAvatar() {
        try {
            Integer uid=18;
            String avatar="/ccc/3.png";
            String modifiedUser="管理员";
            service.changeAvatar(uid, avatar, modifiedUser);
        }catch (Exception e) {
            System.err.println(e.getClass().getName());
            System.err.println(e.getMessage());
        }
    }


### 23. 用户-上传头像-控制器层

**(a) 统一异常处理**

用户上传空文件情况，应该对应异常`FileEmptyException`，上传文件大小超过上限，对应异常`FileSizeException`，文件类型不符合要求，对应异常`FileTypeException`，这些具体的异常已经都继承一个统一的父类`cn.tedu.store.controller.ex.FileUploadException`。


同时，调用`transferTo()`方法时，可能抛出2个受检异常：

    try {
        file.transferTo(dest);
    } catch (IllegalStateException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }

使用`FileStateException`来对应`IllegalStateException`，使用`FileIOException`来对应`IOException`，也继承`FileUploadException`。

在`cn.tedu.store.controller.BaseController`中添加对以上自定义异常的处理：

    @ExceptionHandler({ServiceException.class,FileUploadException.class})
    @ResponseBody
    public JsonResult<Void> handlerException(Throwable e){
        // 根据不同异常的类型提供不同的处理方式
        // 现在的处理方式是根据不同的类型，返回不同的状态码
        JsonResult<Void> jr=new JsonResult<>(e.getMessage());
        
        if(e instanceof UsernameDuplicateException) {
            jr.setState(ERROR_USERNAME_DUPLICATE);
        }else if(e instanceof UserNotFoundException) {
            jr.setState(31);
        }else if(e instanceof PasswordNotMatchException) {
            jr.setState(32);
        }else if(e instanceof InsertException) {
            jr.setState(40);
        }else if(e instanceof UpdateException) {
            jr.setState(41);
        }else if(e instanceof FileEmptyException) {
            jr.setState(50);
        }else if(e instanceof FileSizeException) {
            jr.setState(51);
        }else if(e instanceof FileTypeException) {
            jr.setState(52);
        }else if(e instanceof FileStateException) {
            jr.setState(53);
        }else if(e instanceof FileIOException) {
            jr.setState(54);
        }
        
        return jr;
    }



**(b) 设计请求**

    请求路径：/users/change_avatar
    请求参数：MultipartFile file, HttpServletRequest request, HttpSession session
    请求方式：POST
    响应数据：JsonResult<String>

**(c) 实现请求**

    @PostMapping("change_avatar")
    public JsonResult<String> changeAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request,HttpSession session){
        
        // 生成文件名

        // 生成目标路径
        
        // 将用户上传的头像保存到服务器上

        // 将头像在服务器的路径保存到数据库

    }

不考虑异常处理的实现如下：

    @PostMapping("change_avatar")
    public JsonResult<String> changeAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request,HttpSession session){
        
        // 生成文件名
        String oFilename=file.getOriginalFilename();
        Integer index=oFilename.lastIndexOf(".");
        String suffix="";
        if(index!=-1) {
            suffix=oFilename.substring(index);
        }
        String filename=UUID.randomUUID().toString()+suffix;

        // 生成目标路径
        String filePath=request.getServletContext().getRealPath("upload");
        File parent=new File(filePath);
        if(!parent.exists()) {
            parent.mkdirs();//创建对应的目录
        }
        
        File dest=new File(parent,filename);
        // 将用户上传的头像保存到服务器上
        try {
            file.transferTo(dest);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 将头像在服务器的路径保存到数据库
        String avatar="/upload/"+filename;
        Integer uid=getUidFromSession(request);
        String username=getUsernameFromSession(request);
        service.changeAvatar(uid,avatar,username);
        return new JsonResult<String>(SUCCESS, avatar);
    }

添加必要验证和自定义异常处理后的代码如下：

    private static final long AVATAR_MAX_SIZE=600*1024;

    private static final List<String> AVATAR_TYPES=new ArrayList<String>();
    
    // 静态初始化器：用于初始化本类的静态成员
    static {
        AVATAR_TYPES.add("image/jpeg");
        AVATAR_TYPES.add("image/png");
    }
    
    @PostMapping("change_avatar")
    public JsonResult<String> changeAvatar(@RequestParam("file") MultipartFile file,
                HttpServletRequest request,HttpSession session){
        
        // 空文件验证
        if(file.isEmpty()) {
            throw new FileEmptyException("文件上传异常！文件不能为空");
        }
        // 文件大小验证
        long fileSize=file.getSize();
        if(fileSize>AVATAR_MAX_SIZE) {
            throw new FileSizeException("文件上传异常！文件大小超过上限:"+(AVATAR_MAX_SIZE/1024)+"kb");
        }
        
        // 文件类型验证
        if(!AVATAR_TYPES.contains(file.getContentType())) {
            throw new FileTypeException("文件上传异常！文件类型不正确，允许的类型有："+AVATAR_TYPES);
        }
        
        // 生成文件名
        String oFilename=file.getOriginalFilename();
        Integer index=oFilename.lastIndexOf(".");
        String suffix="";
        if(index!=-1) {
            suffix=oFilename.substring(index);
        }
        String filename=UUID.randomUUID().toString()+suffix;

        // 生成目标路径
        String filePath=request.getServletContext().getRealPath("upload");
        File parent=new File(filePath);
        if(!parent.exists()) {
            parent.mkdirs();//创建对应的目录
        }
        
        File dest=new File(parent,filename);
        // 将用户上传的头像保存到服务器上
        try {
            file.transferTo(dest);
        } catch (IllegalStateException e) {
            throw new FileStateException("文件上传异常！"+e.getMessage());
            // throw new FileStateException("文件上传异常！"+e.getMessage(),e);
        } catch (IOException e) {
            throw new FileIOException("文件上传异常！"+e.getMessage());
        }

        // 将头像在服务器的路径保存到数据库
        String avatar="/upload/"+filename;
        Integer uid=getUidFromSession(request);
        String username=getUsernameFromSession(request);
        service.changeAvatar(uid,avatar,username);
        return new JsonResult<String>(SUCCESS, avatar);
    }

Springboot默认对上传文件的大小进行前置验证，默认上限为10MB，如果文件直接超过这个大小，会被前置验证拒绝，子控制器中的验证逻辑不会被执行。

- spring.servlet.multipart.max-file-size=100Mb
- spring.servlet.multipart.max-request-size=100Mb


### 24. 用户-上传头像-前端界面

[](3.jpg)

文件上传的AJAX代码和普通表单的AJAX代码在`data`，`contentType`和`processData`属性上有区别：

    $("#btn-change-avatar").click(function() {
        $.ajax({
              "url":"/users/change_avatar",
              "data":new FormData($("#form-change-avatar")[0]),
              "type":"post",
              "contentType":false,
                 "processData":false,
              "dataType":"json",
              "success":function(json) {
              // 服务器返回状态码200时触发
                if (json.state == 20) {
                  alert("上传成功！");
                } else {
                  alert(json.message);
                }
              },
              "error":function(json){
                // 响应状态不是200时触发
                alert("登录状态失效，请重新登录！");
                // 页面重定向到login.html
                location.href="login.html";
              }
            });
          });

SpringBoot项目会将上传的文件保存在项目中，可以查看当前项目的**src/main/webapp**文件夹，看文件是否真的上传上去。注意查看前右键**refresh**一下。

数据库中保存的`avatar`的值应该是图片相对于服务器的路径，即`/upload/文件名`，不应该是完整的硬盘路径。

在上传头像成功后，页面中自动显示新头像，就是在上传成功后，从响应中获取新头像的路径，并动态添加到页面的`<img id="img-avatar">`的`src`中:

    if (json.state == 20) {
        alert("上传成功！");
        $("#img-avatar").attr("src",json.data);
    } 

[](4.jpg)

为了能够在用户登录后正确显示用户头像，在`login.html`中修改登录成功后的逻辑，将服务器返回的`avatar`路径添加到浏览器的`cookie`中：

    if (json.state == 20) {
        alert("登录成功！");
          // 将头像路径添加到cookie中,{"expires":7 单位是天}
         $.cookie("avatar",json.data.avatar,{"expires":7});
    }
    
为实现访问`upload.html`时直接显示正确的用户头像，在`upload.html`中添加js代码，从`cookie`中获取已经保存的用户头像路径，并添加到`<img id="img-avatar">`标签中：

    var avatar=$.cookie("avatar");        
    $("#img-avatar").attr("src",avatar);

在用户上传头像成功后，还应该及时更新`cookie`中保存的头像信息，在`upload.html`中添加js的代码：

    if (json.state == 20) {
        alert("上传成功！");
        $("#img-avatar").attr("src",json.data);
        // 更新cookie中头像的路径
        $.cookie("avatar",json.data,{"expires":7});
    }


### 了解：关于持久层方法的命名

1. 按照增删改查的操作来命名
2. 增： insertXXX/saveXXX
3. 删： deleteXXXById
4. 改： updateXXX
5. 查： 
    1. 查1个  getXXXByXXX/findXXXByXXX
    2. 查多个 listXXXByXXX/listAll

6. 业务层和持久层的方法一般不要同名
7. 控制器层和业务层的方法可以同名
