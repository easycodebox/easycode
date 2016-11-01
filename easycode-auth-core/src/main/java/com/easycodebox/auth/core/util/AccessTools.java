package com.easycodebox.auth.core.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easycodebox.auth.core.service.user.OperationService;
import com.easycodebox.auth.model.entity.user.Operation;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.spring.BeanFactory;

/**
 * @author wangxj
 * 
 */
public class AccessTools {
	
	private static final Logger log = LoggerFactory.getLogger(AccessTools.class);
	
	private static OperationService operationService;
	
	private static final long[] operationNoToCode = {
		0x0000000000000001L, 0x0000000000000002L, 0x0000000000000004L, 0x0000000000000008L,
		0x0000000000000010L, 0x0000000000000020L, 0x0000000000000040L, 0x0000000000000080L,	
		0x0000000000000100L, 0x0000000000000200L, 0x0000000000000400L, 0x0000000000000800L,
		0x0000000000001000L, 0x0000000000002000L, 0x0000000000004000L, 0x0000000000008000L,
		0x0000000000010000L, 0x0000000000020000L, 0x0000000000040000L, 0x0000000000080000L,
		0x0000000000100000L, 0x0000000000200000L, 0x0000000000400000L, 0x0000000000800000L,
		0x0000000001000000L, 0x0000000002000000L, 0x0000000004000000L, 0x0000000008000000L,
		0x0000000010000000L, 0x0000000020000000L, 0x0000000040000000L, 0x0000000080000000L,
		0x0000000100000000L, 0x0000000200000000L, 0x0000000400000000L, 0x0000000800000000L,
		0x0000001000000000L, 0x0000002000000000L, 0x0000004000000000L, 0x0000008000000000L,
		0x0000010000000000L, 0x0000020000000000L, 0x0000040000000000L, 0x0000080000000000L,
		0x0000100000000000L, 0x0000200000000000L, 0x0000400000000000L, 0x0000800000000000L,
		0x0001000000000000L, 0x0002000000000000L, 0x0004000000000000L, 0x0008000000000000L,
		0x0010000000000000L, 0x0020000000000000L, 0x0040000000000000L, 0x0080000000000000L,
		0x0100000000000000L, 0x0200000000000000L, 0x0400000000000000L, 0x0800000000000000L,
		0x1000000000000000L, 0x2000000000000000L, 0x4000000000000000L, 0x8000000000000000L };
	
	public static Map<Long, Long> convertOperationNosToOperationCode(List<Long> operationNos) {
		Map<Long, Long> operationCode = new HashMap<Long, Long>();
		
		for(int i = 0; i < operationNos.size(); i ++) {
			long m = operationNos.get(i) / 64;
			int n = (int)(operationNos.get(i) % 64);
			Long val = operationCode.get(m);
			operationCode.put(m, (val == null ? 0 : val) | operationNoToCode[n]);
		}
		
		return operationCode;
	}
	
	public static boolean canDo(long operationNo, Map<Long, Long> operationCode) {
		long m = operationNo / 64;
		int n = (int)(operationNo % 64);
		long val = operationCode.get(m) == null ? 0 : operationCode.get(m);
		return ((val & operationNoToCode[n]) == operationNoToCode[n]);
	}
	
	/**
	 * 判断当前用户能否执行某一操作。
	 * 注意：如果权限列表里面没有对应的权限则默认通过
	 * @param operationId 操作标识
	 * @param operationCodes 当前用户的操作码
	 * @return 可以执行该操作返回true；不能执行该操作返回false。
	 * @throws Exception
	 */
	public static boolean canDo(Integer projectId, String url, Map<Long, Long> operationCodes) {
		if(operationService == null) {
			operationService = BeanFactory.getBean(OperationService.class);
		}
		boolean valid = false;
		Operation o = null;
		try {
			o = operationService.load(null, projectId, url);
			if(o != null)
				valid = true;
		} catch (Exception e) {
			log.error("get operation id error.", e);
		}
		if(valid)
			return AccessTools.canDo(o.getId(), operationCodes);
		else
			return true;
	}
	
}
