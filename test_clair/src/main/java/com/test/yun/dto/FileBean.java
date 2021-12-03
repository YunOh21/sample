package com.test.yun.dto;

import java.io.File;
import java.util.Map;

public class FileBean {
	private int total; // ������ ��ü ����
	private int success; // �Է� ���� ����
	private Map<Integer, String> fdata; // ������ ������ ����
	private String msg; // ���� ���� ���� �� �޽���
	private File file; // ������ ����
	
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
