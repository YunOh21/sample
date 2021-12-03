package com.test.yun.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.test.yun.dto.UserBean;

@Service
public class JoinValidCheck {
	
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
			System.out.println(line.split("/")[0]);
			System.out.println("���л���: Į�� �� ����");
			return false;
		}
		return true;
	}
	
	/* UserBean ������ üũ */
	// ������ Ȯ��
	public LinkedHashMap<String, String> validCheck(UserBean userBean) {
		LinkedHashMap<String, String> invalidJoinMap = new LinkedHashMap<String, String>();
		if (nullCheck(userBean, invalidJoinMap).size()!=0) {
			System.out.println("���л���: null ����");
			System.out.println("map size: "+invalidJoinMap.size());
			System.out.println("ID invalid ����: "+invalidJoinMap.get("ID"));
			System.out.println("PASSWORD invalid ����: "+invalidJoinMap.get("PASSWORD"));
			return invalidJoinMap;
		}
		if (sizeCheck(userBean, invalidJoinMap).size()!=0) {
			System.out.println("���л���: �Է��� �� �ִ� ������ �ʰ��Ͽ����ϴ�.");
			return invalidJoinMap;
		}
		if (timeCheck(userBean, invalidJoinMap).size()!=0) {
			System.out.println("���л���: ������� ������ ���� �ʽ��ϴ�.");
			return invalidJoinMap;
		}
		return invalidJoinMap;
	}

	// NN Į�� null üũ
	public LinkedHashMap<String, String> nullCheck(UserBean userBean, LinkedHashMap<String, String> invalidJoinMap) {
		if (userBean.getId() == null || userBean.getId().equals("")) {
			invalidJoinMap.put("ID", "ID�� �Էµ��� �ʾҽ��ϴ�.");
			System.out.println("map size: "+invalidJoinMap.size());
			System.out.println("ID invalid ����: "+invalidJoinMap.get("ID"));
		}
		if (userBean.getPwd() == null || userBean.getPwd().equals("")) {
			invalidJoinMap.put("PASSWORD", "��й�ȣ�� �Էµ��� �ʾҽ��ϴ�.");
			System.out.println("map size: "+invalidJoinMap.size());
			System.out.println("PASSWORD invalid ����: "+invalidJoinMap.get("PASSWORD"));
		}
		if (userBean.getName() == null || userBean.getName().equals("")) {
			invalidJoinMap.put("�̸�", "�̸��� �Էµ��� �ʾҽ��ϴ�.");
		}
		if (userBean.getLevel() == null || userBean.getLevel().equals("")) {
			invalidJoinMap.put("����", "������ �Էµ��� �ʾҽ��ϴ�.");
		}
		if (userBean.getRegDate() == null || userBean.getRegDate().equals("")) {
			invalidJoinMap.put("�������", "������ڰ� �Էµ��� �ʾҽ��ϴ�.");
		}
		return invalidJoinMap;
	}

	// ������ ������ �ʰ� üũ
	public LinkedHashMap<String, String> sizeCheck(UserBean userBean, LinkedHashMap<String, String> invalidJoinMap) {
		if (userBean.getId().length() > idLength) {
			invalidJoinMap.put("ID", "ID�� �ʹ� ��ϴ�.");
		}
		if (userBean.getPwd().length() > pwdLength) {
			invalidJoinMap.put("PASSWORD", "��й�ȣ�� �ʹ� ��ϴ�.");
		}
		if (userBean.getName().length() > nameLength) {
			invalidJoinMap.put("�̸�", "�̸��� �ʹ� ��ϴ�.");
		}
		if (userBean.getLevel().length() > levelLength) {
			invalidJoinMap.put("����", "������ ���ĺ� �� ���ڸ� �����մϴ�.");
		}
		if (userBean.getDesc().length() > descLength) {
			invalidJoinMap.put("Ư�̻���", "Ư�̻����� �ʹ� ��ϴ�.");
		}
		return invalidJoinMap;
	}

	// Ÿ�ӽ����� ���� üũ
	public LinkedHashMap<String, String> timeCheck(UserBean userBean, LinkedHashMap<String, String> invalidJoinMap) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.parse(userBean.getRegDate());
		} catch (ParseException e) {
			invalidJoinMap.put("�������", "������� ������ Ȯ���� �ּ���.");
		}
		return invalidJoinMap;
	}

}
