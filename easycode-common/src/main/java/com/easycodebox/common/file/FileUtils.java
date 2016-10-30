package com.easycodebox.common.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.file.exception.NonEnlargedException;
import com.easycodebox.common.lang.DecimalUtils;
import com.easycodebox.common.lang.RegularUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 * 
 */
public class FileUtils {
	
	private static final Logger log = LoggerFactory.getLogger(ImageTools.class);
	
	public static final String TMP_PATH = "tmp";
	
	
	/**
	 * @return 获取项目classpath目录的绝对路径
	 */
	public static String getAbsolutePathWithClass() {
		 File directory = new File("");
		 String courseFile = null;
		 try {
			 courseFile = directory.getCanonicalPath();
		 } catch (IOException e) {
			 log.error("The method getAbsolutePathWithClass in HttpUtil:" + e.getMessage());
		 }
		 return courseFile;
	}
	
	/**
	 * 创建文件
	 * @param fileFullName 文件全路径 如D:\\test\\a.txt
	 * @throws IOException 如果目录test不存在时，则报错
	 */
	public static void createFile(String fileFullName) throws IOException {
		createFile(fileFullName, false, false);
	}

	/**
	 * 创建文件
	 * @throws IOException 如果父级目录不存在时，则报错
	 */
	public static void createFile(File file) throws IOException {
		createFile(file, false, false);
	}

	/**
	 * 创建文件
	 * @param file
	 * @param removeFlag	为true时 若文件已存在将原文件删除，创建新文件。
	 * @param createDir		为true时 创建不存在的父级目录
	 * @throws IOException
	 */
	public static void createFile(File file, boolean removeFlag, boolean createDir) throws IOException {
		if (file.exists()) {
			if (removeFlag) {
				file.delete();
			} else {
				throw new IOException("文件已存在");
			}
		}
		File parent = file.getParentFile();
		if(parent != null && !parent.exists()
				&& createDir == true) 
			parent.mkdirs();
		file.createNewFile();
	}
	
	/**
	 * 判断是否有大数据
	 * @param files
	 * @param m 参照的数据大小依据 单位M
	 * @return
	 */
	public static boolean hasLarger(File[] files, double m) {
		if(files != null) {
			for(int i = 0; i < files.length; i++) {
				File f = files[i];
				if(byte2m(f.length(), 6) > m)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 保留两位小数
	 * @param data
	 * @return
	 */
	public static double byte2m(long data, int digit) {
		return new BigDecimal(1.0*data/1024/1024)
			.setScale(digit, BigDecimal.ROUND_UP).doubleValue();
	}

	/**
	 * 创建文件
	 * @param fileFullName 文件全路径 如D:\\a.txt
	 * @param removeFlag 为true时 若文件已存在将原文件删除，创建新文件。
	 * @param createDir		为true时 创建不存在的父级目录
	 * @throws IOException
	 */
	public static void createFile(String fileFullName, boolean removeFlag, boolean createDir)
			throws IOException {
		File file = new File(fileFullName);
		createFile(file, removeFlag, createDir);
	}

	public static void append(File file, List<String> contentList)
			throws IOException {
		if (contentList == null || contentList.size() == 0) {
			return;
		}
		if(file == null || !file.exists())
			throw new IOException(file + " 不存在.");
		String sep = System.getProperty("line.separator");
		FileWriter writer = null;
		try {
			StringBuilder buf = new StringBuilder();
			for (String str : contentList) {
				buf.append(str).append(sep);
			}
			writer = new FileWriter(file, true);
			writer.write(buf.toString().toCharArray());
			writer.flush();
		} finally {
			if (writer != null) 
				writer.close();
		}
	}
	
	/**
	 * 上传文件到服务器
	 * @param imgs			
	 * @param imgsFileName	原文件名
	 * @param toNames		上传文件时规定的文件名 不带文件后缀，不传用当前时间+三位随机数 作为文件名
	 * @param baseRealPath	图片在服务器真实的根路径 例:c:/tomcat/webapps/frontend
	 * @param filePath		图片的相对路径	 例：upload/img
	 * @param returnBigImg  只返回大图信息(生成不生成小图由createSmallImg决定)
	 * @param enlarge   	是否能够放大图片
	 * @param createSmallImg是否生成URL规则里面的小图
	 * @return	
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public static Image[] uploadImgs(File[] imgs, String[] imgsFileName, String[] toNames,
			String baseRealPath, String filePath, boolean returnBigImg,
			boolean scale, boolean enlarge, boolean createSmallImg) throws Exception {
		if(imgs == null || imgs.length == 0)
			throw new NullPointerException("imgs is null.");
		if(StringUtils.isBlank(baseRealPath))
			throw new NullPointerException("arg baseRealPath is blank in (uploadFiles) method.");
		if(StringUtils.isBlank(filePath))
			throw new NullPointerException("arg filePath is blank in (uploadFiles) method.");
		if(imgsFileName == null || imgsFileName.length != imgs.length) 
			throw new BaseException("arg filesFileName's length is not equeal " +
					"files in (uploadFiles) method.");
		
		//解析filePath 。createSmallImg==false说明只上传原图不为原图生成小图 
		Object[] urlAndImgs = analyzeUploadImgUrl(filePath, !createSmallImg, true);
		String url = (String)urlAndImgs[0];
		List<Image> smallImgs = urlAndImgs[1] == null ? null : (List<Image>)urlAndImgs[1];
		
		Image[] images = new Image[imgs.length];
		for(int i = 0; i < imgs.length; i++) {
			String fileName = toNames[i] + ".jpg";
			
			File uploadFile = new File(baseRealPath + Symbol.SLASH + url, fileName);//存储的原图
			org.apache.commons.io.FileUtils.copyFile(imgs[i], uploadFile);
			BufferedImage bufImg = ImageIO.read(new FileInputStream(imgs[i]));
			//有小图则生成小图
			if(smallImgs != null && smallImgs.size() > 0) {
				try {
					Image[] tmp = ImageTools.resizeImage(bufImg, fileName, baseRealPath, scale, enlarge, smallImgs);
					if(returnBigImg) {
						images[i] = tmp[tmp.length - 1];
					}else
						images[i] = tmp[0];
				} catch (NonEnlargedException e) {
					log.error("resizeImage error.", e);
					Image tmp = new Image();
					tmp.setError(e.getMessage());
					tmp.setWidth(bufImg.getWidth());
					tmp.setHeight(bufImg.getHeight());
					tmp.setSize(byte2m(uploadFile.length(), 2));
					tmp.setName(fileName);
					images[i] = tmp;
				}
			}else {
				//不生成小图，返回原图信息
				Image tmp = new Image();
				tmp.setPath(url);
				tmp.setWidth(bufImg.getWidth());
				tmp.setHeight(bufImg.getHeight());
				tmp.setSize(byte2m(uploadFile.length(), 2));
				tmp.setName(fileName);
				images[i] = tmp;
			}
		}
		return images;
	}
	
	/**
	 * 上传图片到服务器，返回大图和所有小图
	 * @param img			
	 * @param imgFileName	原文件名
	 * @param toName		上传文件时规定的文件名 不带文件后缀，不传用当前时间+三位随机数 作为文件名
	 * @param baseRealPath	图片在服务器真实的根路径 例:c:/tomcat/webapps/frontend
	 * @param filePath		图片的相对路径	 例：upload/img
	 * @param enlarge   	是否能够放大图片
	 * @return	
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public static Image[] uploadImg(File img, String imgFileName, String toName,
			String baseRealPath, String filePath, boolean scale, boolean enlarge) throws Exception {
		if(img == null)
			throw new NullPointerException("img is null.");
		if(StringUtils.isBlank(baseRealPath))
			throw new NullPointerException("arg baseRealPath is blank.");
		if(StringUtils.isBlank(filePath))
			throw new NullPointerException("arg filePath is blank.");
		if(imgFileName == null) 
			throw new NullPointerException("imgFileName is null.");
		//解析filePath
		Object[] urlAndImgs = analyzeUploadImgUrl(filePath, false, true);
		String url = (String)urlAndImgs[0];
		List<Image> smallImgs = urlAndImgs[1] == null ? null : (List<Image>)urlAndImgs[1];
		
		String fileName = toName + ".jpg";
		File uploadFile = new File(baseRealPath + Symbol.SLASH + url, fileName);//存储的原图
		org.apache.commons.io.FileUtils.copyFile(img, uploadFile);
		BufferedImage bufImg = ImageIO.read(new FileInputStream(img));
		
		Image bigImg = new Image();
		bigImg.setPath(url);
		bigImg.setWidth(bufImg.getWidth());
		bigImg.setHeight(bufImg.getHeight());
		bigImg.setSize(byte2m(uploadFile.length(), 2));
		
		if(smallImgs != null && smallImgs.size() > 0) {
			try {
				Image[] tmp = ImageTools.resizeImage(bufImg, fileName, baseRealPath, scale, enlarge, smallImgs);
				Image[] retImgs = new Image[tmp.length + 1];
				retImgs[0] = bigImg;
				for(int j = 0; j < tmp.length; j++) {
					retImgs[j+1] = tmp[j];
				}
				return retImgs;
			} catch (NonEnlargedException e) {
				log.error("resizeImage error.", e);
				bigImg.setError(e.getMessage());
			}
		}
		return new Image[]{bigImg};
	}
	
	/**
	 * upload/img/member/catemenu=200c0=90c90 ===> [tmp/upload/img/member/catemenu, [ Image(200c0), Image(90c90) ]]
	 * @param url			原始上传图片路径
	 * @param onlyBackUrl	是否只返回路径（不返回生成的小图图片信息）[upload/img/member/catemenu, null]
	 * @param isTmp			返回对象路径是否是临时路径 在路径前面加上tmp/
	 * @return [tmp/upload/img/member/catemenu, [ Image(200c0), Image(90c90) ]]
	 */
	public static Object[] analyzeUploadImgUrl(String url, boolean onlyBackUrl, boolean isTmp) {
		Object[] back = new Object[2];
		String[] o = url.split(Symbol.EQ);
		//放置url地址到back[0]
		back[0] = (isTmp ? TMP_PATH + Symbol.SLASH : "") + o[0];
		if(!onlyBackUrl && o.length > 1) {
			//解析缩放图片的大小
			Pattern p = Pattern.compile("^(\\d+)c(\\d+)$", Pattern.CASE_INSENSITIVE);
			List<Image> imgs = new ArrayList<Image>(o.length - 1);
			for(int i = 1; i < o.length; i++) {
				Matcher m = p.matcher(o[i]);
				if(m.matches()) {
					String width = m.group(1);
					String height = m.group(2);
					Image img = new Image();
					if(!"0".equals(width))
						img.setWidth(Integer.parseInt(width));
					if(!"0".equals(height))
						img.setHeight(Integer.parseInt(height));
					img.setPath(back[0] + Symbol.SLASH + o[i]);
					imgs.add(img);
				}else 
					throw new IllegalArgumentException("url param is illegal.");
			}
			back[1] = imgs;
		}
		return back;
	}
	
	/**
	 * 升级临时图片文件为对外开放图片
	 * @param fileName
	 * @param filePath
	 */
	@SuppressWarnings("unchecked")
	public static String tmp2Official(String fileName, String baseRealPath, 
			String filePath, boolean createSmallImg) throws IOException {
		fileName = FilenameUtils.getName(fileName);
		if(createSmallImg) {
			Object[] urlAndImgs = analyzeUploadImgUrl(filePath, false, true);
			List<Image> smallImgs = urlAndImgs[1] == null ? null : (List<Image>)urlAndImgs[1];
			int size = (smallImgs == null ? 0 : smallImgs.size()) + 1;
			String tmpPath,path,returnPath = null;
			for(int i = 0; i < size; i++) {
				if(i == 0) 
					tmpPath = baseRealPath + Symbol.SLASH + (String)urlAndImgs[0];
				else
					tmpPath = baseRealPath + Symbol.SLASH + smallImgs.get(i-1).getPath();
				path = tmpPath.replaceFirst(TMP_PATH + Symbol.SLASH, "");
				//设置返回的路径为第一个小图或者大图
				if((size > 1 && i == 1) || size == 1)
					returnPath = path.replace(baseRealPath + Symbol.SLASH, "");
				File rawImg = new File(tmpPath, fileName),
					newImg = new File(path, fileName);
				if(!newImg.getParentFile().exists())
					newImg.getParentFile().mkdirs();
				rawImg.renameTo(newImg);
			}
			return returnPath + Symbol.SLASH + fileName;
		}else {
			Object[] urlAndImgs = analyzeUploadImgUrl(filePath, true, true);
			String tmpPath = baseRealPath + Symbol.SLASH + urlAndImgs[0],
					path = tmpPath.replaceFirst(TMP_PATH + Symbol.SLASH, "");
			File rawImg = new File(tmpPath, fileName),
				newImg = new File(path, fileName);
			if(!newImg.getParentFile().exists())
				newImg.getParentFile().mkdirs();
			rawImg.renameTo(newImg);
			return path.replace(baseRealPath + Symbol.SLASH, "") + Symbol.SLASH + fileName;
		}
	}
	
	/**
	 * 根据rules字符窜获取指定的规则
	 * @param rules
	 * @param ruleType	规则类型
	 * @param obtainMaxVal	true：当出现多个相同规则时取最大值；false：取最小值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends Comparable<T>> T processRule(String rules, String ruleType, boolean obtainMaxVal, Class<T> target) {
		Pattern p = Pattern.compile(ruleType + "\\(([^\\)]*)\\)");
		Matcher m = p.matcher(rules);
		T result = null;
		while(m.find()) {
			T tmp = null;
			if(Number.class.isAssignableFrom(target)) {
				try {
					Constructor<T> c = target.getConstructor(String.class);
					tmp = c.newInstance(m.group(1));
				} catch (Exception e) {
					
				} 
			}else if(String.class.isAssignableFrom(target)) {
				tmp = (T)m.group(1);
			}else {
				throw new IllegalArgumentException(target + " can not be supported.");
			}
			if(tmp == null) 
				continue;
			else if(result == null) {
				result = tmp;
			}else {
				if(obtainMaxVal && tmp.compareTo(result) > 0) {
					result = tmp;
				}else if(!obtainMaxVal && tmp.compareTo(result) < 0){
					result = tmp;
				}
			}
		}
		return result;
	}
	
	/**
	 * 注意：需要手动释放InputStream资源 <br>
	 * 返回"suc"验证成功，除此之外都为验证失败
	 * sl(8)   ==> 大小(size)小于等于8M
	 * sg(8)   ==> 大小(size)大于等于8M
	 * type(jpg,png)  ==> 文件格式 只能为jpg,png
	 * @param rules 规则 
	 * @param filenames 对应的文件名。当不能判断InputStream的mimeType对应的文件扩展名时，使用这个参数的文件名后缀。
	 * @param maxSize	文件的最大值，为null、负值、0时不验证此特性：单位MB
	 * @param transaction	是否一个文件验证失败后，所有的文件都失败
	 * @return CodeMsg
	 * @throws IOException 
	 */
	public static CodeMsg validate(String rules, InputStream[] iss, String[] filenames, long[] fileSizes,
			Integer maxSize, boolean transaction) {
		Assert.notEmpty(iss);
		if(StringUtils.isBlank(rules)) {
			return CodeMsg.FAIL.msg("规则不能为空");
		}
		rules = RegularUtils.getQueryString(rules);
		Double sl = null, sg = null;
		String type = null, error = null;
		if(StringUtils.isNotBlank(rules)) {
			sl = FileUtils.processRule(rules, "sl", false, Double.class);
			if (maxSize != null && maxSize > 0 && (sl == null || maxSize < sl)) {
				sl = maxSize.doubleValue();
			}
			sg = FileUtils.processRule(rules, "sg", true, Double.class);
			type = FileUtils.processRule(rules, "type", true, String.class);
		}
		
		FileInfo[] files = new FileInfo[iss.length];
		for(int i = 0; i < iss.length; i++) {
			FileInfo file = new FileInfo();
			file.setSize(FileUtils.byte2m(fileSizes[i], 2));
			files[i] = file;
			
	        try {
				String fileType = null;
				String[] realFileExts = null;
				if(filenames != null && i < filenames.length && StringUtils.isNotBlank(filenames[i])) {
					//先根据文件名设置文件扩展名
					fileType = FilenameUtils.getExtension(filenames[i]).toLowerCase();
				}
				String mimeType = MimeTypes.getMimeTypeByTika(iss[i]);
				if(mimeType != null) {
					realFileExts = MimeTypes.getExtensions(mimeType);
					if(realFileExts == null) {
						log.warn("File 'mime.types' not contain mime type pair '{0}'-'{1}'. Please ADD this pair.", mimeType, fileType);
					}else if(realFileExts.length > 0) {
						boolean exist = true;
						if (fileType != null && !ArrayUtils.contains(realFileExts, fileType)) {
							log.warn("File 'mime.types' not contain mime type pair '{0}'-'{1}'. Please VERIFY this pair.", mimeType, fileType);
							exist = false;
						}
						fileType = fileType == null || !exist ? realFileExts[0] : fileType;
					}
				} else {
					log.warn("Filename '{0}' can't analyse mime type by Tika.", filenames[i]);
				}
				file.setType(fileType);
				
				if(sl != null && FileUtils.byte2m(fileSizes[i], 6) > sl) {
					/************* error **************/
		        	error = "文件不能大于" + DecimalUtils.fmt(sl, 2, true) + "M";
		        	if (transaction) {
		        		return CodeMsg.FAIL.msg(error);
		        	} else {
		        		file.setError(error);
		        		continue;
		        	}
		        	/************* error **************/
				} 
				if(sg != null && FileUtils.byte2m(fileSizes[i], 6) < sg) {
					/************* error **************/
		        	error = "文件不能小于" + DecimalUtils.fmt(sg, 2, true) + "M";
		        	if (transaction) {
		        		return CodeMsg.FAIL.msg(error);
		        	} else {
		        		file.setError(error);
		        		continue;
		        	}
		        	/************* error **************/
				}
				if(StringUtils.isNotBlank(type)) {
					boolean auth = false;
					if(realFileExts != null) {
						//当文件为已知类型时才放行
						String[] allowTypes = type.split(Symbol.COMMA);
						for(String t : allowTypes) {
							if(ArrayUtils.contains(realFileExts, t)) {
								auth = true;
								break;
							}
						}
					}
					if(!auth) {
						/************* error **************/
			        	error = "文件格式只能为" + type;
			        	if (transaction) {
			        		return CodeMsg.FAIL.msg(error);
			        	} else {
			        		file.setError(error);
			        		continue;
			        	}
			        	/************* error **************/
					}
				}
			} catch (IOException e) {
				log.error("解析文件错误", e);
				/************* error **************/
				error = "上传失败";
	        	if (transaction) {
	        		return CodeMsg.FAIL.msg(error);
	        	} else {
	        		file.setError(error);
	        		continue;
	        	}
	        	/************* error **************/
			}
			
		}
		return CodeMsg.SUC.data(files);
	}
	
	public static void main(String[] args) {
		/*File[] files = new File[2];
		files[0] = new File("e:\\b.png");
		files[1] = new File("e:\\c.png");
		String[] s = new String[2];
		s[0] = "b.png";
		s[1] = "c.png";
		Image[] imgs = null;
		try {
			//imgs = uploadImgs(files, s, null, "e:\\", "upload/catemenu=200c150=350c350=90c0", false, false, false);
			//imgs = uploadImg(new File("e:\\b.png"), "b.png", null, "e:\\", "upload/catemenu=200c150=350c350=90c0", true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(int i = 0; i < imgs.length; i++)
			System.out.println(imgs[i]);*/
		
	}
	
	
	
	
}
