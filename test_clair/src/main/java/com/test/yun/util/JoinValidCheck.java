package com.test.yun.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.test.yun.dto.UserBean;

@Service
public class JoinValidCheck {
	private static Logger logger = LoggerFactory.getLogger(JoinValidCheck.class);

	@Value("${id.length}")
	private int idLength;
	
	@Value("${pwd.length}")
	private int pwdLength;
	
	@Value("${name.length}")
	private int nameLength;
	
	@Value("${level.length}")
	private int levelLength;
	
	@Value("${desc.length}")
	private int descLength;
	
	/* line ������ üũ */
	// Į�� �� üũ
	// 6�� �̸�: split �� makeUserInfo���� UserBean ���� �� null ���� �߻�
	// 6�� �ʰ�: �߸��� �������̹Ƿ� �Է� ���� -- �ʱ� ����. ����� 6�� �̸��� ��쿡�� false
	public boolean isValid(String line, int colnum) {
		if (line.split("/").length < colnum) {
			logger.info(line.split("/")[0]);
			logger.info("���л���: Į�� �� ����");
			return false;
		}
		return true;
	}
	
	/* UserBean ������ üũ */
	// ������ Ȯ��
	public LinkedHashMap<String, String> validCheck(UserBean userBean) {
		logger.info(String.valueOf(idLength));
		logger.info(String.valueOf(pwdLength));
		logger.info(String.valueOf(nameLength));
		logger.info(String.valueOf(levelLength));
		logger.info(String.valueOf(descLength));
		LinkedHashMap<String, String> invalidJoinMap = new LinkedHashMap<String, String>();
		return timeCheck(userBean, sizeCheck(userBean, spaceCheck(userBean, nullCheck(userBean, invalidJoinMap))));
	}

	// NN Į�� null üũ
	public LinkedHashMap<String, String> nullCheck(UserBean userBean, LinkedHashMap<String, String> invalidJoinMap) {
		if (userBean.getId() == null || userBean.getId().equals("")) {
			invalidJoinMap.put("ID", "ID�� �Էµ��� �ʾҽ��ϴ�.");
		}
		if (userBean.getPwd() == null || userBean.getPwd().equals("")) {
			invalidJoinMap.put("PASSWORD", "��й�ȣ�� �Էµ��� �ʾҽ��ϴ�.");
		}
		if (userBean.getName() == null || userBean.getName().equals("")) {
			invalidJoinMap.put("�̸�", "�̸��� �Էµ��� �ʾҽ��ϴ�.");
		}
		if (userBean.getLevel() == null || userBean.getLevel().equals("")) {
			invalidJoinMap.put("���", "����� �Էµ��� �ʾҽ��ϴ�.");
		}
		if (userBean.getRegDate() == null || userBean.getRegDate().equals("")) {
			invalidJoinMap.put("�������", "��������� �Էµ��� �ʾҽ��ϴ�.");
		}
		return invalidJoinMap;
	}
	
	// ���� üũ
	public LinkedHashMap<String, String> spaceCheck(UserBean userBean, LinkedHashMap<String, String> invalidJoinMap){
		if (!userBean.getId().equals(userBean.getId().replaceAll(" ", ""))) {
			invalidJoinMap.put("ID: "+userBean.getId(), "ID�� ��ĭ�� �ֽ��ϴ�.");
		}
		if (!userBean.getPwd().equals(userBean.getPwd().replaceAll(" ", ""))) {
			invalidJoinMap.put("PASSWORD: "+userBean.getPwd(), "��й�ȣ�� ��ĭ�� �ֽ��ϴ�.");
		}
		if (!userBean.getName().equals(userBean.getName().replaceAll(" ", ""))) {
			invalidJoinMap.put("�̸�: "+userBean.getName(), "�̸��� ��ĭ�� �ֽ��ϴ�.");
		}
		return invalidJoinMap;
	}

	// ������ ������ �ʰ� üũ
	public LinkedHashMap<String, String> sizeCheck(UserBean userBean, LinkedHashMap<String, String> invalidJoinMap) {
		if (userBean.getId().length() > idLength) {
			invalidJoinMap.put("ID: "+userBean.getId(), "ID�� �ʹ� ��ϴ�.");
		}
		if (userBean.getPwd().length() > pwdLength) {
			invalidJoinMap.put("PASSWORD: "+userBean.getPwd(), "��й�ȣ�� �ʹ� ��ϴ�.");
		}
		if (userBean.getName().length() > nameLength) {
			invalidJoinMap.put("�̸�: "+userBean.getName(), "�̸��� �ʹ� ��ϴ�.");
		}
		if (userBean.getLevel().length() > levelLength) {
			invalidJoinMap.put("���: "+userBean.getLevel(), "����� ���ĺ� �� ���ڸ� �����մϴ�.");
		}
		if (userBean.getDesc().length() > descLength) {
			invalidJoinMap.put("Ư�̻���: "+userBean.getDesc(), "Ư�̻����� �ʹ� ��ϴ�.");
		}
		return invalidJoinMap;
	}

	// Ÿ�ӽ����� ���� üũ
	public LinkedHashMap<String, String> timeCheck(UserBean userBean, LinkedHashMap<String, String> invalidJoinMap) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.parse(userBean.getRegDate());
		} catch (ParseException e) {
			invalidJoinMap.put("�������: "+userBean.getRegDate(), "������� ������ Ȯ���� �ּ���.");
		}
		return invalidJoinMap;
	}

}
