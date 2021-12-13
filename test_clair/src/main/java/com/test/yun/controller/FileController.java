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
	
	// 최초 접속 -- 파일 전송 화면
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView fileupload(HttpSession session) {
		return uploadService.fileupload(session);
	}
	
	// 파일 업로드 --> 회원가입
	@RequestMapping(value = "/file/upload", method = RequestMethod.POST)
	public ModelAndView upload(@RequestParam("userFile") MultipartFile file) {
		return uploadService.upload(file);
	}

	// 성공 시 조회버튼 ajax // dhtmlx grid로 데이터 뿌려주도록 // grid의 페이징 기능 사용
	@RequestMapping(value = "/file/success", method = RequestMethod.GET, produces = "application/text; charset=utf8")
	@ResponseBody
	public String getRecord() {
		return uploadService.getRecord();
	}

}
