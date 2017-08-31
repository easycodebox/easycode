package com.easycodebox.upload.util;

import com.easycodebox.common.CommonProperties;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.file.*;
import com.easycodebox.common.idgenerator.support.AlphaNumericIdGenerator;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.upload.config.UploadProperties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author WangXiaoJin
 *
 */
@Component
public class UploadHandler {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private AlphaNumericIdGenerator generator = null;
	private final int INCREMENT = 1, FETCHSIZE = 500;
						
	private static final ReentrantLock lock = new ReentrantLock();
	
	private UploadProperties uploadProperties;
	
	private CommonProperties commonProperties;
	
	public UploadHandler(UploadProperties uploadProperties, CommonProperties commonProperties) {
		this.uploadProperties = uploadProperties;
		this.commonProperties = commonProperties;
	}
	
	@PostConstruct
	public void init() {
		PropertiesPool.loadPropertiesFile(uploadProperties.getConfigFile(), true, true);
		
		String initVal = PropertiesPool.getProperty("filename_init_val"),
			curVal = PropertiesPool.getProperty("filename_cur_val"),
			maxVal = PropertiesPool.getProperty("filename_max_val");
		
		if (Strings.isBlank(initVal)) {
			initVal = uploadProperties.getFilenameInitVal();
		}else {
			uploadProperties.setFilenameInitVal(initVal);
		}
		if (Strings.isBlank(curVal)) {
			curVal = uploadProperties.getFilenameCurVal();
		} else {
			uploadProperties.setFilenameCurVal(curVal);
		}
		if (Strings.isBlank(maxVal)) {
			maxVal = uploadProperties.getFilenameMaxVal();
		} else {
			uploadProperties.setFilenameMaxVal(maxVal);
		}
		
		generator = new AlphaNumericIdGenerator(INCREMENT, FETCHSIZE, initVal, curVal, maxVal, YesNo.YES);
		storeFilenameProp(true);
	}
	
	/**
	 *
	 * @param init	判断调用此方法是否是项目初始化时调用的
	 * @return
	 */
	private String storeFilenameProp(boolean init) {
		lock.lock();
		try {
			if(!init) {
				String nextVal = generator.nextVal();
				if (nextVal != null)
					return nextVal;
			}
			//根据uploadPropertiescur.filenameCurVal返回下批次开始值
			String nextStepVal = generator.nextStepVal(uploadProperties.getFilenameCurVal());
			//将下批次值刷入uploadPropertiescur.filenameCurVal
			uploadProperties.setFilenameCurVal(nextStepVal);
			Properties prop = new Properties();
			prop.setProperty("filename_init_val", generator.getInitialVal());
			prop.setProperty("filename_cur_val", nextStepVal);
			prop.setProperty("filename_max_val", generator.getMaxVal());
			try {
				PropertiesUtils.store(prop, uploadProperties.getConfigFile());
			} catch (IOException e) {
				log.error("store properties error.", e);
			}
		} finally {
			lock.unlock();
		}
		return null;
	}
	
	/**
	 * 获取下一个文件名
	 * @return
	 */
	public String getNextFileName() {
		String nextVal = generator.nextVal();
		//nextVal == null 本批次数据已经用完，需要加载下批数据
		while (nextVal == null) {
			nextVal = storeFilenameProp(false);
			if (nextVal == null)
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
	public Image[] uploadImg(FileType fileType, MultipartFile[] itemList, String path, Image[] imgsInfo,
	                         String[] imgNames, boolean transaction) throws Exception {
		int eqIndex = path.indexOf(Symbol.QUESTION);
		path = commonProperties.getTmpFilename() + Symbol.SLASH
				+ (eqIndex == -1 ? path : path.substring(0, eqIndex));
		String absolutePath = uploadProperties.getPath() + Symbol.SLASH + fileType.getFileDir() + Symbol.SLASH + path,
				error = null;
		File absoluteFile = new File(absolutePath);
		if(!absoluteFile.exists()) {
			absoluteFile.mkdirs();
			String[] frags = path.split(Symbol.SLASH);
			String tmpPath = uploadProperties.getPath() + Symbol.SLASH + fileType.getFileDir();
			for(String frag : frags) {
				if(Strings.isBlank(frag))
					continue;
				File fragPath = new File(tmpPath +=  Symbol.SLASH + frag);
				fragPath.setWritable(true, false);
				fragPath.setReadable(true, false);
				fragPath.setExecutable(true, false);
			}
		}
		for(int i = 0; i < itemList.length; i++) {
			if(imgsInfo[i].getError() != null)
				continue;
			MultipartFile item = itemList[i];
			imgsInfo[i].setOldName(FilenameUtils.getName(item.getOriginalFilename()));
			try {
				String imgName = (imgNames == null || imgNames[i] == null
						? getNextFileName() : imgNames[i]) + "." + imgsInfo[i].getType();
				File file = new File(absolutePath + Symbol.SLASH + imgName);
				item.transferTo(file);
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
	        		imgsInfo[i].setError(e.getMessage());
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
	public FileInfo[] uploadFile(FileType fileType, MultipartFile[] itemList, String path, FileInfo[] fileInfos,
			String[] filenames, boolean transaction) throws Exception {
		int eqIndex = path.indexOf(Symbol.QUESTION);
		path = commonProperties.getTmpFilename() + Symbol.SLASH
				+ (eqIndex == -1 ? path : path.substring(0, eqIndex));
		String absolutePath = uploadProperties.getPath() + Symbol.SLASH + fileType.getFileDir() + Symbol.SLASH + path,
				error = null;
		File absoluteFile = new File(absolutePath);
		if(!absoluteFile.exists()) {
			absoluteFile.mkdirs();
			String[] frags = path.split(Symbol.SLASH);
			String tmpPath = uploadProperties.getPath() + Symbol.SLASH + fileType.getFileDir();
			for(String frag : frags) {
				if(Strings.isBlank(frag))
					continue;
				File fragPath = new File(tmpPath +=  Symbol.SLASH + frag);
				fragPath.setWritable(true, false);
				fragPath.setReadable(true, false);
				fragPath.setExecutable(true, false);
			}
		}
		for(int i = 0; i < itemList.length; i++) {
			if(fileInfos[i].getError() != null)
				continue;
			MultipartFile item = itemList[i];
			fileInfos[i].setOldName(FilenameUtils.getName(item.getOriginalFilename()));
			try {
				String filename = (filenames == null || filenames[i] == null ? getNextFileName() : filenames[i])
						+ (fileInfos[i].getType() == null ? Symbol.EMPTY : Symbol.PERIOD + fileInfos[i].getType());
				File file = new File(absolutePath + Symbol.SLASH + filename);
				item.transferTo(file);
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
	        		fileInfos[i].setError(e.getMessage());
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
	public boolean delete(FileType fileType, String ...fileNames) {
		Pattern p = Pattern.compile("^([a-z]+(/[a-z]+)*/[0-9a-z]+)(_[0-9a-z]+)*(\\.[a-z]+)$");
		int count = 0;
		for(int i = 0; i < fileNames.length; i++) {
			Matcher m = p.matcher(fileNames[i]);
			String original = null;
			if(m.find()) {
				String deletePath = uploadProperties.getPath() + Symbol.SLASH + fileType.getFileDir() + Symbol.SLASH
						+ uploadProperties.getDeleteFilename() + Symbol.SLASH + m.group(1);
				original = uploadProperties.getPath() + Symbol.SLASH + fileType.getFileDir() + Symbol.SLASH + m.group(1);
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
					log.error("删除图片{}错误", original, e);
				}
			}
		}
		return count == fileNames.length;
	}
	
}
