package com.test.yun.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.test.yun.dto.UserBean;

@Service
public class ValidCheck {
	
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
	public boolean isValid(UserBean ub) {
		if (isNull(ub)) {
			System.out.println(ub.getId());
			System.out.println("���л���: null ����");
			return false;
		}
		if (isSizeOver(ub)) {
			System.out.println(ub.getId());
			System.out.println("���л���: �Է��� �� �ִ� ������ �ʰ��Ͽ����ϴ�.");
			return false;
		}
		if (!isTimestamp(ub)) {
			System.out.println(ub.getId());
			System.out.println("���л���: ������� ������ ���� �ʽ��ϴ�.");
			return false;
		}
		return true;
	}

	// NN Į�� null üũ
	public boolean isNull(UserBean ub) {
		if (ub.getId() == null || ub.getId().equals("")) {
			System.out.println("id�� null");
			return true;
		}
		if (ub.getPwd() == null || ub.getPwd().equals("")) {
			System.out.println("pwd�� null");
			return true;
		}
		if (ub.getName() == null || ub.getName().equals("")) {
			System.out.println("name�� null");
			return true;
		}
		if (ub.getLevel() == null || ub.getLevel().equals("")) {
			System.out.println("level�� null");
			return true;
		}
		if (ub.getRegDate() == null || ub.getRegDate().equals("")) {
			System.out.println("������� null");
			return true;
		}
		return false;
	}

	// ������ ������ �ʰ� üũ
	public boolean isSizeOver(UserBean ub) {
		if (ub.getId().length() > idLength) {
			System.out.println("id ������ �ʰ�");
			return true;
		}
		if (ub.getPwd().length() > pwdLength) {
			System.out.println("pwd ������ �ʰ�");
			return true;
		}
		if (ub.getName().length() > nameLength) {
			System.out.println("name ������ �ʰ�");
			return true;
		}
		if (ub.getLevel().length() > levelLength) {
			System.out.println("level ������ �ʰ�");
			return true;
		}
		if (ub.getDesc().length() > descLength) {
			System.out.println("desc ������ �ʰ�");
			return true;
		}
		return false;
	}

	// Ÿ�ӽ����� ���� üũ
	public boolean isTimestamp(UserBean ub) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.parse(ub.getRegDate());
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

}
