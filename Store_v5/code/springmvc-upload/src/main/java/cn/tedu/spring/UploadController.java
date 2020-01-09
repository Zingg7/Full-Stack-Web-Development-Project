package cn.tedu.spring;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Controller
public class UploadController {
	
	@RequestMapping("upload.do")
	@ResponseBody
	public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IllegalStateException, IOException {
		// file代表的就是用户上传的文件
		// System.out.println(file.getOriginalFilename());
		
		// 判断文件不为空
		if(file.isEmpty()) {
			throw new RuntimeException("上传的文件为空");
		}
		
		// 判断文件长度符合要求
		long size=file.getSize();
		if(size>1*1024*1024) {
			throw new RuntimeException("上传的文件大小超过1MB");
		}
		
		String contentType=file.getContentType();
		List<String> types=new ArrayList<String>();
		types.add("image/jpeg");
		types.add("image/png");
		types.add("image/gif");
		if(!types.contains(contentType)) {
			throw new RuntimeException("上传文件类型错误");
		}
		
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
		
		// 获取ServletContext对象
		ServletContext sc=request.getServletContext();
		// 获取当前服务器在硬盘上的真实路径
		String dirPath=sc.getRealPath("upload");
		
		// 创建File对象，指定文件的目标保存路径
		File dest=new File(dirPath,newFilename);
		
		// 将上传的文件保存到指定的路径
		file.transferTo(dest);
		return "OK";
	}

}
