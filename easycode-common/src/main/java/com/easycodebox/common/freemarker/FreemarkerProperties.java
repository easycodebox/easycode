package com.easycodebox.common.freemarker;

import com.easycodebox.common.NamedSupport;
import com.easycodebox.common.lang.Symbol;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author WangXiaoJin
 */
public class FreemarkerProperties extends NamedSupport {
	
	public static final String DEFAULT_NAME = FreemarkerProperties.class.getName();
	
	private static FreemarkerProperties INSTANCE;
	
	public static FreemarkerProperties instance() {
		return INSTANCE == null ? (INSTANCE = new FreemarkerProperties()) : INSTANCE;
	}
	
	public FreemarkerProperties() {
		this(DEFAULT_NAME);
	}
	
	public FreemarkerProperties(String name) {
		super(name);
	}
	
	@Value("${freemarker.default_encoding:UTF-8}")
	private String defaultEncoding = "UTF-8";
	
	@Value("${freemarker.locale:zh_CN}")
	private String locale = "zh_CN";
	
	@Value("${freemarker.loader_path:/}")
	private String loaderPath = Symbol.SLASH;
	
	@Value("${freemarker.template_update_delay:3600}")
	private String templateUpdateDelay = "3600";
	
	@Value("${freemarker.classic_compatible:true}")
	private String classicCompatible = "true";
	
	@Value("${freemarker.datetime_format:yyyy-MM-dd HH:mm:ss}")
	private String datetimeFormat = "yyyy-MM-dd HH:mm:ss";
	
	@Value("${freemarker.date_format:yyyy-MM-dd}")
	private String dateFormat = "yyyy-MM-dd";
	
	@Value("${freemarker.time_format:HH:mm:ss}")
	private String timeFormat = "HH:mm:ss";
	
	@Value("${freemarker.number_format:#.##}")
	private String numberFormat = "#.##";
	
	@Value("${freemarker.tag_syntax:auto_detect}")
	private String tagSyntax = "auto_detect";
	
	public String getDefaultEncoding() {
		return defaultEncoding;
	}
	
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}
	
	public String getLocale() {
		return locale;
	}
	
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public String getLoaderPath() {
		return loaderPath;
	}
	
	public void setLoaderPath(String loaderPath) {
		this.loaderPath = loaderPath;
	}
	
	public String getTemplateUpdateDelay() {
		return templateUpdateDelay;
	}
	
	public void setTemplateUpdateDelay(String templateUpdateDelay) {
		this.templateUpdateDelay = templateUpdateDelay;
	}
	
	public String getClassicCompatible() {
		return classicCompatible;
	}
	
	public void setClassicCompatible(String classicCompatible) {
		this.classicCompatible = classicCompatible;
	}
	
	public String getDatetimeFormat() {
		return datetimeFormat;
	}
	
	public void setDatetimeFormat(String datetimeFormat) {
		this.datetimeFormat = datetimeFormat;
	}
	
	public String getDateFormat() {
		return dateFormat;
	}
	
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	public String getTimeFormat() {
		return timeFormat;
	}
	
	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}
	
	public String getNumberFormat() {
		return numberFormat;
	}
	
	public void setNumberFormat(String numberFormat) {
		this.numberFormat = numberFormat;
	}
	
	public String getTagSyntax() {
		return tagSyntax;
	}
	
	public void setTagSyntax(String tagSyntax) {
		this.tagSyntax = tagSyntax;
	}
}
