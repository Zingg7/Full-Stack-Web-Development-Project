package cn.tedu.spring;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController {
	
	@RequestMapping("upload.do")
	@ResponseBody
	public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IllegalStateException, IOException {
		// file代表的就是用户上传的文件
		// System.out.println(file.getOriginalFilename());
		
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
