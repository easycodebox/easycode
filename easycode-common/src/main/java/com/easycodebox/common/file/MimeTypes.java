package com.easycodebox.common.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

/**
 * 
 * 
	ServletContext.getMimeType() //Web容器获取MimeType方法 - 根据文件后缀名判断
  ******************************************************
 	Path source = Paths.get(path);
	System.out.println("1: " + Files.probeContentType(source));
	此功能判断类型不完整
 	
 ******************************************************
 	System.out.println("11: " + FileTypes.getFileType(path));
 
 ******************************************************
 	<dependency>
		<groupId>eu.medsea.mimeutil</groupId>
		<artifactId>mime-util</artifactId>
		<version>2.1.3</version>
	</dependency>
	
	MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
    System.out.println("14: " + MimeUtil.getMimeTypes(new File(path)));
    
 ******************************************************
	<dependency>
		<groupId>jmimemagic</groupId>
		<artifactId>jmimemagic</artifactId>
		<version>0.1.2</version>
	</dependency>
	
	//getMagicMatch可以传参数控制只根据图片后缀生成mimeType
	MagicMatch match = Magic.getMagicMatch(new File(path), true);
	System.out.println("15: " + match.getMimeType());
	
 * @author WangXiaoJin
 *
 */
public class MimeTypes {
	
	private MimeTypes() {
		super();
	}
	
	/**
	 * 通过文件后缀来判断的，通过文件后缀识别
	 * @return
	 */
	public static String getMimeType(String path) {
		return MimetypesFileTypeMap.getInstance().getContentType(path);
	}
	
	/**
	 * 通过文件后缀来判断的，通过文件后缀识别 <br>
	 * <code>ServletContext.getMimeType()</code>也是通过文件后缀判断
	 * @param extension 文件扩展名
	 * @return
	 */
	public static String getMimeTypeByExt(String extension) {
		return MimetypesFileTypeMap.getInstance().getContentTypeByExt(extension);
	}
	
	/**
	 * 根据文件mimeType获取文件支持的扩展名
	 * @param mimeType
	 * @return
	 */
	public static String[] getExtensions(String mimeType) {
		return MimetypesFileTypeMap.getInstance().getExtensions(mimeType);
	}
	
	/**
	 * 通过Tika来获取MimeType。注：通过文件数据来判断的。 <br>
	 * 需要引入Tika包： <br>
	 * <code>
	 * 	&lt;dependency> <br>
	 * 		&lt;groupId>org.apache.tika&lt;/groupId> <br>
	 * 		&lt;artifactId>tika-core&lt;/artifactId> <br>
	 * 		&lt;version>1.12&lt;/version> <br>
	 * 	&lt;/dependency>
	 * </code>
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public static String getMimeTypeByTika(String path) throws IOException {
		return TikaFactory.getInstance().detect(path);
	}
	
	/**
	 * 通过Tika来获取MimeType。注：通过文件数据来判断的。 <br>
	 * 需要引入Tika包： <br>
	 * <code>
	 * 	&lt;dependency> <br>
	 * 		&lt;groupId>org.apache.tika&lt;/groupId> <br>
	 * 		&lt;artifactId>tika-core&lt;/artifactId> <br>
	 * 		&lt;version>1.12&lt;/version> <br>
	 * 	&lt;/dependency>
	 * </code>
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public static String getMimeTypeByTika(File file) throws IOException {
		return TikaFactory.getInstance().detect(file);
	}
	
	/**
	 * 通过Tika来获取MimeType。注：通过文件数据来判断的。 <br>
	 * 需要引入Tika包： <br>
	 * <code>
	 * 	&lt;dependency> <br>
	 * 		&lt;groupId>org.apache.tika&lt;/groupId> <br>
	 * 		&lt;artifactId>tika-core&lt;/artifactId> <br>
	 * 		&lt;version>1.12&lt;/version> <br>
	 * 	&lt;/dependency>
	 * </code>
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public static String getMimeTypeByTika(InputStream stream) throws IOException {
		return TikaFactory.getInstance().detect(stream);
	}
	
	/**
	 * 通过java.net来获取MimeType。注：通过文件后缀来判断的。<b>推荐使用getMimeTypeByActivation</b> <br>
	 * @param path
	 * @return
	 */
	public static String getMimeTypeByJavaNet(String path) {
	     return URLConnection.guessContentTypeFromName(path);
	}
	
	/**
	 * 通过java.net来获取MimeType。注：通过二进制判断。<b>推荐使用apache-tika</b> <br>
	 * 能够识别的mimeType类型不多，只能识别（xml、html、常用的图片类型等）
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String getMimeTypeMagicByJavaNet(String path) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(new File(path)));
		return URLConnection.guessContentTypeFromStream(is);
	}
	
}
