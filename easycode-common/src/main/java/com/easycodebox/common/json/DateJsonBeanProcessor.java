package com.easycodebox.common.json;

import java.util.Date;

import com.easycodebox.common.lang.DateUtils;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonBeanProcessor;

/**
 * @author WangXiaoJin
 * 
 */
public class DateJsonBeanProcessor  implements JsonBeanProcessor {

	private String dateFormat = DateUtils.DATETIME_FMT_STR;
	
	public DateJsonBeanProcessor() {
		
	}

    public DateJsonBeanProcessor(String dateFormat) {
       this.dateFormat = dateFormat;
    }
    
    public JSONObject processBean( Object bean, JsonConfig jsonConfig ) {
      JSONObject jsonObject = null;
      if( bean instanceof java.sql.Date ){
         bean = new Date( ((java.sql.Date) bean).getTime() );
      }
      if( bean instanceof Date ){
         jsonObject = new JSONObject()
     					.element("date", 
 							DateUtils.format((Date)bean, dateFormat));
      }else{
         jsonObject = new JSONObject( true );
      }
      return jsonObject;
   }
}
