package com.test.yun.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.test.yun.dto.UserBean;
import com.test.yun.service.AuthService;

@Controller
public class AuthController {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class); // 운영 로그

	@Autowired
	private AuthService authService;

	// 사용자 로그인폼 -- 로그인한 경우 접근불가, 유저 홈 화면으로 리다이렉트
	@RequestMapping(value = "/user/signin", method = RequestMethod.GET)
	public String login(HttpSession session, RedirectAttributes ra) {
		return authService.login(session, ra);
	}

	// 로그인 처리
	@RequestMapping(value = "/user/signin", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> login(@RequestBody UserBean userBean, HttpServletRequest req) {
		return authService.login(userBean, req);
	}

	// 사용자 가입폼 -- 로그인한 경우 접근불가, 유저 홈 화면으로 리다이렉트
	@RequestMapping(value = "/user/signup", method = RequestMethod.GET)
	public String join(HttpSession session, RedirectAttributes ra) {
		return authService.join(session, ra);
	}

	// 가입 처리
	@RequestMapping(value = "/user/signup", method = RequestMethod.POST, produces = "application/text; charset=utf8")
	@ResponseBody
	public ResponseEntity<String> join(@Valid @RequestBody UserBean userBean, HttpServletRequest req) {
		return authService.join(userBean, req);
	}

	// 로그인, 회원가입 후 -- 세션에 id가 없거나 세션에 있는 id와 다른 값으로 접근하는 경우 접근불가, 파일전송화면 또는 로그인한 유저 화면으로 리다이렉트
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String userHome(@PathVariable("id") String id, HttpSession session, RedirectAttributes ra, Model model) {
		String result;
		logger.info(id);
		if(session.getAttribute("id")==null) { // id가 없는 경우
			result = authService.redirectFileupload(ra);
		}else {
			if(session.getAttribute("id").equals(id)) { // 로그인 id = 이동경로 id
				result = authService.login(session, model);
			}else { // 로그인 id != 이동경로 id
				result = authService.redirectFileupload(session, ra);
			}
		}
		return result;
	}

	// 로그아웃
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpSession session) {
		return authService.logout(session);
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

}
