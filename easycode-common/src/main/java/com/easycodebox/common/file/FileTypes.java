package com.easycodebox.common.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.util.Iterator;


/**
 * 请使用MimeTypes类
 * @author WangXiaoJin
 * 
 */
@Deprecated
public class FileTypes {

	private static final Logger log = LoggerFactory.getLogger(FileTypes.class);
	
	
	/** 
     * 得到上传文件的文件头 
     * @param src 
     * @return 
     */  
    public static String bytesToHexString(byte[] src) {  
        StringBuilder stringBuilder = new StringBuilder();  
        if (src == null || src.length <= 0) {  
            return null;  
        }
	    for (byte b : src) {
		    int v = b & 0xFF;
		    String hv = Integer.toHexString(v);
		    if (hv.length() < 2) {
			    stringBuilder.append(0);
		    }
		    stringBuilder.append(hv);
	    }
        return stringBuilder.toString();  
    }  
    
    /** 
     * 根据制定文件的文件头判断其文件类型 
     */  
    public static FileType getFileType(String filePath){ 
    	if(filePath == null) return null;
        return getFileType(new File(filePath));
    }
    
    /** 
     * 根据制定文件的文件头判断其文件类型 
     */  
    public static FileType getFileType(File file){ 
    	if(file == null) return null;
        try {
			return getFileType(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException", e);
		}
		return null;  
    }
    
    /** 
     * 根据制定文件的文件头判断其文件类型 
     */  
    public static FileType getFileType(InputStream inputStream) {
    	if(inputStream == null) return null;
        try {
            byte[] b = new byte[10];
            inputStream.read(b, 0, b.length);
            String fileCode = bytesToHexString(b);
            if(fileCode == null)
            	return null;
            else
            	fileCode = fileCode.toUpperCase();
            FileType[] types = FileType.values();
	        for (FileType type : types) {
		        if (fileCode.startsWith(type.getValue())) {
			        return type;
		        }
	        }
        } catch (IOException e) {  
        	log.error("IOException", e);
        } finally {
        	try {
				inputStream.close();
			} catch (IOException e) {
				log.error("IOException", e);
			}
        }
        return null;  
    }
    
    /**
     * 只能获取图片的类型，经测试没有getFileType效率高
     * @param file
     * @return
     */
    public static String getImageFileType(File file) {
    	try(ImageInputStream iis = ImageIO.createImageInputStream(file)) {
    		Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);  
    		if (!iter.hasNext()) {
    			return null;
    		}  
    		ImageReader reader = iter.next();
    		return reader.getFormatName();
    	} catch (IOException e) {
    		log.error("IOException", e);
		}
        return null;  
    }
    
    public static void main(String[] args) throws Exception {  
        long time = System.currentTimeMillis();
        testShowFileType(new File("D:\\back\\upload"));
        System.out.println("time ===" + (System.currentTimeMillis() - time));
    }  
    
    /**
     * 测试批量获取文件类型的速度
     * @param file
     */
	private static void testShowFileType(File file) {
    	if(file.isDirectory()){
    		File[] fs = file.listFiles();
		    if (fs == null) return;
		    for (File f : fs) {
			    testShowFileType(f);
		    }
    	} else {
    		System.out.println(getFileType(file));
	    }
    }
	
}
