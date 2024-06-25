package com.dev_cbj.springsocketio.util.func;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


public class FileUtil {
	
	/*public List<FileVO> getWorkFileExt(List<FileVO> flist, HttpServletRequest request) {
		if(!flist.isEmpty()) {
			String ext = flist.get(0).getF_snm().substring(flist.get(0).getF_snm().lastIndexOf(".") + 1,flist.get(0).getF_snm().length());
			flist.get(0).setFile_ext(ext);
			flist.get(0).setImage_icon(GetFileIcon(request , flist.get(0).getF_nm()));
			return flist;
		}else {
			return null;
		}
	}*/
	
	public String GetFileIcon(HttpServletRequest request, String fileName){
		String strName = fileName.substring(0 , fileName.lastIndexOf(".") - 1);
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		String strFileIcon = "multi_icon_" + ext.toLowerCase() + ".gif";
		
		String DirectoryPath = request.getSession().getServletContext().getRealPath("/"+"resources"+"/"+"icon"+"/");
		//String DirectoryPath = File.separator + "resources" + File.separator + "icon" + File.separator;
		String FullPath = DirectoryPath + strFileIcon;
		
		File file = new File(FullPath);
		if(file.exists()) {
			return "/resources/icon/" + strFileIcon;
		}else {
			return "/resources/icon/" + "multi_icon_file.gif";
		}
	}
	
	// 파일 확장자 체크
	public String fileExtCheck(MultipartHttpServletRequest file, String type) {
		Iterator<String> iter = file.getFileNames();
		MultipartFile mFile = null;
		String result = "Y";

		while (iter.hasNext()) {
			String uploadFileName = iter.next();
			mFile = file.getFile(uploadFileName);
			if (mFile == null || mFile.getOriginalFilename() == null) continue;
			File nfile = new File(mFile.getOriginalFilename());
			
			if(type == null || type.isEmpty()) {
				if(uploadFileName.equals("thumb")) type = "img";
				else if(uploadFileName.equals("ir_vod")) type = "vod";
				else if(uploadFileName.equals("dirlink_upload")) type = "xls";
				else type = "nomal";
			}
			
			if (mFile.getSize() > 0) {
				if (badFileExtIsReturnBoolean(nfile, FileExtType(type))) {
					System.out.println("확장자 통과");
				} else {
					System.out.println("확장자 불통과");
					result = "N";
					break;
				}
			}
		}
		return result;
		
	}
	//확장자 타입
	public String[] FileExtType(String type) {
		String[] temp;
		String[] imgEXT = {"jpg", "jpeg", "gif", "png"};
		String[] vodEXT = {"mp4"};
		String[] xlsEXT = {"xls", "xlsx"};
		String[] pdfEXT = {"pdf"};
		String[] nomalEXT = {"jpg", "jpeg", "gif", "png", "xls", "xlsx", "pdf", "hwp", "ppt", "pptx", "doc", "docx"};
		String[] allEXT = {"jpg", "jpeg", "gif", "png", "hwp", "ppt", "pptx", "xls", "xlsx", "js", "doc", "pdf",
				"docx", "mp4", "wmv", "zip", "arj", "rar", "tar", "7z"};
		
		if(Objects.equals(type, "img")) temp = imgEXT;
		else if(Objects.equals(type, "vod")) temp = vodEXT;
		else if(Objects.equals(type, "xls")) temp = xlsEXT;
		else if(Objects.equals(type, "pdf")) temp = pdfEXT;
		else if(Objects.equals(type, "nomal")) temp = nomalEXT;
		else if (Objects.equals(type, "all")) temp = allEXT;
		else temp = allEXT;
		
		return temp;
	}

	//확장자 체크하는 함수
	public boolean badFileExtIsReturnBoolean(File file, String[] BAD_EXTENSION) {
		String fileName = file.getName();
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
		int count = 0;
		
		int len = BAD_EXTENSION.length;
		for (int i = 0; i < len; i++) {
			if (ext.equalsIgnoreCase(BAD_EXTENSION[i])) {
				count++;
				break;
			}
		}

		if (count == 1) return true;
		else return false;
	}
	/*
	public String makeThumbnail(String filePath, String fileName, String fileExt,String rootpath, int maxWidth , int maxHeight , String Kind) throws Exception {
		// 저장된 원본파일로부터 BufferedImage 객체를 생성합니다.
		// BufferedImage srcImg : 실제 이미지
		// int maxWidth : 가로 맥스값
		// int maxHeight : 세로 맥스값
		// String Kind : 가로와 세로 중 어느쪽을 기준으로 잡을것인지 W나 H 입력. W:가로, H:세로, A: 자동
		//String Kind = "H";
		String thumbName ="";
		String path ="";
		String file="P";
		BufferedImage srcImg = ImageIO.read(new File(filePath));
		BufferedImage destImg = null;

		// 원본 이미지의 높이와 너비
		int srcWidth = srcImg.getWidth();
		int srcHeight = srcImg.getHeight();

		int destWidth = srcWidth;
		int destHeight = srcHeight;
		
		if(maxWidth < destWidth && maxHeight > destHeight) {
			Kind = "W";
		}else if(maxWidth > destWidth && maxHeight < destHeight) {
			Kind = "H";
		}

		if(maxWidth < destWidth || maxHeight < destHeight) {
			if(Kind.equals("W")) {
				destImg = Scalr.resize(srcImg, Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, maxWidth, Scalr.OP_ANTIALIAS);
			} else if(Kind.equals("H")) {
				destImg = Scalr.resize(srcImg, Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_HEIGHT, maxHeight, Scalr.OP_ANTIALIAS);
			} else if(Kind.equals("A")) {
				// 가로 우선으로 적용함. 비율은 고정
				if(srcWidth > maxWidth) {
					double ratio = (double)maxWidth/(double)srcWidth; // 둘 다 double로 캐스팅 안하고 연산 후에 캐스팅하면 0으로 오류.
					destWidth = maxWidth;
					destHeight = (int)(srcHeight*ratio);
				}else if(destHeight > maxHeight) {
					double ratio = (double)maxHeight/(double)destHeight;
					destHeight = maxHeight;
					destWidth = (int)(destWidth * ratio);
				}
				destImg = Scalr.resize(srcImg, destWidth, destHeight);
			}
			
			path=File.separator + fileName;
			thumbName = rootpath + path;
			File thumbFile = new File(thumbName);
			ImageIO.write(destImg, fileExt.toUpperCase(), thumbFile);
			System.out.println("섬네일이미지가 생성되었습니다.");
			file="S";
		}
		return file;
	}*/
	public static String encodeFileName(String fileName, HttpServletRequest request) {
		String header = request.getHeader("User-Agent");
		if (header.contains("Edge")){
			fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
		} else if (header.contains("MSIE") || header.contains("Trident")) { // IE 11버전부터 Trident로 변경되었기때문에 추가해준다.
			fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
		} else if (header.contains("Chrome")) {
			fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
		} else if (header.contains("Opera")) {
			fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
		} else if (header.contains("Firefox")) {
			fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
		} else if (header.contains("Safari")) {
			fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
		} else {
			fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
		}
		
		return fileName;
	}
	
	public static String macFileNameProcess(String fileName) {
		return Normalizer.normalize(fileName, Normalizer.Form.NFC);
	}
	
	/**
	 * 파일 유효성 체크 메서드. 파일사이즈가 허용 범위내에 있는지 검사한다.
	 * @param file : 파일
	 * @param size : 허용할 사이즈(MB단위)
	 * @return : 유효성 여부
	 * @author : 최봉준
	 */
	public static boolean fileValidation(MultipartFile file, int size) {
		try {
			if (file == null) return false;
			else return file.getSize() <= (long) size * 1024 * 1024;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 파일 유효성 체크 메서드. 파일확장자가 받은 확장자 안에 있는지 검사한다.
	 * @param file : 파일
	 * @param extArr : 허용할 확장자(문자열 배열)
	 * @return : 유효성 여부
	 * @author : 최봉준
	 */
	//파일 유효성 체크 메서드(확장자 only)
	public static boolean fileValidation(MultipartFile file, String[] extArr) {
		if (extArr == null || extArr.length == 0) return true; // 검사할 확장자가 없다면 true 리턴
		
		try {
			if (file == null) return false;
			else {
				String fileName; // 파일명
				String ext; // 확장자명
				int dotIndex; // .의 인덱스
				
				fileName = file.getOriginalFilename();
				if (fileName == null) return false;
				
				dotIndex = fileName.lastIndexOf(".");
				ext = fileName.substring(dotIndex + 1);
				
				return Arrays.asList(extArr).contains(ext);
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 파일 유효성 체크 메서드. 파일사이즈, 파일확장자가 허용 범위내에 있는지 검사한다.
	 * @param file : 파일
	 * @param size : 허용할 사이즈(MB단위)
	 * @param extArr : 허용할 확장자(문자열 배열)
	 * @return : 유효성 여부
	 * @author : 최봉준
	 */
	//파일 유효성 체크 메서드(size, 확장자)
	public static boolean fileValidation(MultipartFile file, int size, String[] extArr) {
		try {
			
			return fileValidation(file, size) && fileValidation(file, extArr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}