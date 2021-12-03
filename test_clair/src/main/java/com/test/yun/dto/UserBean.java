package com.test.yun.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserBean {
	@NotBlank
	@Size(max=16, message="ID�� �ʹ� ��ϴ�.")
	private String id; // ID
	
	@NotBlank
	@Size(max=32, message="��й�ȣ�� �ʹ� ��ϴ�.")
	private String pwd; // ��й�ȣ
	
	@NotBlank
	@Size(max=128, message="�̸��� �ʹ� ��ϴ�.")
	private String name; // �̸�
	
	@NotBlank
	@Size(max=1, message="������ �ѱ��� ���ĺ��� �����մϴ�.")
	private String level; // ����
	
	@Size(max=256, message="Ư�̻����� �ʹ� ��ϴ�.")
	private String desc; // Ư�̻���
	
	private String regDate; // �������
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getName() {
		return name;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getLevel() {
		return level;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getRegDate() {
		return regDate;
	}
	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	
}
