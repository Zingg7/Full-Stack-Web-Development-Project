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
		// file����ľ����û��ϴ����ļ�
		// System.out.println(file.getOriginalFilename());
		
		// �ж��ļ���Ϊ��
		if(file.isEmpty()) {
			throw new RuntimeException("�ϴ����ļ�Ϊ��");
		}
		
		// �ж��ļ����ȷ���Ҫ��
		long size=file.getSize();
		if(size>1*1024*1024) {
			throw new RuntimeException("�ϴ����ļ���С����1MB");
		}
		
		String contentType=file.getContentType();
		List<String> types=new ArrayList<String>();
		types.add("image/jpeg");
		types.add("image/png");
		types.add("image/gif");
		if(!types.contains(contentType)) {
			throw new RuntimeException("�ϴ��ļ����ʹ���");
		}
		
		// �ļ�������
		String oFilename=file.getOriginalFilename();
		int index=oFilename.lastIndexOf(".");
		// �ļ��ĺ�׺
		String suffix="";
		// ��ֹ�ļ�û�к�׺����������
		if(index>=0) {
			suffix=oFilename.substring(index);
		}
		// ���ļ��� UUID+suffix
		String newFilename=UUID.randomUUID().toString()+suffix;
		
		// ��ȡServletContext����
		ServletContext sc=request.getServletContext();
		// ��ȡ��ǰ��������Ӳ���ϵ���ʵ·��
		String dirPath=sc.getRealPath("upload");
		
		// ����File����ָ���ļ���Ŀ�걣��·��
		File dest=new File(dirPath,newFilename);
		
		// ���ϴ����ļ����浽ָ����·��
		file.transferTo(dest);
		return "OK";
	}

}
