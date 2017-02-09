package com.easycodebox.jdbc;

/**
 * @author WangXiaoJin
 *
 */
public enum LockMode {

	NONE("NONE"),
	READ("READ"),
	WRITE("WRITE"),
	/**
	 * An upgrade lock. Objects loaded in this lock mode are
	 * materialized using an SQL <tt>select ... for update</tt>.
	 */
	UPGRADE("UPGRADE"),
	/**
	 * Attempt to obtain an upgrade lock, using an Oracle-style
	 * <tt>select for update nowait</tt>. The semantics of
	 * this lock mode, once obtained, are the same as
	 * <tt>UPGRADE</tt>.
	 */
	UPGRADE_NOWAIT("UPGRADE_NOWAIT");
	
	private String name;
	
	LockMode(String name) {
		this.name=name;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
	
}
