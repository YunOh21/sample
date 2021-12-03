package com.test.yun.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserBean {
	@NotBlank
	@Size(max=16, message="ID가 너무 깁니다.")
	private String id; // ID
	
	@NotBlank
	@Size(max=32, message="비밀번호가 너무 깁니다.")
	private String pwd; // 비밀번호
	
	@NotBlank
	@Size(max=128, message="이름이 너무 깁니다.")
	private String name; // 이름
	
	@NotBlank
	@Size(max=1, message="레벨은 한글자 알파벳만 가능합니다.")
	private String level; // 레벨
	
	@Size(max=256, message="특이사항이 너무 깁니다.")
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
