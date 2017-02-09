package com.easycodebox.common.lang;

import java.util.Date;

/**
 * 代表一 个Date类型null对象，给那些Date变量不能直接赋null且代表null的逻辑使用
 * @author WangXiaoJin
 *
 */
public class NullDate extends Date {

	public static final NullDate INSTANCE = new NullDate(-1L);
	
	private NullDate() {
        super();
    }

	private NullDate(long date) {
		super(date);
    }

	private NullDate(int year, int month, int date) {
        this(year, month, date, 0, 0, 0);
    }

	private NullDate(int year, int month, int date, int hrs, int min) {
        this(year, month, date, hrs, min, 0);
    }

	@SuppressWarnings("deprecation")
	private NullDate(int year, int month, int date, int hrs, int min, int sec) {
        super(year, month, date, hrs, min, sec);
    }

	@SuppressWarnings("deprecation")
	private NullDate(String s) {
		super(s);
    }
    
}
