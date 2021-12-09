package com.test.yun.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.test.yun.dto.UserBean;

@Service
public class JoinValidCheck {
	private static Logger logger = LoggerFactory.getLogger(JoinValidCheck.class);

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
			logger.info(line.split("/")[0]);
			logger.info("실패사유: 칼럼 수 부족");
			return false;
		}
		return true;
	}
	
	/* UserBean 데이터 체크 */
	// 데이터 확인
	public LinkedHashMap<String, String> validCheck(UserBean userBean) {
		logger.info(String.valueOf(idLength));
		logger.info(String.valueOf(pwdLength));
		logger.info(String.valueOf(nameLength));
		logger.info(String.valueOf(levelLength));
		logger.info(String.valueOf(descLength));
		LinkedHashMap<String, String> invalidJoinMap = new LinkedHashMap<String, String>();
		return timeCheck(userBean, sizeCheck(userBean, spaceCheck(userBean, nullCheck(userBean, invalidJoinMap))));
	}

	// NN 칼럼 null 체크
	public LinkedHashMap<String, String> nullCheck(UserBean userBean, LinkedHashMap<String, String> invalidJoinMap) {
		if (userBean.getId() == null || userBean.getId().equals("")) {
			invalidJoinMap.put("ID", "ID가 입력되지 않았습니다.");
		}
		if (userBean.getPwd() == null || userBean.getPwd().equals("")) {
			invalidJoinMap.put("PASSWORD", "비밀번호가 입력되지 않았습니다.");
		}
		if (userBean.getName() == null || userBean.getName().equals("")) {
			invalidJoinMap.put("이름", "이름이 입력되지 않았습니다.");
		}
		if (userBean.getLevel() == null || userBean.getLevel().equals("")) {
			invalidJoinMap.put("등급", "등급이 입력되지 않았습니다.");
		}
		if (userBean.getRegDate() == null || userBean.getRegDate().equals("")) {
			invalidJoinMap.put("생년월일", "생년월일이 입력되지 않았습니다.");
		}
		return invalidJoinMap;
	}
	
	// 공백 체크
	public LinkedHashMap<String, String> spaceCheck(UserBean userBean, LinkedHashMap<String, String> invalidJoinMap){
		if (!userBean.getId().equals(userBean.getId().replaceAll(" ", ""))) {
			invalidJoinMap.put("ID: "+userBean.getId(), "ID에 빈칸이 있습니다.");
		}
		if (!userBean.getPwd().equals(userBean.getPwd().replaceAll(" ", ""))) {
			invalidJoinMap.put("PASSWORD: "+userBean.getPwd(), "비밀번호에 빈칸이 있습니다.");
		}
		if (!userBean.getName().equals(userBean.getName().replaceAll(" ", ""))) {
			invalidJoinMap.put("이름: "+userBean.getName(), "이름에 빈칸이 있습니다.");
		}
		return invalidJoinMap;
	}

	// 데이터 사이즈 초과 체크
	public LinkedHashMap<String, String> sizeCheck(UserBean userBean, LinkedHashMap<String, String> invalidJoinMap) {
		if (userBean.getId().length() > idLength) {
			invalidJoinMap.put("ID: "+userBean.getId(), "ID가 너무 깁니다.");
		}
		if (userBean.getPwd().length() > pwdLength) {
			invalidJoinMap.put("PASSWORD: "+userBean.getPwd(), "비밀번호가 너무 깁니다.");
		}
		if (userBean.getName().length() > nameLength) {
			invalidJoinMap.put("이름: "+userBean.getName(), "이름이 너무 깁니다.");
		}
		if (userBean.getLevel().length() > levelLength) {
			invalidJoinMap.put("등급: "+userBean.getLevel(), "등급은 알파벳 한 글자만 가능합니다.");
		}
		if (userBean.getDesc().length() > descLength) {
			invalidJoinMap.put("특이사항: "+userBean.getDesc(), "특이사항이 너무 깁니다.");
		}
		return invalidJoinMap;
	}

	// 타임스탬프 형식 체크
	public LinkedHashMap<String, String> timeCheck(UserBean userBean, LinkedHashMap<String, String> invalidJoinMap) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.parse(userBean.getRegDate());
		} catch (ParseException e) {
			invalidJoinMap.put("생년월일: "+userBean.getRegDate(), "생년월일 형식을 확인해 주세요.");
		}
		return invalidJoinMap;
	}

}
