package com.test.yun.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.test.yun.dto.FileBean;
import com.test.yun.dto.UserBean;
import com.test.yun.util.JoinValidCheck;

@Service
public class HomeService {
	private static Logger logger = LoggerFactory.getLogger(HomeService.class);

	@Autowired
	private FileService fileService;

	// 홈화면 = 파일전송 화면
	public ModelAndView fileupload(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		if(session.getAttribute("id")!=null) {
			mav.addObject("success", "["+session.getAttribute("name")+" 님]&nbsp;");
			mav.addObject("userId", session.getAttribute("id"));
		}
		mav.setViewName("fileupload.jsp");
		return mav;
	}
	
	// 로그인폼
	public String login(HttpSession session, RedirectAttributes ra) {
		String id = (String)session.getAttribute("id");
		return id==null? "login.html" : redirectUser(id, ra);
	}
	
	// 회원가입폼
	public String join(HttpSession session, RedirectAttributes ra) {
		String id = (String)session.getAttribute("id");
		return id==null? "join.html" : redirectUser(id, ra);
	}
	
	// 세션에 유효한 id가 있을 때, 유저홈으로 리다이렉트 (홈화면, 로그인폼, 회원가입폼에 get 방식으로 접근 시)
	public String redirectUser(String id, RedirectAttributes ra) {
		ra.addFlashAttribute("msg", "잘못된 접근입니다. 회원 화면으로 이동합니다.");
		return "redirect:/" + id;
	}
	
	// 세션 id가 없는 경우, 파일전송화면으로 리다이렉트
	public String redirectFileupload(RedirectAttributes ra) {
		ra.addFlashAttribute("msg", "잘못된 접근입니다. 초기 화면으로 이동합니다.");
		return "redirect:/";
	}
	
	// 세션 id가 아닌 루트로 접근한 경우, 파일전송화면으로 리다이렉트
	public String redirectFileupload(HttpSession session, RedirectAttributes ra) {
		ra.addFlashAttribute("msg", "잘못된 접근입니다. 회원 화면으로 이동합니다.");
		return "redirect:/" + session.getAttribute("id");
	}
	
	// 로그인,회원가입 성공
	public String login(HttpSession session, Model model) {
		model.addAttribute("success", "["+session.getAttribute("name")+" 님]&nbsp;");
		if(session.getAttribute("join")!=null) {
			model.addAttribute("join", "회원가입이 완료되었습니다!");
			session.setAttribute("join", null); // 회원가입 후 첫 화면에만 welcome 이미지 표시
		}
		return "user.jsp";
	}
	
	// 로그아웃
	public ModelAndView logout(HttpSession session) {
		session.invalidate();
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/");
		return mav;
	}

	// dbfile 업로드
	public ModelAndView upload(MultipartFile file) {
		FileBean fb = null;
		// 기본값: 파일 저장/읽기 실패 시 홈페이지로 돌아감
		ModelAndView mav = uploadFail();
		if (file != null) {
			fb = fileService.save(file);
			logger.info(fb.getMsg());
			if (fb.getMsg().equals("saveOK")) {
				fb = fileService.read(fb);
				logger.info(fb.getMsg());
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
		// json 객체 생성
		JSONArray ja = new JSONArray();

		// HashMap에서 key/value 추출
		//String cause = "";
		for (Entry<Integer, String> entry : fb.getFdata().entrySet()) {
			Map<String, Object> joMap = new HashMap<String, Object>();
			// cause += entry.getKey() + "번 라인: " + entry.getValue() + "\r\n";
			joMap.put("lineNum", entry.getKey());
			joMap.put("lineText", entry.getValue().split("->")[0]);
			joMap.put("failReason", entry.getValue().split("->")[1]);
			JSONObject jo = new JSONObject(joMap);
			ja.add(jo);
		}
		mav.addObject("success", fb.getSuccess());
		mav.addObject("fail", fb.getTotal() - fb.getSuccess());
		mav.addObject("failData", ja);
		mav.setViewName("fail.jsp");
		return mav;
	}
	
	// 로그인 시 사용 메소드 - 패스워드 일치 확인
	public boolean checkPwd(UserBean loginUser, UserBean dbUser) {
		return loginUser.getPwd().equals(dbUser.getPwd());
	}

}
