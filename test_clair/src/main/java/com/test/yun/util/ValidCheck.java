package com.test.yun.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.test.yun.dto.UserBean;

@Service
public class ValidCheck {
	
	@Value("${id.length}")
	private int idLength;
	
	@Value("${pwd.length}")
	private int pwdLength;
	
	@Value("${name.length}")
	private int nameLength;
	
	@Value("${level.length}")
	private int levelLength;
	
	@Value("${desc.length}")
	private int descLength;
	
	/* line 데이터 체크 */
	// 칼럼 수 체크
	// 6개 미만: split 후 makeUserInfo에서 UserBean 리턴 시 null 에러 발생
	// 6개 초과: 잘못된 데이터이므로 입력 방지 -- 초기 버전. 현재는 6개 미만인 경우에만 false
	public boolean isValid(String line, int colnum) {
		if (line.split("/").length < colnum) {
			System.out.println(line.split("/")[0]);
			System.out.println("실패사유: 칼럼 수 부족");
			return false;
		}
		return true;
	}
	
	/* UserBean 데이터 체크 */
	// 데이터 확인
	public boolean isValid(UserBean ub) {
		if (isNull(ub)) {
			System.out.println(ub.getId());
			System.out.println("실패사유: null 있음");
			return false;
		}
		if (isSizeOver(ub)) {
			System.out.println(ub.getId());
			System.out.println("실패사유: 입력할 수 있는 범위를 초과하였습니다.");
			return false;
		}
		if (!isTimestamp(ub)) {
			System.out.println(ub.getId());
			System.out.println("실패사유: 등록일자 형식이 맞지 않습니다.");
			return false;
		}
		return true;
	}

	// NN 칼럼 null 체크
	public boolean isNull(UserBean ub) {
		if (ub.getId() == null || ub.getId().equals("")) {
			System.out.println("id가 null");
			return true;
		}
		if (ub.getPwd() == null || ub.getPwd().equals("")) {
			System.out.println("pwd가 null");
			return true;
		}
		if (ub.getName() == null || ub.getName().equals("")) {
			System.out.println("name이 null");
			return true;
		}
		if (ub.getLevel() == null || ub.getLevel().equals("")) {
			System.out.println("level이 null");
			return true;
		}
		if (ub.getRegDate() == null || ub.getRegDate().equals("")) {
			System.out.println("등록일자 null");
			return true;
		}
		return false;
	}

	// 데이터 사이즈 초과 체크
	public boolean isSizeOver(UserBean ub) {
		if (ub.getId().length() > idLength) {
			System.out.println("id 데이터 초과");
			return true;
		}
		if (ub.getPwd().length() > pwdLength) {
			System.out.println("pwd 데이터 초과");
			return true;
		}
		if (ub.getName().length() > nameLength) {
			System.out.println("name 데이터 초과");
			return true;
		}
		if (ub.getLevel().length() > levelLength) {
			System.out.println("level 데이터 초과");
			return true;
		}
		if (ub.getDesc().length() > descLength) {
			System.out.println("desc 데이터 초과");
			return true;
		}
		return false;
	}

	// 타임스탬프 형식 체크
	public boolean isTimestamp(UserBean ub) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.parse(ub.getRegDate());
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

}
