package com.easycodebox.common.lang;

import com.easycodebox.common.Copyable;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.lang.reflect.*;
import com.easycodebox.common.validate.Assert;
import org.springframework.cglib.beans.BeanCopier;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author WangXiaoJin
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class Objects extends org.apache.commons.lang.ObjectUtils {
	//
	private static final ConcurrentHashMap<Class<?>, BeanCopier> BEAN_COPIERS = new ConcurrentHashMap<>();
	
	//
	private static final Set<Class<?>> PRIMITIVES_AND_WRAPPERS = new HashSet<>();
	static {
		PRIMITIVES_AND_WRAPPERS.add(boolean.class);
		PRIMITIVES_AND_WRAPPERS.add(Boolean.class);
		PRIMITIVES_AND_WRAPPERS.add(byte.class);
		PRIMITIVES_AND_WRAPPERS.add(Byte.class);
		PRIMITIVES_AND_WRAPPERS.add(char.class);
		PRIMITIVES_AND_WRAPPERS.add(Character.class);
		PRIMITIVES_AND_WRAPPERS.add(double.class);
		PRIMITIVES_AND_WRAPPERS.add(Double.class);
		PRIMITIVES_AND_WRAPPERS.add(float.class);
		PRIMITIVES_AND_WRAPPERS.add(Float.class);
		PRIMITIVES_AND_WRAPPERS.add(int.class);
		PRIMITIVES_AND_WRAPPERS.add(Integer.class);
		PRIMITIVES_AND_WRAPPERS.add(long.class);
		PRIMITIVES_AND_WRAPPERS.add(Long.class);
		PRIMITIVES_AND_WRAPPERS.add(short.class);
		PRIMITIVES_AND_WRAPPERS.add(Short.class);
	}
	
	//
	private static final Set<Class<?>> IMMUTABLE_CLASSES = new HashSet<>();
	static {
		IMMUTABLE_CLASSES.add(Boolean.class);
		IMMUTABLE_CLASSES.add(Byte.class);
		IMMUTABLE_CLASSES.add(Character.class);
		IMMUTABLE_CLASSES.add(Double.class);
		IMMUTABLE_CLASSES.add(Float.class);
		IMMUTABLE_CLASSES.add(Integer.class);
		IMMUTABLE_CLASSES.add(Long.class);
		IMMUTABLE_CLASSES.add(Short.class);
		IMMUTABLE_CLASSES.add(Class.class);
		IMMUTABLE_CLASSES.add(String.class);
		IMMUTABLE_CLASSES.add(BigDecimal.class);
		IMMUTABLE_CLASSES.add(BigInteger.class);
		IMMUTABLE_CLASSES.add(java.util.Date.class);
		IMMUTABLE_CLASSES.add(java.sql.Date.class);
	}
	
	/**
	 * 
	 */
	public static <T> boolean isEquals(T lhs, T rhs) {
		if(lhs == null && rhs == null) {
			return true;
		} else if(lhs == null || rhs == null) {
			return false;
		} else {
			return lhs.equals(rhs);
		}
	}
	
	/**
	 * 
	 */
	public static boolean isArray(Object array) {
		// Precondition checking
		if(array == null) {
			return false;
		}
		
		//
		return array.getClass().isArray();
	}
	
	public static boolean isCloneable(Object obj) {
		// Precondition checking
		if(obj == null) {
			return false;
		}
		
		//
		final Class<?> clazz = obj.getClass();
		if(!Cloneable.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		
		//
		Method method = Methods.findPublicMethod(clazz, "clone", new Class<?>[]{});
		return method != null;
	}
	
	public static boolean isPrimitiveOrWrapper(Object obj) {
		// Precondition checking
		if(obj == null) {
			return false;
		}
		
		//
		return PRIMITIVES_AND_WRAPPERS.contains(obj.getClass());
	}
	
	public static boolean isArrayOfPrimitives(Object array) {
		// Precondition checking
		if (array == null) {
			return false;
		}
		
		//
		Class<?> clazz = array.getClass();
		return clazz.isArray() && clazz.getComponentType().isPrimitive();
	}
	
	public static boolean isArrayOfPrimitivesOrWrappers(Object array) {
		// Precondition checking
		if (array == null) {
			return false;
		}
		
		//
		Class<?> clazz = array.getClass();
		return clazz.isArray() && PRIMITIVES_AND_WRAPPERS.contains(clazz);
	}
	
	/**
	 * 
	 */
	public static Object getDefaultValue(Class<?> clazz) {
		// Precondition checking
		if(clazz == null || !clazz.isPrimitive()) {
			return null;
		}
		if(void.class == clazz) return null;
		if(boolean.class == clazz) return Boolean.FALSE;
		if(byte.class == clazz) return (byte) 0;
		if(short.class == clazz) return (short) 0;
		if(int.class == clazz) return 0;
		if(long.class == clazz) return 0L;
		if(char.class == clazz) return (char) 0;
		if(double.class == clazz) return 0d;
		if(float.class == clazz) return 0f;
		throw new RuntimeException("assertion failed, should not reach here, clazz: " + clazz);
	}

	/**
	 * 
	 */
	public static Object copy(Object obj) {
		// Precondition checking
		if(obj == null) {
			return null;
		}
		
		//
		final Class<?> clazz = obj.getClass();
		if(isPrimitiveOrWrapper(obj)) {
			return obj;
		}
		if(IMMUTABLE_CLASSES.contains(clazz)) {
			return obj;
		}
		
		//
		if(isArray(obj)) {
			return arrayCopy(obj);
		}
		if(Set.class.isAssignableFrom(clazz)) {
			return setCopy((Set)obj);
		}
		if(Map.class.isAssignableFrom(clazz)) {
			return mapCopy((Map)obj);
		}
		if(List.class.isAssignableFrom(clazz)) {
			return listCopy((List)obj);
		}
		
		//
		if(Copyable.class.isAssignableFrom(clazz)) {
			return ((Copyable)obj).copy();
		}
		if(Cloneable.class.isAssignableFrom(clazz) && isCloneable(obj)) {
			return cloneCopy(obj);
		}
		if(Serializable.class.isAssignableFrom(clazz)) {
			return Serializations.copy((Serializable)obj);
		}

		//
		try {
			Object r = clazz.newInstance();
			copy(obj, r);
			return r;
		} catch(Exception e) {
			throw new RuntimeException("failed to copy " + obj, e);
		}
	}
	
	public static Object arrayCopy(Object array) {
		// Precondition checking
		if(!isArray(array)) {
			throw new IllegalArgumentException("parameter array is not an Array");
		}
		
		//
		final int length = Array.getLength(array);
		final Object r = Array.newInstance(array.getClass().getComponentType(), length);
		for(int i = 0; i < length; i++) {
			final Object element = Array.get(array, i);
			if(isArray(element)) {
				Array.set(r, i, arrayCopy(element));
			} else {
				Array.set(r, i, copy(element));
			}
		}
		return r;
	}
	
	public static Object setCopy(Set set) {
		// Precondition checking
		if(set == null) {
			return null;
		}
		
		//
		try {
			Set r = set.getClass().newInstance();
			for(Object obj : set) {
				r.add(copy(obj));
			}
			return r;
		} catch (Exception e) {
			throw new RuntimeException("failed to copy set", e);
		}
	}
	
	public static Object mapCopy(Map map) {
		// Precondition checking
		if(map == null) {
			return null;
		}
		
		//
		try {
			Map r = map.getClass().newInstance();
			for(Object key : map.keySet()) {
				r.put(key, copy(map.get(key)));
			}
			return r;
		} catch (Exception e) {
			throw new RuntimeException("failed to copy map", e);
		}
	}
	
	public static Object listCopy(List list) {
		// Precondition checking
		if(list == null) {
			return null;
		}
		
		//
		try {
			List r = list.getClass().newInstance();
			for(Object obj : list) {
				r.add(copy(obj));
			}
			return r;
		} catch (Exception e) {
			throw new RuntimeException("failed to copy list", e);
		}
	}
	
	public static Object cloneCopy(Object obj) {
		// Precondition checking
		if(obj == null) {
			return null;
		}
		if(!isCloneable(obj)) {
			throw new IllegalArgumentException("parameter obj: " + obj + " is not cloneable");
		}
		
		//
		try {
			final Class<?> clazz = obj.getClass();
			Method method = Methods.findPublicMethod(clazz, "clone", new Class<?>[]{});
			method.setAccessible(true);
			return method.invoke(obj);
		} catch (Exception e) {
			throw new RuntimeException("failed to clone copy", e);
		}
	}
	
	/**
	 * 
	 */
	public static void copy(Object src, Object dst) {
		// Precondition checking
		if(src == null) {
			throw new IllegalArgumentException("invalid parameter src");
		}
		if(dst == null) {
			throw new IllegalArgumentException("invalid parameter dst");
		}
		if(!src.getClass().equals(dst.getClass())) {
			throw new IllegalArgumentException("the class does not match, src: " + src.getClass() + ", dst: " + dst.getClass());
		}
		
		//
		final Class<?> clazz = src.getClass();
		BeanCopier copier = BEAN_COPIERS.get(clazz);
		if(copier == null) {
			copier = BeanCopier.create(clazz, clazz, false);
			BeanCopier existing = BEAN_COPIERS.putIfAbsent(clazz, copier);
			if(existing != null) {
				copier = existing;
			}
		}
		
		//
		copier.copy(src, dst, null);
	}

	/**
	 * 
	 */
	public static boolean registerImmutableClass(Class<?> clazz) {
		return IMMUTABLE_CLASSES.add(clazz);
	}
	
	public static boolean unregisterImmutableClass(Class<?> clazz) {
		return IMMUTABLE_CLASSES.remove(clazz);
	}
	
	/**
	 * 请用其他工具类。如：org.apache.commons.beanutils.PropertyUtils
	 * @param data	可以是map等
	 * @param key	可以传name[0].name格式
	 */
	@Deprecated
	public static Object getMappingValue(Object data, String key) {
		if(data == null || Strings.isBlank(key))
			return null;
		int index = key.indexOf(Symbol.PERIOD),
			arrayIndex = -1;
		String firstKey = index > -1 ? key.substring(0, index) : key;
		Pattern p = Pattern.compile("(\\w+)\\[([0-9]+)\\]$");
		Matcher m = p.matcher(firstKey);
		if(m.find()) {
			firstKey = m.group(1);
			arrayIndex = Integer.parseInt(m.group(2));
		}
		Object tmp;
		if(data instanceof Map)
			tmp = ((Map)data).get(firstKey);
		else
			tmp = Fields.getBeanProperty(data, firstKey);
		if(tmp != null && arrayIndex > -1) {
			if(tmp instanceof List) {
				List tmpList = ((List) tmp);
				tmp = tmpList.get(arrayIndex);
			}else if(tmp.getClass().isArray()) {
				if(arrayIndex > ((Object[])tmp).length) {
					tmp = null;
				}else {
					tmp = ((Object[])tmp)[arrayIndex];
				}
			}else 
				throw new BaseException("class {0} is not array or list.", tmp.getClass());
		}
		if(tmp != null && index > -1) {
			return getMappingValue(tmp, key.substring(index + 1, key.length()));
		}else
			return tmp;
	}
	
	/**
	 * 获取指定key对应value的类型
	 * 请用其他工具类。如：org.apache.commons.beanutils.PropertyUtils
	 * @param clazz	可以是map等
	 * @param key	可以传name[0].name格式
	 */
	@Deprecated
	public static Class getMappingValueType(Class clazz, String key) {
		Assert.notNull(clazz, "clazz can not be null.");
		Assert.notBlank(key, "key can not be blank.");
		if(Map.class.isAssignableFrom(clazz))
			return Object.class;
		else {
			int index = key.indexOf(Symbol.PERIOD),
				arrayIndex = -1;
			String firstKey = index > -1 ? key.substring(0, index) : key;
			Pattern p = Pattern.compile("(\\w+)\\[([0-9]+)\\]$");
			Matcher m = p.matcher(firstKey);
			if(m.find()) {
				firstKey = m.group(1);
				arrayIndex = Integer.parseInt(m.group(2));
			}
			Class<?> valType = Fields.getBeanPropertyType(clazz, firstKey);
			if(arrayIndex > -1) {
				if(List.class.isAssignableFrom(valType)) {
					valType = Fields.getFieldGenericType(clazz, firstKey);
				}else if(valType.isArray()) {
					valType = valType.getComponentType();
				}else 
					throw new BaseException("class {0} is not array or list.", valType);
			}
			if(index > -1)
				return getMappingValueType(valType, key.substring(index + 1, key.length()));
			else
				return valType;
		}
	}
	
	/**
	 * 请用其他工具类。如：org.apache.commons.beanutils.PropertyUtils
	 * @param data	可以是map等
	 * @param key	可以传name[0].name格式
	 * @param value
	 */
	@Deprecated
	public static void setMappingValue(Object data, String key, Object value) {
		setMappingValue(data, key, value, null);
	}
	
	/**
	 * 请用其他工具类。如：org.apache.commons.beanutils.PropertyUtils
	 * @param data	可以是map等
	 * @param key	可以传name[0].name格式
	 * @param value
	 */
	@Deprecated
	public static void setMappingValue(Object data, String key, Object value, Map<String, Class<?>> assignClasses) {
		if(data == null) return;
		int index = key.indexOf(Symbol.PERIOD),
			arrayIndex = -1;
		String firstKey = index > -1 ? key.substring(0, index) : key;
		
		Pattern p = Pattern.compile("(\\w+)\\[([0-9]+)\\]$");
		Matcher m = p.matcher(firstKey);
		if(m.find()) {
			firstKey = m.group(1);
			arrayIndex = Integer.parseInt(m.group(2));
		}
		
		Class assignClass = null;
		if(assignClasses != null && assignClasses.size() > 0) {
			String complexKey = arrayIndex > -1 ? firstKey + "[]" : firstKey;
			assignClass = assignClasses.get(complexKey);
			if(assignClasses.size() > 0) {
				Map<String, Class<?>> tmpMap = new HashMap<>(assignClasses.size());
				for(String assignKey : assignClasses.keySet()) {
					if(assignKey.startsWith(complexKey)) {
						tmpMap.put(assignKey.substring(index + 1), assignClasses.get(assignKey));
					}
				}
				assignClasses = tmpMap;
			}
		}
		
		if(data instanceof Map) {
			Map map = ((Map)data);
			if(index > -1) {
				Object tmp = map.get(firstKey);
				if(arrayIndex > -1) {
					if(tmp == null) {
						tmp = new ArrayList();
						map.put(firstKey, tmp);
					}
					if(tmp instanceof List) {
						List tmpList = ((List) tmp);
						Object obj = tmpList.get(arrayIndex);
						if(obj == null) {
							if(assignClass != null) {
								obj = Classes.newInstance(assignClass);
							}else {
								obj = new HashMap();
							}
							tmpList.add(arrayIndex, obj);
						}
						setMappingValue(obj, key.substring(index + 1, key.length()), value, assignClasses);
					}else if(tmp.getClass().isArray()) {
						if(arrayIndex > ((Object[])tmp).length) {
							tmp = Arrays.copyOf((Object[])tmp, arrayIndex);
						}
						Object obj = Array.get(tmp, arrayIndex);
						if(obj == null) {
							if(assignClass != null) {
								obj = Classes.newInstance(assignClass);
							}else {
								Class type = tmp.getClass().getComponentType();
								obj = type == null ? new HashMap() : Classes.newInstance(type);
							}
							Array.set(tmp, arrayIndex, obj);
						}
						setMappingValue(obj, key.substring(index + 1, key.length()), value, assignClasses);
					}else 
						throw new BaseException("class {0} is not array or list.", tmp.getClass());
				}else {
					if(tmp == null) {
						if(assignClass != null) {
							tmp = Classes.newInstance(assignClass);
						}else {
							tmp = new HashMap();
						}
						map.put(firstKey, tmp);
					}
					setMappingValue(tmp, key.substring(index + 1, key.length()), value, assignClasses);
				}
			}else {
				if(value == null) return;
				if(arrayIndex > -1) {
					Object tmp = map.get(firstKey);
					if(tmp == null) {
						tmp = new ArrayList();
						map.put(firstKey, tmp);
					}
					if(tmp instanceof List) {
						((List) tmp).add(arrayIndex, value);
					}else if(tmp.getClass().isArray()) {
						if(arrayIndex > ((Object[])tmp).length) {
							tmp = Arrays.copyOf((Object[])tmp, arrayIndex);
						}
						if(!Classes.isAssignable(value.getClass(), tmp.getClass().getComponentType(), true)) {
							try {
								value = DataConvert.convertType(value.toString(), tmp.getClass().getComponentType());
							} catch (BaseException ignored) {
								
							}
						}
						Array.set(tmp, arrayIndex, value);
					}else 
						throw new BaseException("class {0} is not array or list.", tmp.getClass());
				}else
					map.put(firstKey, value);
			}
		}else {
			Object tmp = Fields.getBeanProperty(data, firstKey);
			if(index > -1) {
				if(tmp == null) {
					if(assignClass != null && arrayIndex == -1) {
						tmp = Classes.newInstance(assignClass);
					}else {
						Class type = Fields.getBeanPropertyType(data.getClass(), firstKey);
						tmp = Classes.newInstance(type);
					}
					Assert.notNull(tmp, "Class {0} can not instance", tmp.getClass());
					Fields.setBeanProperty(data, firstKey, tmp);
				}
				if(arrayIndex > -1) {
					if(tmp instanceof List) {
						List tmpList = ((List) tmp);
						Object obj = tmpList.get(arrayIndex);
						if(obj == null) {
							if(assignClass != null) {
								obj = Classes.newInstance(assignClass);
							}else {
								Class type = Fields.getFieldGenericType(data.getClass(), firstKey);
								obj = type == null ? new HashMap() : Classes.newInstance(type);
							}
							tmpList.add(arrayIndex, obj);
						}
						setMappingValue(obj, key.substring(index + 1, key.length()), value, assignClasses);
					}else if(tmp.getClass().isArray()) {
						if(arrayIndex > ((Object[])tmp).length) {
							tmp = Arrays.copyOf((Object[])tmp, arrayIndex);
						}
						Object obj = Array.get(tmp, arrayIndex);
						if(obj == null) {
							if(assignClass != null) {
								obj = Classes.newInstance(assignClass);
							}else {
								Class type = tmp.getClass().getComponentType();
								obj = type == null ? new HashMap() : Classes.newInstance(type);
							}
							Array.set(tmp, arrayIndex, obj);
						}
						setMappingValue(obj, key.substring(index + 1, key.length()), value, assignClasses);
					}else 
						throw new BaseException("class {0} is not array or list.", tmp.getClass());
				}else {
					setMappingValue(tmp, key.substring(index + 1, key.length()), value, assignClasses);
				}
			}else {
				if(value == null) return;
				if(arrayIndex > -1) {
					if(tmp == null) {
						Class type = Fields.getBeanPropertyType(data.getClass(), firstKey);
						if(type == Object.class)
							type = List.class;
						tmp = Classes.newInstance(type);
						Assert.notNull(tmp, "Class {0} can not instance", tmp.getClass());
						Fields.setBeanProperty(data, firstKey, tmp);
					}
					if(tmp instanceof List) {
						Class type = Fields.getFieldGenericType(data.getClass(), firstKey);
						if(!Classes.isAssignable(value.getClass(), type, true)) {
							try {
								value = DataConvert.convertType(value.toString(), type);
							} catch (BaseException ignored) {
								
							}
						}
						((List) tmp).add(arrayIndex, value);
					}else if(tmp.getClass().isArray()) {
						if(!Classes.isAssignable(value.getClass(), tmp.getClass().getComponentType(), true)) {
							try {
								value = DataConvert.convertType(value.toString(), tmp.getClass().getComponentType());
							} catch (BaseException ignored) {
								
							}
						}
						if(arrayIndex > ((Object[])tmp).length) {
							tmp = Arrays.copyOf((Object[])tmp, arrayIndex);
						}
						Array.set(tmp, arrayIndex, value);
					}else 
						throw new BaseException("class {0} is not array or list.", tmp.getClass());
				}else {
					Class type = tmp == null ? Fields.getBeanPropertyType(data.getClass(), firstKey) : tmp.getClass();
					if(!Classes.isAssignable(value.getClass(), type, true)) {
						try {
							value = DataConvert.convertType(value.toString(), type);
						} catch (BaseException ignored) {
							
						}
					}
					Fields.setBeanProperty(data, firstKey, value);
				}
			}
		}
	}
	
}
