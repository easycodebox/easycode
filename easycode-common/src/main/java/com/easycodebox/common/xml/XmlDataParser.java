package com.easycodebox.common.xml;

import com.easycodebox.common.lang.DataConvert;
import com.easycodebox.common.validate.Assert;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.modelmbean.XMLParseException;
import java.util.*;

/**
 * @author WangXiaoJin
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class XmlDataParser {

	private static final Logger log = LoggerFactory.getLogger(XmlDataParser.class);

	public static final String COMMENT_ELEMENT = "comment";

	public static final String TRUE_VALUE = "true";

	public static final String DEFAULT_VALUE = "default";

	public static final String NAME_ATTRIBUTE = "name";
 
	public static final String TYPE_ATTRIBUTE = "type";

	public static final String VALUE_TYPE_ATTRIBUTE = "value-type";

	public static final String KEY_TYPE_ATTRIBUTE = "key-type";

	public static final String VALUE_ATTRIBUTE = "value";

	public static final String VALUE_ELEMENT = "value";

	public static final String NULL_ELEMENT = "null";

	public static final String LIST_ELEMENT = "list";

	public static final String SET_ELEMENT = "set";

	public static final String MAP_ELEMENT = "map";

	public static final String ENTRY_ELEMENT = "entry";

	public static final String KEY_ELEMENT = "key";

	public static final String KEY_ATTRIBUTE = "key";

	public static final String PROPS_ELEMENT = "props";

	public static final String PROP_ELEMENT = "prop";
	
	public static final Map<String,Class<?>> classPool = new HashMap<>();

	static {
		classPool.put("int", Integer.class);
		classPool.put("long", Long.class);
		classPool.put("date", java.util.Date.class);
		classPool.put("string", String.class);
	}

	public static Object parseDataElement(Element ele) throws XMLParseException{
		List<Element> el = ele.elements();
		Element subElement = null;
		for (Element e : el) {
			if (!COMMENT_ELEMENT.equals(e.getName())) {
				if (subElement != null)
					throw new XMLParseException(ele.getName() + " must not contain more than one sub-element");
				else
					subElement = e;
			}
		}

		Attribute valueAttr = ele.attribute(VALUE_ATTRIBUTE);
		if (valueAttr != null && subElement != null) {
			throw new XMLParseException(ele.getName() +
					" is only allowed to contain either 'value' attribute OR sub-element");
		}

		if(valueAttr != null) {
			String typeClassName = ele.attributeValue(TYPE_ATTRIBUTE);
			String value = ele.attributeValue(VALUE_ATTRIBUTE);
			try {
				return buildTypedStringValue(value , typeClassName, ele);
			} catch (ClassNotFoundException e) {
				log.error("Type class [{}] not found for value {}", typeClassName, value, e);
				return value;
			}
		}else if (subElement != null) {
			return parseDataSubElement(subElement);
		}else 
			throw new XMLParseException(ele.getName() + " must specify a value");
	}

	public static Object parseDataSubElement(Element ele)  throws XMLParseException{
		return parseDataSubElement(ele, null);
	}

	/**
	 * Parse a value, ref or collection sub-element of a property or
	 * constructor-arg element.
	 * @param ele subelement of property element; we don't know which yet
	 * @param defaultTypeClassName the default type (class name) for any
	 * <code>&lt;value&gt;</code> tag that might be created
	 */
	public static Object parseDataSubElement(Element ele, String defaultTypeClassName) throws XMLParseException{
		if (VALUE_ELEMENT.equals(ele.getName()))
			return parseValueElement(ele, defaultTypeClassName);
		else if (NULL_ELEMENT.equals(ele.getName())) 
			return null;
		else if (LIST_ELEMENT.equals(ele.getName())) 
			return parseListElement(ele);
		else if (SET_ELEMENT.equals(ele.getName())) 
			return parseSetElement(ele);
		else if (MAP_ELEMENT.equals(ele.getName())) 
			return parseMapElement(ele);
		else if (PROPS_ELEMENT.equals(ele.getName())) 
			return parsePropsElement(ele);
		else 
			throw new XMLParseException("Unknown property sub-element: [" + ele.getName() + "]");
		
	}


	/**
	 * Return a typed String value Object for the given value element.
	 */
	public static Object parseValueElement(Element ele, String defaultTypeClassName) throws XMLParseException{
		String value = ele.getTextTrim();
		String typeClassName = ele.attributeValue(TYPE_ATTRIBUTE);
		if (StringUtils.isBlank(typeClassName)) {
			typeClassName = defaultTypeClassName;
		}
		try {
			return buildTypedStringValue(value, typeClassName, ele);
		}catch (ClassNotFoundException ex) {
			log.error("Type class [{}] not found for <value> element", typeClassName, ex);
			return value;
		}
	}

	protected static Object buildTypedStringValue(String value, String targetTypeName, Element ele)
			throws ClassNotFoundException, XMLParseException {
		if(StringUtils.isNotBlank(targetTypeName)) {
			Class<?> clazz = classPool.get(targetTypeName);
			if(clazz == null)
				clazz = Class.forName(targetTypeName);
			if (clazz != null) 
				return DataConvert.convertType(value, clazz);
		}
		return value;
	}

	public static List parseListElement(Element collectionEle) throws XMLParseException{
		String defaultTypeClassName = collectionEle.attributeValue(VALUE_TYPE_ATTRIBUTE);
		List<Element> el = collectionEle.elements();
		List values = new ArrayList(el.size());
		for (Element e : el) {
			if (!COMMENT_ELEMENT.equals(e.getName())) {
				values.add(parseDataSubElement(e, defaultTypeClassName));
			}
		}
		return values;
	}

	public static Set parseSetElement(Element collectionEle) throws XMLParseException{
		String defaultTypeClassName = collectionEle.attributeValue(VALUE_TYPE_ATTRIBUTE);
		List<Element> el = collectionEle.elements();
		Set set = new HashSet(el.size());
		for (Element e : el) {
			if (!COMMENT_ELEMENT.equals(e.getName())) {
				set.add(parseDataSubElement(e, defaultTypeClassName));
			}
		}
		return set;
	}

	public static Map parseMapElement(Element mapEle) throws XMLParseException{
		String defaultKeyTypeClassName = mapEle.attributeValue(KEY_TYPE_ATTRIBUTE);
		String defaultValueTypeClassName = mapEle.attributeValue(VALUE_TYPE_ATTRIBUTE);

		List entryEles = mapEle.elements(ENTRY_ELEMENT);
		Map map = new HashMap(entryEles.size());
		for (Iterator it = entryEles.iterator(); it.hasNext();) {
			Element entryEle = (Element) it.next();
			// Should only have one value child element: value, list, etc.
			// Optionally, there might be a key child element.
			List<Element> el = entryEle.elements();

			Element keyEle = null;
			Element valueEle = null;
			for (Element candidateEle : el) {
				if (KEY_ELEMENT.equals(candidateEle.getName())) {
					if (keyEle != null) {
						throw new XMLParseException(entryEle.attributeValue("name") +
								"<entry> element is only allowed to contain one <key> sub-element");
					} else {
						keyEle = candidateEle;
					}
				} else {
					if (valueEle != null) {
						throw new XMLParseException(entryEle.attributeValue("name") +
								"<entry> element must not contain more than one value sub-element");
					} else {
						valueEle = candidateEle;
					}
				}
			}

			// Extract key from attribute or sub-element.
			Object key;
			Attribute keyAttribute = entryEle.attribute(KEY_ATTRIBUTE);
			if (keyAttribute != null && keyEle != null) {
				throw new XMLParseException(entryEle + "<entry> element is only allowed to contain either " +
						"a 'key' attribute OR a <key> sub-element");
			}
			if (keyAttribute != null) {
				key = buildTypedStringValueForMap(
						entryEle.attributeValue(KEY_ATTRIBUTE), defaultKeyTypeClassName, entryEle);
			} else if (keyEle != null) {
				key = parseKeyElement(keyEle,defaultKeyTypeClassName);
			} else {
				throw new XMLParseException("<entry> element must specify a key" + entryEle);
			}

			// Extract value from attribute or sub-element.
			Object value;
			Attribute valueAttribute = entryEle.attribute(VALUE_ATTRIBUTE);
			if (valueAttribute != null && valueEle != null) {
				throw new XMLParseException("<entry> element is only allowed to contain either " +
						"'value' attribute OR <value> sub-element" + entryEle);
			}
			if (valueAttribute != null) {
				value = buildTypedStringValueForMap(
						entryEle.attributeValue(VALUE_ATTRIBUTE), defaultValueTypeClassName, entryEle);
			}
			else if (valueEle != null) {
				value = parseDataSubElement(valueEle,defaultValueTypeClassName);
			}
			else {
				throw new XMLParseException("<entry> element must specify a value" + entryEle);
			}
			map.put(key, value);
		}

		return map;
	}

	protected static Object buildTypedStringValueForMap(String value, String defaultTypeClassName, Element entryEle) throws XMLParseException{
		try {
			return buildTypedStringValue(value, defaultTypeClassName, entryEle);
		}
		catch (ClassNotFoundException ex) {
			log.error("Type class [{}] not found for Map key/value type {}", defaultTypeClassName, entryEle, ex);
			return value;
		}
	}

	public static Object parseKeyElement(Element keyEle, String defaultKeyTypeClassName) throws XMLParseException{
		List<Element> el = keyEle.elements();
		Element subElement = null;
		for (Element e : el) {
			if (!COMMENT_ELEMENT.equals(e.getName())) {
				if (subElement != null) {
					throw new XMLParseException("<key> element must not contain more than one value sub-element" + keyEle);
				} else {
					subElement = e;
				}
			}
		}
		Attribute valueAttribute = keyEle.attribute(VALUE_ATTRIBUTE);
		if (valueAttribute != null && subElement != null) {
			throw new XMLParseException("<entry> element is only allowed to contain either " +
					"'value' attribute OR <value> sub-element" + subElement);
		}
		if (valueAttribute != null) {
			try {
				return  buildTypedStringValue(
							keyEle.attributeValue(VALUE_ATTRIBUTE), defaultKeyTypeClassName, keyEle);
			} catch (ClassNotFoundException e) {
				return keyEle.attributeValue(VALUE_ATTRIBUTE);
			}
		}
		else if (subElement != null) {
			return parseDataSubElement(subElement,defaultKeyTypeClassName);
		}
		else {
			throw new XMLParseException("<entry> element must specify a value" + keyEle);
		}
	}

	public static Properties parsePropsElement(Element propsEle) throws XMLParseException{
		Properties props = new Properties();
		List propEles = propsEle.elements(PROP_ELEMENT);
		for (Iterator it = propEles.iterator(); it.hasNext();) {
			Element propEle = (Element) it.next();
			String key = propEle.attributeValue(KEY_ATTRIBUTE);
			// Trim the text value to avoid unwanted whitespace
			// caused by typical XML formatting.
			String value = propEle.getTextTrim();
			props.put(key, value);
		}

		return props;
	}
	
	/**
	 * 获取指定的属性值，当不存在该属性或属性为空字符窜则报NullPointerException错误
	 * @param e
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static String getXmlAttributeVal(Element e, String name) throws Exception {
		Assert.notNull(e, "Paramter element is null.");
		Assert.notBlank(name, "Paramter name is null.");
		return e.attributeValue(name);
	}
	
	public static String getXmlAttributeVal(Element e, String name, String defaultVal) throws Exception {
		Assert.notNull(e, "Paramter element is null.");
		Assert.notBlank(name, "Paramter name is null.");
		String value = e.attributeValue(name);
		return value == null ? defaultVal : value;
	}

}
