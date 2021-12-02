package com.test.yun.dto;

public class UserBean {
	private String id; // ID
	private String pwd; // 비밀번호
	private String name; // 이름
	private String level; // 레벨
	private String desc; // 특이사항
	private String regDate; // 등록일자
	
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
