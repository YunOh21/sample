package com.test.yun.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.test.yun.dto.FileBean;
import com.test.yun.dto.UserBean;
import com.test.yun.mapper.UserMapper;
import com.test.yun.util.ValidCheck;

@Service
public class FileService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private ValidCheck validCheck;

	// 파일 저장: Mutilpartfile을 File로 변환하여 서버 PC 특정 경로에 저장
	public FileBean save(MultipartFile file) {
		FileBean fb = new FileBean();
		// 기본값: 파일 저장 실패 시 메시지 saveError
		fb.setMsg("saveError");
		if (!file.isEmpty()) {
			try {
				String path = "C:/clair"; // 실서비스라면 os에 따라 경로 설정 필요
				// 서버에 올릴 파일이름 생성
				String reName = makeFileName();
				// 경로가 없는지 확인, 없으면 경로 먼저 생성
				if (makeDir(path)) {
					File rfile = new File(path + "/" + reName + ".dbfile"); // File.separator를 쓰면 좋다: os에 따라 다르게 적용 가능
					// 파일 업로드
					file.transferTo(rfile);
					fb.setMsg("saveOK");
					fb.setFile(rfile);
				}
			} catch (IllegalStateException e) {
				fb.setMsg("saveError: illegal");
				e.printStackTrace();
			} catch (IOException e) {
				fb.setMsg("saveError: ioException");
				e.printStackTrace();
			}
		} else {
			fb.setMsg("saveError: fileIsEmpty");
		}

		return fb;
	}

	// 파일 읽기: 저장한 파일의 데이터를 읽어서 DB에 insert 또는 실패 데이터 확인
	public FileBean read(FileBean fb) {
		// 기본값: 파일 읽기 실패 시 메시지 readError
		fb.setMsg("readError");
		int totalcount = 0;
		int success = 0;
		int colnum = 6;
		Map<Integer, String> fdata = null;

		if (fb.getFile().length() > 0) {
			// try with resources: 자원을 자동으로 반납하므로 close 불요 : AutoCloseable을 상속한 클래스에 적용 가능
			// AutoCloseable 인터페이스를 구현implements하여 사용자가 리소스 클래스를 작성할 수도 있다
			
			// 아래에서 FileInputStream은 InputStream의 자식 클래스, InputStreamReader와 BufferedReader는 Reader의 자식 클래스
			// AutoCloseable을 구현한 Closeable의 구현체이므로 try with resources를 사용할 수 있다
			try (FileInputStream fis = new FileInputStream(fb.getFile());
				InputStreamReader ir = new InputStreamReader(fis, "UTF-8");
				BufferedReader br = new BufferedReader(ir)){
				
				String line = "";

				// 파일 한줄씩 읽어서 DB에 insert
				fdata = new HashMap<Integer, String>();
				while ((line = br.readLine()) != null) {
					totalcount++;
					if (validCheck.isValid(line, colnum)) {
						UserBean ub = makeUserInfo(line);
						if (validCheck.isValid(ub)) {
							if(insertUser(ub)) {
								success++;
								// 만약 이번에 insert성공한 값만 표시하고 싶다고 하면 여기서 line을 저장해주면 될 것
							} else {
								// pk 중복 시 insert 실패
								fdata.put(totalcount, line + " -> 사유: 이미 존재하는 ID입니다.");
							}
						} else {
							System.out.println(line);
							fdata.put(totalcount, line + " -> 사유: 필수값이 비어있거나, 입력할 수 있는 범위를 초과하였습니다.");
						}
					} else {
						fdata.put(totalcount, line + " -> 사유: 필수값이 없습니다.");
					}
				}
				fb.setMsg("readOK");
			} catch (FileNotFoundException e) {
				fb.setMsg("readError: FileNotFoundException");
				e.printStackTrace();
			} catch (IOException e) {
				fb.setMsg("readError: IOException");
				e.printStackTrace();
			} //finally {
				// 최근에는 DBCP(datasource 설정)에서 DB 관련 close 처리를 해주지만, file 처리 할 때는 아래와 같이 null 처리가 반드시 필요
				// java7부터 상기 try with resources 방법을 사용할 수 있게 됨
//				if (fis != null) {
//					try {
//						fis.close();
//					} catch (IOException e) {
//						fb.setMsg("readError: IOException");
//						e.printStackTrace();
//					}
//				}
//				if (ir != null) {
//					try {
//						ir.close();
//					} catch (IOException e) {
//						fb.setMsg("readError: IOException");
//						e.printStackTrace();
//					}
//				}
//				if (br != null) {
//					try {
//						br.close();
//					} catch (IOException e) {
//						fb.setMsg("readError: IOException");
//						e.printStackTrace();
//					}
//				}
			//}
		} else {
			fb.setMsg("readError: no file");
		}
		fb.setTotal(totalcount);
		fb.setSuccess(success);
		fb.setFdata(fdata);
		return fb;
	}

	// 파일 저장: 파일 이름 생성
	public String makeFileName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
		int rand = (int) (Math.random() * 1000);
		String reName = sdf.format(System.currentTimeMillis()) + "_" + rand;
		return reName;
	}

	// 파일 저장: 파일 경로 생성
	public boolean makeDir(String path) {
		File dir = new File(path);

		if (!dir.exists()) {
			try {
				dir.mkdirs(); // *에러처리
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	// 파일 읽기: 데이터 UserBean에 저장
	public UserBean makeUserInfo(String line) {
		String[] rline = line.split("/");
		UserBean ub = new UserBean();
		// 공백에 대한 처리 없음, 필요 시 replaceAll 또는 trim 활용
		ub.setId(rline[0]);
		ub.setPwd(rline[1]);
		ub.setName(rline[2]);
		ub.setLevel(rline[3]);
		ub.setDesc(rline[4]);
		ub.setRegDate(rline[5]);
		return ub;
	}

	// 파일 읽기: UserBean에 담은 데이터 DB에 insert
	public boolean insertUser(UserBean ub) {
		try {
			userMapper.insertUser(ub);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
