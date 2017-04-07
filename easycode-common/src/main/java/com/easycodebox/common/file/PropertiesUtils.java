package com.easycodebox.common.file;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.lang.StringToken.StringFormatToken;
import com.easycodebox.common.lang.reflect.Classes;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.xml.XmlDataParser;
import org.apache.commons.lang.StringUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import javax.management.modelmbean.XMLParseException;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * @author WangXiaoJin
 *
 */
public class PropertiesUtils {
	
	private static final Logger log = LoggerFactory.getLogger(PropertiesUtils.class);

	public static final String PLACEHOLDER_OPEN = "${";
	public static final String PLACEHOLDER_CLOSE = "}";
	
	/**
	 * @param properties
	 * @param dataStr	数据格式为字符窜。格式：name=xxxx\nage=yyyy
	 * @throws IOException 
	 */
	public static void load(Properties properties, String dataStr) throws IOException {
		Assert.notNull(properties, "'properties' can't be null.");
		log.debug("properties load data string {0}.", dataStr);
		if(StringUtils.isNotBlank(dataStr)) {
			try (StringReader reader = new StringReader(dataStr)) {
				properties.load(reader);
			}
		}
	}
	
	/**
	 * @param properties
	 * @param resource	相对于项目的资源文件
	 * @throws IOException 
	 */
	public static void loadFile(Properties properties, String resource) throws IOException {
		Assert.notNull(properties, "'properties' can't be null.");
		log.info("properties load file {0}.", resource);
		try (InputStream i = Classes.getClassLoader().getResourceAsStream(resource)) {
			properties.load(i);
		}
	}
	
	/**
	 * 
	 * @param properties
	 * @param absoluteFile	文件的绝对地址
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void loadAbsoluteFile(Properties properties, String absoluteFile) throws IOException {
		Assert.notNull(properties, "'properties' can't be null.");
		log.info("properties load file {0}.", absoluteFile);
		try (InputStream i = new FileInputStream(absoluteFile)) {
			properties.load(i);
		}
	}
	
	/**
	 * 
	 * @param properties
	 * @param resource	相对于项目的资源文件
	 * @throws IOException 
	 * @throws XMLParseException 
	 * @throws DocumentException 
	 */
	public static void loadXmlFile(Properties properties, String resource) throws IOException, DocumentException, XMLParseException {
		Assert.notNull(properties, "'properties' can't be null.");
		log.info("properties load xml file {0}.", resource);
		try (InputStream is = Classes.getClassLoader().getResourceAsStream(resource)) {
			loadXmlFile(properties, is);
		}
	}
	
	/**
	 * 
	 * @param properties
	 * @param absoluteFile	文件的绝对地址
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws XMLParseException 
	 * @throws DocumentException 
	 */
	public static void loadAbsoluteXmlFile(Properties properties, String absoluteFile) throws IOException, DocumentException, XMLParseException {
		Assert.notNull(properties, "'properties' can't be null.");
		log.info("properties load file {0}.", absoluteFile);
		try (InputStream i = new FileInputStream(absoluteFile)) {
			loadXmlFile(properties, i);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void loadXmlFile(Properties properties, InputStream i) throws DocumentException, XMLParseException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(i);
		Element root = document.getRootElement();
		List<Element> subs = root.elements("data");
		for (Element e : subs) {
			String nameVal = e.attributeValue("name");
			if (StringUtils.isNotBlank(nameVal))
				properties.put(nameVal, XmlDataParser.parseDataElement(e));
		}
	}
	
	public static void store(Properties properties, String filePath) throws IOException {
		Assert.notNull(properties, "properties can not be null.");
		Assert.notBlank(filePath, "filePath can not be blank.");
		File file = new File(filePath);
		if(!file.exists()) {
			File parent = file.getParentFile();
			if(!parent.exists())
				parent.mkdirs();
		}
		try (FileOutputStream fos = new FileOutputStream(filePath)) {
			properties.store(fos, "filename rule cfg");
		}
	}
	
	/**
	 * 处理properties中的占位符
	 * @param properties
	 * @param updateRaw	 是否在properties参数上进行修改
	 * @return
	 */
	public static Properties processPlaceholder(Properties properties, boolean updateRaw) {
		Properties props = updateRaw ? properties : (Properties)properties.clone();
		for(Entry<Object, Object> entry : props.entrySet()) {
			if(entry.getValue() instanceof String) {
				loopPlaceholderVal(props, entry.getKey(), new LinkedList<>());
			}
		}
		return props;
	}
	
	private static String loopPlaceholderVal(Properties props, Object key, LinkedList<Object> visits) {
		visits.push(key);
		Object val = props.get(key);
		if(val instanceof String) {
			StringFormatToken token = new StringFormatToken(PLACEHOLDER_OPEN, PLACEHOLDER_CLOSE, (String)val, true);
			String phkey;
			while((phkey = token.nextKey()) != null) {
				if(visits.contains(phkey)) {
					throw new BaseException("Infinite loop exception.Cause by properties key '{0}'.", phkey);
				}
				String tmpVal = loopPlaceholderVal(props, phkey, visits);
				token.insertBack(tmpVal == null ? PLACEHOLDER_OPEN + phkey + PLACEHOLDER_CLOSE : tmpVal);
			}
			String tmpVal = token.getAssemble();
			if(!val.equals(tmpVal)) {
				props.put(key, tmpVal);
				val = tmpVal;
			}
		}
		visits.pop();
		return val == null ? null : val.toString();
	}
	
	public static void main(String[] args) {
		/*Properties props = new Properties();
		for(int i = 0; i < 100; i++)
			props.setProperty("name" + i, Integer.toString(i));
		props.setProperty("var1", "${var3}");
		props.setProperty("var2", "-xx-");
		props.setProperty("var3", "--${name3}-${var${var1}}-${name3}-");
		
		processPlaceholder(props, true);
		
		System.out.println(props.get("var1"));
		System.out.println(props.get("var2"));
		System.out.println(props.get("var3"));*/
		
		/*Properties properties = new Properties();
		properties.put("name", "a");
		properties.put("name", "hh");
		properties.put("age", "2");
		store(properties, "f:/daa/da/a.properties");*/
		String dataStr = "name=xxxx\nage=yyyy";
		Properties properties = new Properties();
		try {
			load(properties, dataStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(properties.get("name"));
		System.out.println(properties.get("age"));
	}
	
}

