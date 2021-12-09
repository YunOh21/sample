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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class); // � �α�

	@Autowired
	private HomeService homeService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private JoinValidCheck joinValidCheck;

	@Autowired
	private FileService fileService;

	// ���� ���� -- ���� ���� ȭ��
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView fileupload(HttpSession session) {
		return homeService.fileupload(session);
	}

	// ����� �α����� -- �α����� ��� ���� Ȩ ȭ������ �̵�
	@RequestMapping(value = "/user/signin", method = RequestMethod.GET)
	public String login(HttpSession session, RedirectAttributes ra) {
		return homeService.login(session, ra);
	}

	// ����� �α���
	@RequestMapping(value = "/user/signin", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> login(@RequestBody UserBean userBean, HttpServletRequest req) {
		ResponseEntity<String> result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		if (userMapper.loginUser(userBean).size() == 1) { // id ���� Ȯ��
			if (homeService.checkPwd(userBean, userMapper.loginUser(userBean).get(0))) { // pwd ��ġ Ȯ��
				HttpSession session = req.getSession();
				session.setAttribute("id", userBean.getId());
				session.setAttribute("name", userMapper.loginUser(userBean).get(0).getName());
				Map<String, Object> joMap = new HashMap<String, Object>();
				joMap.put("uri", "/");
				joMap.put("id", userBean.getId());
				JSONObject jo = new JSONObject(joMap);
				result = new ResponseEntity(jo.toString(), HttpStatus.OK);
			} else {
				logger.info("pwd ����ġ");
				result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} else {
			logger.info("id ����");
			result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		logger.info(result.toString());
		return result;
	}

	// ����� ������
	@RequestMapping(value = "/user/signup", method = RequestMethod.GET)
	public String join(HttpSession session, RedirectAttributes ra) {
		return homeService.join(session, ra);
	}

	// ����� ���� -- ���� �� �ڵ� �α��� ó��
	// ������ ���̽�
	// 1. ȸ������ ����
	// 2. �Է��׸� ���� 
	// 3. ��Ÿ����
	@RequestMapping(value = "/user/signup", method = RequestMethod.POST, produces = "application/text; charset=utf8")
	@ResponseBody
	public ResponseEntity<String> join(@Valid @RequestBody UserBean userBean, HttpServletRequest req) {
		// ���ҽ� ���� Ŭ���� ���� + � ���̽����� �ش����� �ʴ� ��� http status�� 500���� ������
		ResponseEntity<String> result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		
		/* null, �����ʰ� �׽�Ʈ */
		//userBean.setId("123456781234qqqqqqqq000000qqqqqqqqqqqq");
		//userBean.setPwd("");
		//userBean.setLevel("222");
		
		/* �߰��� ��ĭ �ִ� ��� �׽�Ʈ */
		//userBean.setId("H E L L O");
		//userBean.setPwd("             ");
		//userBean.setName("��           ");
		
		/* ��¥�� �ð� ���� */
		userBean.setRegDate(userBean.getRegDate() + " 00:00:00");
		LinkedHashMap<String, String> invalidJoinMap = joinValidCheck.validCheck(userBean);
		logger.info(String.valueOf(invalidJoinMap.size()));
		if (invalidJoinMap.size() == 0) {
			Context context=null;
			try {
				context = new InitialContext();
				context.lookup("java:comp/env/postgresqlJndi");
				if (fileService.insertUser(userBean)) { // case 1: ȸ������ ����
					HttpSession session = req.getSession();
					session.setAttribute("id", userBean.getId());
					session.setAttribute("name", userBean.getName());
					session.setAttribute("join", "true");
					JSONObject jo = new JSONObject();
					jo.put("uri", "/");
					jo.put("id", userBean.getId());
					result = new ResponseEntity(jo.toString(), HttpStatus.OK);
				} else { // case 2-1: �Է��׸� ����(ID �ߺ� - �����ڵ� 1001)
					logger.info("ID �ߺ�");
					JSONObject parent = new JSONObject();
					JSONArray children = new JSONArray();
					JSONObject child = new JSONObject();
					child.put("field", "ID");
					child.put("reason", "�̹� �����ϴ� ID�Դϴ�.");
					children.add(child);
					parent.put("code", 1001);
					parent.put("message", child.get("reason"));
					parent.put("hasFieldsError", true);
					parent.put("fields", children);
					result = new ResponseEntity(parent.toString(), HttpStatus.BAD_REQUEST);
				}
			}catch(NamingException e){ // case 2-2: �Է��׸� ����(DB���� ���� - �����ڵ� 1003) -- context.xml���� driverName �κ� ��Ÿ�� ����
				logger.info("DB not connected");
				JSONObject parent = new JSONObject();
				JSONArray children = new JSONArray();
				JSONObject child = new JSONObject();
				child.put("field", "postgreSQL");
				child.put("reason", "�����ͺ��̽��� ������ �� �����ϴ�.");
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
					} catch (NamingException e) { // case 3: ��Ÿ��������(���ҽ� close ���� ��)
						e.printStackTrace();
					}
				}
			}
		} else { // case 2-3: �Է��׸� ����(data null �Ǵ� �����ʰ� - �����ڵ� 1002)
			JSONObject parent = new JSONObject();
			JSONArray children = new JSONArray();
			// Map�� key���� value���� �� ���� ��� ���� Entry �������̽� ���
			for (Entry<String, String> entry : invalidJoinMap.entrySet()) {
				JSONObject child = new JSONObject();
				child.put("field", entry.getKey());
				child.put("reason", entry.getValue());
				children.add(child);
			}
			parent.put("code", 1002);
			parent.put("message", "�Է��׸� ����");
			parent.put("hasFieldsError", true);
			parent.put("fields", children);
			result = new ResponseEntity(parent.toString(), HttpStatus.BAD_REQUEST);
		}
		logger.info(result.toString());
		return result;
	}

	// ȸ������ Validation ����ó�� -- ���� �������̽��� ó�� ��Ʈ�ѷ��� ���޵� ��Ȳ������ �߻����� ����. ���� ���� �� json����
	// ��ȯ �ʿ�.
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

	// �α���, ȸ������ ��
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView userHome(HttpSession session) {
		return homeService.login(session);
	}

	// �α׾ƿ�
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpSession session) {
		return homeService.logout(session);
	}

	// ���� ���ε� --> ȸ������
	@RequestMapping(value = "/file/upload", method = RequestMethod.POST)
	public ModelAndView upload(@RequestParam("userFile") MultipartFile file) {
		return homeService.upload(file);
	}

	// ���� �� ��ȸ��ư ajax // dhtmlx grid�� ������ �ѷ��ֵ��� // grid�� ����¡ ��� ���
	@RequestMapping(value = "/file/success", method = RequestMethod.GET, produces = "application/text; charset=utf8")
	@ResponseBody
	public String getRecord() {
		List<UserBean> user = userMapper.selectAll();
		JSONArray ja = new JSONArray();
		for (int i = 0; i < user.size(); i++) {
			// jsonObject ���� �� type warning ��ȸ
			JSONObject jo = new JSONObject();
			jo.put("id", user.get(i).getId());
			jo.put("pwd", user.get(i).getPwd());
			jo.put("name", user.get(i).getName());
			jo.put("level", user.get(i).getLevel());
			jo.put("desc", user.get(i).getDesc());
			jo.put("regDate", user.get(i).getRegDate());
			// jsonArray�� 1�پ� �߰�
			ja.add(jo);
		}
		return ja.toString();
	}
}
