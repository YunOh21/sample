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

	// ���� ����: Mutilpartfile�� File�� ��ȯ�Ͽ� ���� PC Ư�� ��ο� ����
	public FileBean save(MultipartFile file) {
		FileBean fb = new FileBean();
		// �⺻��: ���� ���� ���� �� �޽��� saveError
		fb.setMsg("saveError");
		if (!file.isEmpty()) {
			try {
				String path = "C:/clair"; // �Ǽ��񽺶�� os�� ���� ��� ���� �ʿ�
				// ������ �ø� �����̸� ����
				String reName = makeFileName();
				// ��ΰ� ������ Ȯ��, ������ ��� ���� ����
				if (makeDir(path)) {
					File rfile = new File(path + "/" + reName + ".dbfile"); // File.separator�� ���� ����: os�� ���� �ٸ��� ���� ����
					// ���� ���ε�
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

	// ���� �б�: ������ ������ �����͸� �о DB�� insert �Ǵ� ���� ������ Ȯ��
	public FileBean read(FileBean fb) {
		// �⺻��: ���� �б� ���� �� �޽��� readError
		fb.setMsg("readError");
		int totalcount = 0;
		int success = 0;
		int colnum = 6;
		Map<Integer, String> fdata = null;

		if (fb.getFile().length() > 0) {
			// try with resources: �ڿ��� �ڵ����� �ݳ��ϹǷ� close �ҿ� : AutoCloseable�� ����� Ŭ������ ���� ����
			// AutoCloseable �������̽��� ����implements�Ͽ� ����ڰ� ���ҽ� Ŭ������ �ۼ��� ���� �ִ�
			
			// �Ʒ����� FileInputStream�� InputStream�� �ڽ� Ŭ����, InputStreamReader�� BufferedReader�� Reader�� �ڽ� Ŭ����
			// AutoCloseable�� ������ Closeable�� ����ü�̹Ƿ� try with resources�� ����� �� �ִ�
			try (FileInputStream fis = new FileInputStream(fb.getFile());
				InputStreamReader ir = new InputStreamReader(fis, "UTF-8");
				BufferedReader br = new BufferedReader(ir)){
				
				String line = "";

				// ���� ���پ� �о DB�� insert
				fdata = new HashMap<Integer, String>();
				while ((line = br.readLine()) != null) {
					totalcount++;
					if (validCheck.isValid(line, colnum)) {
						UserBean ub = makeUserInfo(line);
						if (validCheck.isValid(ub)) {
							if(insertUser(ub)) {
								success++;
								// ���� �̹��� insert������ ���� ǥ���ϰ� �ʹٰ� �ϸ� ���⼭ line�� �������ָ� �� ��
							} else {
								// pk �ߺ� �� insert ����
								fdata.put(totalcount, line + " -> ����: �̹� �����ϴ� ID�Դϴ�.");
							}
						} else {
							System.out.println(line);
							fdata.put(totalcount, line + " -> ����: �ʼ����� ����ְų�, �Է��� �� �ִ� ������ �ʰ��Ͽ����ϴ�.");
						}
					} else {
						fdata.put(totalcount, line + " -> ����: �ʼ����� �����ϴ�.");
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
				// �ֱٿ��� DBCP(datasource ����)���� DB ���� close ó���� ��������, file ó�� �� ���� �Ʒ��� ���� null ó���� �ݵ�� �ʿ�
				// java7���� ��� try with resources ����� ����� �� �ְ� ��
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

	// ���� ����: ���� �̸� ����
	public String makeFileName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
		int rand = (int) (Math.random() * 1000);
		String reName = sdf.format(System.currentTimeMillis()) + "_" + rand;
		return reName;
	}

	// ���� ����: ���� ��� ����
	public boolean makeDir(String path) {
		File dir = new File(path);

		if (!dir.exists()) {
			try {
				dir.mkdirs(); // *����ó��
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	// ���� �б�: ������ UserBean�� ����
	public UserBean makeUserInfo(String line) {
		String[] rline = line.split("/");
		UserBean ub = new UserBean();
		// ���鿡 ���� ó�� ����, �ʿ� �� replaceAll �Ǵ� trim Ȱ��
		ub.setId(rline[0]);
		ub.setPwd(rline[1]);
		ub.setName(rline[2]);
		ub.setLevel(rline[3]);
		ub.setDesc(rline[4]);
		ub.setRegDate(rline[5]);
		return ub;
	}

	// ���� �б�: UserBean�� ���� ������ DB�� insert
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
