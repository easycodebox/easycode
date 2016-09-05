package com.easycodebox.common.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import com.easycodebox.common.error.BaseException;

/**
 * @author WangXiaoJin
 * 有关正则表达式的功能
 */
public class RegularUtils {

	
	/**
	 * 在指定路径的倒数第二处添加文件夹
	 *(/frontend/img/a.jpg ==>  /frontend/img/add/a.jpg)
	 * @param originalUri
	 * @param fileName
	 * @return
	 */
	public static String addFileInLastURI(String originalUri, String fileName) {
		if(StringUtils.isBlank(originalUri) || StringUtils.isBlank(fileName)) {
			return originalUri;
		}
		return originalUri.replaceFirst("^((https?://)?(/|\\\\)?([^/\\\\]+(/|\\\\))*?)([^/\\\\]+(/|\\\\)?)$", "$1" + fileName + "/$6");
	}
	
	/**
	 * 在指定路径的指定的文件夹
	 *(/frontend/img/remove/a.jpg ==>  /frontend/img/a.jpg)
	 * @param originalUri
	 * @param fileName
	 * @return
	 */
	public static String removeFileInURI(String originalUri, String fileName) {
		if(StringUtils.isBlank(originalUri) || StringUtils.isBlank(fileName)) {
			return originalUri;
		}
		return originalUri.replaceAll("(/|\\\\)" + fileName + "([/\\\\]|$)", "" + "$2");
	}
	
	public static String fileNamePrefix(String fileName, String prefix) {
		return modifyFileName(fileName, prefix, null);
	}
	
	public static String fileNamePostfix(String fileName, String postfix) {
		return modifyFileName(fileName, null, postfix);
	}
	
	/**
	 * modifyFileName("c:/test/aa/a.txt", "1", "2") ==> "c:/test/aa/1a2.txt"
	 * @param fileName
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	public static String modifyFileName(String fileName, String prefix, String postfix) {
		if(StringUtils.isBlank(fileName))
			throw new BaseException("FileName is blank, can't modifyFileName.");
		prefix = prefix == null ? "" : prefix;
		postfix = postfix == null ? "" : postfix;
		return fileName.replaceFirst("^((https?://)?(/|\\\\)?([^/\\\\]+(/|\\\\))*)([^/\\\\\\.]+)((\\.[^/\\\\\\.]+)?)$", "$1" + prefix + "$6" + postfix + "$7");
	}
	
	/**
	 * modifyFileName("c:/test/aa/a.txt", "b.txt") ==> "c:/test/aa/b.txt"
	 * @return
	 */
	public static String modifyFileName(String path, String newFileName) {
		if(StringUtils.isBlank(path))
			throw new BaseException("path is blank, can't modifyFileName.");
		if(StringUtils.isBlank(newFileName))
			return path;
		return path.replaceAll("^((https?://)?(/|\\\\)?([^/\\\\]+(/|\\\\))*)[^/\\\\\\.]+(\\.[^/\\\\\\.]+)?$", "$1" + newFileName);
	}
	
	/**
	 * 修改文件类型("c:/test/aa/a.txt", "xml") ==> "c:/test/aa/a.xml"
	 * @param path
	 * @param fileType
	 * @return
	 */
	public static String modifyFileType(String path, String fileType) {
		if(StringUtils.isBlank(path)
				|| StringUtils.isBlank(fileType))
			return path;
		path = FilenameUtils.normalize(path, true);
		int dotIndex = path.lastIndexOf(Symbol.PERIOD),
			slashIndex = path.lastIndexOf(Symbol.SLASH);
		if(slashIndex > dotIndex) {
			if(slashIndex == path.length() - 1)
				throw new IllegalArgumentException("arg is not legal file path");
			return path + Symbol.PERIOD + fileType;
		}else {
			return path.substring(0, dotIndex + 1) + fileType;
		}
	}
	
	/**
	 * 判断这个URL是否含有参数
	 * @param url
	 * @return
	 */
	public static boolean isParamUrl(String url) {
		if(StringUtils.isNotBlank(url)) {
			int lastIndex = url.lastIndexOf(Symbol.QUESTION);
			if(lastIndex != -1) {
				url = url.substring(lastIndex + 1);
				if(url.length() > 0)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 验证此文件类型 是否可以通过验证
	 * @param file
	 * @param passType
	 */
	public static boolean verifyFileType(String file, String... passType) {
		boolean passed = false;
		String postfix = FilenameUtils.getExtension(file);
		if(postfix == null)
			throw new BaseException("file is null.");
		else
			postfix = postfix.toLowerCase();
		for(String type : passType) {
			if(postfix.equals(type)) {
				passed = true;
				break;
			}
		}
		return passed;
	}
	
	/**
	 * 获取指定url倒数第二个路径
	 * "c:/test/aa/a.txt" ==> "aa"
	 * @param url
	 * @return
	 */
	public static String getUrl2ndPath(String url) {
		Pattern p = Pattern.compile("^.*?(/|\\\\)([^/\\\\]+)(/|\\\\)[^/\\\\]+(/|\\\\)?$");
		Matcher m = p.matcher(url);
		if(m.matches())
			return m.replaceFirst("$2");
		else 
			return null;
	}
	
	/**
	 * 给指定的url添加新规则
	 * @param imgUrl
	 * @param rule
	 * @return
	 */
	public static String addImgUrlRule(String imgUrl, String[] rules) {
		if(StringUtils.isBlank(imgUrl)
				|| rules.length == 0) 
			return imgUrl;
		String tmp = StringUtils.join(rules, "_");
		return imgUrl.replaceFirst("^(.+?)(\\.[a-z]+)$", "$1_" + tmp + "$2");
	}
	
	/**
	 * 给指定的url添加新规则
	 * @param imgUrl
	 * @param rule
	 * @return
	 */
	public static String addImgUrlRule(String imgUrl, String rule) {
		if(StringUtils.isBlank(imgUrl)
				|| StringUtils.isBlank(rule)) 
			return imgUrl;
		return imgUrl.replaceFirst("^(.+?)(\\.[a-z]+)$", "$1_" + rule + "$2");
	}
	
	/**
	 * 删除图片地址指定的规则
	 * @param imgUrl
	 * @param rules
	 * @return
	 */
	public static String removeImgUrlRule(String imgUrl, String... rules) {
		if(StringUtils.isBlank(imgUrl)
				|| rules.length == 0) 
			return imgUrl;
		String regUrl = "((_" + StringUtils.join(rules, ")|(_") + "))";
		return imgUrl.replaceAll(regUrl, "");
	}
	
	/**
	 * 获取图片地址的原图url
	 * @param imgUrl
	 * @return
	 */
	public static String getImgUrlOriginal(String imgUrl) {
		if(StringUtils.isBlank(imgUrl)) return imgUrl;
		return imgUrl.replaceAll("_[0-9a-z]+", "");
	}
	
	/**
	 * "http://xxx.xx.com?name=11&score=3" ==> "name=11&score=3"
	 * @param url
	 * @return
	 */
	public static String getQueryString(String url) {
		if(url == null) return null;
		int index = url.indexOf(Symbol.QUESTION);
		if(index > -1)
			return url.substring(index + 1);
		return Symbol.EMPTY;
	}
	
	public static void main(String[] args) {
		/*System.out.println("gift/mcq_d1357c814a3257a899.jpg".replaceFirst("(.+?)(\\.[a-z]+)$", "$1_" + "r50c50" + "$2"));
		String[] rules = new String[]{"r100c100"};
		System.out.println("((_" + StringUtils.join(rules, ")|(_") + "))");
		System.out.println(getImgUrlOriginal("http://test/gift/aaa_r100c100_r100c50_r100c100.jpg"));*/
		/*Pattern p = Pattern.compile("^((/|\\\\)?([^/\\\\]+(/|\\\\))*)[^/\\\\\\.]+(\\.[^/\\\\\\.]+)?$");
		p.matcher("c:/test/aa/");
		System.out.println(getUrl2ndPath("aa/bb/cc"));*/
		String a = getQueryString("a?");
		System.out.println(a);
		System.out.println(FilenameUtils.removeExtension("c:/a/b/c.TXT"));
	}
	
}
