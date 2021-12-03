package com.test.yun.mapper;

import java.util.ArrayList;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.test.yun.dto.UserBean;


@Repository
public class UserMapper implements UserMapperInterface { // Ŭ������ ���� �ʿ�
	
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	// ������ �Է�
	@Override
	public void insertUser(UserBean ub) {
		sqlSessionTemplate.getMapper(UserMapperInterface.class).insertUser(ub);
	}
	
	// ������ ��ü ��ȸ
	@Override
	public ArrayList<UserBean> selectAll() {
		//List<UserBean> list = ss.selectList("com.test.yun.selectAll"); // Mapped Statements collection does not contain value for com.test.yun.selectAll
		return sqlSessionTemplate.getMapper(UserMapperInterface.class).selectAll();
	}
	
	// �α���
	@Override
	public ArrayList<UserBean> loginUser(UserBean ub) {
		return sqlSessionTemplate.getMapper(UserMapperInterface.class).loginUser(ub);
		// 1�ܰ�: select �� �� ������ java���� count -- ID ���翩�� Ȯ��
		// 2�ܰ�: select �� pwd�� �Էµ� pwd�� ��ġ�ϴ��� java���� �� -- PWD ��ġ���� Ȯ�� *DB��ȣȭ ó���� ��쿡�� �Ұ����� *���̽� �и�
	}

}
