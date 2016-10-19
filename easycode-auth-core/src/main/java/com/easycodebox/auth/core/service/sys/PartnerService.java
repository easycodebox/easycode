package com.easycodebox.auth.core.service.sys;

import java.util.List;

import com.easycodebox.auth.core.pojo.sys.Partner;
import com.easycodebox.common.enums.entity.OpenClose;
import com.easycodebox.common.lang.dto.DataPage;

/**
 * @author WangXiaoJin
 *
 */
public interface PartnerService {

	/**
	 * 合作商列表
	 * @return
	 */
	List<Partner> list();
	
	/**
	 * 合作商详情
	 * @param id
	 * @return
	 */
	Partner load(String id);
	
	/**
	 * 新增合作商
	 * @param partner
	 * @return	应该实现返回数据能获取到主键
	 */
	Partner add(Partner partner);
	
	/**
	 * 修改合作商
	 * @param partner
	 * @return
	 */
	int update(Partner partner);
	
	/**
	 * 逻辑删除合作商
	 * @param ids
	 * @return
	 */
	int remove(String[] ids);
	
	/**
	 * 物理删除合作商
	 * @param ids
	 * @return
	 */
	int removePhy(String[] ids);
	
	/**
	 * 合作商分页
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	DataPage<Partner> page(String name, String partnerKey, 
			String website, OpenClose status, int pageNo, int pageSize);
	
	/**
	 * 判断是否存在指定的名字
	 * @param name
	 * @param excludeId	可空，查找是排除的ID
	 * @return
	 */
	boolean existName(String name, String excludeId);
	
	/**
	 * 开启、关闭合作商
	 * @param ids
	 * @param status
	 * @return
	 */
	int openClose(String[] ids, OpenClose status);
	
}
