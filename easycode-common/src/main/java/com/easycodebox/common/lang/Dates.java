package com.easycodebox.common.lang;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author WangXiaoJin
 * 
 */
public class Dates extends org.apache.commons.lang.time.DateUtils {
	
	private static final Logger log = LoggerFactory.getLogger(Dates.class);
	
	public static final String DATETIME_FMT_STR = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FMT_STR = "yyyy-MM-dd";
	public static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static Calendar parse2Calenar(String date) {
		return parse2Calenar(date, DATETIME_FMT_STR);
	}
	
	public static Calendar parse2Calenar(String date, String formater) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat(formater).parse(date));
		} catch (ParseException e) {
			log.error("parse2Calenar method has error.",e);
			return null;
		}
		return c;
	}
	
	public static Date parse(String date) {
		return parse(date, DATETIME_FMT_STR);
	}
	
	public static Date parse(String date, String formater) {
		if(StringUtils.isBlank(formater) || StringUtils.isBlank(date)) {
			throw new IllegalArgumentException("date or formater is IllegalArgument.");
		}
		Date fd;
		try {
			fd = new SimpleDateFormat(formater).parse(date);
		} catch (ParseException e) {
			log.error("parse Date Exception.",e);
			return null;
		}
		return fd;
	}
	
	public static String format(Date d, String formater) {
		if(formater == null) throw new BaseException("formater is null.");
		if(d == null) throw new BaseException("date is null.");
		return new SimpleDateFormat(formater).format(d);
	}
	
	public static String format(Date d) {
		if(d == null) throw new BaseException("date is null.");
		return DATE_FMT.format(d);
	}
	
	public static Date format2Date(Date d, String formater) {
		if(d == null) throw new BaseException("date is null.");
		String fmt_str = format(d,formater);
		return parse(fmt_str,formater);
	}
	
	/**
	 * 返回当前的月份剩余的日期（包含今天）
	 * example： 今天2013-04-09 则返回[2013-04-09,2013-04-30]
	 * @return
	 */
	public static Date[] surplusDayOfMonth() {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.MONTH, 1);
		c2.set(Calendar.DATE, 0);
		Date beginDate = Dates.format2Date(c1.getTime(), Dates.DATE_FMT_STR);
		Date endDate = Dates.format2Date(c2.getTime(), Dates.DATE_FMT_STR);
		return new Date[]{beginDate, endDate};
	}
	
	/**
	 * 返回当天开始和结束的时间
	 * 如：当前时间为 2013-07-09 15:33:50 将返回
	 * @return 2013-07-09 00:0:00   ，  2013-07-09 23:59:59 
	 */
	public static Date[] getCurrentDayRange(){
		Calendar begin = new GregorianCalendar(); 
		Calendar end= (Calendar)begin.clone();
		
		begin.set(Calendar.HOUR_OF_DAY, 0);
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		
		end.set(Calendar.HOUR_OF_DAY, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);
		
		return new Date[]{begin.getTime(), end.getTime()};
	}
	
	/**
	 * 返回指定日期的开始和结束时间
	 * date为 2013-07-09 15:33:50 将返回
	 * @return 2013-07-09 00:0:00 ，2013-07-09 23:59:59 
	 */
	public static Date[] getDayRange(Date date){
		Calendar begin = Calendar.getInstance(); 
		begin.setTime(date);
		Calendar end= (Calendar)begin.clone();
		
		begin.set(Calendar.HOUR_OF_DAY, 0);
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		
		end.set(Calendar.HOUR_OF_DAY, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);
		
		return new Date[]{begin.getTime(), end.getTime()};
	}
	
	/**
	 * 获取指定日期的时间范围
	 * @param begin	2013-07-09 15:33:50
	 * @param end 2013-07-12 15:33:50
	 * @return	[2013-07-09 00:00:00,2013-07-12 23:59:59]
	 */
	public static Date[] getDayRange(Date begin, Date end){
		if(begin.after(end))
			throw new BaseException("param begin(Date) is after end(Date). This is invalid.");
		Calendar bc = Calendar.getInstance(); 
		bc.setTime(begin);
		Calendar ec = Calendar.getInstance();
		ec.setTime(end);
		
		bc.set(Calendar.HOUR_OF_DAY, 0);
		bc.set(Calendar.MINUTE, 0);
		bc.set(Calendar.SECOND, 0);
		
		ec.set(Calendar.HOUR_OF_DAY, 23);
		ec.set(Calendar.MINUTE, 59);
		ec.set(Calendar.SECOND, 59);
		
		return new Date[]{bc.getTime(), ec.getTime()};
	}
	
	/**
	 * 把日期和时间 拼成一个完整的日期
	 * @param date	日期
	 * @param time	时间
	 * @return
	 */
	public static Date scrableDate(Date date, Date time) {
		String d = format(date, "yyyy-MM-dd");
		String t = format(time, "HH:mm:ss");
		return parse(d + Symbol.SPACE + t);
	}
	
	/**
	 * 给指定时间 指定的field增加指定的值
	 * @param field		Calendar.field
	 * @param amount	正数或者负数
	 * @return
	 */
	public static Date add(Date date, int field, int amount) {
		Calendar cur = Calendar.getInstance();
		cur.setTime(date);
		cur.add(field, amount);
		return cur.getTime();
	}
	
	/**
	 * 给当前时间 指定的field增加指定的值
	 * @param field		Calendar.field
	 * @param amount	正数或者负数
	 * @return
	 */
	public static Date add(int field, int amount) {
		Calendar cur = Calendar.getInstance();
		cur.add(field, amount);
		return cur.getTime();
	}
	
	/**
	 * 给当前时间 指定的field增加指定的值 与compared比较
	 * @param compared	被比较的日期
	 * @param field		Calendar.field
	 * @param amount	正数或者负数
	 * @return	a negative integer, zero, or a positive integer as the date 
     *  is less than, equal to, or greater than the compared.
	 */
	public static int addAndCompare(Date compared, int field, int amount) {
		return add(field, amount).compareTo(compared);
	}
	
	/**
	 * 给指定时间 指定的field增加指定的值 与compared比较
	 * @param date	date对象add指定field的amount值
	 * @param compared	被比较的日期
	 * @param field		Calendar.field
	 * @param amount	正数或者负数
	 * @return	a negative integer, zero, or a positive integer as the date 
     *  is less than, equal to, or greater than the compared.
	 */
	public static int addAndCompare(Date date, Date compared, int field, int amount) {
		return add(date, field, amount).compareTo(compared);
	}
	
	/**
     * 功能：获取本周的开始时间
     * 示例：2013-05-13 00:00:00
     */   
    public static Date getWeekStart() {// 当周开始时间
            Calendar currentDate = Calendar.getInstance();
            currentDate.setFirstDayOfWeek(Calendar.MONDAY);
            currentDate.set(Calendar.HOUR_OF_DAY, 0);
            currentDate.set(Calendar.MINUTE, 0);
            currentDate.set(Calendar.SECOND, 0);
            currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            return currentDate.getTime();
    }
    
    /**
     * 功能：获取本周的结束时间
     * 示例：2013-05-19 23:59:59
     */   
    public static Date getWeekEnd() {// 当周结束时间
            Calendar currentDate = Calendar.getInstance();
            currentDate.setFirstDayOfWeek(Calendar.MONDAY);
            currentDate.set(Calendar.HOUR_OF_DAY, 23);
            currentDate.set(Calendar.MINUTE, 59);
            currentDate.set(Calendar.SECOND, 59);
            currentDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            return currentDate.getTime();
    }
    
    /**
     * 获取本月第一天
     * @return
     */
    public static Date getMonthStart(){
    	// 本月的第一天
    	  Calendar calendar  =   new  GregorianCalendar();
    	  calendar.set( Calendar.DATE,  1 );
    	 return calendar.getTime();
    }
    
    public static Date getMonthEnd(){
    	Calendar calendar  =   new  GregorianCalendar();
    	calendar.set( Calendar.DATE,  1 );
    	calendar.roll(Calendar.DATE,  - 1 );
    	return calendar.getTime();
    }
    
    /***
     * 获取传入日期的起始时间
     * 示例：2013-09-23 00:00:00
     */
    public static Date getDayStart(Date d) {
		String str = Dates.format(d, "yyyy-MM-dd ") + "00:00:00";
		return Dates.parse(str, "yyyy-MM-dd HH:mm:ss");
	}
    
    /***
     * 获取传入日期的截止时间
     * 示例：2013-09-23 23:59:59
     */
    public static Date getDayEnd(Date d) {
    	String str = Dates.format(d, "yyyy-MM-dd ") + "23:59:59";
    	return Dates.parse(str, "yyyy-MM-dd HH:mm:ss");
    }
    
    public static Date addDate(Date date, int field, int amount) {
        Calendar cld = Calendar.getInstance();
        cld.setTime(date);
        cld.add(field, amount);
        return cld.getTime();
    }
    public static void main(String[] args) {
		System.out.println(getDayEnd(new Date()));
	}
    
    
    /**
     * 通过日期获取这日期所在周的周1和周日时间
     * @param date
     * @return
     */
    public static Date[] getMondayAndSundayByDate(Date date){
    	
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
    	if (dayofweek == 0)
    	   dayofweek = 7;
    	c.add(Calendar.DATE, -dayofweek + 1);
    	Date startDate=c.getTime(); 
    	
    	
    	c = Calendar.getInstance();
    	c.setTime(date);
    	dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
    	if (dayofweek == 0)
    	   dayofweek = 7;
    	c.add(Calendar.DATE, -dayofweek + 7);
    	Date endDate=c.getTime(); 
    	
    	return getDayRange(startDate,endDate);
    	
    }
    
    public static Date getDateByms(long time){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(time);

    	return  calendar.getTime();
    	
    }

}
