package com.test.yun.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.test.yun.dto.UserBean;
import com.test.yun.dto.InvalidBean;

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
	public ArrayList<InvalidBean> validCheck(UserBean userBean) {
		ArrayList<InvalidBean> invalidList = new ArrayList<InvalidBean>();
		if (nullCheck(userBean, invalidList).size()!=0) {
			System.out.println(userBean.getId());
			System.out.println("���л���: null ����");
			return invalidList;
		}
		if (sizeCheck(userBean, invalidList).size()!=0) {
			System.out.println(userBean.getId());
			System.out.println("���л���: �Է��� �� �ִ� ������ �ʰ��Ͽ����ϴ�.");
			return invalidList;
		}
		if (timeCheck(userBean, invalidList).size()!=0) {
			System.out.println(userBean.getId());
			System.out.println("���л���: ������� ������ ���� �ʽ��ϴ�.");
			return invalidList;
		}
		return invalidList;
	}

	// NN Į�� null üũ
	public ArrayList<InvalidBean> nullCheck(UserBean userBean, ArrayList<InvalidBean> invalidList) {
		InvalidBean validBean = new InvalidBean();
		if (userBean.getId() == null || userBean.getId().equals("")) {
			validBean.setInvalidField("ID");
			validBean.setInvalidReason("ID�� �Էµ��� �ʾҽ��ϴ�.");
			invalidList.add(validBean);
		}
		if (userBean.getPwd() == null || userBean.getPwd().equals("")) {
			validBean.setInvalidField("PASSWORD");
			validBean.setInvalidReason("��й�ȣ�� �Էµ��� �ʾҽ��ϴ�.");
			invalidList.add(validBean);
		}
		if (userBean.getName() == null || userBean.getName().equals("")) {
			validBean.setInvalidField("�̸�");
			validBean.setInvalidReason("�̸��� �Էµ��� �ʾҽ��ϴ�.");
			invalidList.add(validBean);
		}
		if (userBean.getLevel() == null || userBean.getLevel().equals("")) {
			validBean.setInvalidField("����");
			validBean.setInvalidReason("������ �Էµ��� �ʾҽ��ϴ�.");
			invalidList.add(validBean);
		}
		if (userBean.getRegDate() == null || userBean.getRegDate().equals("")) {
			validBean.setInvalidField("�������");
			validBean.setInvalidReason("������ڰ� �Էµ��� �ʾҽ��ϴ�.");
			invalidList.add(validBean);
		}
		return invalidList;
	}

	// ������ ������ �ʰ� üũ
	public ArrayList<InvalidBean> sizeCheck(UserBean userBean, ArrayList<InvalidBean> invalidList) {
		InvalidBean validBean = new InvalidBean();
		if (userBean.getId().length() > idLength) {
			validBean.setInvalidField("ID");
			validBean.setInvalidReason("ID�� �ʹ� ��ϴ�.");
			invalidList.add(validBean);
		}
		if (userBean.getPwd().length() > pwdLength) {
			validBean.setInvalidField("PASSWORD");
			validBean.setInvalidReason("��й�ȣ�� �ʹ� ��ϴ�.");
			invalidList.add(validBean);
		}
		if (userBean.getName().length() > nameLength) {
			validBean.setInvalidField("�̸�");
			validBean.setInvalidReason("�̸��� �ʹ� ��ϴ�.");
			invalidList.add(validBean);
		}
		if (userBean.getLevel().length() > levelLength) {
			validBean.setInvalidField("����");
			validBean.setInvalidReason("������ �ʹ� ��ϴ�.");
			invalidList.add(validBean);
		}
		if (userBean.getDesc().length() > descLength) {
			validBean.setInvalidField("Ư�̻���");
			validBean.setInvalidReason("Ư�̻����� �ʹ� ��ϴ�.");
			invalidList.add(validBean);
		}
		return invalidList;
	}

	// Ÿ�ӽ����� ���� üũ
	public ArrayList<InvalidBean> timeCheck(UserBean userBean, ArrayList<InvalidBean> invalidList) {
		InvalidBean validBean = new InvalidBean();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.parse(userBean.getRegDate());
		} catch (ParseException e) {
			validBean.setInvalidField("�������");
			validBean.setInvalidReason("������� ������ Ȯ���� �ּ���.");
			invalidList.add(validBean);
		}
		return invalidList;
	}

}
