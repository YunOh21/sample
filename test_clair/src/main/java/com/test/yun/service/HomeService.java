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

	// Ȩȭ�� = �������� ȭ��
	public ModelAndView fileupload(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		if(session.getAttribute("id")!=null) {
			mav.addObject("success", "["+session.getAttribute("name")+" ��]&nbsp;");
			mav.addObject("userId", session.getAttribute("id"));
		}
		mav.setViewName("fileupload.jsp");
		return mav;
	}
	
	// �α�����
	public String login(HttpSession session, RedirectAttributes ra) {
		String id = (String)session.getAttribute("id");
		return id==null? "login.html" : redirectUser(id, ra);
	}
	
	// ȸ��������
	public String join(HttpSession session, RedirectAttributes ra) {
		String id = (String)session.getAttribute("id");
		return id==null? "join.html" : redirectUser(id, ra);
	}
	
	// ���ǿ� ��ȿ�� id�� ���� ��, ����Ȩ���� �����̷�Ʈ (Ȩȭ��, �α�����, ȸ���������� get ������� ���� ��)
	public String redirectUser(String id, RedirectAttributes ra) {
		ra.addFlashAttribute("msg", "�߸��� �����Դϴ�. ȸ�� ȭ������ �̵��մϴ�.");
		return "redirect:/" + id;
	}
	
	// ���� id�� ���� ���, ��������ȭ������ �����̷�Ʈ
	public String redirectFileupload(RedirectAttributes ra) {
		ra.addFlashAttribute("msg", "�߸��� �����Դϴ�. �ʱ� ȭ������ �̵��մϴ�.");
		return "redirect:/";
	}
	
	// ���� id�� �ƴ� ��Ʈ�� ������ ���, ��������ȭ������ �����̷�Ʈ
	public String redirectFileupload(HttpSession session, RedirectAttributes ra) {
		ra.addFlashAttribute("msg", "�߸��� �����Դϴ�. ȸ�� ȭ������ �̵��մϴ�.");
		return "redirect:/" + session.getAttribute("id");
	}
	
	// �α���,ȸ������ ����
	public String login(HttpSession session, Model model) {
		model.addAttribute("success", "["+session.getAttribute("name")+" ��]&nbsp;");
		if(session.getAttribute("join")!=null) {
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

	// dbfile ���ε�
	public ModelAndView upload(MultipartFile file) {
		FileBean fb = null;
		// �⺻��: ���� ����/�б� ���� �� Ȩ�������� ���ư�
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
	
	// ���� ���ε� ����: ���ε� �����ߴٴ� ���� ǥ��, Ȩ������ �̵�
	private ModelAndView uploadFail() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("uploadFail", "���� ���ε忡 �����߽��ϴ�. �ٽ� ó������ �������ּ���.");
		mav.setViewName("home.jsp");
		return mav;
	}

	// dbfile ������ �Է� ��� ����
	private ModelAndView insertPass(FileBean fb) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("total", fb.getTotal());
		mav.addObject("success", fb.getSuccess());
		mav.setViewName("pass.jsp");
		return mav;
	}

	// dbfile ������ �Է� �Ϻ� �Ǵ� ��� ����
	private ModelAndView insertFail(FileBean fb) {
		ModelAndView mav = new ModelAndView();
		// json ��ü ����
		JSONArray ja = new JSONArray();

		// HashMap���� key/value ����
		//String cause = "";
		for (Entry<Integer, String> entry : fb.getFdata().entrySet()) {
			Map<String, Object> joMap = new HashMap<String, Object>();
			// cause += entry.getKey() + "�� ����: " + entry.getValue() + "\r\n";
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
	
	// �α��� �� ��� �޼ҵ� - �н����� ��ġ Ȯ��
	public boolean checkPwd(UserBean loginUser, UserBean dbUser) {
		return loginUser.getPwd().equals(dbUser.getPwd());
	}

}
