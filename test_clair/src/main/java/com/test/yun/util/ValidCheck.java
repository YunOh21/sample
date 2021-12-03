package com.test.yun.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.test.yun.dto.UserBean;
import com.test.yun.dto.InvalidBean;

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
	public ArrayList<InvalidBean> validCheck(UserBean userBean) {
		ArrayList<InvalidBean> invalidList = new ArrayList<InvalidBean>();
		if (nullCheck(userBean, invalidList).size()!=0) {
			System.out.println(userBean.getId());
			System.out.println("실패사유: null 있음");
			return invalidList;
		}
		if (sizeCheck(userBean, invalidList).size()!=0) {
			System.out.println(userBean.getId());
			System.out.println("실패사유: 입력할 수 있는 범위를 초과하였습니다.");
			return invalidList;
		}
		if (timeCheck(userBean, invalidList).size()!=0) {
			System.out.println(userBean.getId());
			System.out.println("실패사유: 등록일자 형식이 맞지 않습니다.");
			return invalidList;
		}
		return invalidList;
	}

	// NN 칼럼 null 체크
	public ArrayList<InvalidBean> nullCheck(UserBean userBean, ArrayList<InvalidBean> invalidList) {
		InvalidBean validBean = new InvalidBean();
		if (userBean.getId() == null || userBean.getId().equals("")) {
			validBean.setInvalidField("ID");
			validBean.setInvalidReason("ID가 입력되지 않았습니다.");
			invalidList.add(validBean);
		}
		if (userBean.getPwd() == null || userBean.getPwd().equals("")) {
			validBean.setInvalidField("PASSWORD");
			validBean.setInvalidReason("비밀번호가 입력되지 않았습니다.");
			invalidList.add(validBean);
		}
		if (userBean.getName() == null || userBean.getName().equals("")) {
			validBean.setInvalidField("이름");
			validBean.setInvalidReason("이름이 입력되지 않았습니다.");
			invalidList.add(validBean);
		}
		if (userBean.getLevel() == null || userBean.getLevel().equals("")) {
			validBean.setInvalidField("레벨");
			validBean.setInvalidReason("레벨이 입력되지 않았습니다.");
			invalidList.add(validBean);
		}
		if (userBean.getRegDate() == null || userBean.getRegDate().equals("")) {
			validBean.setInvalidField("등록일자");
			validBean.setInvalidReason("등록일자가 입력되지 않았습니다.");
			invalidList.add(validBean);
		}
		return invalidList;
	}

	// 데이터 사이즈 초과 체크
	public ArrayList<InvalidBean> sizeCheck(UserBean userBean, ArrayList<InvalidBean> invalidList) {
		InvalidBean validBean = new InvalidBean();
		if (userBean.getId().length() > idLength) {
			validBean.setInvalidField("ID");
			validBean.setInvalidReason("ID가 너무 깁니다.");
			invalidList.add(validBean);
		}
		if (userBean.getPwd().length() > pwdLength) {
			validBean.setInvalidField("PASSWORD");
			validBean.setInvalidReason("비밀번호가 너무 깁니다.");
			invalidList.add(validBean);
		}
		if (userBean.getName().length() > nameLength) {
			validBean.setInvalidField("이름");
			validBean.setInvalidReason("이름이 너무 깁니다.");
			invalidList.add(validBean);
		}
		if (userBean.getLevel().length() > levelLength) {
			validBean.setInvalidField("레벨");
			validBean.setInvalidReason("레벨이 너무 깁니다.");
			invalidList.add(validBean);
		}
		if (userBean.getDesc().length() > descLength) {
			validBean.setInvalidField("특이사항");
			validBean.setInvalidReason("특이사항이 너무 깁니다.");
			invalidList.add(validBean);
		}
		return invalidList;
	}

	// 타임스탬프 형식 체크
	public ArrayList<InvalidBean> timeCheck(UserBean userBean, ArrayList<InvalidBean> invalidList) {
		InvalidBean validBean = new InvalidBean();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.parse(userBean.getRegDate());
		} catch (ParseException e) {
			validBean.setInvalidField("등록일자");
			validBean.setInvalidReason("등록일자 형식을 확인해 주세요.");
			invalidList.add(validBean);
		}
		return invalidList;
	}

}
