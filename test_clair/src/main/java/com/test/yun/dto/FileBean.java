package com.test.yun.dto;

import java.io.File;
import java.util.Map;

public class FileBean {
	private int total; // 데이터 전체 개수
	private int success; // 입력 성공 개수
	private Map<Integer, String> fdata; // 실패한 라인의 내용
	private String msg; // 파일 저장 실패 시 메시지
	private File file; // 저장한 파일
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getSuccess() {
		return success;
	}
	public void setSuccess(int success) {
		this.success = success;
	}
	public Map<Integer, String> getFdata() {
		return fdata;
	}
	public void setFdata(Map<Integer, String> fdata) {
		this.fdata = fdata;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
}
