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

	// �α�����
	public String login(HttpSession session, RedirectAttributes ra) {
		String id = (String) session.getAttribute("id");
		return id == null ? "login.html" : redirectUser(id, ra);
	}

	// ȸ��������
	public String join(HttpSession session, RedirectAttributes ra) {
		String id = (String) session.getAttribute("id");
		return id == null ? "join.html" : redirectUser(id, ra);
	}

	// ���ǿ� ��ȿ�� id�� ���� ��, ����Ȩ���� �����̷�Ʈ (Ȩȭ��, �α�����, ȸ���������� get ������� ���� ��)
	public String redirectUser(String id, RedirectAttributes ra) {
		ra.addFlashAttribute("msg", "�߸��� �����Դϴ�. ȸ�� ȭ������ �̵��մϴ�.");
		return "redirect:/" + id;
	}

	// ���� id�� ���� ���, ��������ȭ������ �����̷�Ʈ
	public String redirectFileupload(RedirectAttributes ra) {
		ra.addFlashAttribute("msg", "������ ����Ǿ����ϴ�. �ʱ� ȭ������ �̵��մϴ�.");
		return "redirect:/";
	}

	// ���� id�� �ƴ� ��Ʈ�� ������ ���, ��������ȭ������ �����̷�Ʈ
	public String redirectFileupload(HttpSession session, RedirectAttributes ra) {
		ra.addFlashAttribute("msg", "�߸��� �����Դϴ�. ȸ�� ȭ������ �̵��մϴ�.");
		return "redirect:/" + session.getAttribute("id");
	}

	/*
	 * �α��� ó�� response code 3����: 1)�α��� ����: 200 / 2)���Ѿ���-���̵� Ȥ�� �н����� Ʋ��: 400 /
	 * 3)��Ÿ����: 500
	 */
	public ResponseEntity<String> login(UserBean userBean, HttpServletRequest req) {
		ResponseEntity<String> result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		// DB���� üũ(������ �ȵ� ��� or context close ������ ���, http status�� ������ �ʱ�ȭ�� ���� 500 ����)
		Context context = null;
		try {
			context = new InitialContext();
			context.lookup("java:comp/env/postgresqlJndi");
			if (userMapper.selectUser(userBean).size() == 1) { // id ���� Ȯ��
				if (checkPwd(userBean, userMapper.selectUser(userBean).get(0))) { // pwd ��ġ Ȯ��
					HttpSession session = req.getSession();
					session.setAttribute("id", userBean.getId());
					session.setAttribute("name", userMapper.selectUser(userBean).get(0).getName());
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
	 * ȸ������ ó�� response code 3����: 1)ȸ������ ����: 200 / 2)�Է��׸� ����: 400 / 3)��Ÿ����: 500
	 * status 400 ���� �� - 1001: pk�ߺ� - 1002: data valid Ż�� - 1003: DB���� �ȵ� ���
	 */
	public ResponseEntity<String> join(UserBean userBean, HttpServletRequest req) {
		// ���ҽ� ���� Ŭ���� ���� or � ���̽����� �ش����� �ʴ� ���, http status�� 500���� ������
		ResponseEntity<String> result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		/* null, �����ʰ� �׽�Ʈ */
		// userBean.setId("123456781234qqqqqqqq000000qqqqqqqqqqqq");
		// userBean.setPwd("");
		// userBean.setLevel("222");

		/* �߰��� ��ĭ �ִ� ��� �׽�Ʈ */
		// userBean.setId("H E L L O");
		// userBean.setPwd(" ");
		// userBean.setName("�� ");

		/* ��¥�� �ð� ���� */
		userBean.setRegDate(userBean.getRegDate() + " 00:00:00");
		LinkedHashMap<String, String> invalidJoinMap = joinValidCheck.validCheck(userBean);
		logger.info(String.valueOf(invalidJoinMap.size()));
		if (invalidJoinMap.size() == 0) {
			// DB���� üũ
			Context context = null;
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
				} else { // case 2-1: ID �ߺ� - �����ڵ� 1001
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
			} catch (NamingException e) { // case 2-2: DB���� ���� - �����ڵ� 1003 (context.xml���� driverName �κ� ��Ÿ�� ����)
				logger.info("DB not connected");
				e.printStackTrace();
				JSONObject parent = new JSONObject();
				JSONArray children = new JSONArray();
				JSONObject child = new JSONObject();
				child.put("field", "�����ͺ��̽� ����Ұ�");
				child.put("reason", "�����ڿ��� �����ϼ���.");
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

	// �α���,ȸ������ ����
	public String login(HttpSession session, Model model) {
		model.addAttribute("success", "[" + session.getAttribute("name") + " ��]&nbsp;");
		if (session.getAttribute("join") != null) {
			model.addAttribute("join", "ȸ�������� �Ϸ�Ǿ����ϴ�!");
			session.setAttribute("join", null); // ȸ������ �� ù ȭ�鿡�� welcome �̹��� ǥ��
		}
		return "user.jsp";
	}

	// �α׾ƿ�
	public ModelAndView logout(HttpSession session) {
		session.invalidate();
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/");
		return mav;
	}

	// �α��� �� ��� �޼ҵ� - �н����� ��ġ Ȯ��
	private boolean checkPwd(UserBean loginUser, UserBean dbUser) {
		return loginUser.getPwd().equals(dbUser.getPwd());
	}

}
