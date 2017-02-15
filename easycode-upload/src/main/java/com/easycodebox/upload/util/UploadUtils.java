package com.easycodebox.upload.util;

import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.file.*;
import com.easycodebox.common.idgenerator.impl.AlphaNumericIdGenerator;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author WangXiaoJin
 * 
 */
public class UploadUtils {
	
	private static final Logger log = LoggerFactory.getLogger(UploadUtils.class);
	
	private static AlphaNumericIdGenerator generator = null;
	private static final int INCREMENT = 1, 
							FETCHSIZE = 500; 
						
						//文件名初始值
	public static String FILENAME_INIT_VAL = null,
						//文件名的当前值
						FILENAME_CUR_VAL = null,
						//文件名的最大值
						FILENAME_MAX_VAL = null;
	private static final ReentrantLock lock = new ReentrantLock();
	
	static {
		
		PropertiesPool.loadPropertiesFile(Constants.FILENAME_PROPS_PATH, true, true);
		
		FILENAME_INIT_VAL = PropertiesPool.getProperty("filename_init_val");
		FILENAME_CUR_VAL = PropertiesPool.getProperty("filename_cur_val");
		FILENAME_MAX_VAL = PropertiesPool.getProperty("filename_max_val");
		
		generator = new AlphaNumericIdGenerator(
				INCREMENT, FETCHSIZE, FILENAME_INIT_VAL, FILENAME_CUR_VAL, 
				FILENAME_MAX_VAL, YesNo.YES);
		
		storeFilenameProp(true);
	}
	
	/**
	 * 
	 * @param init	判断调用此方法是否是项目初始化时调用的
	 * @return
	 */
	private static String storeFilenameProp(boolean init) {
		lock.lock();
		try {
			if(!init) {
				String nextVal = generator.nextVal();
				if(nextVal != null)
					return nextVal;
			}
			//下一批次的值作为FILENAME_CUR_VAL
			FILENAME_CUR_VAL = generator.nextStepVal(FILENAME_CUR_VAL);
			Properties prop = new Properties();
			prop.setProperty("filename_init_val", FILENAME_INIT_VAL);
			prop.setProperty("filename_cur_val", FILENAME_CUR_VAL);
			prop.setProperty("filename_max_val", FILENAME_MAX_VAL);
			try {
				PropertiesUtils.store(prop, Constants.FILENAME_PROPS_PATH);
			} catch (IOException e) {
				log.error("store properties error.", e);
			}
		}finally {
			lock.unlock();
		}
		return null;
	}
	
	/**
	 * 获取下一个文件名
	 * @return
	 */
	public static String getNextFileName() {
		String nextVal = generator.nextVal();
		//nextVal == null 本批次数据已经用完，需要加载下批数据
		while(nextVal == null) {
			nextVal = storeFilenameProp(false);
			if(nextVal == null)
				nextVal = generator.nextVal();
		}
		return nextVal;
	}
	
	/**
	 * 适合上传图片分离的逻辑
	 * @param path
	 * @param imgNames   期望使用的图片名字，若果为null， 则使用默认规则生成图片名
	 * @return
	 * @throws Exception 
	 */
	public static Image[] uploadImg(FileType fileType, List<DiskFileItem> itemList, String path, Image[] imgsInfo, 
			String[] imgNames, boolean transaction) throws Exception {
		int eqIndex = path.indexOf(Symbol.QUESTION);
		path = Constants.TMP_FILE + Symbol.SLASH 
				+ (eqIndex == -1 ? path : path.substring(0, eqIndex));
		String absolutePath = fileType.getRoot() + Symbol.SLASH + path,
				error = null;
		File absoluteFile = new File(absolutePath);
		if(!absoluteFile.exists()) {
			absoluteFile.mkdirs();
			String[] frags = path.split(Symbol.SLASH);
			String tmpPath = fileType.getRoot();
			File fragPath = null;
			for(String frag : frags) {
				if(Strings.isBlank(frag))
					continue;
				fragPath = new File(tmpPath +=  Symbol.SLASH + frag);
				fragPath.setWritable(true, false);
				fragPath.setReadable(true, false);
				fragPath.setExecutable(true, false);
			}
		}
		for(int i = 0; i < itemList.size(); i++) {
			if(imgsInfo[i].getError() != null)
				continue;
			DiskFileItem item = itemList.get(i);
			imgsInfo[i].setOldName(FilenameUtils.getName(item.getName()));
			try {
				String imgName = (imgNames == null || imgNames[i] == null 
						? getNextFileName() : imgNames[i]) + "." + imgsInfo[i].getType();
				File file = new File(absolutePath + Symbol.SLASH + imgName);
				item.write(file);
				file.setWritable(false, false);
				file.setReadable(true, false);
				file.setExecutable(false, false);
				imgsInfo[i].setPath(path);
				imgsInfo[i].setName(imgName);
			} catch (Exception e) {
				log.warn("生成文件失败！", e);
				/* ------ error ------ */
	        	if (transaction) {
	        		throw e;
	        	} else {
	        		imgsInfo[i].setError(error);
	        		continue;
	        	}
	        	/* ------ error ------ */
			}
		}
		return imgsInfo;
	}
	
	/**
	 * 上传文件
	 * @param fileType
	 * @param itemList
	 * @param path
	 * @param fileInfos
	 * @param filenames
	 * @return
	 * @throws Exception
	 */
	public static FileInfo[] uploadFile(FileType fileType, List<DiskFileItem> itemList, String path, FileInfo[] fileInfos, 
			String[] filenames, boolean transaction) throws Exception {
		int eqIndex = path.indexOf(Symbol.QUESTION);
		path = Constants.TMP_FILE + Symbol.SLASH 
				+ (eqIndex == -1 ? path : path.substring(0, eqIndex));
		String absolutePath = fileType.getRoot() + Symbol.SLASH + path,
				error = null;
		File absoluteFile = new File(absolutePath);
		if(!absoluteFile.exists()) {
			absoluteFile.mkdirs();
			String[] frags = path.split(Symbol.SLASH);
			String tmpPath = fileType.getRoot();
			File fragPath = null;
			for(String frag : frags) {
				if(Strings.isBlank(frag))
					continue;
				fragPath = new File(tmpPath +=  Symbol.SLASH + frag);
				fragPath.setWritable(true, false);
				fragPath.setReadable(true, false);
				fragPath.setExecutable(true, false);
			}
		}
		for(int i = 0; i < itemList.size(); i++) {
			if(fileInfos[i].getError() != null)
				continue;
			DiskFileItem item = itemList.get(i);
			fileInfos[i].setOldName(FilenameUtils.getName(item.getName()));
			try {
				String filename = (filenames == null || filenames[i] == null ? getNextFileName() : filenames[i]) 
						+ (fileInfos[i].getType() == null ? Symbol.EMPTY : Symbol.PERIOD + fileInfos[i].getType());
				File file = new File(absolutePath + Symbol.SLASH + filename);
				item.write(file);
				file.setWritable(false, false);
				file.setReadable(true, false);
				file.setExecutable(false, false);
				fileInfos[i].setPath(path);
				fileInfos[i].setName(filename);
			} catch (Exception e) {
				log.warn("生成文件失败！", e);
				/* ------ error ------ */
	        	if (transaction) {
	        		throw e;
	        	} else {
	        		fileInfos[i].setError(error);
	        		continue;
	        	}
	        	/* ------ error ------ */
			}
			
		}
		return fileInfos;
	}
	
	/**
	 * 删除服务器上的图片
	 * @param fileNames 可以传数组
	 * @return 
	 */
	public static boolean delete(FileType fileType, String ...fileNames) {
		Pattern p = Pattern.compile("^([a-z]+(/[a-z]+)*/[0-9a-z]+)(_[0-9a-z]+)*(\\.[a-z]+)$");
		int count = 0;
		for(int i = 0; i < fileNames.length; i++) {
			Matcher m = p.matcher(fileNames[i]);
			String original = null;
			if(m.find()) {
				String deletePath = fileType.getRoot() + Symbol.SLASH + Constants.DELETE_FILE_PATH
							+ Symbol.SLASH + m.group(1);
				original = fileType.getRoot() + Symbol.SLASH + m.group(1);
				File deleteDir = new File(deletePath);
				if(!deleteDir.getParentFile().exists())
					deleteDir.getParentFile().mkdirs();
				try {
					FileUtils.deleteDirectory(new File(original));
					File ori = new File(original + m.group(4));
					if(ori.exists())
						ori.renameTo(new File(deletePath + m.group(4)));
					count++;
				} catch (Exception e) {
					log.error("删除图片" + original + "错误", e);
				}
			}
		}
		return count == fileNames.length;
	}
	
	public static void main(String[] args) {
		String a = "/data/uplud/tmp/".replaceFirst(
				"(.*?" + Symbol.SLASH + Constants.TMP_FILE 
				+ Symbol.SLASH + "\\w+).*", "$1");
		System.out.println(a);
		/*String original = "f:/a/b/c",
				deletePath = "f:/delete/a/b/c";
		File deleteDir = new File(deletePath);
		if(!deleteDir.getParentFile().exists()) 
			deleteDir.getParentFile().mkdirs();
		new File(original).renameTo(new File(deletePath));*/
		//new File(original + "/a.jpg").renameTo(new File(deletePath + "/a.jpg"));
	}

}
