package com.test.yun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.yun.service.SearchService;

@Controller
public class SearchController {
	@Autowired
	private SearchService searchService;
	
	// ���� �˻� ����Ʈ�ڽ� ajax
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/text; charset=utf8")
	@ResponseBody
	public String searchBase() {
		return searchService.searchBase();
	}
}
