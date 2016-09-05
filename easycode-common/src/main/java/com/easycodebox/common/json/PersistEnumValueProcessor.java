package com.easycodebox.common.json;

import com.easycodebox.common.enums.DetailEnum;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * @author WangXiaoJin
 * 
 */
public class PersistEnumValueProcessor implements JsonValueProcessor {
	

    public Object processArrayValue( Object value, JsonConfig jsonConfig ) {
    	return process( value, jsonConfig );
    }

    public Object processObjectValue( String key, Object value, JsonConfig jsonConfig ) {
       return process( value, jsonConfig );
    }
    
    private Object process(Object value, JsonConfig jsonConfig){
    	JSONObject jsonObject = null;
        if( value instanceof DetailEnum ){
      	  DetailEnum<?> en = (DetailEnum<?>)value;
           jsonObject = new JSONObject()
   					.element("value", en.getValue())
   					.element("desc", en.getDesc());
        }else{
           jsonObject = new JSONObject( true );
        }
        return jsonObject;
    }

}
