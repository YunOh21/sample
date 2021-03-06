package com.test.yun.mapper;

import java.util.ArrayList;

import com.test.yun.dto.SearchBean;
import com.test.yun.dto.UserBean;


public interface UserMapperInterface {
	public void insertUser(UserBean ub);
	public ArrayList<UserBean> selectAll();
	public ArrayList<UserBean> selectUser(UserBean ub);
	public ArrayList<SearchBean> selectSearch();
}
