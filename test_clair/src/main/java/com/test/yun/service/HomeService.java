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

	// 최초 접속
	public ModelAndView home() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("home.jsp");
		return mav;
	}
	
	// 로그인
	public ModelAndView login(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("success", session.getAttribute("name")+"님");
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

	// dbfile 업로드
	public ModelAndView upload(MultipartFile file) {
		FileBean fb = null;
		// 기본값: 파일 저장/읽기 실패 시 홈페이지로 돌아감
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
	
	// 파일 업로드 실패: 업로드 실패했다는 문구 표시, 홈페이지 이동
	private ModelAndView uploadFail() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("uploadFail", "파일 업로드에 실패했습니다. 다시 처음부터 진행해주세요.");
		mav.setViewName("home.jsp");
		return mav;
	}

	// dbfile 데이터 입력 모두 성공
	private ModelAndView insertPass(FileBean fb) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("total", fb.getTotal());
		mav.addObject("success", fb.getSuccess());
		mav.setViewName("pass.jsp");
		return mav;
	}

	// dbfile 데이터 입력 일부 또는 모두 실패
	private ModelAndView insertFail(FileBean fb) {
		ModelAndView mav = new ModelAndView();

		// HashMap에서 key/value 추출
		String cause = "";
		for (Entry<Integer, String> entry : fb.getFdata().entrySet()) {
			cause += entry.getKey() + "번 라인: " + entry.getValue() + "\r\n";
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
