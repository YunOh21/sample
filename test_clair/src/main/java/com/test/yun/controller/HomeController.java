package com.test.yun.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.events.Event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.yun.dto.UserBean;
import com.test.yun.mapper.UserMapper;
import com.test.yun.service.FileService;
import com.test.yun.service.HomeService;
import com.test.yun.util.ValidCheck;

@Controller
public class HomeController {
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);	// � �α�
	
	@Autowired
	private HomeService homeService;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private ValidCheck validCheck;
	
	@Autowired
	private FileService fileService;
	
	// ���� ����
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {
		return homeService.home();
	}
	
	// ����� �α�����
	@RequestMapping(value = "/user/signin", method = RequestMethod.GET)
	public String login() {
		return "login.html";
	}
	
	// ����� �α��� -- ���� ���� ó�� �ʿ�, ���� ��ܿ� �α����� ����� �̸� + �α׾ƿ� ��ư
	@RequestMapping(value = "/user/signin", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> loginResult(@RequestBody UserBean userBean, HttpServletRequest req) {
		ResponseEntity<String> result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		HttpSession session = req.getSession();
		if(userMapper.loginUser(userBean).size()==1) {
			if(homeService.checkPwd(userBean, userMapper.loginUser(userBean).get(0))){
				session.setAttribute("id", userBean.getId());
				session.setAttribute("name", userMapper.loginUser(userBean).get(0).getName());
				Map<String, Object> joMap = new HashMap<String, Object>();
				joMap.put("uri", "/");
				joMap.put("id", userBean.getId());
				JSONObject jo = new JSONObject(joMap);
				result = new ResponseEntity(jo.toString(), HttpStatus.OK);
			}
		}
		System.out.println(result.toString());
		return result;
	}
	
	// ����� ������
	@RequestMapping(value = "/user/signup", method = RequestMethod.GET)
	public String join() {
		return "join.html";
	}
	
	// ����� ���� -- 1001: pk �ߺ� / 1002: data not valid
	@RequestMapping(value = "/user/signup", method = RequestMethod.POST, produces = "application/text; charset=utf8")
	@ResponseBody
	public ResponseEntity<String> joinResult(@RequestBody UserBean userBean) {
		ResponseEntity<String> result;
		//userBean.setRegDate(userBean.getRegDate() + " 00:00:00"); //��¥���� �߸� �׽�Ʈ �� �ּ�ó��
		if(validCheck.isValid(userBean)) {
			if(fileService.insertUser(userBean)) {
				JSONObject jo = new JSONObject();
				jo.put("uri", "/");
				jo.put("id", userBean.getId());
				result = new ResponseEntity(jo.toString(), HttpStatus.OK);
			}else {
				JSONObject parent = new JSONObject();
				JSONArray children = new JSONArray();
				JSONObject child = new JSONObject();
				child.put("field", userBean.getId());
				child.put("reason", "�̹� �����ϴ� ID�Դϴ�.");
				children.add(child);
				parent.put("code", 1001);
				parent.put("message", child.get("reason"));
				parent.put("hasFieldsError", true);
				parent.put("fields", children);
				result = new ResponseEntity(parent.toString(), HttpStatus.BAD_REQUEST);
			}
		}else {
			JSONObject parent = new JSONObject();
			JSONArray children = new JSONArray();
			JSONObject child = new JSONObject();
			child.put("field", "�߸� �Էµ� �Ķ����");
			child.put("reason", "null �Ǵ� sizeOver �Ǵ� ��¥���� �߸�");
			children.add(child);
			parent.put("code", 1002);
			parent.put("message", "�Է��׸� ����");
			parent.put("hasFieldsError", true);
			parent.put("fields", children);
			result = new ResponseEntity(parent.toString(), HttpStatus.BAD_REQUEST);
		}
		System.out.println(result.toString());
		return result;
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
			Map<String, Object> joMap = new HashMap<String, Object>();
			joMap.put("id", user.get(i).getId());
			joMap.put("pwd", user.get(i).getPwd());
			joMap.put("name", user.get(i).getName());
			joMap.put("level", user.get(i).getLevel());
			joMap.put("desc", user.get(i).getDesc());
			joMap.put("regDate", user.get(i).getRegDate());
			JSONObject jo = new JSONObject(joMap);
			// jsonArray�� 1�پ� �߰�
			ja.add(jo);
		}
		return ja.toString();
	}
}
