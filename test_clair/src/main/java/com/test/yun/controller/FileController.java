package com.test.yun.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.test.yun.service.UploadService;

@Controller
public class FileController {
	
	@Autowired
	private UploadService uploadService;
	
	// ���� ���� -- ���� ���� ȭ��
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView fileupload(HttpSession session) {
		return uploadService.fileupload(session);
	}
	
	// ���� ���ε� --> ȸ������
	@RequestMapping(value = "/file/upload", method = RequestMethod.POST)
	public ModelAndView upload(@RequestParam("userFile") MultipartFile file) {
		return uploadService.upload(file);
	}

	// ���� �� ��ȸ��ư ajax // dhtmlx grid�� ������ �ѷ��ֵ��� // grid�� ����¡ ��� ���
	@RequestMapping(value = "/file/success", method = RequestMethod.GET, produces = "application/text; charset=utf8")
	@ResponseBody
	public String getRecord() {
		return uploadService.getRecord();
	}

}
