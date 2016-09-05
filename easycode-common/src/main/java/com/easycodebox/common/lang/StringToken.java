package com.easycodebox.common.lang;

import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 *
 */
public class StringToken {

	private String source;
	private int index = -1;
	public static final char NULL = '\000';
	
	public StringToken(String source) {
		this.source = source;
	}
	
	public boolean more() {
		return source != null && index < source.length() - 1;
	}
	
	public char next() {
		if(more()) {
			return source.charAt(++index);
		}
		return NULL;
	}
	
	protected void back() {
		if(index > 0) 
			index--;
	}

	protected void setSource(String source) {
		this.source = source;
	}
	
	public String getSource() {
		return source;
	}

	public int getIndex() {
		return index;
	}
	
	protected void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * OGNL的简单实现 <br>
	 * a.b.c 	==> 1、a:false 2、b:false 3、c:false <br>
	 * a\.b.c	==> 1、a\.b:false 2、c:false <br>
	 * a[b]  	==> 1、a:false 2、b:true <br>
	 * a['b']	==> 1、a:false 2、b:false <br>
	 * a["b"]	==> 1、a:false 2、b:false <br>
	 * 第一个值key;第二个值代表dynamicKey属性,表明表明此key从根对象上获取数据还是从前一个key上获取数据; <br>
	 * 可以通过反斜杠转义. ' " [ ] 等字符 <br>
	 */
	public static class OgnlToken extends StringToken {
		
		private boolean dynamicKey = false;
		
		public OgnlToken(String source) {
			super(source);
		}
		
		/**
		 * 判断是否是动态的key值，如果是则从根对象上获取key值
		 * @return 
		 */
		public boolean isDynamicKey() {
			return dynamicKey;
		}
		
		public void resetDynamicKey() {
			this.dynamicKey = false;
		}
		
		/**
		 * 返回空字符窜时，说明获取key截止了
		 * @return
		 */
		public String nextKey() {
			StringBuilder sb = new StringBuilder();
			dynamicKey = false;
			
			char c;
			HEAD:
			while((c = next()) != StringToken.NULL) {
				
				switch(c) {
				
				case '\\':
					sb.append(c).append(next());
					break;
				case '.': 
					break HEAD;
					
				case '[':
					if(sb.length() > 0) {
						back();
						break HEAD;
					}
					
					char inner;
					INNER:
					while((inner = next()) != StringToken.NULL) {
						
						boolean isStrKey = false;
						switch(inner) {
						
						case ']':
							break INNER;
						case '\\':
							sb.append(inner).append(next());
							break;
						case '"':
						case '\'':
							char inmost;
							INMOST:
							while((inmost = next()) != StringToken.NULL) {
								if(inmost == inner) {
									isStrKey = true;
									break INMOST;
								}
								else if(inmost == '\\')
									sb.append(inmost).append(next());
								else
									sb.append(inmost);
							}
							break;
						default:
							sb.append(inner);
							break;
							
						}
						
						dynamicKey = isStrKey ? false : true;
					}
					break;
				default:
					sb.append(c);
					break;
				}
				
			}
			
			return sb.toString().trim();
		}
		
	}
	
	/**
	 * 处理占位符格式字符窜： aa{name}bb{age}cc <br>
	 * 也可以是嵌套的占位符: aa{name{index}}bb
	 * @author WangXiaoJin
	 *
	 */
	public static class StringFormatToken extends StringToken {
		
		private StringBuilder assemble;
		/**
		 * 是否把非key字符窜窜组装进assemble对象中
		 */
		private boolean isAssemble;
		/**
		 * 当前的占位符是否处于嵌套的格式
		 */
		private boolean nest;
		private String open , close;
		
		/**
		 * 占位符的开始索引
		 */
		private int start = -1;
		
		/**
		 * 内嵌的占位标识起始索引
		 */
		private int nestStart = -1;

		/**
		 * 默认 open = '{'  close = '}'  isAssemble = false
		 * @param source
		 */
		public StringFormatToken(String source) {
			this(source, false);
		}
		
		/**
		 * 默认 open = '{'  close = '}'
		 * @param source
		 */
		public StringFormatToken(String source, boolean isAssemble) {
			this("{", "}", source, isAssemble);
		}
		
		/**
		 * 
		 * @param open
		 * @param close
		 * @param source
		 * @param isAssemble
		 */
		public StringFormatToken(String open, String close, String source, boolean isAssemble) {
			super(source);
			Assert.notBlank(open);
			Assert.notBlank(close);
			this.open = open;
			this.close = close;
			if(this.isAssemble = isAssemble) {
				assemble = new StringBuilder(source.length()*3/2);
			}
		}
		
		/**
		 * 匹配开始字符
		 * @return
		 */
		private boolean matchOpen(char c) {
			int curIndex = getIndex();
			for(int i = 0; i < open.length(); ) {
				if(c != open.charAt(i)) {
					setIndex(curIndex);
					return false;
				}
				if(++i < open.length())
					c = next();
			}
			return true;
		}
		
		/**
		 * 匹配结束字符
		 * @return
		 */
		private boolean matchClose(char c) {
			int curIndex = getIndex();
			for(int i = 0; i < close.length(); ) {
				if(c != close.charAt(i)) {
					setIndex(curIndex);
					return false;
				}
				if(++i < close.length()) 
					c = next();
			}
			return true;
		}
		
		private void initStatus() {
			start = nestStart = -1;
			nest = false;
		}
		
		/**
		 * 获取下一个key，没有则返回null
		 */
		public String nextKey() {
			initStatus();
			char c;
			boolean hasOpen = false,
					hasClose = false;
			StringBuilder key = null, nestKey = null;
			KEY:
			while((c = next()) != StringToken.NULL) {
				
				if(c == '\\') {
					if(hasOpen) {
						char n = next();
						key.append(c).append(n);
						if(nest) {
							nestKey.append(c).append(n);
						}
					}else {
						if(isAssemble)
							assemble.append(c).append(next());
					}
				}else if(matchOpen(c)) {
					if(hasOpen) {
						nest = true;
						nestKey = new StringBuilder();
						nestStart = getIndex() - open.length();
					}else {
						hasOpen = true;
						key = new StringBuilder();
						start = getIndex() - open.length();
					}
				}else if(matchClose(c)) {
					if(hasOpen) {
						hasClose = true;
						break KEY;
					}else {
						if(isAssemble)
							assemble.append(c);
					}
				}else {
					if(hasOpen) {
						key.append(c);
						if(nest) {
							nestKey.append(c);
						}
					}else {
						if(isAssemble)
							assemble.append(c);
					}
				}
				
			}
			//判断只有open没有close的情况
			if(hasOpen && !hasClose) {
				if(isAssemble)
					assemble.append(open).append(key);
				key = null;
				start = -1;
				if(nest) {
					nestKey = null;
					nestStart = -1;
				}
			}
			return nestKey == null ? key == null ? null : key.toString() : nestKey.toString();
		}

		/**
		 * 获取组装后的数据
		 * @return
		 */
		public String getAssemble() {
			return assemble == null ? null : assemble.toString();
		}

		/**
		 * 如果占位符是嵌套格式的，需要把里面占位值填充进去后才能得到外层的key
		 * @return
		 */
		public boolean isNest() {
			return nest;
		}
		
		/**
		 * 把key对应的value回插进去
		 */
		public void insertBack(String val) {
			if(nest) {
				setSource(getSource().substring(0, nestStart + 1) + val + getSource().substring(getIndex() + 1));
				setIndex(start);
			} else if(isAssemble) {
				assemble.append(val);
			}
		}

		public String getOpen() {
			return open;
		}

		public String getClose() {
			return close;
		}
		
	}
	
	
	public static void main(String[] args) {
		StringFormatToken p = new StringFormatToken("{", "}", "a{0-{x{}}b{na}mec", true);
		String key = null;
		while((key = p.nextKey()) != null) {
			System.out.println("key: " + key);
			if(key.equals("x"))
				p.insertBack("xv");
			else 
				p.insertBack("i");
		}
		System.out.println(p.getAssemble());
	}

}
