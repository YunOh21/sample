package com.test.yun.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.test.yun.dto.FileBean;
import com.test.yun.dto.UserBean;
import com.test.yun.mapper.UserMapper;

@Service
public class UploadService {
	
	private static Logger logger = LoggerFactory.getLogger(UploadService.class);

	@Autowired
	private FileService fileService;
	
	@Autowired
	private UserMapper userMapper;
	
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
	
	// 성공 시 이동한 페이지에서 유저 정보를 ajax로 조회
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

}
