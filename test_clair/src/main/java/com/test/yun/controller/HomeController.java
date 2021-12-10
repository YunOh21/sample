package com.test.yun.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.test.yun.dto.UserBean;
import com.test.yun.mapper.UserMapper;
import com.test.yun.service.FileService;
import com.test.yun.service.HomeService;
import com.test.yun.util.JoinValidCheck;

@Controller
public class HomeController {
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class); // 운영 로그

	@Autowired
	private HomeService homeService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private JoinValidCheck joinValidCheck;

	@Autowired
	private FileService fileService;

	// 최초 접속 -- 파일 전송 화면
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView fileupload(HttpSession session) {
		return homeService.fileupload(session);
	}

	// 사용자 로그인폼 -- 로그인한 경우 접근불가, 유저 홈 화면으로 리다이렉트
	@RequestMapping(value = "/user/signin", method = RequestMethod.GET)
	public String login(HttpSession session, RedirectAttributes ra) {
		return homeService.login(session, ra);
	}

	/* 사용자 로그인
	response code 3가지: 1)로그인 성공: 200 / 2)권한없음-아이디 혹은 패스워드 틀림: 400 / 3)기타오류: 500 */
	@RequestMapping(value = "/user/signin", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> login(@RequestBody UserBean userBean, HttpServletRequest req) {
		ResponseEntity<String> result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		// DB연결 체크(연결이 안된 경우 or context close 실패한 경우, http status는 위에서 초기화한 값인 500 리턴)
		Context context=null;
		try {
			context = new InitialContext();
			context.lookup("java:comp/env/postgresqlJndi");
			if (userMapper.selectUser(userBean).size() == 1) { // id 존재 확인
				if (homeService.checkPwd(userBean, userMapper.selectUser(userBean).get(0))) { // pwd 일치 확인
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
		}catch(NamingException e){
			logger.info("DB not connected");
			e.printStackTrace();
		}finally {
			if(context!=null) {
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

	// 사용자 가입폼 -- 로그인한 경우 접근불가, 유저 홈 화면으로 리다이렉트
	@RequestMapping(value = "/user/signup", method = RequestMethod.GET)
	public String join(HttpSession session, RedirectAttributes ra) {
		return homeService.join(session, ra);
	}

	/* 사용자 가입 -- 가입 후 자동 로그인 처리
	response code 3가지: 1)회원가입 성공: 200 / 2)입력항목 오류: 400 / 3)기타오류: 500
	status 400 리턴 시, 에러코드 1001/1002/1003 발생사유는 API 설계에 명시되어 있지 않음 -> 아래 내용은 임의 설정
	- 1001: pk중복
	- 1002: data valid 탈락
	- 1003: DB연결 안된 경우 */
	@RequestMapping(value = "/user/signup", method = RequestMethod.POST, produces = "application/text; charset=utf8")
	@ResponseBody
	public ResponseEntity<String> join(@Valid @RequestBody UserBean userBean, HttpServletRequest req) {
		// 리소스 서버 클로즈 실패 or 어떤 케이스에도 해당하지 않는 경우, http status를 500으로 리턴함
		ResponseEntity<String> result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		
		/* null, 범위초과 테스트 */
		//userBean.setId("123456781234qqqqqqqq000000qqqqqqqqqqqq");
		//userBean.setPwd("");
		//userBean.setLevel("222");
		
		/* 중간에 빈칸 있는 경우 테스트 */
		//userBean.setId("H E L L O");
		//userBean.setPwd("             ");
		//userBean.setName("오           ");
		
		/* 날짜에 시간 더함 */
		userBean.setRegDate(userBean.getRegDate() + " 00:00:00");
		LinkedHashMap<String, String> invalidJoinMap = joinValidCheck.validCheck(userBean);
		logger.info(String.valueOf(invalidJoinMap.size()));
		if (invalidJoinMap.size() == 0) {
			// DB연결 체크
			Context context=null;
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
			}catch(NamingException e){ // case 2-2: DB연결 실패 - 에러코드 1003 (context.xml에서 driverName 부분 오타로 재현)
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
			}finally {
				if(context!=null) {
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

	// 회원가입 Validation 예외처리 -- 현재 에러케이스는 처음 컨트롤러에 전달된 상황에서는 발생x, 추후 적용 시 json 리턴 처리 필요
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public String handleValidationException(MethodArgumentNotValidException exception) {
		BindingResult bindingResult = exception.getBindingResult();

		StringBuilder builder = new StringBuilder();
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			builder.append("[");
			builder.append(fieldError.getField());
			builder.append("](은)는 ");
			builder.append(fieldError.getDefaultMessage());
			builder.append(" 입력된 값: [");
			builder.append(fieldError.getRejectedValue());
			builder.append("]");
		}

		return builder.toString();
	}

	// 로그인, 회원가입 후 -- 세션에 id가 없거나 세션에 있는 id와 다른 값으로 접근하는 경우 접근불가, 파일전송화면 또는 로그인한 유저 화면으로 리다이렉트
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String userHome(@PathVariable("id") String id, HttpSession session, RedirectAttributes ra, Model model) {
		String result;
		logger.info(id);
		if(session.getAttribute("id")==null) { // id가 없는 경우
			result = homeService.redirectFileupload(ra);
		}else {
			if(session.getAttribute("id").equals(id)) { // 로그인 id = 이동경로 id
				result = homeService.login(session, model);
			}else { // 로그인 id != 이동경로 id
				result = homeService.redirectFileupload(session, ra);
			}
		}
		return result;
	}

	// 로그아웃
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpSession session) {
		return homeService.logout(session);
	}

	// 파일 업로드 --> 회원가입
	@RequestMapping(value = "/file/upload", method = RequestMethod.POST)
	public ModelAndView upload(@RequestParam("userFile") MultipartFile file) {
		return homeService.upload(file);
	}

	// 성공 시 조회버튼 ajax // dhtmlx grid로 데이터 뿌려주도록 // grid의 페이징 기능 사용
	@RequestMapping(value = "/file/success", method = RequestMethod.GET, produces = "application/text; charset=utf8")
	@ResponseBody
	public String getRecord() {
		List<UserBean> user = userMapper.selectAll();
		JSONArray ja = new JSONArray();
		for (int i = 0; i < user.size(); i++) {
			// jsonObject 생성 시 type warning 우회
			JSONObject jo = new JSONObject();
			jo.put("id", user.get(i).getId());
			jo.put("pwd", user.get(i).getPwd());
			jo.put("name", user.get(i).getName());
			jo.put("level", user.get(i).getLevel());
			jo.put("desc", user.get(i).getDesc());
			jo.put("regDate", user.get(i).getRegDate());
			// jsonArray에 1줄씩 추가
			ja.add(jo);
		}
		return ja.toString();
	}
}
