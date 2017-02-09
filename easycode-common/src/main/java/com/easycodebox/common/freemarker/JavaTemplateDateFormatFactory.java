package com.easycodebox.common.freemarker;

import com.easycodebox.common.lang.*;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import freemarker.core.*;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModelException;

import java.text.*;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 重写freemarker.core.JavaTemplateDateFormatFactory类。当表达式${obj.birth?datetime}中birth属性为null且classic_compatible=true时
 * 会抛异常，classic_compatible=true时会把null转成空字符串，这时拿空字符窜转换成datetime类型，会报空字符串不能格式化成datetime格式。此时唯一的
 * 解决方案是：${(obj.birth?string('yyyy-MM-dd HH:mm:ss'))!}，不过这种解决方案比较繁琐，所以重写了Freemarker日期格式化类。
 * 此类可直接使用${obj.birth?datetime}，并且不会抛异常。 <br>
 * 修改过的地方已标注了。
 * @author WangXiaoJin
 *
 */
public class JavaTemplateDateFormatFactory extends TemplateDateFormatFactory {
    
    static final JavaTemplateDateFormatFactory INSTANCE = new JavaTemplateDateFormatFactory(); 
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private static final ConcurrentHashMap<CacheKey, DateFormat> GLOBAL_FORMAT_CACHE = new ConcurrentHashMap<>();
    private static final int LEAK_ALERT_NUMBER_FORMAT_CACHE_SIZE = 1024;
    
    private JavaTemplateDateFormatFactory() {
        // Can't be instantiated
    }
    
    /**
     * @param zonelessInput
     *            Has no effect in this implementation.
     */
    @Override
    public TemplateDateFormat get(String params, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput,
            Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
        return new JavaTemplateDateFormat(getJavaDateFormat(dateType, params, locale, timeZone));
    }

    /**
     * Returns a "private" copy (not in the global cache) for the given format.  
     */
    private DateFormat getJavaDateFormat(int dateType, String nameOrPattern, Locale locale, TimeZone timeZone)
            throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {

        // Get DateFormat from global cache:
        CacheKey cacheKey = new CacheKey(dateType, nameOrPattern, locale, timeZone);
        DateFormat jFormat;
        
        jFormat = GLOBAL_FORMAT_CACHE.get(cacheKey);
        if (jFormat == null) {
            // Add format to global format cache.
            StringTokenizer tok = new StringTokenizer(nameOrPattern, "_");
            int tok1Style = tok.hasMoreTokens() ? parseDateStyleToken(tok.nextToken()) : DateFormat.DEFAULT;
            if (tok1Style != -1) {
                switch (dateType) {
                    case TemplateDateModel.UNKNOWN: {
                        throw new UnknownDateTypeFormattingUnsupportedException();
                    }
                    case TemplateDateModel.TIME: {
                        jFormat = DateFormat.getTimeInstance(tok1Style, cacheKey.locale);
                        break;
                    }
                    case TemplateDateModel.DATE: {
                        jFormat = DateFormat.getDateInstance(tok1Style, cacheKey.locale);
                        break;
                    }
                    case TemplateDateModel.DATETIME: {
                        int tok2Style = tok.hasMoreTokens() ? parseDateStyleToken(tok.nextToken()) : tok1Style;
                        if (tok2Style != -1) {
                            jFormat = DateFormat.getDateTimeInstance(tok1Style, tok2Style, cacheKey.locale);
                        }
                        break;
                    }
                }
            }
            if (jFormat == null) {
                try {
                    jFormat = new SimpleDateFormat(nameOrPattern, cacheKey.locale);
                } catch (IllegalArgumentException e) {
                    final String msg = e.getMessage();
                    throw new InvalidFormatParametersException(
                            msg != null ? msg : "Invalid SimpleDateFormat pattern", e);
                }
            }
            jFormat.setTimeZone(cacheKey.timeZone);
            
            if (GLOBAL_FORMAT_CACHE.size() >= LEAK_ALERT_NUMBER_FORMAT_CACHE_SIZE) {
                boolean triggered = false;
                synchronized (JavaTemplateDateFormatFactory.class) {
                    if (GLOBAL_FORMAT_CACHE.size() >= LEAK_ALERT_NUMBER_FORMAT_CACHE_SIZE) {
                        triggered = true;
                        GLOBAL_FORMAT_CACHE.clear();
                    }
                }
                if (triggered) {
                	log.warn("Global Java DateFormat cache has exceeded " + LEAK_ALERT_NUMBER_FORMAT_CACHE_SIZE
                            + " entries => cache flushed. "
                            + "Typical cause: Some template generates high variety of format pattern strings.");
                }
            }
            
            DateFormat prevJFormat = GLOBAL_FORMAT_CACHE.putIfAbsent(cacheKey, jFormat);
            if (prevJFormat != null) {
                jFormat = prevJFormat;
            }
        }  // if cache miss
        
        return (DateFormat) jFormat.clone();  // For thread safety
    }

    private static final class CacheKey {
        private final int dateType;
        private final String pattern;
        private final Locale locale;
        private final TimeZone timeZone;

        CacheKey(int dateType, String pattern, Locale locale, TimeZone timeZone) {
            this.dateType = dateType;
            this.pattern = pattern;
            this.locale = locale;
            this.timeZone = timeZone;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof CacheKey) {
                CacheKey fk = (CacheKey) o;
                return dateType == fk.dateType && fk.pattern.equals(pattern) && fk.locale.equals(locale)
                        && fk.timeZone.equals(timeZone);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return dateType ^ pattern.hashCode() ^ locale.hashCode() ^ timeZone.hashCode();
        }
    }

    private int parseDateStyleToken(String token) {
        if ("short".equals(token)) {
            return DateFormat.SHORT;
        }
        if ("medium".equals(token)) {
            return DateFormat.MEDIUM;
        }
        if ("long".equals(token)) {
            return DateFormat.LONG;
        }
        if ("full".equals(token)) {
            return DateFormat.FULL;
        }
        return -1;
    }
    
    class JavaTemplateDateFormat extends TemplateDateFormat {
        
        private final DateFormat javaDateFormat;

        public JavaTemplateDateFormat(DateFormat javaDateFormat) {
            this.javaDateFormat = javaDateFormat;
        }
        
        /**
         * Modify by WangXiaoJin
         */
        @Override
        public String formatToPlainText(TemplateDateModel dateModel) throws TemplateModelException {
            return dateModel.getAsDate() == NullDate.INSTANCE ? Symbol.EMPTY
            		: javaDateFormat.format(TemplateFormatUtil.getNonNullDate(dateModel));
        }

        /**
         * Modify by WangXiaoJin
         */
        @Override
        public Date parse(String s, final int dateType) throws UnparsableValueException {
            try {
                return Strings.isEmpty(s) ? NullDate.INSTANCE : javaDateFormat.parse(s);
            } catch (ParseException e) {
                throw new UnparsableValueException(e.getMessage(), e);
            }
        }

        @Override
        public String getDescription() {
            return javaDateFormat instanceof SimpleDateFormat
                    ? ((SimpleDateFormat) javaDateFormat).toPattern()
                    : javaDateFormat.toString();
        }

        @Override
        public boolean isLocaleBound() {
            return true;
        }

        @Override
        public boolean isTimeZoneBound() {
            return true;
        }
        
    }
    
}
