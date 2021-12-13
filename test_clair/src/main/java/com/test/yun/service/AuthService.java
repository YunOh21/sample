package com.test.yun.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.test.yun.controller.AuthController;
import com.test.yun.dto.UserBean;
import com.test.yun.mapper.UserMapper;
import com.test.yun.util.JoinValidCheck;

@Service
public class AuthService {
	private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private JoinValidCheck joinValidCheck;

	@Autowired
	private FileService fileService;

	// 로그인폼
	public String login(HttpSession session, RedirectAttributes ra) {
		String id = (String) session.getAttribute("id");
		return id == null ? "login.html" : redirectUser(id, ra);
	}

	// 회원가입폼
	public String join(HttpSession session, RedirectAttributes ra) {
		String id = (String) session.getAttribute("id");
		return id == null ? "join.html" : redirectUser(id, ra);
	}

	// 세션에 유효한 id가 있을 때, 유저홈으로 리다이렉트 (홈화면, 로그인폼, 회원가입폼에 get 방식으로 접근 시)
	public String redirectUser(String id, RedirectAttributes ra) {
		ra.addFlashAttribute("msg", "잘못된 접근입니다. 회원 화면으로 이동합니다.");
		return "redirect:/" + id;
	}

	// 세션 id가 없는 경우, 파일전송화면으로 리다이렉트
	public String redirectFileupload(RedirectAttributes ra) {
		ra.addFlashAttribute("msg", "세션이 만료되었습니다. 초기 화면으로 이동합니다.");
		return "redirect:/";
	}

	// 세션 id가 아닌 루트로 접근한 경우, 파일전송화면으로 리다이렉트
	public String redirectFileupload(HttpSession session, RedirectAttributes ra) {
		ra.addFlashAttribute("msg", "잘못된 접근입니다. 회원 화면으로 이동합니다.");
		return "redirect:/" + session.getAttribute("id");
	}

	/*
	 * 로그인 처리 response code 3가지: 1)로그인 성공: 200 / 2)권한없음-아이디 혹은 패스워드 틀림: 400 /
	 * 3)기타오류: 500
	 */
	public ResponseEntity<String> login(UserBean userBean, HttpServletRequest req) {
		ResponseEntity<String> result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		// DB연결 체크(연결이 안된 경우 or context close 실패한 경우, http status는 위에서 초기화한 값인 500 리턴)
		Context context = null;
		try {
			context = new InitialContext();
			context.lookup("java:comp/env/postgresqlJndi");
			if (userMapper.selectUser(userBean).size() == 1) { // id 존재 확인
				if (checkPwd(userBean, userMapper.selectUser(userBean).get(0))) { // pwd 일치 확인
					HttpSession session = req.getSession();
					session.setAttribute("id", userBean.getId());
					session.setAttribute("name", userMapper.selectUser(userBean).get(0).getName());
					Map<String, Object> joMap = new HashMap<String, Object>();
					joMap.put("uri", "/");
					joMap.put("id", userBean.getId());
					JSONObject jo = new JSONObject(joMap);
					result = new ResponseEntity(jo.toString(), HttpStatus.OK);
				} else {
					logger.info("pwd 불일치");
					result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
				}
			} else {
				logger.info("id 없음");
				result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} catch (NamingException e) {
			logger.info("DB not connected");
			e.printStackTrace();
		} finally {
			if (context != null) {
				try {
					context.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
			}
		}
		logger.info(result.toString());
		return result;
	}

	/*
	 * 회원가입 처리 response code 3가지: 1)회원가입 성공: 200 / 2)입력항목 오류: 400 / 3)기타오류: 500
	 * status 400 리턴 시 - 1001: pk중복 - 1002: data valid 탈락 - 1003: DB연결 안된 경우
	 */
	public ResponseEntity<String> join(UserBean userBean, HttpServletRequest req) {
		// 리소스 서버 클로즈 실패 or 어떤 케이스에도 해당하지 않는 경우, http status를 500으로 리턴함
		ResponseEntity<String> result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		/* null, 범위초과 테스트 */
		// userBean.setId("123456781234qqqqqqqq000000qqqqqqqqqqqq");
		// userBean.setPwd("");
		// userBean.setLevel("222");

		/* 중간에 빈칸 있는 경우 테스트 */
		// userBean.setId("H E L L O");
		// userBean.setPwd(" ");
		// userBean.setName("오 ");

		/* 날짜에 시간 더함 */
		userBean.setRegDate(userBean.getRegDate() + " 00:00:00");
		LinkedHashMap<String, String> invalidJoinMap = joinValidCheck.validCheck(userBean);
		logger.info(String.valueOf(invalidJoinMap.size()));
		if (invalidJoinMap.size() == 0) {
			// DB연결 체크
			Context context = null;
			try {
				context = new InitialContext();
				context.lookup("java:comp/env/postgresqlJndi");
				if (fileService.insertUser(userBean)) { // case 1: 회원가입 성공
					HttpSession session = req.getSession();
					session.setAttribute("id", userBean.getId());
					session.setAttribute("name", userBean.getName());
					session.setAttribute("join", "true");
					JSONObject jo = new JSONObject();
					jo.put("uri", "/");
					jo.put("id", userBean.getId());
					result = new ResponseEntity(jo.toString(), HttpStatus.OK);
				} else { // case 2-1: ID 중복 - 에러코드 1001
					logger.info("ID 중복");
					JSONObject parent = new JSONObject();
					JSONArray children = new JSONArray();
					JSONObject child = new JSONObject();
					child.put("field", "ID");
					child.put("reason", "이미 존재하는 ID입니다.");
					children.add(child);
					parent.put("code", 1001);
					parent.put("message", child.get("reason"));
					parent.put("hasFieldsError", true);
					parent.put("fields", children);
					result = new ResponseEntity(parent.toString(), HttpStatus.BAD_REQUEST);
				}
			} catch (NamingException e) { // case 2-2: DB연결 실패 - 에러코드 1003 (context.xml에서 driverName 부분 오타로 재현)
				logger.info("DB not connected");
				e.printStackTrace();
				JSONObject parent = new JSONObject();
				JSONArray children = new JSONArray();
				JSONObject child = new JSONObject();
				child.put("field", "데이터베이스 연결불가");
				child.put("reason", "관리자에게 문의하세요.");
				children.add(child);
				parent.put("code", 1003);
				parent.put("message", child.get("reason"));
				parent.put("hasFieldsError", true);
				parent.put("fields", children);
				result = new ResponseEntity(parent.toString(), HttpStatus.BAD_REQUEST);
			} finally {
				if (context != null) {
					try {
						context.close();
					} catch (NamingException e) { // case 3: 기타서버오류(리소스 close 실패 시)
						e.printStackTrace();
					}
				}
			}
		} else { // case 2-3: 입력항목 오류(data null 또는 범위초과 - 에러코드 1002)
			JSONObject parent = new JSONObject();
			JSONArray children = new JSONArray();
			// Map의 key값과 value값을 한 번에 얻기 위해 Entry 인터페이스 사용
			for (Entry<String, String> entry : invalidJoinMap.entrySet()) {
				JSONObject child = new JSONObject();
				child.put("field", entry.getKey());
				child.put("reason", entry.getValue());
				children.add(child);
			}
			parent.put("code", 1002);
			parent.put("message", "입력항목 오류");
			parent.put("hasFieldsError", true);
			parent.put("fields", children);
			result = new ResponseEntity(parent.toString(), HttpStatus.BAD_REQUEST);
		}
		logger.info(result.toString());
		return result;
	}

	// 로그인,회원가입 성공
	public String login(HttpSession session, Model model) {
		model.addAttribute("success", "[" + session.getAttribute("name") + " 님]&nbsp;");
		if (session.getAttribute("join") != null) {
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

	// 로그인 시 사용 메소드 - 패스워드 일치 확인
	private boolean checkPwd(UserBean loginUser, UserBean dbUser) {
		return loginUser.getPwd().equals(dbUser.getPwd());
	}

}
