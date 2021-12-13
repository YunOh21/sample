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
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class); // � �α�

	@Autowired
	private AuthService authService;

	// ����� �α����� -- �α����� ��� ���ٺҰ�, ���� Ȩ ȭ������ �����̷�Ʈ
	@RequestMapping(value = "/user/signin", method = RequestMethod.GET)
	public String login(HttpSession session, RedirectAttributes ra) {
		return authService.login(session, ra);
	}

	// �α��� ó��
	@RequestMapping(value = "/user/signin", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> login(@RequestBody UserBean userBean, HttpServletRequest req) {
		return authService.login(userBean, req);
	}

	// ����� ������ -- �α����� ��� ���ٺҰ�, ���� Ȩ ȭ������ �����̷�Ʈ
	@RequestMapping(value = "/user/signup", method = RequestMethod.GET)
	public String join(HttpSession session, RedirectAttributes ra) {
		return authService.join(session, ra);
	}

	// ���� ó��
	@RequestMapping(value = "/user/signup", method = RequestMethod.POST, produces = "application/text; charset=utf8")
	@ResponseBody
	public ResponseEntity<String> join(@Valid @RequestBody UserBean userBean, HttpServletRequest req) {
		return authService.join(userBean, req);
	}

	// �α���, ȸ������ �� -- ���ǿ� id�� ���ų� ���ǿ� �ִ� id�� �ٸ� ������ �����ϴ� ��� ���ٺҰ�, ��������ȭ�� �Ǵ� �α����� ���� ȭ������ �����̷�Ʈ
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String userHome(@PathVariable("id") String id, HttpSession session, RedirectAttributes ra, Model model) {
		String result;
		logger.info(id);
		if(session.getAttribute("id")==null) { // id�� ���� ���
			result = authService.redirectFileupload(ra);
		}else {
			if(session.getAttribute("id").equals(id)) { // �α��� id = �̵���� id
				result = authService.login(session, model);
			}else { // �α��� id != �̵���� id
				result = authService.redirectFileupload(session, ra);
			}
		}
		return result;
	}

	// �α׾ƿ�
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpSession session) {
		return authService.logout(session);
	}
	

	// ȸ������ Validation ����ó�� -- ���� �������̽��� ó�� ��Ʈ�ѷ��� ���޵� ��Ȳ������ �߻�x, ���� ���� �� json ���� ó�� �ʿ�
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public String handleValidationException(MethodArgumentNotValidException exception) {
		BindingResult bindingResult = exception.getBindingResult();

		StringBuilder builder = new StringBuilder();
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			builder.append("[");
			builder.append(fieldError.getField());
			builder.append("](��)�� ");
			builder.append(fieldError.getDefaultMessage());
			builder.append(" �Էµ� ��: [");
			builder.append(fieldError.getRejectedValue());
			builder.append("]");
		}

		return builder.toString();
	}

}
