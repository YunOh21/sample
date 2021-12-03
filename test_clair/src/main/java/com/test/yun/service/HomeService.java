package com.test.yun.service;

import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.test.yun.dto.FileBean;
import com.test.yun.dto.UserBean;

@Service
public class HomeService {

	@Autowired
	private FileService fileService;

	// ���� ����
	public ModelAndView home() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("home.jsp");
		return mav;
	}
	
	// �α���
	public ModelAndView login(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("success", session.getAttribute("name")+"��");
		mav.setViewName("home.jsp");
		return mav;
	}
	
	// 
	public ModelAndView logout(HttpSession session) {
		session.invalidate();
		ModelAndView mav = new ModelAndView();
		mav.setViewName("home.jsp");
		return mav;
	}

	// dbfile ���ε�
	public ModelAndView upload(MultipartFile file) {
		FileBean fb = null;
		// �⺻��: ���� ����/�б� ���� �� Ȩ�������� ���ư�
		ModelAndView mav = uploadFail();
		if (file != null) {
			fb = fileService.save(file);
			System.out.println(fb.getMsg());
			if (fb.getMsg().equals("saveOK")) {
				fb = fileService.read(fb);
				System.out.println(fb.getMsg());
				if (fb.getMsg().equals("readOK")) {
					if(fb.getFdata()!=null) {
						if (fb.getTotal() == fb.getSuccess()) {
							mav = insertPass(fb);
						} else {
							mav = insertFail(fb);
						}
					}
				}
			}
		}
		return mav;
	}
	
	// ���� ���ε� ����: ���ε� �����ߴٴ� ���� ǥ��, Ȩ������ �̵�
	private ModelAndView uploadFail() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("uploadFail", "���� ���ε忡 �����߽��ϴ�. �ٽ� ó������ �������ּ���.");
		mav.setViewName("home.jsp");
		return mav;
	}

	// dbfile ������ �Է� ��� ����
	private ModelAndView insertPass(FileBean fb) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("total", fb.getTotal());
		mav.addObject("success", fb.getSuccess());
		mav.setViewName("pass.jsp");
		return mav;
	}

	// dbfile ������ �Է� �Ϻ� �Ǵ� ��� ����
	private ModelAndView insertFail(FileBean fb) {
		ModelAndView mav = new ModelAndView();

		// HashMap���� key/value ����
		String cause = "";
		for (Entry<Integer, String> entry : fb.getFdata().entrySet()) {
			cause += entry.getKey() + "�� ����: " + entry.getValue() + "\r\n";
		}
		mav.addObject("success", fb.getSuccess());
		mav.addObject("fail", fb.getTotal() - fb.getSuccess());
		mav.addObject("cause", cause.replace("\r\n", "<br>"));
		mav.setViewName("fail.jsp");
		return mav;
	}

	public boolean checkPwd(UserBean loginUser, UserBean dbUser) {
		return loginUser.getPwd().equals(dbUser.getPwd());
	}

}
