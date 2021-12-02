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
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);	// 운영 로그
	
	@Autowired
	private HomeService homeService;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private ValidCheck validCheck;
	
	@Autowired
	private FileService fileService;
	
	// 최초 접속
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {
		return homeService.home();
	}
	
	// 사용자 로그인폼
	@RequestMapping(value = "/user/signin", method = RequestMethod.GET)
	public String login() {
		return "login.html";
	}
	
	// 사용자 로그인 -- 세션 인증 처리 필요, 우측 상단에 로그인한 사용자 이름 + 로그아웃 버튼
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
	
	// 사용자 가입폼
	@RequestMapping(value = "/user/signup", method = RequestMethod.GET)
	public String join() {
		return "join.html";
	}
	
	// 사용자 가입 -- 1001: pk 중복 / 1002: data not valid
	@RequestMapping(value = "/user/signup", method = RequestMethod.POST, produces = "application/text; charset=utf8")
	@ResponseBody
	public ResponseEntity<String> joinResult(@RequestBody UserBean userBean) {
		ResponseEntity<String> result;
		//userBean.setRegDate(userBean.getRegDate() + " 00:00:00"); //날짜형식 잘못 테스트 시 주석처리
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
				child.put("reason", "이미 존재하는 ID입니다.");
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
			child.put("field", "잘못 입력된 파라미터");
			child.put("reason", "null 또는 sizeOver 또는 날짜형식 잘못");
			children.add(child);
			parent.put("code", 1002);
			parent.put("message", "입력항목 오류");
			parent.put("hasFieldsError", true);
			parent.put("fields", children);
			result = new ResponseEntity(parent.toString(), HttpStatus.BAD_REQUEST);
		}
		System.out.println(result.toString());
		return result;
	}
	
	// 로그인, 회원가입 후
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView userHome(HttpSession session) {
		return homeService.login(session);
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
			Map<String, Object> joMap = new HashMap<String, Object>();
			joMap.put("id", user.get(i).getId());
			joMap.put("pwd", user.get(i).getPwd());
			joMap.put("name", user.get(i).getName());
			joMap.put("level", user.get(i).getLevel());
			joMap.put("desc", user.get(i).getDesc());
			joMap.put("regDate", user.get(i).getRegDate());
			JSONObject jo = new JSONObject(joMap);
			// jsonArray에 1줄씩 추가
			ja.add(jo);
		}
		return ja.toString();
	}
}
