package com.test.yun.mapper;

import java.util.ArrayList;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.test.yun.dto.UserBean;


@Repository
public class UserMapper implements UserMapperInterface { // 클래스명 변경 필요
	
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	// 데이터 입력
	@Override
	public void insertUser(UserBean ub) {
		sqlSessionTemplate.getMapper(UserMapperInterface.class).insertUser(ub);
	}
	
	// 데이터 전체 조회
	@Override
	public ArrayList<UserBean> selectAll() {
		//List<UserBean> list = ss.selectList("com.test.yun.selectAll"); // Mapped Statements collection does not contain value for com.test.yun.selectAll
		return sqlSessionTemplate.getMapper(UserMapperInterface.class).selectAll();
	}
	
	// 로그인
	@Override
	public ArrayList<UserBean> loginUser(UserBean ub) {
		return sqlSessionTemplate.getMapper(UserMapperInterface.class).loginUser(ub);
		// 1단계: select 된 행 개수를 java에서 count -- ID 존재여부 확인
		// 2단계: select 된 pwd가 입력된 pwd와 일치하는지 java에서 비교 -- PWD 일치여부 확인 *DB암호화 처리한 경우에는 불가능함 *케이스 분리
	}

}
