package com.test.yun.service;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.yun.dto.SearchBean;
import com.test.yun.mapper.UserMapper;

@Service
public class SearchService {
	@Autowired
	private UserMapper userMapper;
	
	public String searchBase() {
		List<SearchBean> select = userMapper.selectSearch();
		JSONArray ja = new JSONArray();
		for (int i=0; i < select.size(); i++) {
			JSONObject jo = new JSONObject();
			jo.put("id", select.get(i).getRealName());
			jo.put("value", select.get(i).getDisplayName());
			ja.add(jo);
		}
		return ja.toString();
	}
}
