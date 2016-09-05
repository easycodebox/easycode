package com.easycodebox.common.jdbc;

import com.easycodebox.common.error.BaseException;

/**
 * @author WangXiaoJin
 *
 */
public class MySqlDialect extends Dialect{
	
	@Override
	public char closeQuote() {
		return '`';
	}

	@Override
	public char openQuote() {
		return '`';
	}

	/**
	 * joinType = NONE 时，return null
	 * @param joinType
	 * @return
	 */
	@Override
	public String joinSql(JoinType joinType) {
		switch (joinType) {
			case INNER_JOIN:
				return "INNER JOIN";
			case LEFT_OUTER_JOIN:
				return "LEFT JOIN";
			case RIGHT_OUTER_JOIN:
				return "RIGHT JOIN";
			case FULL_JOIN:
				throw new BaseException("MySql do not support full join.");
			case NONE:
				return null;
			default:
				throw new BaseException("unknown JoinType " + joinType);
		}
	}

	/**
	 * 把param参数的特殊符号转义
	 * @param param
	 * @return
	 */
	@Override
	public String escapeString(String param) {
		if (param == null)
			return "null";
		StringBuilder buf = new StringBuilder((int) (param.length() * 1.1));
		buf.append('\'');
		for (int i = 0; i < param.length(); ++i) {
			char c = param.charAt(i);

			switch (c) {
			case 0: /* Must be escaped for 'mysql' */
				buf.append('\\');
				buf.append('0');
				break;

			case '\n': /* Must be escaped for logs */
				buf.append('\\');
				buf.append('n');
				break;

			case '\r':
				buf.append('\\');
				buf.append('r');
				break;

			case '\\':
				buf.append('\\');
				buf.append('\\');
				break;

			case '\'':
				buf.append('\\');
				buf.append('\'');
				break;

			case '"': /* Better safe than sorry */
				buf.append('\\');
				buf.append('"');
				break;

			case '\032': /* This gives problems on Win32 */
				buf.append('\\');
				buf.append('Z');
				break;

			default:
				buf.append(c);
			}
		}

		buf.append('\'');
		return buf.toString();
	}
	
}
