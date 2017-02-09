package com.easycodebox.jdbc.mybatis;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.enums.EnumClassFactory;
import com.easycodebox.common.lang.Objects;
import com.easycodebox.common.lang.StringToken.StringFormatToken;
import com.easycodebox.common.lang.reflect.Fields;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * 处理SQL语句中包含 %{} 占位符， 使其转换成枚举值
 * @author WangXiaoJin
 *
 */
public class DelegateSqlSource implements SqlSource {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private SqlSource delegate;
	
	public DelegateSqlSource(SqlSource delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BoundSql getBoundSql(Object parameterObject) {
		BoundSql boundSql = delegate.getBoundSql(parameterObject);
		String sql = boundSql.getSql();
		StringFormatToken token = new StringFormatToken("%{", "}", sql, true);
		String key;
		while((key = token.nextKey()) != null) {
			String[] frags = key.split("\\.");
			if(frags.length == 2) {
				Class<? extends Enum<?>> enumClass = EnumClassFactory.newInstance(frags[0]);
				try {
					Enum e = Enum.valueOf((Class)enumClass, frags[1]);
					if(e instanceof DetailEnum)
						token.insertBack(Objects.toString(((DetailEnum)e).getValue(), "null"));
					else
						token.insertBack(Integer.toString(e.ordinal()));
				} catch (Exception e) {
					//不匹配则插入原值
					token.insertBack("%{" + key + "}");
				}
			}else {
				//不匹配则插入原值
				token.insertBack("%{" + key + "}");
			}
		}
		try {
			Fields.writeField(boundSql, "sql", token.getAssemble(), true);
		} catch (Exception e) {
			log.error("set field value error.", e);
		}
		return boundSql;
	}

}
